package com.mygdx.potatoandtomato.scenes.social_login_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.abstractions.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class SocialLoginLogic extends LogicAbstract {

    SocialLoginScene _scene;

    public SocialLoginLogic(PTScreen screen, Textures textures, Fonts fonts) {
        super(screen, textures, fonts);
        _scene = new SocialLoginScene(this);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
