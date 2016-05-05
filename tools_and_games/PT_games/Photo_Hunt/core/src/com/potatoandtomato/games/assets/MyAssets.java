package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.*;

/**
 * Created by SiongLeng on 30/3/2016.
 */
public class MyAssets extends Assets {

    private Textures textures;
    private Sounds sounds;

    public MyAssets(PTAssetsManager manager, FontAssets fontAssets, AnimationAssets animationAssets,
                    Sounds soundAssets, PatchAssets patchAssets, Textures textureAssets) {
        super(manager, fontAssets, animationAssets, soundAssets, patchAssets, textureAssets);
        this.textures = textureAssets;
        this.sounds = soundAssets;
    }

    @Override
    public Textures getTextures() {
        return textures;
    }

    @Override
    public Sounds getSounds() {
        return sounds;
    }
}
