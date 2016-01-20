package com.mygdx.potatoandtomato.helpers.utils;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameEntrance;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by SiongLeng on 25/8/2015.
 */
public class JarUtils {

    public static GameCoordinator fillGameEntrance(Class<?> loadedClass, GameCoordinator gameCoordinator){
        GameEntrance instance = null;
        try {
            Class[] cArg = new Class[1]; //Our constructor has 1 arguments
            cArg[0] = GameCoordinator.class;
            instance = (GameEntrance) loadedClass.getDeclaredConstructor(cArg)
                    .newInstance(gameCoordinator);
            gameCoordinator.setGameEntrance(instance);
            return gameCoordinator;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }







}
