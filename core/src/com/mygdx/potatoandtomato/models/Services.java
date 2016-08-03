package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.assets.MyAssets;
import com.mygdx.potatoandtomato.services.*;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.absints.ITutorials;
import com.potatoandtomato.common.assets.Assets;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Services {

    Texts _texts;
    MyAssets _assets;
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
    SoundsPlayer _soundsPlayer;
    VersionControl _versionControl;
    Broadcaster _broadcaster;
    Tutorials _tutorials;
    IRestfulApi _restfulApi;
    ConnectionWatcher _connectionWatcher;
    Coins _coins;
    DataCaches _dataCaches;

    public Services(MyAssets assets, Texts texts, Preferences preferences,
                    Profile profile, IDatabase database, Shaders shaders, GamingKit gamingKit, IDownloader downloader,
                    Chat chat, Socials socials, GCMSender gcmSender, Confirm confirm, Notification notification,
                    Recorder recorder, SoundsPlayer soundsPlayer, VersionControl versionControl,
                    Broadcaster broadcaster, Tutorials tutorials, IRestfulApi restfulApi, ConnectionWatcher connectionWatcher,
                    Coins coins, DataCaches dataCaches) {
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
        _soundsPlayer = soundsPlayer;
        _versionControl = versionControl;
        _broadcaster = broadcaster;
        _tutorials = tutorials;
        _restfulApi = restfulApi;
        _connectionWatcher = connectionWatcher;
        _coins = coins;
        _dataCaches = dataCaches;
    }


    public DataCaches getDataCaches() {
        return _dataCaches;
    }

    public Coins getCoins() {
        return _coins;
    }

    public ConnectionWatcher getConnectionWatcher() {
        return _connectionWatcher;
    }

    public void setConnectionWatcher(ConnectionWatcher _connectionWatcher) {
        this._connectionWatcher = _connectionWatcher;
    }

    public IRestfulApi getRestfulApi() {
        return _restfulApi;
    }

    public void setRestfulApi(IRestfulApi _restfulApi) {
        this._restfulApi = _restfulApi;
    }

    public Tutorials getTutorials() {
        return _tutorials;
    }

    public void setTutorials(Tutorials _tutorials) {
        this._tutorials = _tutorials;
    }

    public Broadcaster getBroadcaster() {
        return _broadcaster;
    }

    public void setBroadcaster(Broadcaster _broadcaster) {
        this._broadcaster = _broadcaster;
    }

    public VersionControl getVersionControl() {
        return _versionControl;
    }

    public SoundsPlayer getSoundsPlayer() {
        return _soundsPlayer;
    }

    public void setSoundsPlayer(SoundsPlayer _soundsPlayer) {
        this._soundsPlayer = _soundsPlayer;
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

    public MyAssets getAssets() {
        return _assets;
    }



}


