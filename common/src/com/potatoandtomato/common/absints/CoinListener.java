package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 15/6/2016.
 */
public abstract class CoinListener {

    public void onEnoughCoins(){}

    public void onDeductCoinsDone(){}

    public void onDismiss(String dismissUserId){}

    public void onTransactionAlreadyProcessed() {}

}
