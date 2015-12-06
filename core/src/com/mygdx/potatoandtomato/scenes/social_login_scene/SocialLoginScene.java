package com.mygdx.potatoandtomato.scenes.social_login_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.abstractions.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Assets;
import com.mygdx.potatoandtomato.scenes.shared_actors.BtnEggUpright;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class SocialLoginScene extends SceneAbstract {

    public SocialLoginScene(Assets assets) {
        super(assets);
    }

    @Override
    public void populateRoot() {
        Image img = new Image(_textures.getPlayIcon());
        BtnEggUpright bb = new BtnEggUpright(_textures);
        _root.add(img);
    }
}
