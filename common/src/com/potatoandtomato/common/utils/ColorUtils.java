package com.potatoandtomato.common.utils;

import com.badlogic.gdx.graphics.Color;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public class ColorUtils {

    @JsonIgnore
    public static Color getUserColorByIndex(int slotIndex){
        String hex = "ffffff";

        switch (slotIndex){
            case 0:
                hex = "FF420E";
                break;
            case 1:
                hex = "89DA59";
                break;
            case 2:
                hex = "E6D72A";
                break;
            case 3:
                hex = "FAAF08";
                break;
            case 4:
                hex = "BA5536";
                break;
            case 5:
                hex = "004445";
                break;
            case 6:
                hex = "336B87";
                break;
            case 7:
                hex = "808D9E";
                break;
            case 8:
                hex = "6FB98F";
                break;
            case 9:
                hex = "90AFC5";
                break;
            case 10:
                hex = "F18D9E";
                break;
            case 11:
                hex = "F98866";
                break;
            case 12:
                hex = "86AC41";
                break;
            case 13:
                hex = "F1F1F2";
                break;
            case 14:
                hex = "BCBABE";
                break;
            case 15:
                hex = "A43820";
                break;
            case 16:
                hex = "1995AD";
                break;
            case 17:
                hex = "9A9EAB";
                break;
            case 18:
                hex = "DFE166";
                break;
            case 19:
                hex = "F0810F";
                break;
            case 20:
                hex = "E6DF44";
                break;
            case 21:
                hex = "063852";
                break;
            case 22:
                hex = "D9B44A";
                break;
            case 23:
                hex = "8EBA43";
                break;
            case 24:
                hex = "F9DC24";
                break;
            case 25:
                hex = "F52549";
                break;
            case 26:
                hex = "FFD64D";
                break;
            case 27:
                hex = "B38867";
                break;
            case 28:
                hex = "626D71";
                break;
            case 29:
                hex = "31A9B8";
                break;
            case 30:
                hex = "258039";
                break;
            case 31:
                hex = "752A07";
                break;
            case 32:
                hex = "FBCB7B";
                break;
        }

        return Color.valueOf(hex);

    }

}
