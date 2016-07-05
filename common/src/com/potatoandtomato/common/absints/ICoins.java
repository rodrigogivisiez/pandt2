package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.utils.Pair;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 23/6/2016.
 */
public interface ICoins {

    void showCoinMachine();
    void hideCoinMachine();
    void reset();
    void requestCoinsMachineStateFromOthers();
    void initCoinMachine(int expectingCoin, String transactionId, ArrayList<Pair<String, String>> userIdToNamePairs,
                         boolean requestFromOthers);
    void startDeductCoins();
    void setCoinListener(CoinListener coinListener);

}
