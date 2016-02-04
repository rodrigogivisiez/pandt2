package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.game_file_checker.GameFileCheckerListener;
import com.potatoandtomato.common.DownloaderListener;
import com.potatoandtomato.common.IDownloader;
import com.mygdx.potatoandtomato.helpers.services.Preferences;
import com.mygdx.potatoandtomato.helpers.services.VersionControl;
import com.mygdx.potatoandtomato.helpers.utils.Files;
import com.potatoandtomato.common.SafeThread;
import com.potatoandtomato.common.Threadings;
import com.mygdx.potatoandtomato.helpers.utils.Zippings;
import com.mygdx.potatoandtomato.models.Game;
import com.potatoandtomato.common.Status;

import java.io.File;

/**
 * Created by SiongLeng on 17/12/2015.
 */
public class GameFileChecker implements Disposable {

    public enum GameFileResult {
        FAILED_RETRIEVE, CLIENT_OUTDATED, GAME_OUTDATED
    }

    private GameFileCheckerListener _listener;
    double _currentPercent = 0;
    double _downloadGameWeight = 20, _downloadAssetsWeight = 79, _unzipWeight = 1;     //total must be 100;
    Thread _downloadThread;
    SafeThread _downloadAssetThread, _downloadJarThread;
    Preferences _preferences;
    IDownloader _downloader;
    Game _game;
    IDatabase _database;
    VersionControl _versionControl;

    public GameFileChecker(Game _game, Preferences _preferences,
                           IDownloader _downloader, IDatabase database, VersionControl versionControl,
                           GameFileCheckerListener _listener) {
        this._listener = _listener;
        this._game = _game;
        this._database = database;
        this._preferences = _preferences;
        this._downloader = _downloader;
        this._versionControl = versionControl;
        getLatestGameModel();
    }

    public void getLatestGameModel(){
        _database.getGameByAbbr(_game.getAbbr(), new DatabaseListener<Game>(Game.class) {
            @Override
            public void onCallback(Game obj, Status st) {
                if(st == Status.SUCCESS){
                    if(Integer.valueOf(obj.getCommonVersion()) > Integer.valueOf(_versionControl.getCommonVersion())){
                        _listener.onCallback(GameFileResult.CLIENT_OUTDATED, Status.FAILED);
                    }
                    else{
                        if(!obj.getVersion().equals(_game.getVersion())){
                            _listener.onCallback(GameFileResult.GAME_OUTDATED, Status.FAILED);
                        }
                        else{
                            checkGameVersion();
                        }
                    }
                }
                else{
                    _listener.onCallback(GameFileResult.FAILED_RETRIEVE, Status.FAILED);
                }
            }
        });
    }

    public void checkGameVersion(){
        String localVersion;
        localVersion = _preferences.get(_game.getAbbr());
        if(localVersion == null || !localVersion.equals(_game.getVersion())
                || !Gdx.files.local(_game.getLocalJarPath()).exists()){
            downloadGame();
        }
        else{
            _listener.onCallback(null, Status.SUCCESS);
        }
    }

    public void downloadGame(){
        _downloadThread = Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                final FileHandle jarDownloadPath, zipDownloadPath;
                final boolean[] completeJar = new boolean[1];
                ;
                final boolean[] completeAssets = new boolean[1];

                jarDownloadPath = Files.createIfNotExist(Gdx.files.local(_game.getLocalJarPath()));
                zipDownloadPath = Files.createIfNotExist(Gdx.files.local(_game.getLocalAssetsPath()));

                _downloadJarThread = downloadFile(_game.getGameUrl(), jarDownloadPath.file(), _downloadGameWeight, new Runnable() {
                    @Override
                    public void run() {
                        completeJar[0] = true;
                    }
                });
                _downloadAssetThread = downloadFile(_game.getAssetUrl(), zipDownloadPath.file(), _downloadAssetsWeight, new Runnable() {
                    @Override
                    public void run() {
                        completeAssets[0] = true;
                    }
                });

                while (!completeAssets[0] || !completeJar[0]) {
                    Threadings.sleep(1000);
                    if (_downloadAssetThread.isKilled() || _downloadJarThread.isKilled()) return;
                }

                Zippings.unZipIt(zipDownloadPath.file().getAbsolutePath(), _game.getFullBasePath());

                _preferences.put(_game.getAbbr(), _game.getVersion());
                zipDownloadPath.delete();

                updatePercent(_unzipWeight);

                _listener.onCallback(null, Status.SUCCESS);

            }
        });
    }

    public SafeThread downloadFile(String url, File f, final double weight, final Runnable complete){
        final double[] _prevStep = new double[1];
        return _downloader.downloadFileToPath(url, f, new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                if(st == Status.FAILED){
                    killDownloads();
                }
                else{
                    complete.run();
                }
            }

            @Override
            public void onStep(double percentage) {
                super.onStep(percentage);
                updatePercent((percentage - _prevStep[0]) * weight/100);
                _prevStep[0] = percentage;
            }
        });
    }

    public void updatePercent(final double added){
        _currentPercent += added;
        _listener.onStep(_currentPercent);
    }

    public void killDownloads(){
        if(_downloadJarThread != null) _downloadJarThread.kill();
        if(_downloadAssetThread != null) _downloadAssetThread.kill();
        _listener.onCallback(GameFileResult.FAILED_RETRIEVE, Status.FAILED);

    }

    @Override
    public void dispose() {
        killDownloads();
    }
}
