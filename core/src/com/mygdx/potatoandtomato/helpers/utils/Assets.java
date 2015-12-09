package com.mygdx.potatoandtomato.helpers.utils;

import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.helpers.assets.*;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Assets {

    Fonts _fonts;
    Texts _texts;
    Textures _textures;
    Preferences _preferences;
    Profile _profile;
    IDatabase _database;


    public Assets(Textures textures, Fonts fonts, Texts texts, Preferences preferences, Profile profile, IDatabase database) {
        _fonts = fonts;
        _texts = texts;
        _textures = textures;
        _preferences = preferences;
        _profile = profile;
        _database = database;
    }

    public Fonts getFonts() {
        return _fonts;
    }

    public Texts getTexts() {
        return _texts;
    }

    public Textures getTextures() {
        return _textures;
    }

    public Preferences getPreferences() { return _preferences; }

    public Profile getProfile() { return _profile; }

    public void setProfile(Profile _profile) {
        this._profile = _profile;
    }

    public IDatabase getDatabase() { return _database; }
}
