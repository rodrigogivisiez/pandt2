package com.ptuploader.process;

import com.ptuploader.process.Paths;
import com.ptuploader.utils.Helpers;
import com.ptuploader.utils.Logs;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Zippings
{

    private Logs _logs;
    private Paths _paths;
    private long _originalSize, _newSize;

    public Zippings(Paths paths, Logs logs){
        _paths = paths;
        this._logs = logs;
    }

    public boolean run() {
        File f = _paths.getAssetsZip();
        if(f.exists()){
            _originalSize = f.length();
            f.delete();
        }
        try {

            _logs.write("Zipping assets start...");

            // Initiate ZipFile object with the path/name of the zip file.
            ZipFile zipFile = null;

            zipFile = new ZipFile(_paths.getAssetsZip().getName());



            // Folder to add
            String folderToAdd = _paths.getAssetsDir().getAbsolutePath();

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc.
            ZipParameters parameters = new ZipParameters();

            // set compression method to store compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            parameters.setIncludeRootFolder(false);

            zipFile.addFile(_paths.getAfterDxGameJar(), parameters);

            // Add folder to the zip file
            zipFile.addFolder(folderToAdd, parameters);



            f = _paths.getAssetsZip();
            if(f.exists()){
                _newSize = f.length();
            }

            _paths.getAfterDxGameJar().delete();

        } catch (ZipException e) {
            e.printStackTrace();
            _logs.write("Zipping process failed.");
            return false;
        }

        _logs.write("Zipping assets ended successfully, around " + Helpers.byteToMb(_newSize) + "mb.");
        return true;
    }

    public boolean hasModified(){
        return _originalSize != _newSize;
    }

    public long getNewFileSize(){
        return _newSize;
    }
}
