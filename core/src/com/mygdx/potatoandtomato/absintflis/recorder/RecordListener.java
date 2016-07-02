package com.mygdx.potatoandtomato.absintflis.recorder;

import com.badlogic.gdx.files.FileHandle;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 12/1/2016.
 */
public abstract class RecordListener {

    private String binFilePath;

    public RecordListener(String binFilePath) {
        this.binFilePath = binFilePath;
    }

    public String getBinFilePath() {
        return binFilePath;
    }

    public abstract void onStart();

    public abstract void onRecording(int volumeLevel, int remainingSecs);

    public abstract void onPreSuccessRecord(FileHandle resultFile);

    public abstract void onFinishedRecord(FileHandle resultFile, int totalSecs, Status status);

}
