package com.mygdx.potatoandtomato.scenes.settings_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public class SettingsLogic extends LogicAbstract {

    SettingsScene _scene;

    public SettingsLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new SettingsScene(services, screen);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
