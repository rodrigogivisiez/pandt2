package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.potatoandtomato.absintflis.recorder.PlaybackListener;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.utils.Files;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.enums.Status;
import ogg.OggFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by SiongLeng on 11/1/2016.
 */
public class Recorder {

    private final int _samples = 44100;
    private boolean _isMono = true;
    private boolean _recording = false;
    private short[] _results;
    private long _startTime;
    private boolean _recordSuccess;
    private boolean _canRecord = true;
    private SoundsPlayer soundsPlayer;
    private Broadcaster broadcaster;
    private Music playingAudioMsg;
    private final int MAX_SECS = 10;
    private PlaybackListener playbackListener;
    private List<Runnable> playbackRunnablesQueue;
    private Assets assets;


    public Recorder(Assets assets, SoundsPlayer soundsPlayer, Broadcaster broadcaster) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.broadcaster = broadcaster;
        playbackRunnablesQueue = Collections.synchronizedList(new ArrayList());
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

        broadcaster.broadcast(BroadcastEvent.RECORD_START, new Pair<String, RunnableArgs>(binFile.file().getAbsolutePath(), new RunnableArgs() {
            @Override
            public void run() {

                long elapsed = System.currentTimeMillis() - _startTime;
                int remainingSecs = MAX_SECS - (int) (elapsed / 1000);
                _listener.onRecording((int) this.getArgs()[0], remainingSecs);

                if (remainingSecs <= 0) {
                    stopRecording();
                }
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
                    broadcaster.broadcast(BroadcastEvent.RECORD_END);
                    _canRecord = true;
                    return;
                }

                broadcaster.subscribeOnce(BroadcastEvent.RECORD_RESPONSE, new BroadcastListener() {
                    @Override
                    public void onCallback(Object obj, Status st) {
                        if (st == Status.FAILED) {
                            _listener.onFinishedRecord(null, 0, Status.FAILED);
                            _canRecord = true;
                        } else {
                            final int totalSecs = Math.max((int) (System.currentTimeMillis() - _startTime) / 1000, 1);
                            _listener.onPreSuccessRecord(oggFile);

                            convertAndCompressAudioFile(binFile, oggFile, new RunnableArgs() {
                                @Override
                                public void run() {
                                    if (this.getArgCount() > 0) {
                                        FileHandle oggFile = (FileHandle) this.getArgs()[0];
                                        if (oggFile.exists()) {
                                            _listener.onFinishedRecord(oggFile, totalSecs, Status.SUCCESS);
                                        }
                                    }
                                    _canRecord = true;
                                }
                            });
                        }
                    }
                });

                broadcaster.broadcast(BroadcastEvent.RECORD_END);
            }
        });
    }

    public void stopRecording(){
        if(System.currentTimeMillis() - _startTime < 300){
            _recordSuccess = false;
        }
        else{
            _recordSuccess = true;
        }
        _recording = false;
    }

    public void cancelRecord(){
        _recordSuccess = false;
        _recording = false;
    }

    public void playBack(final boolean useQueue, final FileHandle fileHandle, final PlaybackListener newPlaybackListener){
        if(useQueue && playingAudioMsg != null){
            playbackRunnablesQueue.add(new Runnable() {
                @Override
                public void run() {
                    playBack(true, fileHandle, newPlaybackListener);
                }
            });
            return;
        }
        else{
            playbackRunnablesQueue.clear();
        }

        if(playingAudioMsg != null){
            soundsPlayer.stopMusic(playingAudioMsg);
        }

        if(this.playbackListener != null){
            if(playbackListener.getId().equals(newPlaybackListener.getId())){
                return;
            }
        }

        if(this.playbackListener != null){
            this.playbackListener.onEndPlay(Status.FAILED);
            playbackRunnablesQueue.clear();
            playbackListener = null;
        }

        this.playbackListener = newPlaybackListener;

        if(fileHandle != null && fileHandle.exists()){
            soundsPlayer.setVolume(0);
            Music music = soundsPlayer.playMusicFromFile(fileHandle);
            playingAudioMsg = music;
            playbackListener.onStartPlay();
            music.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    music.dispose();
                    playingAudioMsg = null;
                    playbackListener.onEndPlay(Status.SUCCESS);
                    soundsPlayer.playSound(assets.getSounds().getSound(Sounds.Name.FINISH_PLAY_AUDIO_MSG), 0.4f);

                    playbackListener = null;

                    Threadings.delay(500, new Runnable() {
                        @Override
                        public void run() {
                            if(useQueue && playbackRunnablesQueue.size() > 0){
                                Runnable runnable = playbackRunnablesQueue.get(0);
                                playbackRunnablesQueue.remove(runnable);
                                runnable.run();
                                return;
                            }
                            else{
                                if(playingAudioMsg != null) return;
                                soundsPlayer.setVolume(1);
                            }
                        }
                    });
                }
            });
        }
        else{
            playbackListener.onEndPlay(Status.FAILED);
            playbackRunnablesQueue.clear();
            playbackListener = null;
        }
    }

    public void stopPlayback(){
        if(playingAudioMsg != null){
            soundsPlayer.stopMusic(playingAudioMsg);
        }
        if(playbackListener != null){
            playbackListener.onEndPlay(Status.FAILED);
            playbackListener = null;
            playbackRunnablesQueue.clear();
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
        if(playingAudioMsg != null){
            soundsPlayer.stopMusic(playingAudioMsg);
            soundsPlayer.setVolume(1);
        }
    }

    public int getMaxSecs(){
        return MAX_SECS;
    }

}
