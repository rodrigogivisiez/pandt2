package com.mygdx.potatoandtomato.absintflis.uploader;

import com.badlogic.gdx.files.FileHandle;

import java.io.File;

/**
 * Created by SiongLeng on 11/1/2016.
 */
public interface IUploader {

    void uploadFile(FileHandle file, UploadListener<String> listener);

    void getUploadedFile(String fileName, FileHandle saveToFile, UploadListener<FileHandle> listener);

}
