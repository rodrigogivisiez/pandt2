package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import ogg.OggFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
    boolean _canRecord = true;


    public Recorder(SoundsPlayer _soundsPlayer) {
        this._soundsPlayer = _soundsPlayer;
    }

    public void recordToFile(final String recordPath, final RecordListener _listener){
        if(!_canRecord) return;
        _startTime = System.currentTimeMillis();
        _recording = true;
        _recordSuccess = false;

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                Array<short[]> datas = new Array();
                _canRecord = false;
                AudioRecorder recorder = Gdx.audio.newAudioRecorder(_samples, _isMono);

                while (_recording){
                    short[] data = new short[_samples];
                    recorder.read(data, 0, data.length);

                    datas.add(data);

                    int max = 0;
                    for(short each : data){
                        max = Math.max(max, (Math.abs(each)));
                    }

                    double maxDouble = Short.MAX_VALUE;
                    double currentMax = max;

                    _listener.onRecording((int) Math.floor(currentMax / maxDouble * 5));
                }
                recorder.dispose();
                _canRecord = true;

                if(!_recordSuccess){
                    _listener.onFinishedRecord(null, Status.FAILED);
                    return;
                }


                _results = new short[_samples * datas.size];
                int i = 0;
                for(short[] data : datas){
                    System.arraycopy(data, 0, _results, i * _samples, data.length);
                    i++;
                }

                saveAudioToFile(_results, recordPath, new RunnableArgs() {
                    @Override
                    public void run() {
                        if(this.getArgCount() > 0){
                            FileHandle oggFile = (FileHandle) this.getArgs()[0];
                            if(oggFile.exists()){
                                _listener.onFinishedRecord(oggFile, Status.SUCCESS);
                                Logs.show("end recording...........................................");
                            }
                        }
                    }
                });
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
        if(Global.ENABLE_SOUND){
            Music music = _soundsPlayer.playMusicFromFile(fileHandle);
            music.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    music.dispose();
                    onFinish.run();
                }
            });
        }
    }

    private void saveAudioToFile(final short[] data, final String recordPath, final RunnableArgs onFinish) {
        String fileName =  System.currentTimeMillis() + "_" + MathUtils.random(0, 10000);
        FileHandle binFile = Gdx.files.local(recordPath + fileName + ".bin");
        FileHandle oggFile = Gdx.files.local(recordPath + fileName + ".ogg");
        try {
            byte[] temp = new byte[data.length * 2]; //create a byte array to hold the data passed (short = 2 bytes)
            ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data); // cast a short array to byte array
            binFile.writeBytes(temp, false); //save bytes to file

            oggFile.file().createNewFile();

            OggFile oggFileConverter = new OggFile();
            oggFileConverter.convertOggFile(binFile.file().getAbsolutePath(), oggFile.file().getAbsolutePath(), 2f);

            binFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
        onFinish.run(oggFile);

    }



}
