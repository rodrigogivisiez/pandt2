package com.potatoandtomato.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class GameCoordinator {

    private String jarPath;
    private String assetsPath;
    private String basePath;
    private GameEntrance gameEntrance;
    private ArrayList<Team> teams;
    private boolean meIsHost;
    private float gameWidth, gameHeight;
    private IPTGame game;
    private SpriteBatch spriteBatch;
    private String userId;
    private IGameSandBox gameSandBox;

    private ArrayList<String> _subscribedIds;
    private Array<InputProcessor> _processors;
    private ArrayList<InGameUpdateListener> _inGameUpdateListeners;

    public GameCoordinator(String jarPath, String assetsPath,
                           String basePath, ArrayList<Team> teams,
                           float gameWidth, float gameHeight,
                           IPTGame game, SpriteBatch batch, boolean meIsHost,
                           String userId, IGameSandBox gameSandBox) {
        this.jarPath = jarPath;
        this.assetsPath = assetsPath;
        this.basePath = basePath;
        this.teams = teams;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.game = game;
        this.spriteBatch = batch;
        this.meIsHost = meIsHost;
        this.userId = userId;
        this.gameSandBox = gameSandBox;

        _subscribedIds = new ArrayList<String>();
        _processors = new Array<InputProcessor>();
        _inGameUpdateListeners = new ArrayList<InGameUpdateListener>();
        subscribeListeners();
    }

    public IGameSandBox getGameSandBox() {
        return gameSandBox;
    }

    public void setGameSandBox(IGameSandBox gameSandBox) {
        this.gameSandBox = gameSandBox;
    }

    public boolean getMeIsHost() {
        return meIsHost;
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
        if(Gdx.files.internal(path).exists()){
            return Gdx.files.internal(path);
        }
        else{
            return Gdx.files.local(basePath + "/" + path);
        }
    }

    public void subscribedBroadcastListener(String id){
        _subscribedIds.add(id);
    }

    public void endGame(){
        for(String id : _subscribedIds){
            Broadcaster.getInstance().unsubscribe(id);
        }
        for(InputProcessor p : _processors){
            getGame().removeInputProcessor(p);
        }

        Broadcaster.getInstance().broadcast(BroadcastEvent.GAME_END);
    }

    public void abandon(){
        getGameSandBox().useConfirm("PTTEXT_ABANDON", new Runnable() {
            @Override
            public void run() {     //yes
                endGame();
            }
        }, new Runnable() {
            @Override
            public void run() {     //no

            }
        });
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
        Broadcaster.getInstance().broadcast(BroadcastEvent.INGAME_UPDATE_REQUEST, msg);
    }

    public void addInGameUpdateListener(InGameUpdateListener listener){
        _inGameUpdateListeners.add(listener);
    }

    public void removeInGameUpdateListener(InGameUpdateListener listener){
        _inGameUpdateListeners.remove(listener);
    }

    private void subscribeListeners(){
        Broadcaster.getInstance().subscribe(BroadcastEvent.INGAME_UPDATE_RESPONSE, new BroadcastListener<InGameUpdateMessage>() {
            @Override
            public void onCallback(InGameUpdateMessage obj, Status st) {
                for(InGameUpdateListener listener : _inGameUpdateListeners){
                    listener.onUpdateReceived(obj.getMsg(), obj.getSenderId());
                }
            }
        });
    }

    public AssetManager getAssetManagerInstance(){
        return new AssetManager(new MyFileResolver(this));
    }


}