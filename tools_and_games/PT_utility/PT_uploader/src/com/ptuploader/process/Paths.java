package com.ptuploader.process;

import com.ptuploader.utils.FileIO;
import com.ptuploader.utils.Logs;

import java.io.*;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Paths {

    private Logs logs;
    private File baseDir;
    private File assetsDir;
    private File workingDir;
    private File jarFile;
    private File dxBat;
    private File iconFile;

    public Paths(Logs logs) {
        this.logs = logs;
    }

    public boolean checkAll(){
        logs.write("Analyzing paths....");

        this.workingDir = new File(System.getProperty("user.dir"));
        if(!fileOrDirExist("Working", workingDir)) return false;

        this.baseDir = new File(workingDir.getParent());
        if(!fileOrDirExist("Base", baseDir)) return false;

        this.iconFile = new File(workingDir.getAbsolutePath() + "/icon.png");
        if(!fileOrDirExist("Icon File", iconFile)) return false;

        this.assetsDir = new File(baseDir.getAbsolutePath() + "/android/assets/");
        if(!fileOrDirExist("Assets", assetsDir)) return false;

        this.jarFile = new File(baseDir.getAbsolutePath() + "/out/artifacts/core_jar/core.jar");
        if(!fileOrDirExist("Game Jar", jarFile)) return false;

        String dxPath = System.getenv("LOCALAPPDATA") + "/Android/sdk/build-tools/22.0.1/dx.bat";
        File dxPathFile = new File(workingDir + "/dx_path.txt");
        if(dxPathFile.exists()){
            dxPath = FileIO.read(dxPathFile);
        }

        this.dxBat = new File(dxPath);
        if(!fileOrDirExist("dx.bat", dxBat)){
            logs.write("Cannot find dx.bat file, please input the file path manually:");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                dxPath = br.readLine();
                this.dxBat = new File(dxPath);
                if(!fileOrDirExist("dx.bat", dxBat)) return false;
                else{
                    FileIO.write(dxPathFile, dxPath);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        logs.write("Path analyzing completed successfully.");

        return true;
    }

    private boolean fileOrDirExist(String type, File file){
        String word = file.isDirectory() ? "directory" : "file";
        if(!file.exists()){
            logs.write(type + " " + word + " not found error = " + file.getAbsolutePath());
            return false;
        }
        else{
            logs.write(type + " " + word + " = " +file.getAbsolutePath());
        }
        return true;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getAssetsDir() {
        return assetsDir;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public File getJarFile() {
        return jarFile;
    }

    public File getDxBat() {
        return dxBat;
    }

    public File getCommonVersionFile(){
        return new File(assetsDir.getAbsolutePath() + "/common_version.txt");
    }

    public File getDetailsFile(){
        return new File(workingDir.getAbsolutePath() + "/details.json");
    }

    public File getAfterDxGameJar(){
        return new File(workingDir.getAbsolutePath() + "/game.jar");
    }

    public File getAssetsZip(){
        return new File(workingDir.getAbsolutePath() + "/assets.zip");
    }

    public File getIconFile() {
        return iconFile;
    }
}
