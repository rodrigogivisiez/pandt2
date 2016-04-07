package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.assets.*;

/**
 * Created by SiongLeng on 30/3/2016.
 */
public class MyAssets extends Assets {

    private Textures textures;

    public MyAssets(AssetManager manager, FontAssets fontAssets, AnimationAssets animationAssets,
                                SoundAssets soundAssets, PatchAssets patchAssets, Textures textureAssets) {
        super(manager, fontAssets, animationAssets, soundAssets, patchAssets, textureAssets);
        this.textures = textureAssets;
    }

    @Override
    public Textures getTextures() {
        return textures;
    }
}
