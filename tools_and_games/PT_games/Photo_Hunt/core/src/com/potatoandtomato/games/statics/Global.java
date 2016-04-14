package com.potatoandtomato.games.statics;

import javax.xml.bind.annotation.XmlElementDecl;

/**
 * Created by SiongLeng on 22/2/2016.
 */
public class Global {

    public static boolean DEBUG = false;         //default false
    public static boolean REVIEW_MODE = false;
    public static int ATTACK_TIME_PERCENT = 20;
    public static int START_PREPARE_TIME = 5000;            //in milisec

    public static void setDEBUG(boolean isDebugging){
        DEBUG = isDebugging;
        if(DEBUG) START_PREPARE_TIME = 0;
    }

}
