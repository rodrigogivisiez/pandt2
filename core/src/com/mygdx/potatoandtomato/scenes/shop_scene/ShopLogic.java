package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.services.ClientInternalCoinListener;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.*;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopLogic extends LogicAbstract {

    private ShopScene shopScene;
    private boolean canWatchAds;
    private ArrayList<CoinProduct> coinProducts;
    private int retrievedSuccessCount = 0;

    public ShopLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        setSaveToStack(false);

        shopScene = new ShopScene(services, screen);

        services.getSoundsPlayer().pauseCurrentAndPlayAnotherMusic(Sounds.Name.SHOP_MUSIC);
    }

    @Override
    public void onShown() {
        super.onShown();
        shopScene.randomAnimateStyle();

        refreshProducts();
        refreshAdsAvailability();
        refreshRetrievableCoinsCount();
    }

    @Override
    public void onHide() {
        super.onHide();

        _services.getSoundsPlayer().resumeCurrentMusic();
    }

    public void refreshProducts(){
        _services.getCoins().getProducts(new ClientInternalCoinListener() {
            @Override
            public void onProductsRetrieved(final ArrayList<CoinProduct> refreshedCoinProducts) {
                super.onProductsRetrieved(refreshedCoinProducts);
                _services.getDatabase().getRewardVideoCoinCount(new DatabaseListener<Integer>(Integer.class) {
                    @Override
                    public void onCallback(Integer result, Status st) {
                        coinProducts = (ArrayList) refreshedCoinProducts.clone();

                        if(st == Status.SUCCESS && result != null){
                            coinProducts.add(0, new CoinProduct(Terms.WATCH_ADS_ID, result, _texts.watchAdsDescription()));
                        }

                        shopScene.setProductsDesign(coinProducts);
                        shopScene.setCanWatchAds(canWatchAds);
                        setCoinProductsListeners();
                        addRetrievedSuccessCount();
                    }
                });



            }
        });
    }

    public void refreshRetrievableCoinsCount(){
        _services.getCoins().refreshRetrievableCoins();
    }

    public void updateCurrentRetrievableCoinsData(RetrievableCoinsData newData){
        shopScene.refreshPurseDesign(newData);

        if(newData.getCanRetrieveCoinsCount() >= newData.getMaxRetrieveableCoins()) {
            shopScene.refreshNextCoinTimer(0, true);
        }
    }

    public void refreshAdsAvailability(){
        _services.getCoins().hasAds(new RunnableArgs<Boolean>() {
            @Override
            public void run() {
                receivedAdsAvailability(this.getFirstArg());
            }
        });
    }

    private void receivedAdsAvailability(boolean isAvailable){
        canWatchAds = isAvailable;
        shopScene.setCanWatchAds(isAvailable);
    }

    private void addRetrievedSuccessCount(){
        retrievedSuccessCount++;
        if(retrievedSuccessCount == 2){
            shopScene.finishLoading();
        }
    }

    @Override
    public void setListeners() {
        super.setListeners();
        shopScene.getRetrieveCoinsButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_services.getCoins().retrieveFreeCoins()){
                    _services.getSoundsPlayer().playSoundEffect(Sounds.Name.WRONG);
                }
            }
        });

        _services.getCoins().addCoinsListener(getClassTag(), new ClientInternalCoinListener(true) {
            @Override
            public void retrievableCoinChanged(RetrievableCoinsData coinsData) {
                super.retrievableCoinChanged(coinsData);
                updateCurrentRetrievableCoinsData(coinsData);
                addRetrievedSuccessCount();
            }

            @Override
            public void nextCoinTimeChanged(int nextCoinSecs) {
                super.nextCoinTimeChanged(nextCoinSecs);
                shopScene.refreshNextCoinTimer(nextCoinSecs, false);
            }
        });

    }

    public void setCoinProductsListeners(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(final CoinProduct coinProduct : coinProducts){
                    if(coinProduct.getId().equals(Terms.WATCH_ADS_ID)){
                        Actor button = shopScene.getProductButtonById(coinProduct.getId());
                        if(button != null){
                            button.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    if (canWatchAds) {
                                        _services.getCoins().watchAds(new RunnableArgs<Boolean>() {
                                            @Override
                                            public void run() {
                                                receivedAdsAvailability(this.getFirstArg());
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                    else{
                        Actor button = shopScene.getProductButtonById(coinProduct.getId());
                        if(button != null){
                            button.addListener(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                   _services.getCoins().purchaseCoins(coinProduct);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean disposeEarly() {
        if(super.disposeEarly()){
            _services.getCoins().removeCoinsListenersByClassTag(getClassTag());
        }
        return true;
    }

    @Override
    public SceneAbstract getScene() {
        return shopScene;
    }
}
