package com.mygdx.potatoandtomato.helpers.utils;

import java.util.ArrayList;

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

}
