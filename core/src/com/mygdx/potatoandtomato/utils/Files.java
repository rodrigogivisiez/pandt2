package com.mygdx.potatoandtomato.utils;

import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by SiongLeng on 11/7/2015.
 */
public class Files {

    public static FileHandle createIfNotExist(com.badlogic.gdx.files.FileHandle input){
        if(!input.file().getParentFile().exists()) input.file().getParentFile().mkdirs();
        return input;
    }

    public static byte[] fileToByte(File file){
        FileInputStream fileInputStream=null;

        byte[] bFile = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return bFile;
    }


}
