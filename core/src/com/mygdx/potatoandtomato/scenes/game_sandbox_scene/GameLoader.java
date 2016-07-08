package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.GameLoaderListener;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.helpers.RemoteHelper;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public class GameLoader implements Disposable {

    private Room room;
    private Services services;
    private PTScreen ptScreen;
    private GameLoaderListener gameLoaderListener;
    private String broadcastId;
    private IGameSandBox gameSandBox;
    private GameCoordinator gameCoordinator;

    public GameLoader(Room room, Services services, PTScreen ptScreen, IGameSandBox gameSandBox) {
        this.room = room;
        this.services = services;
        this.ptScreen = ptScreen;
        this.gameSandBox = gameSandBox;

        services.getPreferences().setGameAbbr(room.getGame().getAbbr());
    }

    public void load(){
        broadcastId = services.getBroadcaster().subscribe(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                if (st == Status.SUCCESS) {
                    gameCoordinator = obj;
                    gameLoaderListener.onFinished(obj, Status.SUCCESS);
                } else {
                    gameLoaderListener.onFinished(null, Status.FAILED);
                }
            }
        });

        services.getBroadcaster().broadcast(BroadcastEvent.LOAD_GAME_REQUEST, new GameCoordinator(room.getGame().getFullLocalJarPath(),
                        room.getGame().getBasePath(), room.getTeams(),
                        Positions.getWidth(), Positions.getHeight(), ptScreen.getGame(), ptScreen.getGame().getSpriteBatch(),
                        services.getProfile().getUserId(), gameSandBox, services.getDatabase().getGameBelongDatabase(room.getGame().getAbbr()),
                        room.getId(), services.getSoundsPlayer(), new RemoteHelper(services.getBroadcaster()), services.getTutorials(),
                        services.getPreferences(), Global.LEADERBOARD_COUNT, services.getConnectionWatcher(), services.getCoins()));

    }

    public void disposeGameCoordinator(){
        if(gameCoordinator != null){
            if(gameCoordinator.getGameEntrance() != null) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gameCoordinator.getGameEntrance().dispose();
                    }
                });
            }
            gameCoordinator.dispose();
        }
    }

    @Override
    public void dispose() {
        if(broadcastId != null) services.getBroadcaster().unsubscribe(broadcastId);
    }

    public void setGameLoaderListener(GameLoaderListener gameLoaderListener){
        this.gameLoaderListener = gameLoaderListener;
    }


}
