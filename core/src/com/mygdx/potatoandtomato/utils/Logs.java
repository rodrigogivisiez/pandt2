package com.mygdx.potatoandtomato.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Logs {

    private static long _startTime;
    private static FPSLogger _fps;
    private static SafeThread _fpsThread;
    private static ArrayList<String> _logs = new ArrayList();
    public static String LAST_GAME = "EMPTY";

    public static void add(){
        final Throwable t = new Throwable();
        final StackTraceElement[] elements = t.getStackTrace();

        final String callerClassName = elements[1].getFileName();
        final String callerMethodName = elements[1].getMethodName();

        String TAG = "[" + callerClassName + "]";

        _logs.add(TAG + " [" + callerMethodName + "]");
        Logs.show(TAG + " [" + callerMethodName + "]");

        ArrayList<String> clone = (ArrayList) _logs.clone();

        for(int i = _logs.size() - 1; i > 10; i--){
            clone.remove(clone.get(0));
        }

        _logs = clone;
    }

    public static ArrayList<String> getAllLogs(){
        return _logs;
    }

    public static void show(String msg){
        if(Global.DEBUG)
        System.out.println(msg);
    }

    public static void show(float msg){
        if(Global.DEBUG)
        System.out.println(msg);
    }

    public static void startLogFps(){
        if(_fps == null){
            _fps = new FPSLogger();
            _fpsThread = new SafeThread();
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (_fpsThread.isKilled()) return;
                        _fps.log();
                        Threadings.sleep(1000);
                    }
                }
            });

        }
    }

    public static void stopLogFps(){
        _fpsThread.kill();
        _fps = null;
    }

    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logs.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
                if (callerClassName==null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClassName();
                }
            }
        }
        return null;
    }

    public static void startMeasure(){
        _startTime = System.nanoTime();
    }

    public static long endMeasure(){
        return System.nanoTime() - _startTime;
    }

    public static void writeToLog(String msg){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        FileHandle handle = Gdx.files.local("pt_logs.txt");
        msg = "Last game: " + LAST_GAME  + "\n ";
        msg = "Report date: " + dateFormat.format(date) + "\n " + msg;
        msg += "-------------------------------\n ";
        for(String callLog : getAllLogs()){
            msg += callLog + "\n ";
        }
        handle.writeString(msg, false);
    }

    public static String getAndDeleteLogMsg(){
        FileHandle logFile = Gdx.files.local("pt_logs.txt");
        if(logFile.exists()){
            String result =  logFile.readString();
            logFile.delete();
            return result;
        }
       else{
            return "" ;
        }
    }

}
