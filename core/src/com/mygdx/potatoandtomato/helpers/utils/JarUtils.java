package com.mygdx.potatoandtomato.helpers.utils;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameEntrance;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by SiongLeng on 25/8/2015.
 */
public class JarUtils {

    public static GameCoordinator fillGameEntrance(Class<?> loadedClass, GameCoordinator gameCoordinator) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        GameEntrance instance = null;
        Class[] cArg = new Class[1]; //Our constructor has 1 arguments
        cArg[0] = GameCoordinator.class;
        instance = (GameEntrance) loadedClass.getDeclaredConstructor(cArg)
                .newInstance(gameCoordinator);
        gameCoordinator.setGameEntrance(instance);
        return gameCoordinator;
    }







}
