package com.mygdx.potatoandtomato.helpers.utils;

import com.potatoandtomato.common.models.ScoreDetails;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 5/5/2016.
 */
public class Scores {

    public static Double getTotalScoresInScoresArray(ArrayList<ScoreDetails> scoreDetails){
        double total = 0;
        for(ScoreDetails detail : scoreDetails){
            if(detail.isAddOrMultiply()){
                total += detail.getValue();
            }
        }
        return total;
    }

}
