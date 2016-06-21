package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopLogic extends LogicAbstract {

    private ShopScene shopScene;
    private boolean canWatchAds;
    private RetrievableCoinsData currentRetrievableCoinsData;
    private SafeThread safeThread;

    public ShopLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        Threadings.setContinuousRenderLock(true);
        shopScene = new ShopScene(services, screen);

        shopScene.setProductsDesign();

        services.getSoundsPlayer().stopMusic(Sounds.Name.THEME_MUSIC);
        services.getSoundsPlayer().playMusic(Sounds.Name.SHOP_MUSIC);

        refreshAdsAvailability();
        refreshRetrievableCoinsCount();
    }

    @Override
    public void onHide() {
        super.onHide();
        Threadings.setContinuousRenderLock(false);

        _services.getSoundsPlayer().stopMusic(Sounds.Name.SHOP_MUSIC);
        _services.getSoundsPlayer().playMusic(Sounds.Name.THEME_MUSIC);
    }

    public void refreshRetrievableCoinsCount(){
        _services.getRestfulApi().getRetrievableCoinsData(_services.getProfile(), new RestfulApiListener<RetrievableCoinsData>() {
            @Override
            public void onCallback(RetrievableCoinsData obj, Status st) {
                updateCurrentRetrievableCoinsData(obj);
            }
        });
    }

    public void updateCurrentRetrievableCoinsData(RetrievableCoinsData newData){
        currentRetrievableCoinsData = newData;
        shopScene.refreshPurseDesign(currentRetrievableCoinsData);

        if(currentRetrievableCoinsData.getCanRetrieveCoinsCount() < currentRetrievableCoinsData.getMaxRetrieveableCoins()){
            safeThread = new SafeThread();
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    int duration = currentRetrievableCoinsData.getNextCoinInSecs();
                    while (true){
                        if(safeThread.isKilled()) break;
                        else{
                            shopScene.refreshNextCoinTimer(duration, false);
                            Threadings.sleep(1000);
                            duration--;
                            if(duration <= -1){
                                addPurseRetrievableCount();
                                break;
                            }
                        }
                    }
                }
            });
        }
        else{
            shopScene.refreshNextCoinTimer(0, true);
        }
    }

    public void addPurseRetrievableCount(){
        currentRetrievableCoinsData.setCanRetrieveCoinsCount(currentRetrievableCoinsData.getCanRetrieveCoinsCount() + 1);
        currentRetrievableCoinsData.setNextCoinInSecs(currentRetrievableCoinsData.getSecsPerCoin());
        updateCurrentRetrievableCoinsData(currentRetrievableCoinsData);
    }


    public void refreshAdsAvailability(){
        _services.getBroadcaster().broadcast(BroadcastEvent.HAS_REWARD_VIDEO, new RunnableArgs<Boolean>() {
            @Override
            public void run() {
                canWatchAds = this.getFirstArg();
                shopScene.setCanWatchAds(this.getFirstArg());
            }
        });
    }


    @Override
    public void setListeners() {
        super.setListeners();
        if(shopScene.getWatchVideoAdsButton() != null){
            shopScene.getWatchVideoAdsButton().addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if(canWatchAds){
                        _services.getBroadcaster().broadcast(BroadcastEvent.SHOW_REWARD_VIDEO);
                        refreshAdsAvailability();
                    }
                }
            });
        }

        shopScene.getRetrieveCoinsButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(currentRetrievableCoinsData != null && currentRetrievableCoinsData.getCanRetrieveCoinsCount() > 0){
                    _services.getRestfulApi().retrieveCoins(_services.getProfile(), new RestfulApiListener<RetrievableCoinsData>() {
                        @Override
                        public void onCallback(RetrievableCoinsData obj, Status st) {
                            updateCurrentRetrievableCoinsData(obj);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void dispose() {
        super.dispose();
        if(safeThread != null) safeThread.kill();
    }

    @Override
    public SceneAbstract getScene() {
        return shopScene;
    }
}
