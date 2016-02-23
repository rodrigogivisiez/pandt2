package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.absint.IAssetFragment;
import com.potatoandtomato.games.enums.ChessType;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Textures implements IAssetFragment {

    private AssetManager _manager;
    private String _packPath = "pack.atlas";
    private TextureAtlas _pack;

    private TextureRegion background, blackBg,
            blackBgTrans, redSide, yellowSide, vs,
            yellowPawn, redPawn, unknownPawn, yellowPawnSelected, redPawnSelected, unknownPawnSelected,
            empty, glowingTile, greenTile, redTile, arrowLeft, arrowRight, arrowUp, arrowDown,
            redCat, redWolf, redTiger, redElephant, redMouse, redLion, redDog,
            yellowCat, yellowWolf, yellowTiger, yellowElephant, yellowMouse, yellowLion, yellowDog,
            battleCloud, battleEffect,
            greyBg, selectedBase, redChessTotal, yellowChessTotal, glowChess,
            pointLeft, pointRight, darkBrownBgRounded;

    public Textures(AssetManager _manager) {
        this._manager = _manager;
    }

    @Override
    public void load() {
        _manager.load(_packPath, TextureAtlas.class);
    }

    @Override
    public void onLoaded() {
        _pack = _manager.get(_packPath, TextureAtlas.class);
        background = getTextureRegion("bg");
        blackBg = getTextureRegion("black_bg");
        yellowSide = getTextureRegion("yellow_side");
        redSide = getTextureRegion("red_side");
        blackBgTrans = getTextureRegion("black_bg_trans");
        vs = getTextureRegion("vs");
        yellowPawn = getTextureRegion("yellow_pawn");
        redPawn = getTextureRegion("red_pawn");
        unknownPawn = getTextureRegion("unknown_pawn");
        yellowPawnSelected = getTextureRegion("yellow_pawn_selected");
        redPawnSelected = getTextureRegion("red_pawn_selected");
        unknownPawnSelected = getTextureRegion("unknown_pawn_selected");
        empty = getTextureRegion("empty");
        glowingTile = getTextureRegion("glowing");
        greenTile = getTextureRegion("green_tile");
        redTile = getTextureRegion("red_tile");
        arrowDown = getTextureRegion("arrow_down");
        arrowLeft = getTextureRegion("arrow_left");
        arrowUp = getTextureRegion("arrow_up");
        arrowRight = getTextureRegion("arrow_right");

        redCat = getTextureRegion("cat_red");
        redWolf = getTextureRegion("wolf_red");
        redTiger = getTextureRegion("tiger_red");
        redElephant = getTextureRegion("elephant_red");
        redMouse = getTextureRegion("mouse_red");
        redLion = getTextureRegion("lion_red");
        redDog = getTextureRegion("dog_red");

        yellowCat = getTextureRegion("cat_yellow");
        yellowWolf = getTextureRegion("wolf_yellow");
        yellowTiger = getTextureRegion("tiger_yellow");
        yellowElephant = getTextureRegion("elephant_yellow");
        yellowMouse = getTextureRegion("mouse_yellow");
        yellowLion = getTextureRegion("lion_yellow");
        yellowDog = getTextureRegion("dog_yellow");

        battleCloud = getTextureRegion("battle_cloud");
        battleEffect = getTextureRegion("battle_effects");

        greyBg = getTextureRegion("grey_bg");

        redChessTotal = getTextureRegion("red_chess_total");
        yellowChessTotal = getTextureRegion("yellow_chess_total");

        glowChess = getTextureRegion("glow_chess");

        pointLeft = getTextureRegion("point_left");
        pointRight = getTextureRegion("point_right");
        darkBrownBgRounded = getTextureRegion("dark_brown_bg_rounded");
    }

    public TextureRegion getDarkBrownBgRounded() {
        return darkBrownBgRounded;
    }

    public TextureRegion getPointRight() {
        return pointRight;
    }

    public TextureRegion getPointLeft() {
        return pointLeft;
    }

    public TextureRegion getGlowChess() {
        return glowChess;
    }

    public TextureRegion getRedChessTotal() {
        return redChessTotal;
    }

    public TextureRegion getYellowChessTotal() {
        return yellowChessTotal;
    }

    public TextureRegion getGreyBg() {
        return greyBg;
    }

    public TextureRegion getBattleEffect() {
        return battleEffect;
    }

    public TextureRegion getBattleCloud() {
        return battleCloud;
    }

    public TextureRegion getRedCat() {
        return redCat;
    }

    public TextureRegion getRedWolf() {
        return redWolf;
    }

    public TextureRegion getRedTiger() {
        return redTiger;
    }

    public TextureRegion getRedElephant() {
        return redElephant;
    }

    public TextureRegion getRedMouse() {
        return redMouse;
    }

    public TextureRegion getRedLion() {
        return redLion;
    }

    public TextureRegion getRedDog() {
        return redDog;
    }

    public TextureRegion getYellowCat() {
        return yellowCat;
    }

    public TextureRegion getYellowWolf() {
        return yellowWolf;
    }

    public TextureRegion getYellowTiger() {
        return yellowTiger;
    }

    public TextureRegion getYellowElephant() {
        return yellowElephant;
    }

    public TextureRegion getYellowMouse() {
        return yellowMouse;
    }

    public TextureRegion getYellowLion() {
        return yellowLion;
    }

    public TextureRegion getYellowDog() {
        return yellowDog;
    }

    public TextureRegion getArrowLeft() {
        return arrowLeft;
    }

    public TextureRegion getArrowRight() {
        return arrowRight;
    }

    public TextureRegion getArrowUp() {
        return arrowUp;
    }

    public TextureRegion getArrowDown() {
        return arrowDown;
    }

    public TextureRegion getGlowingTile() {
        return glowingTile;
    }

    public TextureRegion getGreenTile() {
        return greenTile;
    }

    public TextureRegion getRedTile() {
        return redTile;
    }

    public TextureRegion getYellowPawnSelected() {
        return yellowPawnSelected;
    }

    public TextureRegion getRedPawnSelected() {
        return redPawnSelected;
    }

    public TextureRegion getUnknownPawnSelected() {
        return unknownPawnSelected;
    }

    public TextureRegion getEmpty() {
        return empty;
    }

    public TextureRegion getYellowPawn() {
        return yellowPawn;
    }

    public TextureRegion getRedPawn() {
        return redPawn;
    }

    public TextureRegion getUnknownPawn() {
        return unknownPawn;
    }

    public TextureRegion getBlackBgTrans() {
        return blackBgTrans;
    }

    public TextureRegion getBackground() {
        return background;
    }

    public TextureRegion getBlackBg() {
        return blackBg;
    }

    public TextureRegion getRedSide() {
        return redSide;
    }

    public TextureRegion getYellowSide() {
        return yellowSide;
    }

    public TextureRegion getVs() {
        return vs;
    }

    public TextureRegion getAnimalByType(ChessType chessType){
        String chessTypeString = chessType.name();
        if(chessTypeString.endsWith("ELEPHANT")){
            if(chessTypeString.startsWith("RED")) return getRedElephant();
            else return getYellowElephant();
        }
        else if(chessTypeString.endsWith("MOUSE")){
            if(chessTypeString.startsWith("RED")) return getRedMouse();
            else return getYellowMouse();
        }
        else if(chessTypeString.endsWith("CAT")){
            if(chessTypeString.startsWith("RED")) return getRedCat();
            else return getYellowCat();
        }
        else if(chessTypeString.endsWith("DOG")){
            if(chessTypeString.startsWith("RED")) return getRedDog();
            else return getYellowDog();
        }
        else if(chessTypeString.endsWith("LION")){
            if(chessTypeString.startsWith("RED")) return getRedLion();
            else return getYellowLion();
        }
        else if(chessTypeString.endsWith("TIGER")){
            if(chessTypeString.startsWith("RED")) return getRedTiger();
            else return getYellowTiger();
        }
        else if(chessTypeString.endsWith("WOLF")){
            if(chessTypeString.startsWith("RED")) return getRedWolf();
            else return getYellowWolf();
        }
        else{
            return null;
        }
    }
    
    
    private TextureRegion getTextureRegion(String name){
        return _pack.findRegion(name);
    }

    
    
    public TextureAtlas getPack() {
        return _pack;
    }

    
    
    @Override
    public void dispose() {

    }
}
