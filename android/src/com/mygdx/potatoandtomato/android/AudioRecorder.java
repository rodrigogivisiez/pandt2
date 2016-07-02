package com.mygdx.potatoandtomato.android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SiongLeng on 15/5/2016.
 */
public class AudioRecorder {

    private int RECORDER_SAMPLERATE;
    private int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int bufferElements2Rec; // want to play 2048 (2K) since 2 bytes we use only 1024
    int bytesPerElement = 2; // 2 bytes in 16bit format
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private Broadcaster broadcaster;
    private AndroidLauncher androidLauncher;
    private boolean recordCanceled;

    public AudioRecorder(AndroidLauncher androidLauncher, Broadcaster broadcaster) {
        this.androidLauncher = androidLauncher;
        this.broadcaster = broadcaster;

        broadcaster.subscribe(BroadcastEvent.RECORD_START, new BroadcastListener<RecordListener>() {
            @Override
            public void onCallback(RecordListener recordListener, Status st) {
                recordAudio(recordListener.getBinFilePath(), recordListener);
            }
        });

        broadcaster.subscribe(BroadcastEvent.RECORD_END, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                stopRecord(st == Status.FAILED);
            }
        });
    }

    public void recordAudio(final String recordBinPath, final RecordListener recordListener){
        stopRecord(false);

        if(!isStoragePermissionGranted()){
            broadcaster.broadcast(BroadcastEvent.RECORD_RESPONSE, "", Status.FAILED);
            return;
        }
        refreshParametersIfNeeded();

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferElements2Rec * bytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile(recordBinPath, recordListener);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile(String filePath, RecordListener recordListener) {
        // Write the output audio in byte
        boolean error = false;
        short sData[] = new short[bufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            error = true;
        }

        recordListener.onStart();

        while (isRecording) {
            // gets the voice output from microphone to byte format
            double sum = 0;
            int readSize = recorder.read(sData, 0, bufferElements2Rec);
            for (int i = 0; i < readSize; i++) {
                sum += sData [i] * sData [i];
            }
            if (readSize > 0) {
                final double amplitude = sum / readSize;
                int result = (int) Math.sqrt(amplitude);
                if(result > 0 && result < 200){
                    recordListener.onRecording(0, -1);
                }
                else if(result > 200 && result < 500){
                    recordListener.onRecording(1, -1);
                }
                else if(result > 500 && result < 1000){
                    recordListener.onRecording(2, -1);
                }
                else if(result > 1000){
                    recordListener.onRecording(3, -1);
                }
            }

            try {
                byte bData[] = short2byte(sData);
                os.write(bData, 0, bufferElements2Rec * bytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            error = true;
        }

        recordListener.onFinishedRecord(null, -1, error || recordCanceled ? Status.FAILED : Status.SUCCESS);
    }

    public void stopRecord(boolean recordCanceled){
        this.recordCanceled = recordCanceled;
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void refreshParametersIfNeeded(){
        if(RECORDER_SAMPLERATE == 0){
            for (int rate : new int[] {44100, 8000, 11025, 16000, 22050}) {
                int bufferSize = AudioRecord.getMinBufferSize(rate, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
                if (bufferSize > 0) {
                    // buffer size is valid, Sample rate supported
                    AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                    if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                        audioRecorder.release();
                    } else {
                        RECORDER_SAMPLERATE = rate;
                        bufferElements2Rec = bufferSize / bytesPerElement;
                        break;
                    }
                }
            }
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(androidLauncher, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(androidLauncher, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }


    }

}
