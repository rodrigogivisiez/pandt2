package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.helpers.services.Downloader;
import com.mygdx.potatoandtomato.helpers.services.Preferences;
import com.mygdx.potatoandtomato.helpers.utils.Files;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.helpers.utils.Zippings;
import com.mygdx.potatoandtomato.models.Game;

import java.io.File;

/**
 * Created by SiongLeng on 17/12/2015.
 */
public class GameClientChecker {

    private DownloaderListener _listener;
    double _currentPercent = 0;
    double _downloadGameWeight = 20, _downloadAssetsWeight = 79, _unzipWeight = 1;     //total must be 100;
    Thread _downloadThread;
    SafeThread _downloadAssetThread, _downloadJarThread;
    Preferences _preferences;
    IDownloader _downloader;
    Game _game;

    public GameClientChecker(Game _game, Preferences _preferences, IDownloader _downloader, DownloaderListener _listener) {
        this._listener = _listener;
        this._game = _game;
        this._preferences = _preferences;
        this._downloader = _downloader;
        checkGameVersion();
    }

    public void checkGameVersion(){
        String localVersion;
        localVersion = _preferences.get(_game.getAbbr());
        if(localVersion == null || !localVersion.equals(_game.getVersion())){
            downloadGame();
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

            }
        });
    }

    public SafeThread downloadFile(String url, File f, final double weight, final Runnable complete){
        final double[] _prevStep = new double[1];
        return _downloader.downloadFileToPath(url, f, new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                if(st == Status.FAILED){
                    downloadFailed();
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
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                _listener.onStep(_currentPercent);
            }
        });
    }

    public void downloadFailed(){
        if(_downloadJarThread != null) _downloadJarThread.kill();
        if(_downloadAssetThread != null) _downloadAssetThread.kill();
    }

}
