package com.potatoandtomato.common.absints;

/**
 * Created by SiongLeng on 29/5/2016.
 */
public abstract class GameDataContractAbstract {

    public abstract String generateGameData();

    public abstract String getCurrentGameData();

    public abstract void onGameDataOutdated();      //user dc, need to retrieve game data again

    public abstract void onGameDataReceived(String gameData);

    //return false mean not handled
    public boolean onFailedRetrieve(){
        return false;
    }

}
