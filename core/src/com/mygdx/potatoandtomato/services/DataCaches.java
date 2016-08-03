package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.cachings.GamesListCache;
import com.mygdx.potatoandtomato.cachings.RetrieveCoinDataCache;
import com.mygdx.potatoandtomato.cachings.ShopProductsCache;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.broadcaster.Broadcaster;

/**
 * Created by SiongLeng on 18/7/2016.
 */
public class DataCaches implements Disposable {

    private Profile profile;
    private IDatabase database;
    private IRestfulApi restfulApi;
    private Broadcaster broadcaster;

    private GamesListCache gamesListCache;
    private RetrieveCoinDataCache retrieveCoinDataCache;
    private ShopProductsCache shopProductsCache;

    public DataCaches(IDatabase database, IRestfulApi restfulApi, Profile profile, Broadcaster broadcaster) {
        this.database = database;
        this.restfulApi = restfulApi;
        this.profile = profile;
        this.broadcaster = broadcaster;
    }

    public void startCaches(){
        gamesListCache = new GamesListCache(database);
        retrieveCoinDataCache = new RetrieveCoinDataCache(restfulApi, profile);
        shopProductsCache = new ShopProductsCache(broadcaster, database);
    }

    public ShopProductsCache getShopProductsCache() {
        return shopProductsCache;
    }

    public GamesListCache getGamesListCache() {
        return gamesListCache;
    }

    public RetrieveCoinDataCache getRetrieveCoinDataCache() {
        return retrieveCoinDataCache;
    }

    @Override
    public void dispose() {
        if(gamesListCache != null) gamesListCache.dispose();
        if(retrieveCoinDataCache != null) retrieveCoinDataCache.dispose();
        if(shopProductsCache != null) shopProductsCache.dispose();
    }
}
