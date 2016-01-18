package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.potatoandtomato.common.GameCoordinator;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Assets {

    private AssetManager _manager;
    TextureAtlas _pack;
    private String _packPath = "pack.atlas";
    private String _fontBoldPath = "fonts/helvetica_bold.ttf";
    private String _fontRegularPath = "fonts/helvetica_regular.ttf";
    private String _fontPizzaPath = "fonts/pizza.ttf";
    private TextureRegion background, blackBg,
                blackBgTrans, redSide, yellowSide, vs,
                yellowPawn, redPawn, unknownPawn, yellowPawnSelected, redPawnSelected, unknownPawnSelected,
                empty, glowingTile, greenTile, redTile, arrowLeft, arrowRight, arrowUp, arrowDown,
                redCat, redWolf, redTiger, redElephant, redMouse, redLion, redDog,
                 yellowCat, yellowWolf, yellowTiger, yellowElephant, yellowMouse, yellowLion, yellowDog,
                battleCloud, battleEffect,
                greyBg;



    private NinePatch yellowBox;
    private BitmapFont whiteBold2BlackS, blackBold1,
            blackNormal1Green, greyPizza4BlackS,
            orangePizza5BlackS, greyPizza5BlackS, whitePizza2BlackS;

    public Assets(GameCoordinator coordinator) {
        _manager = coordinator.getAssetManagerInstance();
    }

    public void loadAll(Runnable onFinish){
        FileHandleResolver resolver = new InternalFileHandleResolver();

        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        _manager.load(_packPath, TextureAtlas.class);
        loadOneFont(_fontBoldPath, "whiteBold2BlackS.ttf", Color.WHITE, 17, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontBoldPath, "blackBold1.ttf", Color.BLACK, 14);
        loadOneFont(_fontRegularPath, "blackNormal1Green.ttf", Color.BLACK, 12, 1, Color.valueOf("588e54"), 0, Color.GRAY);
        loadOneFont(_fontPizzaPath, "greyPizza4BlackS.ttf", Color.valueOf("e9e9e9"), 30, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "greyPizza5BlackS.ttf", Color.valueOf("e9e9e9"), 50, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "orangePizza5BlackS.ttf", Color.valueOf("fbbb31"), 50, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "whitePizza2BlackS.ttf", Color.WHITE, 20, 1, Color.BLACK, 1, Color.GRAY);
        loadAllSounds();
        _manager.finishLoading();

        loadAllFonts();
        loadAllTextures();
        soundsLoaded();
        //basicNinePatchLoaded();

        if(onFinish != null) onFinish.run();

    }


    private void loadAllTextures(){
        _pack = _manager.get(_packPath, TextureAtlas.class);
        background = getTextureRegion("bg");
        blackBg = getTextureRegion("black_bg");
        yellowSide = getTextureRegion("yellow_side");
        redSide = getTextureRegion("red_side");
        blackBgTrans = getTextureRegion("black_bg_trans");
        vs = getTextureRegion("vs");
        yellowBox = getPatch("yellow_gradient_box");
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
    }

    private void loadAllFonts(){
        blackBold1 = _manager.get("blackBold1.ttf", BitmapFont.class);
        whiteBold2BlackS = _manager.get("whiteBold2BlackS.ttf", BitmapFont.class);
        blackNormal1Green = _manager.get("blackNormal1Green.ttf", BitmapFont.class);
        greyPizza4BlackS = _manager.get("greyPizza4BlackS.ttf", BitmapFont.class);
        greyPizza5BlackS = _manager.get("greyPizza5BlackS.ttf", BitmapFont.class);
        orangePizza5BlackS = _manager.get("orangePizza5BlackS.ttf", BitmapFont.class);
        whitePizza2BlackS = _manager.get("whitePizza2BlackS.ttf", BitmapFont.class);
    }

    private NinePatch getPatch(String name){
        return _pack.createPatch(name);
    }

    private TextureRegion getTextureRegion(String name){
        return _pack.findRegion(name);
    }

    private void loadOneFont(String path, String name, Color color, int size){
        loadOneFont(path, name, color, size, 0, Color.BLACK, 0, Color.BLACK);
    }

    private void loadOneFont(String path, String name, Color color, int size, int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = path;
        size2Params.fontParameters.size = size;
        size2Params.fontParameters.color = color;
        if(borderWidth != 0){
            size2Params.fontParameters.borderWidth = borderWidth;
            size2Params.fontParameters.borderColor = borderColor;
        }
        if(shadowOffset != 0){
            size2Params.fontParameters.shadowColor = shadowColor;
            size2Params.fontParameters.shadowOffsetX = shadowOffset;
            size2Params.fontParameters.shadowOffsetY = shadowOffset;
        }
        _manager.load(name, BitmapFont.class, size2Params);
    }

    public BitmapFont getWhitePizza2BlackS() {
        return whitePizza2BlackS;
    }

    public BitmapFont getGreyPizza5BlackS() {
        return greyPizza5BlackS;
    }

    public BitmapFont getOrangePizza5BlackS() {
        return orangePizza5BlackS;
    }

    public BitmapFont getGreyPizza4BlackS() {
        return greyPizza4BlackS;
    }

    public BitmapFont getWhiteBold2BlackS() {
        return whiteBold2BlackS;
    }

    public BitmapFont getBlackBold1() {
        return blackBold1;
    }

    public BitmapFont getBlackNormal1Green() {
        return blackNormal1Green;
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

    public NinePatch getYellowBox() {
        return yellowBox;
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


    private Music themeMusic;
    private Sound fightChessSound, flipChessSound, lossSound, moveSound, openSlideSound, startGameSound, winSound;


    private void loadAllSounds(){
        _manager.load("sounds/theme.mp3", Music.class);
        _manager.load("sounds/fight_chess.ogg", Sound.class);
        _manager.load("sounds/flip_chess.ogg", Sound.class);
        _manager.load("sounds/lose.ogg", Sound.class);
        _manager.load("sounds/move.ogg", Sound.class);
        _manager.load("sounds/open_slide.ogg", Sound.class);
        _manager.load("sounds/start_game.ogg", Sound.class);
        _manager.load("sounds/win.ogg", Sound.class);
    }


    private void soundsLoaded(){
        themeMusic = _manager.get("sounds/theme.mp3", Music.class);
        fightChessSound  = _manager.get("sounds/fight_chess.ogg", Sound.class);
        flipChessSound = _manager.get("sounds/flip_chess.ogg", Sound.class);
        lossSound = _manager.get("sounds/lose.ogg", Sound.class);
        moveSound = _manager.get("sounds/move.ogg", Sound.class);
        openSlideSound = _manager.get("sounds/open_slide.ogg", Sound.class);
        startGameSound = _manager.get("sounds/start_game.ogg", Sound.class);
        winSound = _manager.get("sounds/win.ogg", Sound.class);
    }

    public Sound getWinSound() {
        return winSound;
    }

    public Sound getStartGameSound() {
        return startGameSound;
    }

    public Sound getOpenSlideSound() {
        return openSlideSound;
    }

    public Sound getMoveSound() {
        return moveSound;
    }

    public Sound getLossSound() {
        return lossSound;
    }

    public Sound getFlipChessSound() {
        return flipChessSound;
    }

    public Sound getFightChessSound() {
        return fightChessSound;
    }

    public Music getThemeMusic() {
        return themeMusic;
    }
}
