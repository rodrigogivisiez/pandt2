package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
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
    private TextureRegion background, topBackground,
                blackBgTrans, chessBoard, chessBoardBackground;


    private BitmapFont whiteBold2BlackS, blackBold1,
            blackNormal1Green, greyPizza4BlackS,
            orangePizza5BlackS, greyPizza5BlackS, whitePizza2BlackS;

    private NinePatch redBox;

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
        _manager.finishLoading();

        loadAllFonts();
        loadAllTextures();
        //basicNinePatchLoaded();

        if(onFinish != null) onFinish.run();

    }


    private void loadAllTextures(){
        _pack = _manager.get(_packPath, TextureAtlas.class);
        background = getTextureRegion("background");
        blackBgTrans = getTextureRegion("blackBgTrans");
        topBackground = getTextureRegion("topBackground");
        chessBoard = getTextureRegion("chessBoard");
        chessBoardBackground = getTextureRegion("chessBoardBackground");

        redBox = getPatch("redBox");
    }

    public TextureRegion getChessBoardBackground() {
        return chessBoardBackground;
    }

    public TextureRegion getChessBoard() {
        return chessBoard;
    }

    public TextureRegion getTopBackground() {
        return topBackground;
    }

    public TextureRegion getBlackBgTrans() {
        return blackBgTrans;
    }

    public TextureRegion getBackground() {
        return background;
    }

    public NinePatch getRedBox() {
        return redBox;
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


}
