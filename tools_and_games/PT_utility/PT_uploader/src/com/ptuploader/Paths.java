package com.ptuploader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Paths {

    String base;
    String dx;
    String assets;
    String working;
    String jar;
    String screenshots;
    String details;
    String icon;
    String commonVersion;


    public Paths() {
    }

    public void getAll() throws IOException {
        String line;
        try (
                InputStream fis = new FileInputStream("paths.txt");
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("=");
                if(tokens.length < 2) continue;
                else{
                    String path = tokens[1].replace("\"", "");
                    if(tokens[0].equals("BASE")){
                        base = path;
                    }
                    else if(tokens[0].equals("ASSETS")){
                        assets = path;
                    }
                    else if(tokens[0].equals("JAR")){
                        jar = path;
                    }
                    else if(tokens[0].equals("DX")){
                        dx = path;
                    }
                    else if(tokens[0].equals("WORKING")){
                        working = path;
                    }
                }
            }

            assets = assets.replace("{BASE}", base);
            commonVersion = working.replace("{BASE}", base) + "common_version.txt";
            jar = jar.replace("{BASE}", base);
            dx = dx.replace("{BASE}", base);
            screenshots = System.getProperty("user.dir") +"/screenshots/";
            details = System.getProperty("user.dir") +"/details.json";
            icon = System.getProperty("user.dir") +"/icon.png";
        }
    }

    public void checkAllPathsExist(){
        File f = new File(assets);
        if(!f.exists() || !f.isDirectory()) {
            throw new InvalidPathException(assets, "");
        }
        f = new File(screenshots);
        if(!f.exists() || !f.isDirectory()) {
            throw new InvalidPathException(screenshots, "");
        }
        f = new File(jar);
        if(!f.exists() || !f.isFile()) {
            throw new InvalidPathException(jar, "");
        }
        f = new File(dx);
        if(!f.exists() || !f.isFile()) {
            throw new InvalidPathException(dx, "");
        }
        f = new File(details);
        if(!f.exists() || !f.isFile()) {
            throw new InvalidPathException(details, "");
        }
        f = new File(icon);
        if(!f.exists() || !f.isFile()) {
            throw new InvalidPathException(icon, "");
        }
        f = new File(commonVersion);
        if(!f.exists() || !f.isFile()) {
            throw new InvalidPathException(commonVersion, "");
        }
    }

}
