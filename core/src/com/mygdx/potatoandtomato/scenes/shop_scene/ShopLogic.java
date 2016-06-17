package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopLogic extends LogicAbstract {

    private ShopScene shopScene;

    public ShopLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        Threadings.setContinuousRenderLock(true);
        shopScene = new ShopScene(services, screen);

        shopScene.setPurseCoinsCount(5);
        shopScene.setProductsDesign();

        services.getSoundsPlayer().stopMusic(Sounds.Name.THEME_MUSIC);
        services.getSoundsPlayer().playMusic(Sounds.Name.SHOP_MUSIC);
    }

    @Override
    public void onHide() {
        super.onHide();
        Threadings.setContinuousRenderLock(false);

        _services.getSoundsPlayer().stopMusic(Sounds.Name.SHOP_MUSIC);
        _services.getSoundsPlayer().playMusic(Sounds.Name.THEME_MUSIC);
    }

    @Override
    public SceneAbstract getScene() {
        return shopScene;
    }
}
