package com.ptuploader.utils;

import com.ptuploader.process.Details;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Logs {


    public Logs() {
    }




    public void writeSuccess(Details details, int screenshotCount, String commonVersion){
        write("Upload success!");
//        write("Name : " +details.name);
//        write("Min Players : " +details.min_players);
//        write("Max Players : " +details.max_players);
//        write("Current Version : " +details.version);
//        write("Screenshot Counts : " +screenshotCount);
//        write("Description : " +details.description);
//        write("Common Version : " + commonVersion);
//        System.out.println("Upload Success!");
//        System.out.println("Game Version:" + details.version);
//        System.out.println("Common Version:" + commonVersion);
    }

    public void writeFailed(String msg){
        write("Upload failed : " +msg);
        System.out.println("Upload Failed....");
    }

    public void write(String msg){
        System.out.println(msg);
    }

}
