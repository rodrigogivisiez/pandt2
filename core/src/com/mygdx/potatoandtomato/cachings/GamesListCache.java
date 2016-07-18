package com.mygdx.potatoandtomato.cachings;

import com.mygdx.potatoandtomato.absintflis.cachings.CacheAbstract;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.models.Game;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 18/7/2016.
 */
public class GamesListCache extends CacheAbstract<ArrayList<Game>> {

    private IDatabase database;

    public GamesListCache(IDatabase database) {
        this.database = database;
        refreshCache();
        useCountRefreshMethod(5);
    }

    @Override
    public void retrieveDataRemote(final CacheListener<ArrayList<Game>> cacheListener) {
        database.getAllGamesSimple(new DatabaseListener<ArrayList<Game>>() {
            @Override
            public void onCallback(ArrayList<Game> obj, Status st) {
                if(obj != null && st == Status.SUCCESS){
                    cacheListener.onResult(obj);
                }
            }
        });
    }
}
