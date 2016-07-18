package com.mygdx.potatoandtomato.cachings;

import com.mygdx.potatoandtomato.absintflis.cachings.CacheAbstract;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 18/7/2016.
 */
public class RetrieveCoinDataCache extends CacheAbstract<RetrievableCoinsData> {

    private IRestfulApi restfulApi;
    private Profile profile;

    public RetrieveCoinDataCache(IRestfulApi restfulApi, Profile profile) {
        super();
        this.restfulApi = restfulApi;
        this.profile = profile;
        refreshCache();
        useTimeRefreshMethod(30 * 60);      //refresh every 30 minutes
        startTicking();
    }

    public void startTicking(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(disposed) return;
                    else{
                        Threadings.sleep(1000);
                        if(cached != null){
                            cached.oneSecondTicked();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void retrieveDataRemote(final CacheListener<RetrievableCoinsData> cacheListener) {
        restfulApi.getRetrievableCoinsData(profile, new RestfulApiListener<RetrievableCoinsData>() {
            @Override
            public void onCallback(RetrievableCoinsData obj, Status st) {
                if(st == Status.SUCCESS && obj != null){
                    if(obj.getNextCoinInSecs() + 2 < obj.getSecsPerCoin()){
                        obj.setNextCoinInSecs(obj.getNextCoinInSecs() + 2);
                    }
                    cacheListener.onResult(obj);
                }
            }
        });
    }
}
