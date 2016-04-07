package com.potatoandtomato.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.firebase.client.Firebase;
import com.potatoandtomato.common.absints.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.helpers.ConnectionMonitor;
import com.potatoandtomato.common.models.*;
import com.potatoandtomato.common.utils.DecisionsMaker;
import com.potatoandtomato.common.utils.MyFileResolver;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class GameCoordinator implements Disposable {

    private String jarPath;
    private String assetsPath;
    private String basePath;
    private GameEntrance gameEntrance;
    private ArrayList<Team> teams;
    private float gameWidth, gameHeight;
    private IPTGame game;
    private SpriteBatch spriteBatch;
    private String myUserId;
    private IGameSandBox gameSandBox;
    private UserStateListener userStateListener;
    private Object database;
    private String roomId;
    private ISoundsPlayer soundsPlayer;
    private AssetManager assetsManager;
    private Broadcaster broadcaster;
    private DecisionsMaker decisionsMaker;
    private IDownloader downloader;
    private ITutorials tutorials;
    private GamePreferencesAbstract gamePreferences;

    private boolean _landscape;
    private ArrayList<String> _subscribedIds;
    private Array<InputProcessor> _processors;
    private ArrayList<InGameUpdateListener> _inGameUpdateListeners;

    private String _broadcastSubscribedId;

    private EndGameResult _endGameResult;
    private ConnectionMonitor _connectionMonitor;

    public GameCoordinator(String jarPath, String assetsPath,
                           String basePath, ArrayList<Team> teams,
                           float gameWidth, float gameHeight,
                           IPTGame game, SpriteBatch batch,
                           String myUserId, IGameSandBox gameSandBox,
                           Object database, String roomId,
                           ISoundsPlayer sounds, Broadcaster broadcaster,
                           IDownloader downloader, ITutorials tutorials,
                           GamePreferencesAbstract gamePreferences) {
        this.jarPath = jarPath;
        this.assetsPath = assetsPath;
        this.basePath = basePath;
        this.teams = teams;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.game = game;
        this.spriteBatch = batch;
        this.myUserId = myUserId;
        this.gameSandBox = gameSandBox;
        this.database = database;
        this.roomId = roomId;
        this.soundsPlayer = sounds;
        this.broadcaster = broadcaster;
        this.downloader = downloader;
        this.tutorials = tutorials;
        this.gamePreferences = gamePreferences;
        this.decisionsMaker = new DecisionsMaker(this.teams);

        _subscribedIds = new ArrayList<String>();
        _processors = new Array<InputProcessor>();
        _inGameUpdateListeners = new ArrayList<InGameUpdateListener>();
        _connectionMonitor = new ConnectionMonitor(new ConnectionMonitorListener() {
            @Override
            public void onExceedReconnectLimitTime(String userId) {
                forceAbandonOtherUser(userId);
            }
        });
        subscribeListeners();
    }

    public ITutorials getTutorials() {
        return tutorials;
    }

    public GamePreferencesAbstract getGamePreferences() {
        return gamePreferences;
    }

    public IDownloader getDownloader() {
        return downloader;
    }

    public void setDownloader(IDownloader downloader) {
        this.downloader = downloader;
    }

    public IGameSandBox getGameSandBox() {
        return gameSandBox;
    }

    public void setGameSandBox(IGameSandBox gameSandBox) {
        this.gameSandBox = gameSandBox;
    }

    public String getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void setSpriteBatch(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public IPTGame getGame() {
        return game;
    }

    public float getGameWidth() {
        return gameWidth;
    }

    public float getGameHeight() {
        return gameHeight;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
        decisionsMaker.teamsChanged(teams);
    }

    public Team getMyTeam(){
        for(Team team : teams){
            if(team.hasUser(getMyUserId())){
                return team;
            }
        }
        return null;
    }


    public ArrayList<Player> getMyTeamPlayers(){
        for(Team team : teams){
            if(team.hasUser(getMyUserId())){
                return team.getPlayers();
            }
        }
        return new ArrayList<Player>();
    }

    public ArrayList<Team> getEnemyTeams(){
        ArrayList<Team> result = new ArrayList<Team>();
        for(Team team : teams){
            if(!team.hasUser(getMyUserId())){
                result.add(team);
            }
        }
        return result;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    public GameEntrance getGameEntrance() {
        return gameEntrance;
    }

    public void setGameEntrance(GameEntrance gameEntrance) {
        this.gameEntrance = gameEntrance;
    }

    public FileHandle getFileH(String path){
        if(path.contains(".gen")) path = path.replace(".gen", "");

        if(Gdx.files.local(basePath + "/" + path).exists()){
            return Gdx.files.local(basePath + "/" + path);
        }
        else{
            return Gdx.files.internal(path);
        }
    }

    public void setLandscape(){
        float originalHeight = this.gameHeight;
        this.gameHeight = this.gameWidth;
        this.gameWidth = originalHeight;
        _landscape = true;
    }

    public boolean isLandscape() {
        return _landscape;
    }

    public void subscribedBroadcastListener(String id){
        _subscribedIds.add(id);
    }

    public void endGame(){
        if(_endGameResult == null){
            System.out.println("Error: please call beforeEndGame() function before end game!!!!!");
            return;
        }

        for(String id : _subscribedIds){
            broadcaster.unsubscribe(id);
        }
        for(InputProcessor p : _processors){
            getGame().removeInputProcessor(p);
        }

        getGameSandBox().endGame();
    }

    public void abandon(){
        getGameSandBox().useConfirm("PTTEXT_ABANDON", new Runnable() {
            @Override
            public void run() {     //yes
                getGameSandBox().userAbandoned(getMyUserId());
                ArrayList<Team> losersTeam = new ArrayList<Team>();
                losersTeam.add(getMyTeam());
                beforeEndGame(new HashMap<Team, ArrayList<ScoreDetails>>(), losersTeam);
                endGame();
            }
        }, new Runnable() {
            @Override
            public void run() {     //no

            }
        });
    }

    public void forceAbandonOtherUser(String userId){
        getGameSandBox().userAbandoned(userId);
    }

    public void userAbandon(String userId){
        setPlayerConnectionChanged(userId, false);
        _connectionMonitor.userAbandoned(userId);
        if(this.userStateListener != null) userStateListener.userAbandoned(userId);
    }

    public void userConnectionChanged(String userId, boolean connected){
        setPlayerConnectionChanged(userId, connected);
        _connectionMonitor.connectionChanged(userId, connected);
        if(this.userStateListener != null) {
            if(connected){
                userStateListener.userConnected(userId);
            }
            else{
                userStateListener.userDisconnected(userId);
            }
        }
    }

    private void setPlayerConnectionChanged(String userId, boolean connected){
        for(Team team : teams){
            if(team.getPlayerByUserId(userId) != null){
                team.getPlayerByUserId(userId).setIsConnected(connected);
                decisionsMaker.teamsChanged(teams);
                return;
            }
        }
    }

    public boolean meIsDecisionMaker(){
        return decisionsMaker.checkIsDecisionMaker(this.getMyUserId());
    }

    public int getMyUniqueIndex(){
        int i = 0;
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                if(player.getUserId().equals(getMyUserId())){
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    public Player getPlayerByUniqueIndex(int index){
        int i = 0;
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                if(index == i){
                    return player;
                }
                i++;
            }
        }
        return new Player("", "", false, true, Color.BLACK);
    }


    public void addInputProcessor(InputProcessor processor){
        _processors.add(processor);
        getGame().addInputProcessor(processor);
    }

    public void removeInputProcessor(InputProcessor processor){
        _processors.removeValue(processor, false);
        getGame().removeInputProcessor(processor);
    }

    public void sendRoomUpdate(String msg){
        getGameSandBox().inGameUpdateRequest(msg);
    }

    public void addInGameUpdateListener(InGameUpdateListener listener){
        _inGameUpdateListeners.add(listener);
    }

    public void removeInGameUpdateListener(InGameUpdateListener listener){
        _inGameUpdateListeners.remove(listener);
    }

    private void subscribeListeners(){
        _broadcastSubscribedId = broadcaster.subscribe(BroadcastEvent.INGAME_UPDATE_RESPONSE, new BroadcastListener<InGameUpdateMessage>() {
            @Override
            public void onCallback(InGameUpdateMessage obj, Status st) {
                for (InGameUpdateListener listener : _inGameUpdateListeners) {
                    listener.onUpdateReceived(obj.getMsg(), obj.getSenderId());
                }
            }
        });
    }

    public AssetManager getAssetManager(boolean singleton){
        if(assetsManager == null) assetsManager = new AssetManager(new MyFileResolver(this));
        if(singleton){
            return assetsManager;
        }
        else{
            return new AssetManager(new MyFileResolver(this));
        }
    }

    public void setUserStateListener(UserStateListener userStateListener){
        this.userStateListener = userStateListener;
    }

    public String getHostUserId(){
        for(Team team : getTeams()){
            for(Player player : team.getPlayers()){
                if(player.getIsHost()){
                    return player.getUserId();
                }
            }
        }
        return null;
    }

    public boolean isHost(){
        return getHostUserId().equals(getMyUserId());
    }

    public Firebase getFirebase(){
        return (Firebase) database;
    }

    public Firebase getTestingFirebase(){
        return getFirebase().child("testing");
    }

    public String getRoomId() {
        return roomId;
    }

    public ISoundsPlayer getSoundsPlayer() {
        return soundsPlayer;
    }

    public void requestVibrate(double periodInMili){
        broadcaster.broadcast(BroadcastEvent.VIBRATE_DEVICE, periodInMili);
    }

    public void finishLoading(){
        this.getGameSandBox().onGameLoaded();
    }

    public ArrayList<InGameUpdateListener> getInGameUpdateListeners() {
        return _inGameUpdateListeners;
    }

    public EndGameResult getEndGameResult() {
        return _endGameResult;
    }

    public void beforeEndGame(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers){
        if(winners == null && losers == null){
            this._endGameResult = new EndGameResult();
            return;
        }

        if(winners == null) winners = new HashMap<Team, ArrayList<ScoreDetails>>();
        if(losers == null) losers = new ArrayList<Team>();

        if(meIsDecisionMaker()){
            gameSandBox.updateScores(winners, losers);
        }

        this._endGameResult = new EndGameResult();
        this._endGameResult.setMyTeam(getMyTeamPlayers());

        for(Team loserTeam : losers){
            if(loserTeam.hasUser(getMyUserId())){
                this._endGameResult.setWon(false);
            }
        }

        for(Team winnerTeam : winners.keySet()){
            if(winnerTeam.hasUser(getMyUserId())){
                this._endGameResult.setWon(true);
                this._endGameResult.setScoreDetails(winners.get(winnerTeam));
            }
        }
    }


    @Override
    public void dispose() {
        _connectionMonitor.dispose();
        broadcaster.unsubscribe(_broadcastSubscribedId);
        userStateListener = null;
        _inGameUpdateListeners.clear();
        if(assetsManager != null) assetsManager.dispose();
        broadcaster.broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
        for(InputProcessor processor : _processors){
            getGame().removeInputProcessor(processor);
        }
    }
}
