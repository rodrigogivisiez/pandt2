package com.potatoandtomato.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by SiongLeng on 1/8/2016.
 */
public class DoubleUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
