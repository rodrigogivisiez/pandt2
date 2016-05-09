package com.mygdx.potatoandtomato.utils;

import com.mygdx.potatoandtomato.absintflis.entrance.EntranceLoaderListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameEntrance;
import com.potatoandtomato.common.utils.Threadings;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by SiongLeng on 25/8/2015.
 */
public class JarUtils {

    public static void fillGameEntrance(final Class<?> loadedClass,
                                                   final GameCoordinator gameCoordinator,
                                                   final EntranceLoaderListener listener) {
        final GameEntrance[] instance = {null};
        final Class[] cArg = new Class[1]; //Our constructor has 1 arguments
        cArg[0] = GameCoordinator.class;

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    instance[0] = (GameEntrance) loadedClass.getDeclaredConstructor(cArg)
                            .newInstance(gameCoordinator);
                    gameCoordinator.setGameEntrance(instance[0]);
                    listener.onLoadedSuccess();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    listener.onLoadedFailed();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    listener.onLoadedFailed();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    listener.onLoadedFailed();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    listener.onLoadedFailed();
                }
            }
        });
    }







}
