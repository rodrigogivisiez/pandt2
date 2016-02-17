package com.potatoandtomato.games.screens.loading_screen;

import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.games.abs.screens.LogicAbstract;
import com.potatoandtomato.games.helpers.MainController;

/**
 * Created by SiongLeng on 15/2/2016.
 */
public class LoadingLogic extends LogicAbstract {

    private LoadingScreen _loadingScreen;


    public LoadingLogic(MainController mainController) {
        super(mainController);

        _loadingScreen = new LoadingScreen(getGameCoordinator(), getAssets());
    }

    @Override
    public GameScreen getScreen() {
        return _loadingScreen;
    }
}
