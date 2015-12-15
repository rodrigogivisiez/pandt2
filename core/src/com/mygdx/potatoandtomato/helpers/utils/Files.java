package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by SiongLeng on 11/7/2015.
 */
public class Files {

    public static FileHandle createIfNotExist(com.badlogic.gdx.files.FileHandle input){
        if(!input.file().getParentFile().exists()) input.file().getParentFile().mkdirs();
        return input;
    }


}
