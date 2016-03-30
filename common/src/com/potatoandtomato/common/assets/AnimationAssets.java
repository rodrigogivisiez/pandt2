package com.potatoandtomato.common.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.absints.IAssetFragment;

import java.util.HashMap;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public abstract class AnimationAssets implements IAssetFragment {

    private HashMap<String, TextureAtlas> _animationAtlases;
    private AssetManager _assetManager;

    public AnimationAssets(AssetManager assetManager) {
        this._assetManager = assetManager;
        _animationAtlases = new HashMap<String, TextureAtlas>();
    }

    @Override
    public void load() {

    }

    public void disposeAnimation(String name){
        if(_animationAtlases.containsKey(name)){
            _animationAtlases.get(name).dispose();
            _animationAtlases.remove(name);
        }
    }

    @Override
    public void dispose() {
        for(String name : _animationAtlases.keySet()){
            _animationAtlases.get(name).dispose();
        }
        _animationAtlases.clear();
    }

    @Override
    public void onLoaded() {

    }

    public Array<? extends TextureRegion> get(Object object){
        String name = object.toString();
        if(!_animationAtlases.containsKey(name)){
            TextureAtlas atlas = new TextureAtlas(_assetManager.getFileHandleResolver().resolve("animations/" + name + ".pack"));;
            _animationAtlases.put(name, atlas);
        }
        return _animationAtlases.get(name).getRegions();
    }

}
