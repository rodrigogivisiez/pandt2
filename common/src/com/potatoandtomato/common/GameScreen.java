package com.potatoandtomato.common;

import com.badlogic.gdx.Screen;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public abstract class GameScreen implements Screen{

    private boolean _pause;;
    private GameCoordinator _coordinator;
    private String _pausedSubscribedId, _resumedSubscribedId;

    public GameScreen(GameCoordinator gameCoordinator) {
        this._coordinator = gameCoordinator;
        _pausedSubscribedId = Broadcaster.getInstance().subscribe(BroadcastEvent.GAME_PAUSED, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                _pause = true;
            }
        });

        _resumedSubscribedId = Broadcaster.getInstance().subscribe(BroadcastEvent.GAME_RESUME, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                _pause = false;
            }
        });

        _coordinator.subscribedBroadcastListener(_pausedSubscribedId);
        _coordinator.subscribedBroadcastListener(_resumedSubscribedId);
    }

    public void setPause(boolean _pause) {
        this._pause = _pause;
    }

    public boolean isPause() {
        return _pause;
    }

    public GameCoordinator getCoordinator() {
        return _coordinator;
    }

}
