package com.mygdx.potatoandtomato.android;

import android.content.Context;
import com.mygdx.potatoandtomato.helpers.utils.JarUtils;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.GameCoordinator;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class JarLoader {

    private Context context;

    public JarLoader(Context context) {
        this.context = context;
    }

    public GameCoordinator load(GameCoordinator gameCoordinator) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?> loadedClass = null;

        loadedClass = loadClassDynamically(Terms.GAME_ENTRANCE,
                gameCoordinator.getJarPath(), context);

        return JarUtils.fillGameEntrance(loadedClass, gameCoordinator);
    }


    private Class<?> loadClassDynamically(String fullClassName, String fullPathToApk, Context context) throws ClassNotFoundException, NullPointerException {

        if(!new File(fullPathToApk).exists()){
            throw new NullPointerException();
        }

        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);

        DexClassLoader dexLoader = new DexClassLoader(fullPathToApk,
                dexOutputDir.getAbsolutePath(),
                null,
                context.getClassLoader());

        Class<?> loadedClass = null;

        loadedClass = Class.forName(fullClassName, true, dexLoader);

        return loadedClass;
    }

}
