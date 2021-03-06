package com.potatoandtomato.common.utils;

import javax.xml.bind.annotation.XmlElementDecl;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by SiongLeng on 27/1/2016.
 */
public class Strings {

    public static String Salt = "default";

    public static String joinArr(ArrayList<String> arr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = arr.size(); i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(arr.get(i));
        }
        return sbStr.toString();
    }

    public static ArrayList<String> split(String input, String sSep) {
        String[] tmp = input.split(sSep);
        ArrayList<String> result = new ArrayList<String>();
        for(String s : tmp){
            result.add(s);
        }
        return result;
    }

    public static ArrayList<String> split(String input, int limitPerString){
        ArrayList<String> result = new ArrayList<String>();
        if(input.length() < limitPerString){
            result.add(input);
        }
        else{
            int index = 0;
            while (index < input.length()) {
                result.add(input.substring(index, Math.min(index + limitPerString, input.length())));
                index += limitPerString;
            }
        }

        return result;
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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
            input = input.substring(0, limit - 2);
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

    public static String generateUniqueRandomKey(int length){
        String alphabet =
                new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"); //9
        int n = alphabet.length(); //10

        String result = new String();
        Random r = new Random(); //11

        for (int i=0; i<length; i++) //12
            result = result + alphabet.charAt(r.nextInt(n)); //13

        long unixTime = System.currentTimeMillis() / 1000L;
        result = unixTime + result;
        if(result.length() > length){
            result = result.substring(0, length);
        }
        return result;
    }

    public static String formatNum(int input){
        return String.format("%,d", input);
    }

    public static String formatNum(double input){
        double remain = input - Math.floor(input);
        String result = String.format("%,d", (int) input);
        if(remain > 0){
            result =  String.format("%,d", (int) input) + "." + String.valueOf(DoubleUtils.round(remain, 2)).replace("0.", "");
        }
        return result;
    }

    public static String compress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decompress(String str){
        try {
            if (str == null || str.length() == 0) {
                return str;
            }
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
            BufferedReader bf = null;
            bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
            String outStr = "";
            String line;
            while ((line=bf.readLine())!=null) {
                outStr += line;
            }
            return outStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getHash(String input){
        input = input + Strings.Salt;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(input.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "nohash";
    }


    public static ArrayList<Pair<String, Boolean>> splitUrl(String text){
        String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern p = Pattern.compile(URL_REGEX);

        ArrayList<Pair<String, Boolean>> result = new ArrayList();
        ArrayList<String> splitted = Strings.split(text, " ");
        String currentText = "";

        for(String word : splitted){
            if(isMatchPattern(word, p)){
                if(!currentText.equals("")){
                    result.add(new Pair<String, Boolean>(currentText, false));
                }
                result.add(new Pair<String, Boolean>(word, true));
                currentText = "";
            }
            else{
                currentText += word + " ";
            }
        }

        if(!currentText.equals("")){
            result.add(new Pair<String, Boolean>(currentText, false));
        }


        return result;
    }

    public static boolean isMatchPattern(String text, Pattern p){
        Matcher m = p.matcher(text);//replace with string to compare
        if(m.find()) {
           return true;
        }
        else{
            return false;
        }
    }

}
