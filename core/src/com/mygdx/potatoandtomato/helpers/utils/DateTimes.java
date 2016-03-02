package com.mygdx.potatoandtomato.helpers.utils;

/**
 * Created by SiongLeng on 24/12/2015.
 */
public class DateTimes {

    public static String calculateTimeAgo(long timeStamp) {

        long timeDiffernce;
        long unixTime = System.currentTimeMillis() / 1000L;  //get current time in seconds.
        int j;
        String[] periods = {"s", "m", "h", "d", "w", "m", "y", "d"};
        // you may choose to write full time intervals like seconds, minutes, days and so on
        double[] lengths = {60, 60, 24, 7, 4.35, 12, 10};
        timeDiffernce = unixTime - timeStamp;
        String tense = "ago";
        for (j = 0; timeDiffernce >= lengths[j] && j < lengths.length - 1; j++) {
            timeDiffernce /= lengths[j];
        }
        return timeDiffernce + periods[j] + " " + tense;
    }

}
