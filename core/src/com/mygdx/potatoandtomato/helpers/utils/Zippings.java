package com.mygdx.potatoandtomato.helpers.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;


public class Zippings
{
    public static boolean unZipIt(String source, String destination) throws ZipException {

        if(new File(source).exists()){
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
            }
            zipFile.extractAll(destination);
            return true;
        }

        return false;
    }
}

