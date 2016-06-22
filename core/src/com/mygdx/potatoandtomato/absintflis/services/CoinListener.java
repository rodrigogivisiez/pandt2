package com.mygdx.potatoandtomato.absintflis.services;

import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 15/6/2016.
 */
public abstract class CoinListener {

    public abstract void onEnoughCoins();

    public abstract void onDeductCoinsDone(String extra, Status status);

}
