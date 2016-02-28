package com.potatoandtomato.games.helpers;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/2/2016.
 */
public class Strings {

    public static String cut(String input, int count){
        if(input.length() > count){
            return input.substring(0, count - 2) + "..";
        }
        return input;
    }

    public static String join(ArrayList<String> input){
        StringBuilder sb = new StringBuilder();
        for (String s : input)
        {
            sb.append(s);
            sb.append(",");
        }
        return sb.toString();
    }


}
