package com.ptuploader;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Logs {

    PrintWriter _writer;
    File _log;

    public Logs() {
        File theDir = new File("logs");
        if(!theDir.exists()) theDir.mkdir();
    }

    public void writeSuccess(Details details, int screenshotCount){
        open();
        write("Upload success!");
        write("Name : " +details.name);
        write("Min Players : " +details.min_players);
        write("Max Players : " +details.max_players);
        write("Current Version : " +details.version);
        write("Screenshot Counts : " +screenshotCount);
        write("Description : " +details.description);
        close();
        System.out.println("Upload Success!");
    }

    public void writeFailed(String msg){
        open();
        write("Upload failed : " +msg);
        close();
        System.out.println("Upload Failed....");
    }

    private void open(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        _log = new File("logs/log_"+dateFormat.format(date)+".txt");
        _writer = null;
        try {
            _writer = new PrintWriter(new FileWriter(_log, false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(){
        _writer.close();
    }

    private void write(String msg){
        _writer.println(msg);
    }

}
