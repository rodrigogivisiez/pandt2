package com.potatoandtomato.common.absints;

/**
 * Created by SiongLeng on 28/3/2016.
 */
public abstract class GamePreferencesAbstract {

    public abstract String getGamePref(String key);

    public abstract void putGamePref(String key, String value);

    public abstract void deleteGamePref(String key);

}
