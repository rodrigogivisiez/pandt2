package com.potatoandtomato.games.screens;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.statics.Global;

/**
 * Created by SiongLeng on 24/2/2016.
 */
public class SplashLogic {

    private SplashActor _splashActor;
    private Services _services;
    private Runnable _onFinish;
    private boolean finished;

    public SplashLogic(GameCoordinator coordinator, Runnable onFinish, Services services) {
        this._services = services;
        this._onFinish = onFinish;
        _splashActor = new SplashActor(coordinator, services.getAssets(), services.getTexts());

    }

    public void newGame(){
        _services.getSoundsWrapper().playSounds(Sounds.Name.START_GAME);
        Threadings.delay(!Global.NO_ENTRANCE ? 7000 : 0, new Runnable() {
            @Override
            public void run() {
                _splashActor.fadeOutActor(new Runnable() {
                    @Override
                    public void run() {
                        _splashActor.remove();
                        _services.getSoundsWrapper().playTheme();
                        finished = true;
                        _onFinish.run();
                    }
                });
            }
        });
    }

    public void continueGame(){
        _splashActor.remove();
        _services.getSoundsWrapper().playTheme();
        finished = true;
        _onFinish.run();
    }

    public SplashActor getSplashActor() {
        return _splashActor;
    }

    public boolean isFinished() {
        return finished;
    }
}
