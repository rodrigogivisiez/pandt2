package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

import java.util.HashMap;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Textures implements IAssetFragment {

    private AssetManager _manager;
    private TextureAtlas _UIPack;
    private String _path = "ui_pack.atlas";
    private HashMap<String, TextureRegion> _regions;

    public Textures(AssetManager _manager) {
        this._manager = _manager;
        _regions = new HashMap<String, TextureRegion>();
    }

    @Override
    public void load() {
        _manager.load(_path, TextureAtlas.class);
    }

    @Override
    public void dispose() {

    }

    public TextureRegion get(Name name){
        return _regions.get(name.name());
    }

    @Override
    public void onLoaded() {
        _UIPack = _manager.get(_path, TextureAtlas.class);

        for(TextureAtlas.AtlasRegion textureRegion : _UIPack.getRegions()){
            if(textureRegion.splits == null){
                _regions.put(textureRegion.name, textureRegion);
            }
        }
    }

    public TextureAtlas getUIPack() {
        return _UIPack;
    }

    public enum Name{
        BLUE_BG, AUTUMN_BG, SUNRISE, SUNRAY, LOGO_NO_WEAPON, LOGO_POTATO_WEAPON, LOGO_TOMATO_WEAPON, GREEN_GROUND, AUTUMN_GROUND,
        TOP_BAR_BG, TRANS_WHITE_BG, TRANS_BLACK_BG, WHITE_ROUND_BUTTON_BG,
        UPRIGHT_EGG_BUTTON, DOWNWARD_EGG_BUTTON,
        TOMATO_HI, POTATO_HI, LOGGING_IN_MASCOTS,
        PLAY_ICON, FACEBOOK_ICON, TICK_ICON, CROSS_ICON, QUIT_ICON, BACK_ICON, SETTINGS_ICON, RATE_ICON,
        EXPANDED_ICON, COLLAPSED_ICON, KICK_ICON, INVITED_ICON, CLOSE_KEYBOARD_ICON, POINT_LEFT_ICON, VOICE_ICON,
        MIC_BIG_ICON, MIC_ICON, DOWNLOAD_ICON, UNKNOWN_ICON, BULLET_ICON,
        NO_IMAGE, EMPTY,
        WOOD_BG_SMALL, WOOD_BG_TALL, WOOD_BG_FAT, WOOD_BG_NORMAL, WOOD_BG_TITLE, WOOD_SEPARATOR_HORIZONTAL,
        WHITE_VERTICAL_LINE, ORANGE_HORIZONTAL_LINE, GREY_HORIZONTAL_LINE, WHITE_HORIZONTAL_LINE,
        CURSOR_BLACK,
        GAMELIST_HIGHLIGHT, GAMELIST_TITLE_BG,
        KEYBOARD_BUTTON, MIC_BUTTON,
        LOADING_IMAGE, LOADING_PAGE,
        CHAT_CONTAINER,
        SELECT_BOX, UNSELECT_BOX
    }





}
