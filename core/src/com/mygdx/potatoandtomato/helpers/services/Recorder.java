package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.potatoandtomato.common.Threadings;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.Status;

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
    boolean _canRecord = true;


    public void recordToFile(final FileHandle fileHandle,  final RecordListener _listener){
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

                saveAudioToFile(_results, fileHandle);
                _listener.onFinishedRecord(fileHandle, Status.SUCCESS);
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
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                if(Global.ENABLE_SOUND){
                    short[] data = getAudioDataFromFile(fileHandle);
                    final AudioDevice player = Gdx.audio.newAudioDevice(_samples, _isMono);
                    player.setVolume(1);
                    player.writeSamples(data, 0, data.length);
                    player.dispose();
                }
                onFinish.run();
            }
        });
    }

    private short[] getAudioDataFromFile(FileHandle fileHandle) {
        byte[] temp = fileHandle.readBytes(); // get all bytes from file
        short[] data = new short[temp.length / 2]; // create short with half the length (short = 2 bytes)
        ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(data); // cast a byte array to short array
        return data;
    }

    private void saveAudioToFile(short[] data, FileHandle file) {
        byte[] temp = new byte[data.length * 2]; //create a byte array to hold the data passed (short = 2 bytes)
        ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data); // cast a short array to byte array
        file.writeBytes(temp, false); //save bytes to file
    }



}
