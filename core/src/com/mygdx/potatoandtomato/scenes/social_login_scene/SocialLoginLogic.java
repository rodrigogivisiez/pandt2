package com.mygdx.potatoandtomato.scenes.social_login_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.abstractions.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class SocialLoginLogic extends LogicAbstract {

    SocialLoginScene _scene;

    public SocialLoginLogic(PTScreen screen, Assets assets) {
        super(screen, assets);
        _scene = new SocialLoginScene(assets);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
