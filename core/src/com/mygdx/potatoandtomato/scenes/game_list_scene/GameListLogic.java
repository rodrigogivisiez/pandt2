package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListLogic extends LogicAbstract {

    GameListScene _scene;

    public GameListLogic(PTScreen screen, Assets assets) {
        super(screen, assets);
        _scene = new GameListScene(assets);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
