package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.utils.Pair;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 23/6/2016.
 */
public interface ICoins {

    void showCoinMachine(boolean forceShow);
    void hideCoinMachine();
    void reset();
    void requestCoinsMachineStateFromOthers();
    void initCoinMachine(String coinsPurpose, int expectingCoin, String transactionId, ArrayList<Pair<String, String>> userIdToNamePairs,
                         boolean requestFromOthers, ArrayList<SpeechAction> potatoSpeechActions,
                         ArrayList<SpeechAction> tomatoSpeechActions, String dismissText);
    void startDeductCoins();
    void setCoinListener(CoinListener coinListener);
    boolean isTransactionIdProcessed(String transactionId);


    boolean isVisible();
    String getCoinsPurpose();

}
