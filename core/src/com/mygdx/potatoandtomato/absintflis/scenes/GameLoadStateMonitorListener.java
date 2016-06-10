package com.mygdx.potatoandtomato.absintflis.scenes;

import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.Player;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public abstract class GameLoadStateMonitorListener {

    public abstract void onAllSuccess(GameCoordinator gameCoordinator);
    public abstract void onPlayerReady(Player player);
    public abstract void onFailed(Player failedPlayers);

}
