package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by SiongLeng on 28/2/2016.
 */
public class ArrayLists {

    public static ArrayList<String> randomNumericArray(int count, int min, int max){
        ArrayList<String> strings = new ArrayList<String>();
        for(int i = 0; i < count; i++){
            strings.add(String.valueOf(MathUtils.random(min, max)));
        }
        return strings;
    }

}
