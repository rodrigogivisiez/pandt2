package com.ptuploader;

import java.io.File;
import java.io.IOException;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Dx {

    private String _dxPath;
    private String _jarPath;
    private long _originalSize, _newSize;

    public Dx(String _dxPath, String _jarPath) {
        this._dxPath = _dxPath;
        this._jarPath = _jarPath;
    }

    public void run() throws IOException, InterruptedException {

        File f = new File("game.jar");
        if(f.exists()) {
            _originalSize = f.length();
            f.delete();
        }

        Runtime rt = Runtime.getRuntime();
        String command = _dxPath + " --dex --keep-classes --output=\"game.jar\" \""+_jarPath+"\"";
        Process pr = rt.exec(command);
        int retVal = pr.waitFor();
        if(retVal != 0) throw new IllegalThreadStateException();

        f = new File("game.jar");
        if(f.exists()) {
            _newSize = f.length();
        }

    }

    public boolean hasModified(){
        return _originalSize != _newSize;
    }


}
