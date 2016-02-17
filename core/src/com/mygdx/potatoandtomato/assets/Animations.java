package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Animations implements IAssetFragment {
    @Override
    public void load() {

    }

    @Override
    public void dispose() {
        if(_loadingAnimation != null){
            _loadingAnimation.dispose();
            _loadingAnimation = null;
        }
    }

    @Override
    public void onLoaded() {

    }

    TextureAtlas _loadingAnimation;
    public Array<? extends TextureRegion> getLoadingAnimation() {
        if(_loadingAnimation == null){
            _loadingAnimation = new TextureAtlas(Gdx.files.internal("animations/loading.atlas"));;
        }
        return _loadingAnimation.getRegions();
    }
}
