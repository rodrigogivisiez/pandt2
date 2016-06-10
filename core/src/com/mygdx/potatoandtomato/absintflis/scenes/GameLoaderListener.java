package com.mygdx.potatoandtomato.absintflis.scenes;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public abstract class GameLoaderListener {

    public abstract void onFinished(GameCoordinator coordinator, Status status);
}
