package com.mygdx.potatoandtomato.absintflis.services;

import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 30/6/2016.
 */
public abstract class ClientInternalCoinListener {

    private boolean monitorNextCoin;

    public ClientInternalCoinListener() {
    }

    public ClientInternalCoinListener(boolean monitorNextCoin) {
        this.monitorNextCoin = monitorNextCoin;
    }

    public void userHasCoinChanged(String userId, boolean userHasCoin){}

    public void retrievableCoinChanged(RetrievableCoinsData coinsData){}

    public void nextCoinTimeChanged(int nextCoinSecs){}

    public void onProductsRetrieved(ArrayList<CoinProduct> coinProducts){}

    public boolean isMonitorNextCoin() {
        return monitorNextCoin;
    }
}
