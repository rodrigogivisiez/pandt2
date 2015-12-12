package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class CreateGameLogic extends LogicAbstract {

    CreateGameScene _scene;

    public CreateGameLogic(PTScreen screen, Assets assets) {
        super(screen, assets);
        _scene = new CreateGameScene(_assets);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
