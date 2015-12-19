package com.mygdx.potatoandtomato.helpers.utils;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Logs {

    public static void show(String msg){
        System.out.println(msg);
    }

    public static void show(float msg){
        System.out.println(msg);
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

}
