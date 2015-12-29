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

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Assets {

    private AssetManager _manager;
    TextureAtlas _pack;
    private String _packPath = "pack.atlas";
    private String _fontBoldPath = "fonts/helvetica_bold.ttf";
    private TextureRegion background, blackBg,
                blackBgTrans, redSide, yellowSide, vs,
                yellowPawn, redPawn, unknownPawn, pawnSelectedBase;
    private NinePatch yellowBox;
    private BitmapFont whiteBold2BlackS, blackBold1;

    public Assets() {
        _manager = new AssetManager();
    }

    public void loadAll(Runnable onFinish){
        FileHandleResolver resolver = new InternalFileHandleResolver();
        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        _manager.load(_packPath, TextureAtlas.class);
        loadOneFont(_fontBoldPath, "whiteBold2BlackS.ttf", Color.WHITE, 17, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontBoldPath, "blackBold1.ttf", Color.BLACK, 14);
        _manager.finishLoading();

        loadAllFonts();
        loadAllTextures();
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
        yellowPawn = getTextureRegion("pawn_yellow");
        redPawn = getTextureRegion("pawn_red");
        unknownPawn = getTextureRegion("pawn_unknown");
        pawnSelectedBase = getTextureRegion("pawn_selected");
    }

    private void loadAllFonts(){
        blackBold1 = _manager.get("blackBold1.ttf", BitmapFont.class);
        whiteBold2BlackS = _manager.get("whiteBold2BlackS.ttf", BitmapFont.class);
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

    public BitmapFont getWhiteBold2BlackS() {
        return whiteBold2BlackS;
    }

    public BitmapFont getBlackBold1() {
        return blackBold1;
    }

    public TextureRegion getPawnSelectedBase() {
        return pawnSelectedBase;
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
}
