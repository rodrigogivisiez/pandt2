package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.cachings.GamesListCache;
import com.mygdx.potatoandtomato.cachings.RetrieveCoinDataCache;
import com.mygdx.potatoandtomato.models.Profile;

/**
 * Created by SiongLeng on 18/7/2016.
 */
public class DataCaches implements Disposable {

    private Profile profile;
    private IDatabase database;
    private IRestfulApi restfulApi;

    private GamesListCache gamesListCache;
    private RetrieveCoinDataCache retrieveCoinDataCache;

    public DataCaches(IDatabase database, IRestfulApi restfulApi, Profile profile) {
        this.database = database;
        this.restfulApi = restfulApi;
        this.profile = profile;
    }

    public void startCaches(){
        gamesListCache = new GamesListCache(database);
        retrieveCoinDataCache = new RetrieveCoinDataCache(restfulApi, profile);
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
    }
}
