package com.potatoandtomato.games.assets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.potatoandtomato.games.absint.IAssetFragment;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Patches implements IAssetFragment {

    private TextureAtlas _pack;

    private NinePatch yellowBox;

    @Override
    public void load() {

    }

    public void setPack(TextureAtlas _pack) {
        this._pack = _pack;
    }

    @Override
    public void onLoaded() {
        yellowBox = getPatch("yellow_gradient_box");
    }

    public NinePatch getYellowBox() {
        return yellowBox;
    }

    private NinePatch getPatch(String name){
        return _pack.createPatch(name);
    }

    @Override
    public void dispose() {

    }
}
