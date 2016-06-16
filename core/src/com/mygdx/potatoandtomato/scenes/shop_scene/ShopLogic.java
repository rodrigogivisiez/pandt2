package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopLogic extends LogicAbstract {

    private ShopScene shopScene;

    public ShopLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        shopScene = new ShopScene(services, screen);

        Threadings.setContinuousRenderLock(true);
    }

    @Override
    public void onHide() {
        super.onHide();
        Threadings.setContinuousRenderLock(false);
    }

    @Override
    public SceneAbstract getScene() {
        return shopScene;
    }
}
