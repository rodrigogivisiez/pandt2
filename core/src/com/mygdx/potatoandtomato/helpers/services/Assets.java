package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class Assets {

    AssetManager _manager;

    TextureAtlas _backgroundsPack;
    TextureAtlas _potatoHiAnimation, _tomatoHiAnimation;
    private String _backgroundPackPath = "ui_pack.atlas";
    private String _fontNormalPath = "fonts/helvetica_regular.ttf";
    private String _fontBoldPath = "fonts/helvetica_bold.ttf";
    private String _fontPizzaPath = "fonts/pizza.ttf";

    private TextureRegion blueBg, autumnBg, sunrise, sunray, logoNoWeapon, logoPotatoWeapon,
            logoTomatoWeapon, greenGround, autumnGround, uprightEggButton, playIcon, empty,
            tomatoHi, potatoHi, socialIcon, loginIcon, tick, cross, woodBgSmall, woodBgTall, woodBgNormal,
            woodBgTitle, downwardEggButton, topBarBg, quitIcon, gameListHighlight, gameListTitleSeparator,
            gameListBg, gameListTitleBg, btnWhiteRound, blackBg, ratingIcon, settingsIcon, tomatoIcon,
            potatoIcon, unknownMascotIcon, backIcon, infoIcon, importantIcon, comingSoon, noImage,
            pointLeftIcon, closeButton, downloadIconSmall, messageNotification, orangeLine, greyLine,
            chatContainer, textCursor, unselectBox, selectBox, whiteLine, expandIcon, collapsedIcon, loading, webImageLoading;
    private NinePatch popupBg, buttonRed, buttonGreen, progressBarInner, progressBarBg, whiteRoundedBg,
            yellowRoundedBg,  greenRoundedBg,
            blackRoundedBg, chatBox, yellowGradientBox, scrollVerticalHandle, irregularBg, expandTitleBg;
    private BitmapFont blackNormal2, redNormal2, blueNormal2, blackBold2, blackNormal3, orangePizza3,
            whiteBold3GrayS, topBarFont, whitePizza3BlackS, orangePizza2White, whiteNormal3GrayS, whiteNormal2GrayS,
            whiteNormal2, whitePizza2BlackS, greenNormal2, grayBold2, redBold2, blueBold2;


    public Assets() {
        _manager = new AssetManager();;
    }

    public void loadBasic(Runnable onFinish){

        FileHandleResolver resolver = new InternalFileHandleResolver();
        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        _manager.load(_backgroundPackPath, TextureAtlas.class);
        loadOneFont(_fontPizzaPath, "orangePizza3.ttf", Color.valueOf("f05837"), 22);
        loadOneFont(_fontPizzaPath, "whitePizza3BlackS.ttf", Color.WHITE, 22, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "whitePizza2BlackS.ttf", Color.WHITE, 17, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "orangePizza2White.ttf", Color.valueOf("976b2d"), 17, 1, Color.WHITE, 0, Color.GRAY);
        loadOneFont(_fontPizzaPath, "topBarFont.ttf", Color.valueOf("000000"), 30, 4, Color.valueOf("fed778"), 0, Color.BLACK);
        loadOneFont(_fontNormalPath, "blackNormal2.ttf", Color.BLACK, 11);
        loadOneFont(_fontNormalPath, "blackNormal3.ttf", Color.BLACK, 15);
        loadOneFont(_fontNormalPath, "whiteNormal3GrayS.ttf", Color.WHITE, 15, 1, Color.GRAY, 1, Color.GRAY);
        loadOneFont(_fontNormalPath, "whiteNormal2GrayS.ttf", Color.WHITE, 11, 1, Color.GRAY, 1, Color.GRAY);
        loadOneFont(_fontNormalPath, "redNormal2.ttf", Color.valueOf("e40404"), 11);
        loadOneFont(_fontNormalPath, "whiteNormal2.ttf", Color.WHITE, 11);
        loadOneFont(_fontNormalPath, "blueNormal2.ttf", Color.valueOf("4169e1"), 11);
        loadOneFont(_fontNormalPath, "greenNormal2.ttf", Color.valueOf("51bf1b"), 11);
        loadOneFont(_fontBoldPath, "blackBold2.ttf", Color.BLACK, 11);
        loadOneFont(_fontBoldPath, "grayBold2.ttf", Color.valueOf("c4c4c4"), 11);
        loadOneFont(_fontBoldPath, "whiteBold3GrayS.ttf", Color.WHITE, 13, 1, Color.GRAY, 1, Color.GRAY);
        loadOneFont(_fontBoldPath, "redBold2.ttf", Color.valueOf("e40404"), 11);
        loadOneFont(_fontBoldPath, "blueBold2.ttf", Color.valueOf("11b1bf"), 11);

        _manager.finishLoading();

        basicFontsLoaded();
        basicTextureLoaded();
        basicNinePatchLoaded();

        if(onFinish != null) onFinish.run();
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

    private void basicFontsLoaded(){
        blackNormal2 = _manager.get("blackNormal2.ttf", BitmapFont.class);
        redNormal2 = _manager.get("redNormal2.ttf", BitmapFont.class);
        blueNormal2 = _manager.get("blueNormal2.ttf", BitmapFont.class);
        blackBold2 = _manager.get("blackBold2.ttf", BitmapFont.class);
        blackNormal3 = _manager.get("blackNormal3.ttf", BitmapFont.class);
        orangePizza3 = _manager.get("orangePizza3.ttf", BitmapFont.class);
        whiteBold3GrayS = _manager.get("whiteBold3GrayS.ttf", BitmapFont.class);
        topBarFont = _manager.get("topBarFont.ttf", BitmapFont.class);
        whitePizza3BlackS = _manager.get("whitePizza3BlackS.ttf", BitmapFont.class);
        orangePizza2White = _manager.get("orangePizza2White.ttf", BitmapFont.class);
        whiteNormal3GrayS = _manager.get("whiteNormal3GrayS.ttf", BitmapFont.class);
        whiteNormal2GrayS = _manager.get("whiteNormal2GrayS.ttf", BitmapFont.class);
        whiteNormal2 = _manager.get("whiteNormal2.ttf", BitmapFont.class);
        whitePizza2BlackS = _manager.get("whitePizza2BlackS.ttf", BitmapFont.class);
        greenNormal2 = _manager.get("greenNormal2.ttf", BitmapFont.class);
        grayBold2 = _manager.get("grayBold2.ttf", BitmapFont.class);
        redBold2 = _manager.get("redBold2.ttf", BitmapFont.class);
        blueBold2 = _manager.get("blueBold2.ttf", BitmapFont.class);

    }

    public BitmapFont getBlueBold2() {
        return blueBold2;
    }

    public BitmapFont getRedBold2() {
        return redBold2;
    }

    public BitmapFont getGrayBold2() {
        return grayBold2;
    }

    public BitmapFont getGreenNormal2() {
        return greenNormal2;
    }

    public BitmapFont getWhitePizza2BlackS() {
        return whitePizza2BlackS;
    }

    public BitmapFont getWhiteNormal2() {
        return whiteNormal2;
    }

    public BitmapFont getWhiteNormal2GrayS() {
        return whiteNormal2GrayS;
    }

    public BitmapFont getWhiteNormal3GrayS() {
        return whiteNormal3GrayS;
    }

    public BitmapFont getOrangePizza2White() {
        return orangePizza2White;
    }

    public BitmapFont getWhitePizza3BlackS() {
        return whitePizza3BlackS;
    }

    public BitmapFont getTopBarFont() {
        return topBarFont;
    }

    private void basicTextureLoaded(){
        _backgroundsPack = _manager.get(_backgroundPackPath, TextureAtlas.class);
        blueBg = _backgroundsPack.findRegion("blue");
        autumnBg = _backgroundsPack.findRegion("autumn_bg");
        sunrise = _backgroundsPack.findRegion("sunrise");
        sunray = _backgroundsPack.findRegion("sunray");
        logoNoWeapon = _backgroundsPack.findRegion("logo");
        logoPotatoWeapon = _backgroundsPack.findRegion("potato_weapon");
        logoTomatoWeapon = _backgroundsPack.findRegion("tomato_weapon");
        greenGround = _backgroundsPack.findRegion("grass_green");
        autumnGround = _backgroundsPack.findRegion("grass_autumn");
        uprightEggButton = _backgroundsPack.findRegion("upright_egg_button");
        playIcon = _backgroundsPack.findRegion("play_icon");
        empty = _backgroundsPack.findRegion("empty");
        tomatoHi = _backgroundsPack.findRegion("tomato_hi");
        potatoHi = _backgroundsPack.findRegion("potato_hi");
        socialIcon = _backgroundsPack.findRegion("social");
        loginIcon = _backgroundsPack.findRegion("login_icon");
        tick = _backgroundsPack.findRegion("tick");
        cross = _backgroundsPack.findRegion("cross");
        woodBgSmall = _backgroundsPack.findRegion("wood_bg_small");
        woodBgTall = _backgroundsPack.findRegion("wood_bg_tall");
        woodBgNormal = _backgroundsPack.findRegion("wood_bg_normal");
        woodBgTitle = _backgroundsPack.findRegion("title_wood_board");
        downwardEggButton = _backgroundsPack.findRegion("downward_egg_btn");
        topBarBg = _backgroundsPack.findRegion("topbar_bg");
        quitIcon = _backgroundsPack.findRegion("quit_icon");
        gameListHighlight = _backgroundsPack.findRegion("gamelist_highlight");
        gameListTitleSeparator = _backgroundsPack.findRegion("gamelist_title_separator");
        gameListBg = _backgroundsPack.findRegion("gamelist_bg");
        gameListTitleBg = _backgroundsPack.findRegion("gamelist_titlebg");
        btnWhiteRound = _backgroundsPack.findRegion("btn_white_round");
        blackBg = _backgroundsPack.findRegion("black_bg");
        ratingIcon = _backgroundsPack.findRegion("rating_icon");
        settingsIcon = _backgroundsPack.findRegion("settings_icon");
        tomatoIcon = _backgroundsPack.findRegion("tomato_icon");
        potatoIcon = _backgroundsPack.findRegion("potato_icon");
        unknownMascotIcon = _backgroundsPack.findRegion("unknown_mascot");
        backIcon = _backgroundsPack.findRegion("back_icon");
        infoIcon = _backgroundsPack.findRegion("info_icon");
        importantIcon = _backgroundsPack.findRegion("important_icon");
        comingSoon = _backgroundsPack.findRegion("coming_soon");
        noImage = _backgroundsPack.findRegion("noimage");
        pointLeftIcon = _backgroundsPack.findRegion("point_left_icon");
        closeButton = _backgroundsPack.findRegion("close_button");
        downloadIconSmall = _backgroundsPack.findRegion("download_icon_small");
        messageNotification = _backgroundsPack.findRegion("message_notification");
        orangeLine = _backgroundsPack.findRegion("orange_line");
        greyLine = _backgroundsPack.findRegion("grey_line");
        chatContainer = _backgroundsPack.findRegion("chat_container");
        textCursor = _backgroundsPack.findRegion("cursor_black");
        expandIcon = _backgroundsPack.findRegion("expand_icon");
        collapsedIcon = _backgroundsPack.findRegion("collapsed_icon");
        whiteLine = _backgroundsPack.findRegion("white_line");
        selectBox = _backgroundsPack.findRegion("select_box");
        unselectBox = _backgroundsPack.findRegion("unselect_box");
        loading = _backgroundsPack.findRegion("loading");
        webImageLoading = _backgroundsPack.findRegion("loading_image");
    }

    private void basicNinePatchLoaded(){
        popupBg =  _backgroundsPack .createPatch("popup_bg");
        buttonRed =  _backgroundsPack .createPatch("btn_red");
        buttonGreen =  _backgroundsPack .createPatch("btn_green");
        progressBarInner =  _backgroundsPack .createPatch("progress_bar_inner");
        progressBarBg =  _backgroundsPack .createPatch("progress_bar_bg");
        whiteRoundedBg =  _backgroundsPack .createPatch("white_rounded_bg");
        yellowRoundedBg =  _backgroundsPack .createPatch("yellow_rounded_bg");
        greenRoundedBg =  _backgroundsPack .createPatch("green_rounded_bg");
        blackRoundedBg =  _backgroundsPack .createPatch("black_rounded_bg");;
        chatBox =  _backgroundsPack .createPatch("chat_box");
        yellowGradientBox =  _backgroundsPack .createPatch("yellow_gradient_box");
        scrollVerticalHandle =  _backgroundsPack .createPatch("scrollbar_handle");
        irregularBg =  _backgroundsPack .createPatch("irregular_bg");
        expandTitleBg = _backgroundsPack.createPatch("expandable_title_bg");
    }

    public TextureRegion getWebImageLoading() {
        return webImageLoading;
    }

    public TextureRegion getBlueBg() {
        return blueBg;
    }

    public TextureRegion getAutumnBg() {
        return autumnBg;
    }

    public TextureRegion getSunrise() {
        return sunrise;
    }

    public TextureRegion getSunray() {
        return sunray;
    }

    public TextureRegion getLogoNoWeapon() {
        return logoNoWeapon;
    }

    public TextureRegion getLogoPotatoWeapon() {
        return logoPotatoWeapon;
    }

    public TextureRegion getLogoTomatoWeapon() {
        return logoTomatoWeapon;
    }

    public TextureRegion getGreenGround() {
        return greenGround;
    }

    public TextureRegion getAutumnGround() {
        return autumnGround;
    }

    public TextureRegion getUprightEggButton() {
        return uprightEggButton;
    }

    public TextureRegion getPlayIcon() {
        return playIcon;
    }

    public TextureRegion getEmpty() {
        return empty;
    }

    public TextureRegion getTomatoHi() {
        return tomatoHi;
    }

    public TextureRegion getPotatoHi() {
        return potatoHi;
    }

    public TextureRegion getSocialIcon() {
        return socialIcon;
    }

    public TextureRegion getLoginIcon() {
        return loginIcon;
    }

    public TextureRegion getTick() {
        return tick;
    }

    public TextureRegion getCross() {
        return cross;
    }

    public TextureRegion getWoodBgSmall() {
        return woodBgSmall;
    }

    public TextureRegion getWoodBgTall() {
        return woodBgTall;
    }

    public TextureRegion getWoodBgNormal() {
        return woodBgNormal;
    }

    public TextureRegion getWoodBgTitle() {
        return woodBgTitle;
    }

    public TextureRegion getDownwardEggButton() {
        return downwardEggButton;
    }

    public TextureRegion getTopBarBg() {
        return topBarBg;
    }

    public TextureRegion getQuitIcon() {
        return quitIcon;
    }

    public TextureRegion getGameListHighlight() {
        return gameListHighlight;
    }

    public TextureRegion getGameListTitleSeparator() {
        return gameListTitleSeparator;
    }

    public TextureRegion getGameListBg() {
        return gameListBg;
    }

    public TextureRegion getGameListTitleBg() {
        return gameListTitleBg;
    }

    public TextureRegion getBtnWhiteRound() {
        return btnWhiteRound;
    }

    public TextureRegion getBlackBg() {
        return blackBg;
    }

    public TextureRegion getRatingIcon() {
        return ratingIcon;
    }

    public TextureRegion getSettingsIcon() {
        return settingsIcon;
    }

    public TextureRegion getTomatoIcon() {
        return tomatoIcon;
    }

    public TextureRegion getPotatoIcon() {
        return potatoIcon;
    }

    public TextureRegion getUnknownMascotIcon() {
        return unknownMascotIcon;
    }

    public TextureRegion getBackIcon() {
        return backIcon;
    }

    public TextureRegion getInfoIcon() {
        return infoIcon;
    }

    public TextureRegion getImportantIcon() {
        return importantIcon;
    }

    public TextureRegion getComingSoon() {
        return comingSoon;
    }

    public TextureRegion getNoImage() {
        return noImage;
    }

    public TextureRegion getPointLeftIcon() {
        return pointLeftIcon;
    }

    public TextureRegion getCloseButton() {
        return closeButton;
    }

    public TextureRegion getDownloadIconSmall() {
        return downloadIconSmall;
    }

    public TextureRegion getMessageNotification() {
        return messageNotification;
    }

    public TextureRegion getOrangeLine() {
        return orangeLine;
    }

    public TextureRegion getGreyLine() {
        return greyLine;
    }

    public TextureRegion getChatContainer() {
        return chatContainer;
    }

    public TextureRegion getTextCursor() {
        return textCursor;
    }

    public TextureRegion getUnselectBox() {
        return unselectBox;
    }

    public TextureRegion getSelectBox() {
        return selectBox;
    }

    public TextureRegion getWhiteLine() {
        return whiteLine;
    }

    public TextureRegion getExpandIcon() {
        return expandIcon;
    }

    public TextureRegion getCollapsedIcon() {
        return collapsedIcon;
    }

    public TextureRegion getLoading() {
        return loading;
    }

    public NinePatch getExpandTitleBg() {
        return expandTitleBg;
    }

    public NinePatch getPopupBg() {
        return popupBg;
    }

    public NinePatch getButtonRed() {
        return buttonRed;
    }

    public NinePatch getButtonGreen() {
        return buttonGreen;
    }

    public NinePatch getProgressBarInner() {
        return progressBarInner;
    }

    public NinePatch getProgressBarBg() {
        return progressBarBg;
    }

    public NinePatch getGreenRoundedBg() {
        return greenRoundedBg;
    }

    public NinePatch getYellowRoundedBg() {
        return yellowRoundedBg;
    }

    public NinePatch getWhiteRoundedBg() {
        return whiteRoundedBg;
    }

    public NinePatch getBlackRoundedBg() {
        return blackRoundedBg;
    }

    public NinePatch getChatBox() {
        return chatBox;
    }

    public NinePatch getYellowGradientBox() {
        return yellowGradientBox;
    }

    public NinePatch getScrollVerticalHandle() {
        return scrollVerticalHandle;
    }

    public NinePatch getIrregularBg() {
        return irregularBg;
    }

    public BitmapFont getBlackNormal2() {
        return blackNormal2;
    }

    public BitmapFont getRedNormal2() {
        return redNormal2;
    }

    public BitmapFont getBlueNormal2() {
        return blueNormal2;
    }

    public BitmapFont getBlackBold2() {
        return blackBold2;
    }

    public BitmapFont getBlackNormal3() {
        return blackNormal3;
    }

    public BitmapFont getOrangePizza3() {
        return orangePizza3;
    }

    public BitmapFont getWhiteBold3GrayS() {
        return whiteBold3GrayS;
    }

    public Array<? extends TextureRegion> getPotatoHiAnimation() {
        if(_potatoHiAnimation == null){
            _potatoHiAnimation = new TextureAtlas(Gdx.files.internal("animations/potato_hi.txt"));;
        }
        return _potatoHiAnimation.getRegions();
    }

    public Array<? extends TextureRegion> getTomatoHiAnimation() {
        if(_tomatoHiAnimation == null){
            _tomatoHiAnimation = new TextureAtlas(Gdx.files.internal("animations/tomato_hi.txt"));;
        }
        return _tomatoHiAnimation.getRegions();
    }

    public void disposeHiAnimation() {
        if(_potatoHiAnimation != null){
            _potatoHiAnimation.dispose();
            _potatoHiAnimation = null;
        }
        if(_tomatoHiAnimation != null){
            _tomatoHiAnimation.dispose();
            _tomatoHiAnimation = null;
        }
    }


}
