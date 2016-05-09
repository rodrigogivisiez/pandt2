package com.potatoandtomato.common.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.absints.IAssetFragment;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.utils.Pair;

import java.util.HashMap;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public abstract class TextureAssets implements IAssetFragment {

    private PTAssetsManager _manager;
    private TextureAtlas _UIPack;
    private String _path;
    private HashMap<String, TextureRegion> _regions;
    private HashMap<String, Pair<Integer, Texture>> _miniRefCountMap;

    public TextureAssets(PTAssetsManager _manager, String packPath) {
        this._manager = _manager;
        this._path = packPath;
        _regions = new HashMap<String, TextureRegion>();
        _miniRefCountMap = new HashMap();
    }

    @Override
    public void load() {
        _manager.load(_path, TextureAtlas.class);
    }

    @Override
    public void dispose() {
        for(Pair<Integer, Texture> pair : _miniRefCountMap.values()){
            pair.getSecond().dispose();
        }
        _miniRefCountMap.clear();
        _regions.clear();
    }

    public TextureRegion get(Object object){
        return _regions.get(object.toString());
    }

    public void addRef(String name, Texture texture){
        if(_miniRefCountMap.containsKey(name)){
            _miniRefCountMap.get(name).setFirst(_miniRefCountMap.get(name).getFirst() + 1);
        }
        else{
            _miniRefCountMap.put(name, new Pair<Integer, Texture>(1, texture));
        }
    }

    public void removeRef(String name){
        if(_miniRefCountMap.containsKey(name)){
            int currentRefCount =  _miniRefCountMap.get(name).getFirst();
            currentRefCount--;
            if(currentRefCount <= 0){
                _miniRefCountMap.get(name).getSecond().dispose();
                _miniRefCountMap.remove(name);
            }
            else{
                _miniRefCountMap.get(name).setFirst(currentRefCount);
            }
        }
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
