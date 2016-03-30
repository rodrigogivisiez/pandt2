package com.potatoandtomato.common.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public class Files {

    public FileHandle getFileH(String path, String basePath){
        if(path.contains(".gen")) path = path.replace(".gen", "");

        if(Gdx.files.local(basePath + "/" + path).exists()){
            return Gdx.files.local(basePath + "/" + path);
        }
        else{
            return Gdx.files.internal(path);
        }
    }

}
