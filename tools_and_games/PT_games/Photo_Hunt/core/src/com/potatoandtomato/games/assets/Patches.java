package com.potatoandtomato.games.assets;

import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.PatchAssets;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Patches extends PatchAssets {


    public Patches(PTAssetsManager assetsManager) {
        super(assetsManager);
    }

    public enum Name{
        WHITE_ROUNDED_BG, RED_ARROW, BLACK_TRANS_BG, GAME_OVER_TRANS_BG,
    }

}
