package com.mygdx.potatoandtomato.android;

import android.content.Context;
import com.mygdx.potatoandtomato.helpers.utils.JarUtils;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.GameLibCoordinator;
import dalvik.system.DexClassLoader;

import java.io.File;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class JarLoader {

    private Context context;

    public JarLoader(Context context) {
        this.context = context;
    }

    public GameLibCoordinator load(GameLibCoordinator gameLibCoordinator) throws ClassNotFoundException {
        Class<?> loadedClass = null;

        loadedClass = loadClassDynamically(Terms.GAME_ENTRANCE,
                gameLibCoordinator.getJarPath(), context);

        return JarUtils.fillGameEntrance(loadedClass, gameLibCoordinator);
    }


    private Class<?> loadClassDynamically(String fullClassName, String fullPathToApk, Context context) throws ClassNotFoundException {

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
