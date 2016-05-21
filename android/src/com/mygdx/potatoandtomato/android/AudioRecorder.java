package com.mygdx.potatoandtomato.android;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SiongLeng on 15/5/2016.
 */
public class AudioRecorder {

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    int BufferElements2Rec; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    private int lastSoundLevel = 0;
    private SafeThread safeThread;
    private Broadcaster broadcaster;

    public AudioRecorder(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;

        int size = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        BufferElements2Rec = size / BytesPerElement;

        broadcaster.subscribe(BroadcastEvent.RECORD_START, new BroadcastListener<Pair<String, RunnableArgs>>() {
            @Override
            public void onCallback(Pair<String, RunnableArgs> pair, Status st) {
                recordAudio(pair.getFirst(), pair.getSecond());
            }
        });

        broadcaster.subscribe(BroadcastEvent.RECORD_END, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                stopRecord();
            }
        });
    }

    public void recordAudio(final String recordBinPath, final RunnableArgs onVolumeChange){
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);



        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile(recordBinPath, onVolumeChange);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();

//        safeThread = new SafeThread();
//        Threadings.runInBackground(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    Threadings.sleep(200);
//                    if(safeThread.isKilled()) break;
//
//                    onVolumeChange.run(lastSoundLevel);
//                    lastSoundLevel = 0;
//                }
//            }
//        });

    }

    private void writeAudioDataToFile(String filePath, RunnableArgs onVolumeChange) {
        // Write the output audio in byte
        boolean error = false;
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            error = true;
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            double sum = 0;
            int readSize = recorder.read(sData, 0, BufferElements2Rec);
            for (int i = 0; i < readSize; i++) {
                sum += sData [i] * sData [i];
            }
            if (readSize > 0) {
                final double amplitude = sum / readSize;
                int result = (int) Math.sqrt(amplitude);
                int newSoundLevel = 0;
                if(result > 0 && result < 200){
                    onVolumeChange.run(0);
                }
                else if(result > 200 && result < 500){
                    onVolumeChange.run(1);
                }
                else if(result > 500 && result < 1000){
                    onVolumeChange.run(2);
                }
                else if(result > 1000){
                    onVolumeChange.run(3);
                }
                if(newSoundLevel > lastSoundLevel){
                    lastSoundLevel = newSoundLevel;
                }
            }

            try {
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
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

        broadcaster.broadcast(BroadcastEvent.RECORD_RESPONSE, "", error ? Status.FAILED : Status.SUCCESS);
    }

    public void stopRecord(){
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


}
