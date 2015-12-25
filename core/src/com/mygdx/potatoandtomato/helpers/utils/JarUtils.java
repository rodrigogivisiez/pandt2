package com.mygdx.potatoandtomato.helpers.utils;

import com.potatoandtomato.common.GameLibCoordinator;
import com.potatoandtomato.common.GameEntranceAbstract;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by SiongLeng on 25/8/2015.
 */
public class JarUtils {

    public static GameLibCoordinator fillGameEntrance(Class<?> loadedClass, GameLibCoordinator gameLibCoordinator){
        GameEntranceAbstract instance = null;
        try {
            Class[] cArg = new Class[1]; //Our constructor has 3 arguments
            cArg[0] = GameLibCoordinator.class;
            instance = (GameEntranceAbstract) loadedClass.getDeclaredConstructor(cArg)
                    .newInstance(gameLibCoordinator);
            gameLibCoordinator.setGameEntrance(instance);
            return gameLibCoordinator;

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
