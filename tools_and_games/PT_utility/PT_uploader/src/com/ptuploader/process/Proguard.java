package com.ptuploader.process;

import com.ptuploader.utils.Logs;

import java.io.File;
import java.io.IOException;

/**
 * Created by SiongLeng on 19/7/2016.
 */
public class Proguard {

    private String _proguardPath;
    private String _proguardConfigFilePath;
    private String _jarPath;
    private Logs _logs;
    private Paths _paths;

    public Proguard(Paths paths, Logs logs) {
        this._proguardPath = paths.getProguardJarFile().getAbsolutePath();
        this._proguardConfigFilePath = paths.getProguardConfigFile().getAbsolutePath();
        this._paths = paths;
        this._logs = logs;
    }

    public boolean run() {

        _logs.write("Executing proguard tools.........");

        File afterDxGameJar = _paths.getAfterDxGameJar();
        File finalGameJar = _paths.getFinishProcessGameJar();

        if(finalGameJar.exists()) {
            finalGameJar.delete();
        }

        Runtime rt = Runtime.getRuntime();
        String command = "java -jar \"" + _proguardPath + "\" -include \"" + _proguardConfigFilePath + "\" -printmapping \"mapping.txt\" -injars \"" + afterDxGameJar.getAbsolutePath() +"\" -outjars \"" + finalGameJar.getAbsolutePath() +"\"";

        _logs.write(command);

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
            _logs.write("Error occured in Proguard tool, error code: " + retVal);
            return false;
        }

        afterDxGameJar.delete();

        _logs.write("Proguard tool completed successfully.");

        return true;
    }


}
