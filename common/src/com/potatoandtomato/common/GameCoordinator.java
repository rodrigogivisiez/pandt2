package com.potatoandtomato.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.firebase.client.Firebase;

import java.util.ArrayList;

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
    private String userId;
    private IGameSandBox gameSandBox;
    private UserStateListener userStateListener;
    private Object database;
    private String roomId;
    private ISounds soundManager;
    private AssetsWrapper assetsWrapper;
    private Broadcaster broadcaster;
    private DecisionsMaker decisionsMaker;
    private IDownloader downloader;

    private boolean _landscape;
    private ArrayList<String> _subscribedIds;
    private Array<InputProcessor> _processors;
    private ArrayList<InGameUpdateListener> _inGameUpdateListeners;


    private String _broadcastSubscribedId;


    public GameCoordinator(String jarPath, String assetsPath,
                           String basePath, ArrayList<Team> teams,
                           float gameWidth, float gameHeight,
                           IPTGame game, SpriteBatch batch,
                           String userId, IGameSandBox gameSandBox,
                           Object database, String roomId,
                           ISounds sounds, Broadcaster broadcaster,
                           IDownloader downloader) {
        this.jarPath = jarPath;
        this.assetsPath = assetsPath;
        this.basePath = basePath;
        this.teams = teams;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.game = game;
        this.spriteBatch = batch;
        this.userId = userId;
        this.gameSandBox = gameSandBox;
        this.database = database;
        this.roomId = roomId;
        this.soundManager = sounds;
        this.broadcaster = broadcaster;
        this.downloader = downloader;
        this.decisionsMaker = new DecisionsMaker(this.teams);

        _subscribedIds = new ArrayList<String>();
        _processors = new Array<InputProcessor>();
        _inGameUpdateListeners = new ArrayList<InGameUpdateListener>();
        subscribeListeners();
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    protected void setSpriteBatch(SpriteBatch spriteBatch) {
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
                getGameSandBox().userAbandoned();
                endGame();
            }
        }, new Runnable() {
            @Override
            public void run() {     //no

            }
        });
    }

    public void userAbandon(String userId){
        if(this.userStateListener != null) userStateListener.userAbandoned(userId);
        setPlayerConnectionChanged(userId, false);
    }

    public void userConnectionChanged(String userId, boolean connected){
        if(this.userStateListener != null) {
            if(connected){
                userStateListener.userConnected(userId);
            }
            else{
                userStateListener.userDisconnected(userId);
            }
        }
        setPlayerConnectionChanged(userId, connected);
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
        return decisionsMaker.checkIsDecisionMaker(this.getUserId());
    }

    public int getMyUniqueIndex(){
        int i = 0;
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                if(player.getUserId().equals(getUserId())){
                    return i;
                }
                i++;
            }
        }
        return -1;
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

    public AssetManager getAssetManager(){
        if(assetsWrapper == null) assetsWrapper = new AssetsWrapper(new MyFileResolver(this));

        return assetsWrapper.getAssetManager();
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
        return getHostUserId().equals(getUserId());
    }

    public Firebase getFirebase(){
        return (Firebase) database;
    }

    public String getRoomId() {
        return roomId;
    }

    public ISounds getSoundManager() {
        return soundManager;
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

    @Override
    public void dispose() {
        broadcaster.unsubscribe(_broadcastSubscribedId);
        userStateListener = null;
        _inGameUpdateListeners.clear();
        if(assetsWrapper != null) assetsWrapper.dispose();
        broadcaster.broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
    }
}
