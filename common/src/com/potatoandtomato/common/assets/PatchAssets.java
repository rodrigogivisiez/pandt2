package com.potatoandtomato.common.assets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public abstract class PatchAssets implements Disposable {

    private HashMap<String, NinePatch> _patches;

    public PatchAssets() {
        _patches = new HashMap<String, NinePatch>();
    }

    public NinePatch get(Object object){
        return _patches.get(object.toString());
    }

    public void onLoaded(TextureAtlas UIPack) {
        for(TextureAtlas.AtlasRegion region : UIPack.getRegions()){
            if(region.splits != null){
                _patches.put(region.name, UIPack.createPatch(region.name));
            }
        }
    }

    @Override
    public void dispose() {
        _patches.clear();
    }
}
