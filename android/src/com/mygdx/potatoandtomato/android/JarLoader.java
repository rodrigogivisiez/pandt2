package com.mygdx.potatoandtomato.android;

import android.content.Context;
import com.mygdx.potatoandtomato.absintflis.entrance.EntranceLoaderListener;
import com.mygdx.potatoandtomato.helpers.utils.JarUtils;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
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

    public void load(GameCoordinator gameCoordinator, EntranceLoaderListener listener) throws ClassNotFoundException {
        Class<?> loadedClass = null;

        loadedClass = loadClassDynamically(Terms.GAME_ENTRANCE,
                gameCoordinator.getJarPath(), context);

        JarUtils.fillGameEntrance(loadedClass, gameCoordinator, listener);
    }


    private Class<?> loadClassDynamically(final String fullClassName, String fullPathToApk, Context context) throws ClassNotFoundException, NullPointerException {

        if(!new File(fullPathToApk).exists()){
            throw new NullPointerException();
        }

        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);

        final DexClassLoader dexLoader = new DexClassLoader(fullPathToApk,
                dexOutputDir.getAbsolutePath(),
                null,
                context.getClassLoader());

        Class<?> loadedClass = null;

        loadedClass = Class.forName(fullClassName, true, dexLoader);

        return loadedClass;
    }

}
