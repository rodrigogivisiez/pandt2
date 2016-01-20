package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.helpers.controls.Notification;
import com.mygdx.potatoandtomato.helpers.services.*;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Services {

    Texts _texts;
    Assets _assets;
    Preferences _preferences;
    Profile _profile;
    IDatabase _database;
    Shaders _shaders;
    GamingKit _gamingKit;
    IDownloader _downloader;
    Chat _chat;
    Socials _socials;
    GCMSender _gcmSender;
    Confirm _confirm;
    Notification _notification;
    Recorder _recorder;
    IUploader _uploader;
    Sounds _sounds;
    VersionControl _versionControl;

    public Services(Assets assets, Texts texts, Preferences preferences,
                    Profile profile, IDatabase database, Shaders shaders, GamingKit gamingKit, IDownloader downloader,
                    Chat chat, Socials socials, GCMSender gcmSender, Confirm confirm, Notification notification,
                    Recorder recorder, IUploader uploader, Sounds sounds, VersionControl versionControl) {
        _texts = texts;
        _assets = assets;
        _preferences = preferences;
        _profile = profile;
        _database = database;
        _shaders = shaders;
        _gamingKit = gamingKit;
        _downloader = downloader;
        _chat = chat;
        _socials = socials;
        _gcmSender = gcmSender;
        _confirm = confirm;
        _notification = notification;
        _recorder = recorder;
        _uploader = uploader;
        _sounds = sounds;
        _versionControl = versionControl;
    }

    public VersionControl getVersionControl() {
        return _versionControl;
    }

    public Sounds getSounds() {
        return _sounds;
    }

    public void setSounds(Sounds _sounds) {
        this._sounds = _sounds;
    }

    public IUploader getUploader() {
        return _uploader;
    }

    public void setUploader(IUploader _uploader) {
        this._uploader = _uploader;
    }

    public Recorder getRecorder() {
        return _recorder;
    }

    public void setRecorder(Recorder _recorder) {
        this._recorder = _recorder;
    }

    public Notification getNotification() {
        return _notification;
    }

    public void setNotification(Notification _notification) {
        this._notification = _notification;
    }

    public Confirm getConfirm() {
        return _confirm;
    }

    public void setConfirm(Confirm _confirm) {
        this._confirm = _confirm;
    }

    public Texts getTexts() {
        return _texts;
    }

    public Assets getTextures() {
        return _assets;
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

    public void setChat(Chat _chat) {
        this._chat = _chat;
    }

    public Socials getSocials() {
        return _socials;
    }

    public void setSocials(Socials _socials) {
        this._socials = _socials;
    }

    public GCMSender getGcmSender() {
        return _gcmSender;
    }

    public void setGcmSender(GCMSender _gcmSender) {
        this._gcmSender = _gcmSender;
    }
}


