package com.potatoandtomato.common.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by SiongLeng on 27/1/2016.
 */
public class Strings {

    public static String joinArr(ArrayList<String> arr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = arr.size(); i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(arr.get(i));
        }
        return sbStr.toString();
    }

    public static String formatToTwoDec(float f){
        return String.format("%.2f", f);
    }

    public static String byteToMb(long b){
        return formatToTwoDec((float) b / 1024f / 1024f);
    }

    public static String cutOff(String input, int limit){
        if(limit == 0) limit = 9999;
        if(input == null) return null;
        if(input.length() > limit) {
            input = input.substring(0, limit);
            input+="..";
        }
        return input;
    }

    public static boolean isLargerLexically(String target, String against){
        if(target.length() != against.length()){
            return target.length() > against.length();
        }
        else{
            return (target.compareTo(against) >= 0);
        }
    }

    public static boolean isEmpty(String input){
        if(input == null) return true;
        if(input.trim().equals("")) return true;
        return false;
    }

    public static String generateRandomKey(int length){
        String alphabet =
                new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"); //9
        int n = alphabet.length(); //10

        String result = new String();
        Random r = new Random(); //11

        for (int i=0; i<length; i++) //12
            result = result + alphabet.charAt(r.nextInt(n)); //13

        return result;
    }

    public static String formatNum(int input){
        return String.format("%,d", input);
    }

    public static String formatNum(double input){
        double remain = input - Math.floor(input);
        String result = String.format("%,d", (int) input);
        if(remain > 0){
            result = String.valueOf(Double.valueOf(result) + remain);
        }
        return result;
    }

}
