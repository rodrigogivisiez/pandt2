package com.ptuploader.process;

import com.ptuploader.utils.Helpers;
import com.ptuploader.utils.Logs;

import java.io.File;
import java.io.IOException;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Dx {

    private String _dxPath;
    private String _jarPath;
    private Logs _logs;
    private Paths _paths;

    public Dx(Paths paths, Logs logs) {
        this._dxPath = paths.getDxBat().getAbsolutePath();
        this._jarPath = paths.getJarFile().getAbsolutePath();
        this._paths = paths;
        this._logs = logs;
    }

    public boolean run() {

        _logs.write("Executing dx tools.........");

        File f = _paths.getAfterDxGameJar();
        if(f.exists()) {
            f.delete();
        }

        Runtime rt = Runtime.getRuntime();
        String command = _dxPath + " --dex --keep-classes --output=\"game.jar\" \""+_jarPath+"\"";

        //String command = "dx.bat --dex --keep-classes --output=\"game.jar\" \""+_jarPath+"\"";

        Process pr = null;
        int retVal = 0;
        try {

            pr = rt.exec(command);
            retVal = pr.waitFor();
        } catch (IOException | InterruptedException e) {
            retVal = 99;
            e.printStackTrace();
        }

        if(retVal != 0 || !_paths.getAfterDxGameJar().exists()) {
            _logs.write("Error occured in Dx tool, error code: " + retVal);
            return false;
        }

        _logs.write("Dx tool completed successfully.");

        return true;
    }

}
