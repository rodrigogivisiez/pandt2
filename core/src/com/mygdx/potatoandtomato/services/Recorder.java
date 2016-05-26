package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.utils.Files;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import ogg.OggFile;

import java.io.IOException;

/**
 * Created by SiongLeng on 11/1/2016.
 */
public class Recorder {

    final int _samples = 44100;
    boolean _isMono = true;
    boolean _recording = false;
    short[] _results;
    long _startTime;
    boolean _recordSuccess;
    SoundsPlayer _soundsPlayer;
    Broadcaster _broadcaster;
    boolean _canRecord = true;
    Music _playingMusic;


    public Recorder(SoundsPlayer _soundsPlayer, Broadcaster broadcaster) {
        this._soundsPlayer = _soundsPlayer;
        this._broadcaster = broadcaster;
    }

    public void recordToFile(final String recordPath, final RecordListener _listener){
        _startTime = System.currentTimeMillis();
        if(!_canRecord) return;

        _canRecord = false;
        String fileName =  System.currentTimeMillis() + "_" + MathUtils.random(0, 10000);
        final FileHandle binFile = Gdx.files.local(recordPath + fileName + ".bin");
        Files.createIfNotExist(binFile);
        final FileHandle oggFile = Gdx.files.local(recordPath + fileName + ".ogg");
        Files.createIfNotExist(oggFile);

        _recording = true;
        _recordSuccess = false;

        _broadcaster.broadcast(BroadcastEvent.RECORD_START, new Pair<String, RunnableArgs>(binFile.file().getAbsolutePath(), new RunnableArgs() {
            @Override
            public void run() {
                _listener.onRecording((int) this.getArgs()[0]);
            }
        }));

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                while (_recording){
                    Threadings.sleep(100);
                }
                if(!_recordSuccess){
                    _listener.onFinishedRecord(null, 0, Status.FAILED);
                    _broadcaster.broadcast(BroadcastEvent.RECORD_END);
                    _canRecord = true;
                    return;
                }

                _broadcaster.subscribeOnce(BroadcastEvent.RECORD_RESPONSE, new BroadcastListener() {
                    @Override
                    public void onCallback(Object obj, Status st) {
                        if (st == Status.FAILED) {
                            _listener.onFinishedRecord(null, 0, Status.FAILED);
                            _canRecord = true;
                        } else {
                            convertAndCompressAudioFile(binFile, oggFile, new RunnableArgs() {
                                @Override
                                public void run() {
                                    if (this.getArgCount() > 0) {
                                        FileHandle oggFile = (FileHandle) this.getArgs()[0];
                                        if (oggFile.exists()) {
                                            _listener.onFinishedRecord(oggFile,
                                                    (int) (System.currentTimeMillis() - _startTime) / 1000,
                                                    Status.SUCCESS);
                                            Logs.show("end recording...........................................");
                                        }
                                    }
                                    _canRecord = true;
                                }
                            });
                        }
                    }
                });

                _broadcaster.broadcast(BroadcastEvent.RECORD_END);
            }
        });
    }

    public void stopRecording(){
        if(System.currentTimeMillis() - _startTime < 1000){
            _recordSuccess = false;
        }
        else{
            _recordSuccess = true;
        }
        _recording = false;
    }

    public void playBack(final FileHandle fileHandle, final Runnable onFinish){
        if(_playingMusic != null){
            _soundsPlayer.stopMusic(_playingMusic);
        }
        if(Global.ENABLE_SOUND && fileHandle.exists()){
            Music music = _soundsPlayer.playMusicFromFile(fileHandle);
            _playingMusic = music;
            music.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    music.dispose();
                    _playingMusic = null;
                    onFinish.run();
                }
            });
        }
    }

    private void convertAndCompressAudioFile(FileHandle threegpFile, FileHandle oggFile, final RunnableArgs onFinish) {
        OggFile oggFileConverter = new OggFile();
        oggFileConverter.convertOggFile(threegpFile.file().getAbsolutePath(), oggFile.file().getAbsolutePath(), 10f);
        threegpFile.delete();
        onFinish.run(oggFile);
    }


    public boolean isCanRecord() {
        return _canRecord;
    }

    public void reset(){
        if(_playingMusic != null){
            _soundsPlayer.stopMusic(_playingMusic);
            _soundsPlayer.setVolume(1);
        }
    }

}
