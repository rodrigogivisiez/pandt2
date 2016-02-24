package com.potatoandtomato.games.assets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.potatoandtomato.games.absint.IAssetFragment;

import java.util.HashMap;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Patches implements IAssetFragment {

    private TextureAtlas _pack;
    private HashMap<String, NinePatch> _patches;

    public Patches() {
        _patches = new HashMap<String, NinePatch>();
    }

    @Override
    public void load() {

    }

    public void setPack(TextureAtlas _pack) {
        this._pack = _pack;
    }

    @Override
    public void onLoaded() {
        for(TextureAtlas.AtlasRegion region : _pack.getRegions()){
            if(region.splits != null){
                _patches.put(region.name, _pack.createPatch(region.name));
            }
        }
    }

    public NinePatch get(Name name) {
        return _patches.get(name.name());
    }

    public enum Name{
        YELLOW_GRADIENT_BOX
    }

}
