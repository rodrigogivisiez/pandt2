package com.potatoandtomato.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.firebase.client.Firebase;
import com.potatoandtomato.common.absints.*;
import com.potatoandtomato.common.enums.*;
import com.potatoandtomato.common.helpers.DecisionsMaker;
import com.potatoandtomato.common.helpers.GameDataHelper;
import com.potatoandtomato.common.models.*;
import com.potatoandtomato.common.utils.MyFileResolver;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
    private PTAssetsManager ptAssetsManager;
    private DecisionsMaker decisionsMaker;
    private ITutorials tutorials;
    private GamePreferencesAbstract gamePreferences;
    private int leaderboardSize;
    private GameDataHelper gameDataHelper;
    private IDisconnectOverlayControl disconnectOverlayControl;
    private ICoins iCoins;

    private boolean landscape;
    private boolean gameStarted;
    private boolean finalized;
    private boolean finishLoading;
    private ArrayList<InGameUpdateListener> inGameUpdateListeners;
    private ArrayList<LeaderboardRecord> gameLeaderboardRecords;

    private EndGameResult endGameResult;
    private IRemoteHelper remoteHelper;
    private ArrayList<Runnable> onResumeRunnables;
    private ArrayList<SelfConnectionListener> selfConnectionListeners;

    public GameCoordinator(String jarPath, String assetsPath,
                           String basePath, ArrayList<Team> teams,
                           float gameWidth, float gameHeight,
                           IPTGame game, SpriteBatch batch,
                           String myUserId, IGameSandBox gameSandBox,
                           Object database, String roomId,
                           ISoundsPlayer sounds, IRemoteHelper remoteHelper,
                           ITutorials tutorials,
                           GamePreferencesAbstract gamePreferences,
                           int leaderboardSize, IDisconnectOverlayControl iDisconnectOverlayControl,
                           ICoins iCoins) {
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
        this.tutorials = tutorials;
        this.gamePreferences = gamePreferences;
        this.decisionsMaker = new DecisionsMaker(this.teams, myUserId, gameSandBox);
        this.leaderboardSize = leaderboardSize;
        this.remoteHelper = remoteHelper;
        this.disconnectOverlayControl = iDisconnectOverlayControl;
        this.iCoins = iCoins;

        gameDataHelper = new GameDataHelper(teams, myUserId, decisionsMaker,
                                                    gameSandBox, game, disconnectOverlayControl, this);
        onResumeRunnables = new ArrayList();
        gameLeaderboardRecords = new ArrayList<LeaderboardRecord>();
        inGameUpdateListeners = new ArrayList<InGameUpdateListener>();
        selfConnectionListeners = new ArrayList<SelfConnectionListener>();
    }

    ///////////////////////////////////////////////////////////////
    //All about teams
    ///////////////////////////////////////////////////////////////
    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
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
    ///////////////////////////////////////////////////////
    //All about loading game jar
    /////////////////////////////////////////////////////////
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

    /////////////////////////////////////////////////
    //Landscaping
    /////////////////////////////////////////////////
    public void setLandscape(){
        float originalHeight = this.gameHeight;
        this.gameHeight = this.gameWidth;
        this.gameWidth = originalHeight;
        landscape = true;
    }

    public boolean isLandscape() {
        return landscape;
    }

    ////////////////////////////////////////////////////
    //Abandoning
    ///////////////////////////////////////////////////
    public void abandon(){
        abandon(null);
    }

    public void abandon(final Runnable confirmedAbandon){
        if(!finalized){

            ConfirmMsgType confirmMsgType = ConfirmMsgType.AbandonGameNoCons;
            if(willAbandonLoseStreak()) confirmMsgType = ConfirmMsgType.AbandonGameConsLoseStreak;

            gameSandBox.useConfirm(confirmMsgType, new Runnable() {
                @Override
                public void run() {     //yes
                    gameSandBox.userAbandoned(getMyUserId());
                    if (confirmedAbandon != null) confirmedAbandon.run();
                }
            }, new Runnable() {
                @Override
                public void run() {     //no

                }
            });
        }
    }

    public boolean willAbandonLoseStreak(){
        int otherConnectedPlayerCount = 0;
        ArrayList<Player> players = getAllConnectedPlayers();
        for(Player player : players){
            if(!player.getUserId().equals(myUserId)){
                otherConnectedPlayerCount++;
            }
        }

        if(getMyLeaderRecord() != null && getMyLeaderRecord().getStreak().hasValidStreak() && otherConnectedPlayerCount > 0){
             return true;
        }
        else{
            return false;
        }
    }

    ////////////////////////////////////////////////////
    //User connection related
    ////////////////////////////////////////////////////
    public void userAbandon(String userId){
        gameDataHelper.userConnectionChanged(userId, false);
        decisionsMaker.userConnectionChanged(userId, false);

        if(this.userStateListener != null && !userId.equals(myUserId)) userStateListener.userAbandoned(userId);
    }

    public void userConnectionChanged(String userId, boolean connected){
        gameDataHelper.userConnectionChanged(userId, connected);
        decisionsMaker.userConnectionChanged(userId, connected);
        if(this.userStateListener != null && !userId.equals(myUserId)) {
            if(connected){
                userStateListener.userConnected(userId);
            }
            else{
                userStateListener.userDisconnected(userId);
            }
        }

        if(userId.equals(myUserId)){
            for(SelfConnectionListener listener : selfConnectionListeners){
                listener.onSelfConnectionChanged(connected ?
                        SelfConnectionStatus.ConnectionRecoverd : SelfConnectionStatus.DisconnectedButRecoverable);
            }
        }

    }


    public void setUserStateListener(UserStateListener userStateListener){
        this.userStateListener = userStateListener;
    }

    public void addSelfConnectionListener(SelfConnectionListener listener) {
        selfConnectionListeners.add(listener);
    }

    public void removeSelfConnectionListener(SelfConnectionListener listener) {
        selfConnectionListeners.remove(listener);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //Retrieving players data
    //////////////////////////////////////////////////////////////////////////////////////
    public ConcurrentHashMap<Integer, Player> getIndexToPlayersConcurrentMap(){
        ConcurrentHashMap<Integer, Player> playerHashMap = new ConcurrentHashMap();

        for(Team team : teams){
            for(Player player : team.getPlayers()){
                playerHashMap.put(player.getSlotIndex(), player);
            }
        }

        return playerHashMap;
    }

    //unique index is the same as slot index
    public int getMyUniqueIndex(){
        return getPlayerUniqueIndex(getMyUserId());
    }

    public int getPlayerUniqueIndex(String userId){
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                if(player.getUserId().equals(userId)){
                    return player.getSlotIndex();
                }
            }
        }
        return -1;
    }


    public Player getPlayerByUniqueIndex(int index){
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                if(index == player.getSlotIndex()){
                    return player;
                }
            }
        }
        return null;
    }

    public Player getPlayerByUserId(String userId){
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                if(player.getUserId().equals(userId)){
                    return player;
                }
            }
        }
        return null;
    }

    public int getTotalPlayersCount(){
        int result = 0;

        for(Team team : teams){
            result += team.getPlayers().size();
        }
        return result;
    }

    public ArrayList<Player> getAllConnectedPlayers(){
        ArrayList<String> connectedUserIds = decisionsMaker.getDecisionMakersSequence();
        ArrayList<Player> result = new ArrayList();
        for(String userId : connectedUserIds){
            result.add(getPlayerByUserId(userId));
        }
        return result;
    }


    /////////////////////////////////////////////////////////////////////////////
    //Decision maker
    ////////////////////////////////////////////////////////////////////////////
    public DecisionsMaker getDecisionsMaker(){
        return decisionsMaker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //Leaderboards related
    ////////////////////////////////////////////////////////////////////////////////////////
    public int getLeaderboardSize() {
        return leaderboardSize;
    }

    public void setLeaderboardSize(int leaderboardSize) {
        this.leaderboardSize = leaderboardSize;
    }

    public ArrayList<LeaderboardRecord> getGameLeaderboardRecords() {
        return gameLeaderboardRecords;
    }

    public void setGameLeaderboardRecords(ArrayList<LeaderboardRecord> _gameLeaderboardRecords) {
        this.gameLeaderboardRecords = _gameLeaderboardRecords;
    }

    public LeaderboardRecord getMyLeaderRecord(){
        return getMyTeam().getLeaderboardRecord();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //Ingame update
    /////////////////////////////////////////////////////////////////////////////////////////
    public void sendRoomUpdate(String msg){
        if(!gameStarted){
            System.out.println("You are not allowed to send update message before game started.");
            return;
        }
        gameSandBox.sendUpdate(RoomUpdateType.InGame, msg);
    }

    public void sendPrivateRoomUpdate(String toUserId, String msg){
        if(!gameStarted){
            System.out.println("You are not allowed to send update message before game started.");
            return;
        }
        gameSandBox.sendPrivateUpdate(RoomUpdateType.InGame, toUserId, msg);
    }

    public void receivedRoomUpdate(final String msg, final String senderId){
        Runnable toRun = new Runnable() {
            @Override
            public void run() {
                for (InGameUpdateListener listener : inGameUpdateListeners) {
                    listener.onUpdateReceived(msg, senderId);
                }
            }
        };

        if(gameDataHelper.isActivated() && !gameDataHelper.hasData()){
            gameDataHelper.addToRunWhenHaveData(toRun);
        }
        else{
            toRun.run();
        }
    }

    public void addInGameUpdateListener(InGameUpdateListener listener){
        inGameUpdateListeners.add(listener);
    }

    public void removeInGameUpdateListener(InGameUpdateListener listener){
        inGameUpdateListeners.remove(listener);
    }

    public ArrayList<InGameUpdateListener> getInGameUpdateListeners() {
        return inGameUpdateListeners;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //Ingame firebase
    ///////////////////////////////////////////////////////////////////////////////////
    public Firebase getFirebase(){
        return (Firebase) database;
    }

    public Firebase getTestingFirebase(){
        return getFirebase().child("testing");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //Retrieving web image
    /////////////////////////////////////////////////////////////////////////////////////////
    public void getRemoteImage(final String url, final WebImageListener listener){
        getRemoteHelper().getRemoteImage(url, listener);
    }

    public IRemoteHelper getRemoteHelper() {
        return remoteHelper;
    }

    public void setRemoteHelper(IRemoteHelper _remoteHelper) {
        this.remoteHelper = _remoteHelper;
    }

    //will be called when onResume lifecycle fired, mainly used to solve web image became black square problem
    public void addOnResumeRunnable(Runnable runnable){
        onResumeRunnables.add(runnable);
        game.addOnResumeRunnable(runnable);
    }

    public void removeOnResumeRunnable(Runnable runnable){
        onResumeRunnables.remove(runnable);
        game.removeOnResumeRunnable(runnable);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //Assets and sounds
    ////////////////////////////////////////////////////////////////////////////////////////
    public PTAssetsManager getPTAssetManager(boolean singleton){
        if(ptAssetsManager == null) ptAssetsManager = new PTAssetsManager(new MyFileResolver(this), game);
        if(singleton){
            return ptAssetsManager;
        }
        else{
            return new PTAssetsManager(new MyFileResolver(this), game);
        }
    }

    public ISoundsPlayer getSoundsPlayer() {
        return soundsPlayer;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //For reuse client stage spritebatch
    ////////////////////////////////////////////////////////////////////////////////////////
    public void addInputProcessor(InputProcessor processor){
        game.addInputProcessor(processor, 0, true);
    }

    public void removeInputProcessor(InputProcessor processor){
        game.removeInputProcessor(processor);
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void setSpriteBatch(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public void setScreen(Screen screen){
        game.setScreen(screen);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Ending game
    /////////////////////////////////////////////////////////////////////////////////////////

    //finalize game, after this method, other user will not be allowed to reconnect back to game
    public void finalizeAndEndGame(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, boolean abandon){
        finalizeGame(winners, losers, abandon);
        endGame();
    }

    public void finalizeGame(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, boolean abandon){
        finalized = true;

        gameDataHelper.gameFinalized();

        if(winners == null) winners = new HashMap<Team, ArrayList<ScoreDetails>>();
        if(losers == null) losers = new ArrayList<Team>();

        gameSandBox.finalizing(winners, losers, abandon);

        if(winners.size() == 0 && losers.size() == 0){
            this.endGameResult = new EndGameResult();
            this.endGameResult.setAbandon(abandon);
            if(abandon) this.endGameResult.setWillLoseStreak(willAbandonLoseStreak());
            return;
        }

        this.endGameResult = new EndGameResult();
        this.endGameResult.setAbandon(abandon);
        this.endGameResult.setMyTeam(getMyTeamPlayers());
        if(abandon) this.endGameResult.setWillLoseStreak(willAbandonLoseStreak());

        for(Team loserTeam : losers){
            if(loserTeam.hasUser(getMyUserId())){
                this.endGameResult.setWon(false);
            }
        }

        this.endGameResult.setWinnersScoreDetails(winners);
        this.endGameResult.setLoserTeams(losers);

        for(Team winnerTeam : winners.keySet()){
            if(winnerTeam.hasUser(getMyUserId())){
                this.endGameResult.setWon(true);
            }
        }
    }

    public void endGame(){
        if(!finalized){
            System.out.println("Error: please call finalizeGame() function before end game.");
            return;
        }

        gameSandBox.endGame();
    }

    public void raiseGameFailedError(String msg){
        gameSandBox.gameFailed(msg);
    }

    //////////////////////////////////////////////////////////////////////////////////
    //about coins
    ////////////////////////////////////////////////////////////////////////////////////
    public void coinsInputRequest(CoinRequestType coinRequestType, int coinPerPerson, final CoinListener coinListener){
        iCoins.reset();

        ArrayList<Pair<String, String>> userIdToNamePairs = new ArrayList();
        switch (coinRequestType){
            case MeOnly:
                for(Player player : getMyTeam().getPlayers()){
                    if(player.getUserId().equals(myUserId)){
                        userIdToNamePairs.add(new Pair<String, String>(player.getUserId(), player.getName()));
                        break;
                    }
                }
                break;
            case MyTeam:
                for(Player player : getMyTeam().getPlayers()){
                    userIdToNamePairs.add(new Pair<String, String>(player.getUserId(), player.getName()));
                }
                break;
            case Everyone:
                for(Team team : getTeams()){
                    for(Player player : team.getPlayers()){
                        userIdToNamePairs.add(new Pair<String, String>(player.getUserId(), player.getName()));
                    }
                }
                break;
        }

        iCoins.initCoinMachine(coinPerPerson * userIdToNamePairs.size(), roomId + "_game", userIdToNamePairs, true);
        iCoins.setCoinListener(new CoinListener() {
            @Override
            public void onEnoughCoins() {
                coinListener.onEnoughCoins();
                iCoins.hideCoinMachine();
                iCoins.startDeductCoins();
            }

            @Override
            public void onDeductCoinsDone(String extra, Status status) {
                coinListener.onDeductCoinsDone(extra, status);
            }
        });
        iCoins.showCoinMachine();

    }


    //////////////////////////////////////////////////////////////////////////////////
    //MISC.
    /////////////////////////////////////////////////////////////////////////////////////
    public void requestVibrate(double periodInMili){
        gameSandBox.vibrate(periodInMili);
    }

    public void finishLoading(){
        this.finishLoading = true;
    }

    public boolean isFinishLoading() {
        return finishLoading;
    }

    //will be called when all users loaded successfully and game started
    public void setGameStarted(boolean gameStarted, boolean isContinue) {
        this.gameStarted = gameStarted;
        if(gameStarted){
            gameDataHelper.onGameStarted(isContinue);
        }
    }


    /////////////////////////////////////////////////////////////////
    //Disposing
    ///////////////////////////////////////////////////////////////
    @Override
    public void dispose() {
        userStateListener = null;
        inGameUpdateListeners.clear();
        if(ptAssetsManager != null) {
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    ptAssetsManager.dispose();
                }
            });
        }

        game.removeAllExternalProcessors();

        for(Runnable runnable : onResumeRunnables){
            game.removeOnResumeRunnable(runnable);
        }
        onResumeRunnables.clear();
        remoteHelper.dispose();
        selfConnectionListeners.clear();
        gameDataHelper.dispose();
        decisionsMaker.dispose();
        iCoins.reset();
    }

    ////////////////////////////////////////////////////////////////
    //Getters and setters
    ////////////////////////////////////////////////////////////////

    //database room id, not warp roomid
    public String getRoomId() {
        return roomId;
    }

    public EndGameResult getEndGameResult() {
        return endGameResult;
    }

    public ITutorials getTutorials() {
        return tutorials;
    }

    public GamePreferencesAbstract getGamePreferences() {
        return gamePreferences;
    }

    public String getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
        gameDataHelper.setMyUserId(myUserId);
        decisionsMaker.setMyUserId(myUserId);
    }

    public float getGameWidth() {
        return gameWidth;
    }

    public float getGameHeight() {
        return gameHeight;
    }

    public GameDataHelper getGameDataHelper() {
        return gameDataHelper;
    }

    public void setGameDataHelper(GameDataHelper gameDataHelper) {
        this.gameDataHelper = gameDataHelper;
    }

    public void setDecisionsMaker(DecisionsMaker decisionsMaker) {
        this.decisionsMaker = decisionsMaker;
    }
}
