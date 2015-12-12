package com.ptuploader;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 12/12/2015.
 */
public class ScreenShots {

    private String _path;
    private ArrayList<File> _result;

    public ScreenShots(String _path) {
        this._path = _path;
        _result = new ArrayList<>();
    }

    public void run(){
        File directory = new File(_path);
        for (File file : directory.listFiles()) {
            if(file.isFile()){
                _result.add(file);
            }
        }
    }

    public ArrayList<File> getAllScreenShotsPath(){
        return _result;
    }

}
