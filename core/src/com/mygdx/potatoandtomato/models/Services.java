package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Services {

    Fonts _fonts;
    Texts _texts;
    Textures _textures;
    Preferences _preferences;
    Profile _profile;
    IDatabase _database;
    Shaders _shaders;
    GamingKit _gamingKit;
    IDownloader _downloader;
    Chat _chat;

    public Services(Textures textures, Fonts fonts, Texts texts, Preferences preferences,
                    Profile profile, IDatabase database, Shaders shaders, GamingKit gamingKit, IDownloader downloader, Chat chat) {
        _fonts = fonts;
        _texts = texts;
        _textures = textures;
        _preferences = preferences;
        _profile = profile;
        _database = database;
        _shaders = shaders;
        _gamingKit = gamingKit;
        _downloader = downloader;
        _chat = chat;
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

    public void setDatabase(IDatabase _database) {
        this._database = _database;
    }

    public Shaders getShaders() {
        return _shaders;
    }

    public GamingKit getGamingKit() {
        return _gamingKit;
    }

    public IDownloader getDownloader() {
        return _downloader;
    }

    public void setDownloader(IDownloader _downloader) {
        this._downloader = _downloader;
    }

    public void setGamingKit(GamingKit _gamingKit) {
        this._gamingKit = _gamingKit;
    }

    public Chat getChat() {
        return _chat;
    }
}
