package com.mygdx.potatoandtomato.assets;

import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.*;

/**
 * Created by SiongLeng on 5/7/2016.
 */
public class MyAssets extends Assets {

    private Textures textures;

    public MyAssets(PTAssetsManager manager, FontAssets fontAssets, AnimationAssets animationAssets, SoundAssets soundAssets, PatchAssets patchAssets, Textures textureAssets) {
        super(manager, fontAssets, animationAssets, soundAssets, patchAssets, textureAssets);

        this.textures = textureAssets;
    }

    @Override
    public Textures getTextures() {
        return textures;
    }
}
