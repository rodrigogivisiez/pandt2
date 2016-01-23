package com.potatoandtomato.common;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by SiongLeng on 21/1/2016.
 */
public class AssetsWrapper implements Disposable {

    public AssetManager _manager;

    public AssetsWrapper(FileHandleResolver resolver) {
        this._manager = new AssetManager(resolver);
    }

    public AssetManager getAssetManager() {
        return _manager;
    }

    @Override
    public void dispose() {
        _manager.dispose();
    }
}
