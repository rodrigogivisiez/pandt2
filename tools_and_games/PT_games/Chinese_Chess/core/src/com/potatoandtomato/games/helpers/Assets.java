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
    private TextureRegion background,
                blackBgTrans, chessBoard, chessBoardBackground,
                chess, chessSelected, empty, yourTurn, enemyTurn,
                redJiangJun, blackJiangJun, youWin, youLose,
                redChe, redMa, redXiang, redPao, redBing, redShi, redShuai,
                 blackChe, blackMa, blackXiang, blackPao, blackBing, blackShi, blackShuai;


    private NinePatch nameTag;

    public Assets(GameCoordinator coordinator) {
        _manager = coordinator.getAssetManagerInstance();
    }

    public void loadAll(Runnable onFinish){
        FileHandleResolver resolver = new InternalFileHandleResolver();

        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        _manager.load(_packPath, TextureAtlas.class);
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
        chessBoard = getTextureRegion("chessBoard");
        chessBoardBackground = getTextureRegion("chessBoardBackground");
        chess = getTextureRegion("chess");
        chessSelected = getTextureRegion("chessSelected");
        empty = getTextureRegion("empty");

        redChe = getTextureRegion("redChe");
        redMa = getTextureRegion("redMa");
        redXiang = getTextureRegion("redXiang");
        redPao = getTextureRegion("redPao");
        redBing = getTextureRegion("redBing");
        redShi = getTextureRegion("redShi");
        redShuai = getTextureRegion("redShuai");

        blackChe = getTextureRegion("blackChe");
        blackMa = getTextureRegion("blackMa");
        blackXiang = getTextureRegion("blackXiang");
        blackPao = getTextureRegion("blackPao");
        blackBing = getTextureRegion("blackBing");
        blackShi = getTextureRegion("blackShi");
        blackShuai = getTextureRegion("blackShuai");

        yourTurn = getTextureRegion("yourTurn");
        enemyTurn = getTextureRegion("enemyTurn");

        redJiangJun = getTextureRegion("redJiangJun");
        blackJiangJun = getTextureRegion("blackJiangJun");
        youWin = getTextureRegion("youWin");
        youLose = getTextureRegion("youLose");
        nameTag = getPatch("nameTag");

    }

    public TextureRegion getYouLose() {
        return youLose;
    }

    public TextureRegion getYouWin() {
        return youWin;
    }

    public TextureRegion getBlackJiangJun() {
        return blackJiangJun;
    }

    public TextureRegion getRedJiangJun() {
        return redJiangJun;
    }

    public TextureRegion getEnemyTurn() {
        return enemyTurn;
    }

    public TextureRegion getYourTurn() {
        return yourTurn;
    }

    public NinePatch getNameTag() {
        return nameTag;
    }

    public TextureRegion getEmpty() {
        return empty;
    }

    public TextureRegion getRedChe() {
        return redChe;
    }

    public TextureRegion getRedMa() {
        return redMa;
    }

    public TextureRegion getRedXiang() {
        return redXiang;
    }

    public TextureRegion getRedPao() {
        return redPao;
    }

    public TextureRegion getRedBing() {
        return redBing;
    }

    public TextureRegion getRedShi() {
        return redShi;
    }

    public TextureRegion getRedShuai() {
        return redShuai;
    }

    public TextureRegion getBlackChe() {
        return blackChe;
    }

    public TextureRegion getBlackMa() {
        return blackMa;
    }

    public TextureRegion getBlackXiang() {
        return blackXiang;
    }

    public TextureRegion getBlackPao() {
        return blackPao;
    }

    public TextureRegion getBlackBing() {
        return blackBing;
    }

    public TextureRegion getBlackShi() {
        return blackShi;
    }

    public TextureRegion getBlackShuai() {
        return blackShuai;
    }

    public TextureRegion getChessBoardBackground() {
        return chessBoardBackground;
    }

    public TextureRegion getChessSelected() {
        return chessSelected;
    }

    public TextureRegion getChess() {
        return chess;
    }

    public TextureRegion getChessBoard() {
        return chessBoard;
    }

    public TextureRegion getBlackBgTrans() {
        return blackBgTrans;
    }

    public TextureRegion getBackground() {
        return background;
    }


























    private void loadAllFonts(){

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



}
