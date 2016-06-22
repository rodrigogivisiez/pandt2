package com.mygdx.potatoandtomato.utils;

/**
 * Created by SiongLeng on 24/12/2015.
 */
public class DateTimes {

    public static String calculateTimeAgo(long timeStamp) {

        long timeDiffernce;
        long unixTime = System.currentTimeMillis() / 1000L;  //get current time in seconds.
        int j;
        String[] periods = {"sec", "minute", "hour", "day", "week", "month", "year", "decade"};
        // you may choose to write full time intervals like seconds, minutes, days and so on
        double[] lengths = {60, 60, 24, 7, 4.35, 12, 10};
        timeDiffernce = unixTime - timeStamp;
        String tense = "ago";
        for (j = 0; timeDiffernce >= lengths[j] && j < lengths.length - 1; j++) {
            timeDiffernce /= lengths[j];
        }
        if(timeDiffernce > 1){
            periods[j] += "s";
        }

        return timeDiffernce + " " + periods[j] + " " + tense;
    }

    public static String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

}
