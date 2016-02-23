package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

import java.util.HashMap;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Patches{

    private HashMap<String, NinePatch> _patches;

    public Patches() {
        _patches = new HashMap<String, NinePatch>();
    }

    public NinePatch get(Name name){
        return _patches.get(name.name());
    }

    public void onLoaded(TextureAtlas UIPack) {
        for(TextureAtlas.AtlasRegion region : UIPack.getRegions()){
            if(region.splits != null){
                _patches.put(region.name, UIPack.createPatch(region.name));
            }
        }
    }

    public enum Name{
        CHAT_BOX, POPUP_BG,
        BTN_GREEN, BTN_BLUE,
        WHITE_ROUNDED_BG, TRANS_BLACK_ROUNDED_BG, EXPANDABLE_TITLE_BG,
        YELLOW_GRADIENT_BOX, YELLOW_GRADIENT_BOX_ROUNDED,
        SCROLLBAR_VERTICAL_HANDLE, WOOD_BG_SMALL_PATCH, WOOD_BG_FAT_PATCH,
        GAMELIST_BG, TEXT_FIELD_BG
    }

}
