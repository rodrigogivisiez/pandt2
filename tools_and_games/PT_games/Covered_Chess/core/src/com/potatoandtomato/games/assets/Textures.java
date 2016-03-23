package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.absint.IAssetFragment;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;

import java.util.HashMap;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Textures implements IAssetFragment {

    private AssetManager _manager;
    private String _packPath = "pack.atlas";
    private TextureAtlas _pack;
    private HashMap<String, TextureRegion> _regions;

    public Textures(AssetManager _manager) {
        this._manager = _manager;
        _regions = new HashMap<String, TextureRegion>();
    }

    public TextureRegion get(Name name){
        return _regions.get(name.name());
    }


    @Override
    public void load() {
        _manager.load(_packPath, TextureAtlas.class);
    }

    @Override
    public void onLoaded() {

        _pack = _manager.get(_packPath, TextureAtlas.class);

        for(TextureAtlas.AtlasRegion textureRegion : _pack.getRegions()){
            if(textureRegion.splits == null){
                _regions.put(textureRegion.name, textureRegion);
            }
        }
    }

    public TextureRegion getAnimalByType(ChessType chessType){
        String chessTypeString = chessType.name();
        return _regions.get(chessTypeString);
    }

    public TextureRegion getStatus(Status status){
        return _regions.get("STATUS_" + status.name());
    }



    public TextureAtlas getPack() {
        return _pack;
    }

    public enum Name{
        ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_BOTTOM_LEFT, ARROW_TOP_LEFT, ARROW_BOTTOM_RIGHT, ARROW_TOP_RIGHT,
        GREEN_TILE, RED_TILE, GLOWING_TILE,
        SUDDEN_DEATH_GAME_BG, GAME_BG, TRANS_DARK_BROWN_ROUNDED_BG, GRAVE_BG, TRANS_BLACK_BG, BLACK_BG, SPLASH_BG, FULL_BLACK,
        POINT_LEFT_ICON, POINT_RIGHT_ICON, PREVIEW_ICON,
        GLOW_CHESS, RED_CHESS_TOTAL, YELLOW_CHESS_TOTAL,
        BATTLE_EFFECT, BATTLE_CLOUD,
        EMPTY,
        YELLOW_CHESS, RED_CHESS, UNKNOWN_CHESS, YELLOW_CHESS_SELECTED, RED_CHESS_SELECTED, UNKNOWN_CHESS_SELECTED,
        PREVIEW_CHESS,
        YELLOW_LION_SPLASH, RED_LION_SPLASH, YELLOW_TIGER_SPLASH, RED_TIGER_SPLASH, YELLOW_WOLF_SPLASH, RED_WOLF_SPLASH,
        CRACK,
        YOU_LOSE_YELLOW, YOU_WIN_YELLOW, YOU_LOSE_RED, YOU_WIN_RED
    }



}
