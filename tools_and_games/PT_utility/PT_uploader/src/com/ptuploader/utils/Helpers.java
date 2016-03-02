package com.ptuploader.utils;

/**
 * Created by SiongLeng on 1/3/2016.
 */
public class Helpers {

    public static String formatToTwoDec(float f){
        return String.format("%.2f", f);
    }

    public static String byteToMb(long b){
        return formatToTwoDec((float) b / 1024f / 1024f);
    }
}
