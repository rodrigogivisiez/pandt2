package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxScene extends SceneAbstract {

    public GameSandboxScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        Image loadingImage = new Image(_assets.getLoading());
        _root.add(loadingImage).expand().fill();
    }

    public void clearRoot(){
        _root.clear();
    }

}
