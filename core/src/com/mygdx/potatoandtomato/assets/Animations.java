package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

import java.util.HashMap;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Animations implements IAssetFragment {

    private HashMap<Name, TextureAtlas> _animationAtlases;

    public Animations() {
        _animationAtlases = new HashMap<Name, TextureAtlas>();
    }

    @Override
    public void load() {

    }

    public void disposeAnimation(Name name){
        if(_animationAtlases.containsKey(name)){
            _animationAtlases.get(name).dispose();
            _animationAtlases.remove(name);
        }
    }

    @Override
    public void dispose() {
        for(Name name : _animationAtlases.keySet()){
            _animationAtlases.get(name).dispose();
        }
        _animationAtlases.clear();
    }

    @Override
    public void onLoaded() {

    }

    public Array<? extends TextureRegion> get(Name name){
        if(!_animationAtlases.containsKey(name)){
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("animations/" + name.name() + ".pack"));;
            _animationAtlases.put(name, atlas);
        }
        return _animationAtlases.get(name).getRegions();
    }

    public enum Name{
        LOADING,
        POTATO_BORING, POTATO_FAILED, POTATO_CRY, POTATO_HAPPY, POTATO_ANTICIPATE,
        TOMATO_BORING, TOMATO_FAILED, TOMATO_CRY, TOMATO_HAPPY, TOMATO_ANTICIPATE
    }


}
