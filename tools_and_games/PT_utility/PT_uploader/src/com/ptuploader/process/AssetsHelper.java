package com.ptuploader.process;

import com.ptuploader.models.FileData;
import com.ptuploader.utils.Helpers;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by SiongLeng on 7/7/2016.
 */
public class AssetsHelper {

    private File assetsDir;
    private File jarFile;
    private HashMap<String, FileData> fileDatasMap;
    private long gameSize;

    public AssetsHelper(File assetsDir, File jarPath) {
        this.assetsDir = assetsDir;
        this.jarFile = jarPath;
        fileDatasMap = new HashMap();
    }

    public void run(){
        gameSize = 0;
        Collection<File> files = FileUtils.listFiles(assetsDir, null, true);
        for(File file : files){
            if(!file.isDirectory()){
                String path = file.getAbsolutePath();
                String base = assetsDir.getAbsolutePath();
                String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();
                fileDatasMap.put(relative, new FileData(String.valueOf(file.lastModified()), "", file.getAbsolutePath(), file.length()));
                gameSize += file.length();
            }
        }
        gameSize += jarFile.length();
    }

    public HashMap<String, FileData> getFileDatasMap() {
        return fileDatasMap;
    }

    public String getGameSize() {
        return String.valueOf(gameSize);
    }
}
