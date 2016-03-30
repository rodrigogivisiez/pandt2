package com.potatoandtomato.common.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.absints.IAssetFragment;

import java.util.HashMap;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public abstract class TextureAssets implements IAssetFragment {

    private AssetManager _manager;
    private TextureAtlas _UIPack;
    private String _path;
    private HashMap<String, TextureRegion> _regions;

    public TextureAssets(AssetManager _manager, String packPath) {
        this._manager = _manager;
        this._path = packPath;
        _regions = new HashMap<String, TextureRegion>();
    }

    @Override
    public void load() {
        _manager.load(_path, TextureAtlas.class);
    }

    @Override
    public void dispose() {

    }

    public TextureRegion get(Object object){
        return _regions.get(object.toString());
    }

    @Override
    public void onLoaded() {
        _UIPack = _manager.get(_path, TextureAtlas.class);

        for(TextureAtlas.AtlasRegion textureRegion : _UIPack.getRegions()){
            if(textureRegion.splits == null){
                _regions.put(textureRegion.name, textureRegion);
            }
        }
    }

    public TextureAtlas getUIPack() {
        return _UIPack;
    }


}
