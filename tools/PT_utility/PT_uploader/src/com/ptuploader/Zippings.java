package com.ptuploader;

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
class Zippings
{

    private String _assets;
    private long _originalSize, _newSize;

    public Zippings(String assets){
        _assets = assets;
    }

    public void run() throws IOException, ZipException {
        File f = new File("assets.zip");
        if(f.exists()){
            _originalSize = f.length();
            f.delete();
        }



        // Initiate ZipFile object with the path/name of the zip file.
        ZipFile zipFile = new ZipFile("assets.zip");

        // Folder to add
        String folderToAdd = _assets;

        // Initiate Zip Parameters which define various properties such
        // as compression method, etc.
        ZipParameters parameters = new ZipParameters();

        // set compression method to store compression
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        // Set the compression level
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        parameters.setIncludeRootFolder(false);
        // Add folder to the zip file
        zipFile.addFolder(folderToAdd, parameters);


        f = new File("assets.zip");
        if(f.exists()){
            _newSize = f.length();
        }
    }

    public boolean hasModified(){
        return _originalSize != _newSize;
    }


}
