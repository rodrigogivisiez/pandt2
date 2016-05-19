package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.GamePreferencesAbstract;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Preferences extends GamePreferencesAbstract {

    private com.badlogic.gdx.Preferences _prefs;
    private String _gameAbbr;

    public Preferences() {
        _prefs = Gdx.app.getPreferences((Global.DEBUG ? "DEBUG_" : "") + Terms.PREF_NAME);
    }

    public Preferences(String name) {
        _prefs = Gdx.app.getPreferences(name);
    }

    public void deleteAll(){
        _prefs.clear();
        _prefs.flush();
    }

    public String get(String key){
        return _prefs.getString(key, null);
    }

    public void put(String key, String value){
        _prefs.putString(key, value);
        _prefs.flush();
    }

    public void delete(String key){
        _prefs.remove(key);
        _prefs.flush();
    }


    public void setGameAbbr(String _gameAbbr) {
        this._gameAbbr = _gameAbbr;
    }

    @Override
    public String getGamePref(String key) {
        return get(appendAbbrToKey(key));
    }

    @Override
    public void putGamePref(String key, String value) {
        put(appendAbbrToKey(key), value);
    }

    @Override
    public void deleteGamePref(String key) {
        delete(appendAbbrToKey(key));
    }

    private String appendAbbrToKey(String key){
        return this._gameAbbr + "_" + key;
    }

}
