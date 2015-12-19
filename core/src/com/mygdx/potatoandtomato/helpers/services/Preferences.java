package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.mygdx.potatoandtomato.helpers.utils.Terms;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Preferences {

    private com.badlogic.gdx.Preferences _prefs;

    public Preferences() {
        _prefs = Gdx.app.getPreferences(Terms.PREF_NAME);
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



}
