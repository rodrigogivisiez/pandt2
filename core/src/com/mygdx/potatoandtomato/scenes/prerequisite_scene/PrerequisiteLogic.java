package com.mygdx.potatoandtomato.scenes.prerequisite_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.JoinRoomListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.utils.*;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;

import java.io.File;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class PrerequisiteLogic extends LogicAbstract {

    PrerequisiteScene _scene;
    Game _game;
    double _currentPercent = 0;
    double _checkVersionWeight = 1,  _downloadGameWeight = 20,
            _downloadAssetsWeight = 70, _unzipGameWeight = 1, _gameKitWeight = 8;        //total must be 100;
    Texts _texts;
    Thread _downloadThread;
    boolean _isCreating;
    SafeThread _downloadAssetThread, _downloadJarThread;

    public PrerequisiteLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _texts = _services.getTexts();
        _scene = new PrerequisiteScene(_services);
        _game = (Game) objs[0];
        _isCreating = (boolean) objs[1];

        _scene.getRetryButton().addListener((new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                restart();     //retry whole process
            }
        }));
    }

    @Override
    public void init() {
        super.init();
        restart();
    }

    public void restart(){
        _currentPercent = 0;
        _scene.setProgressBarValue(0);
        checkGameVersion();
    }

    public void checkGameVersion(){
        String localVersion;
        localVersion = _services.getPreferences().get(_game.getAbbr());
        _scene.changeMessage(_texts.checkGameVersion());
        if(localVersion == null || !localVersion.equals(_game.getVersion())){
            downloadGame();
        }
        else{
            updatePercent(_downloadGameWeight + _downloadAssetsWeight + _unzipGameWeight);
            gameClientComplete();
        }
        updatePercent(_checkVersionWeight);
    }

    public void downloadGame(){
        _scene.changeMessage(_texts.downloadingGame());

        _downloadThread = Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                final FileHandle jarDownloadPath, zipDownloadPath;
                final boolean[] completeJar = new boolean[1];;
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

                while (!completeAssets[0] || !completeJar[0]){
                    Threadings.sleep(1000);
                    if(_downloadAssetThread.isKilled() || _downloadJarThread.isKilled()) return;
                }

                Zippings.unZipIt(zipDownloadPath.file().getAbsolutePath(), _game.getFullBasePath());

                updatePercent(_unzipGameWeight);
                _services.getPreferences().put(_game.getAbbr(), _game.getVersion());
                zipDownloadPath.delete();

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gameClientComplete();
                    }
                });

            }
        });
    }

    public SafeThread downloadFile(String url, File f, final double weight, final Runnable complete){
        final double[] _prevStep = new double[1];
        return _services.getDownloader().downloadFileToPath(url, f, new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                if(st == Status.FAILED){
                    getGameClientFailed();
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

    public void gameClientComplete(){
        if(_isCreating){
            createRoom();
        }
        else{
            joinRoom();
        }
    }

    public void createRoom(){
        _scene.changeMessage(_texts.creatingRoom());
        _services.getGamingKit().addListener(new JoinRoomListener() {
            @Override
            public void onRoomJoined(String roomId) {
                joinRoomSuccess(roomId);
            }

            @Override
            public void onJoinRoomFailed() {
                joinRoomFailed();
            }
        });
        _services.getGamingKit().createAndJoinRoom();
    }

    public void joinRoom(){
        _scene.changeMessage(_texts.joiningRoom());
    }


    public void getGameClientFailed(){
        _scene.failedMessage(_texts.gameClientFailed());
        if(_downloadJarThread != null) _downloadJarThread.kill();
        if(_downloadAssetThread != null) _downloadAssetThread.kill();

    }

    public void joinRoomFailed(){
        _scene.failedMessage(_texts.joinRoomFailed());
    }

    public void joinRoomSuccess(String roomId){
        updatePercent(_gameKitWeight);
        final Room room = new Room();
        room.setRoomId(roomId);
        room.setGame(_game);
        room.setOpen(true);
        room.setHost(_services.getProfile());
        room.setPlaying(false);
        room.setRoundCounter(0);
        _screen.toScene(SceneEnum.ROOM, room);
    }

    private void updatePercent(final double added){
        _currentPercent += added;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.setProgressBarValue((float) _currentPercent);
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
