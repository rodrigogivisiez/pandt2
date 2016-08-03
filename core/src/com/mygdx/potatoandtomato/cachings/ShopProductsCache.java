package com.mygdx.potatoandtomato.cachings;

import com.mygdx.potatoandtomato.absintflis.cachings.CacheAbstract;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 2/8/2016.
 */
public class ShopProductsCache extends CacheAbstract<ArrayList<CoinProduct>>  {

    private Broadcaster broadcaster;
    private IDatabase database;
    private CacheListener<ArrayList<CoinProduct>> cacheListener;
    private String broadcastId;

    public ShopProductsCache(Broadcaster broadcaster, IDatabase database) {
        this.broadcaster = broadcaster;
        this.database = database;
        subscribeBroadcaster();
        refreshCache();
        useTimeRefreshMethod(30 * 60);  //refresh every 30 minutes
    }

    public void subscribeBroadcaster(){
        broadcastId = broadcaster.subscribe(BroadcastEvent.IAB_PRODUCTS_RESPONSE, new BroadcastListener<ArrayList<CoinProduct>>() {
            @Override
            public void onCallback(ArrayList<CoinProduct> obj, Status st) {
                if(cacheListener != null)  cacheListener.onResult(obj);

                //re-get product again since it return empty
                if(obj == null || obj.size() == 0){
                    Threadings.delay(5000, new Runnable() {
                        @Override
                        public void run() {
                            refreshCache();
                        }
                    });

                }
            }
        });
    }

    @Override
    public void retrieveDataRemote(final CacheListener<ArrayList<CoinProduct>> cacheListener) {
        this.cacheListener = cacheListener;
        broadcaster.broadcast(BroadcastEvent.IAB_PRODUCTS_REQUEST, database);
    }

    @Override
    public void dispose() {
        super.dispose();
        if(broadcastId != null) broadcaster.unsubscribe(broadcastId);
    }
}
