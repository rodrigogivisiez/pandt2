package com.potatoandtomato.common.utils;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.potatoandtomato.common.GameCoordinator;

/**
 * Created by SiongLeng on 2/1/2016.
 */
public class MyFileResolver implements FileHandleResolver {

    private GameCoordinator _coordinator;

    public MyFileResolver(GameCoordinator coordinator) {
        _coordinator = coordinator;
    }

    @Override
    public FileHandle resolve(String fileName) {
        return _coordinator.getFileH(fileName);
    }
}
