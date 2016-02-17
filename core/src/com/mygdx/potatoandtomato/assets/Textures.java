package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Textures implements IAssetFragment {

    private AssetManager _manager;
    private TextureAtlas _UIPack;
    private String _path = "ui_pack.atlas";

    public Textures(AssetManager _manager) {
        this._manager = _manager;
    }

    @Override
    public void load() {
        _manager.load(_path, TextureAtlas.class);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onLoaded() {
        _UIPack = _manager.get(_path, TextureAtlas.class);
        blueBg = _UIPack.findRegion("blue");
        autumnBg = _UIPack.findRegion("autumn_bg");
        sunrise = _UIPack.findRegion("sunrise");
        sunray = _UIPack.findRegion("sunray");
        logoNoWeapon = _UIPack.findRegion("logo");
        logoPotatoWeapon = _UIPack.findRegion("potato_weapon");
        logoTomatoWeapon = _UIPack.findRegion("tomato_weapon");
        greenGround = _UIPack.findRegion("grass_green");
        autumnGround = _UIPack.findRegion("grass_autumn");
        uprightEggButton = _UIPack.findRegion("upright_egg_button");
        playIcon = _UIPack.findRegion("play_icon");
        empty = _UIPack.findRegion("empty");
        tomatoHi = _UIPack.findRegion("tomato_hi");
        potatoHi = _UIPack.findRegion("potato_hi");
        facebookIcon = _UIPack.findRegion("facebook_icon");
        loginIcon = _UIPack.findRegion("login_icon");
        tick = _UIPack.findRegion("tick");
        cross = _UIPack.findRegion("cross");
        woodBgSmall = _UIPack.findRegion("wood_bg_small");
        woodBgTall = _UIPack.findRegion("wood_bg_tall");
        woodBgNormal = _UIPack.findRegion("wood_bg_normal");
        woodBgTitle = _UIPack.findRegion("title_wood_board");
        downwardEggButton = _UIPack.findRegion("downward_egg_btn");
        topBarBg = _UIPack.findRegion("topbar_bg");
        quitIcon = _UIPack.findRegion("quit_icon");
        gameListHighlight = _UIPack.findRegion("gamelist_highlight");
        gameListTitleSeparator = _UIPack.findRegion("gamelist_title_separator");
        gameListTitleBg = _UIPack.findRegion("gamelist_titlebg");
        btnWhiteRound = _UIPack.findRegion("btn_white_round");
        blackBg = _UIPack.findRegion("black_bg");
        ratingIcon = _UIPack.findRegion("rating_icon");
        settingsIcon = _UIPack.findRegion("settings_icon");
        tomatoIcon = _UIPack.findRegion("tomato_icon");
        potatoIcon = _UIPack.findRegion("potato_icon");
        unknownIcon = _UIPack.findRegion("unknown_mascot");
        backIcon = _UIPack.findRegion("back_icon");
        infoIcon = _UIPack.findRegion("info_icon");
        importantIcon = _UIPack.findRegion("important_icon");
        comingSoon = _UIPack.findRegion("coming_soon");
        noImage = _UIPack.findRegion("noimage");
        pointLeftIcon = _UIPack.findRegion("point_left_icon");
        closeButton = _UIPack.findRegion("close_button");
        downloadIconSmall = _UIPack.findRegion("download_icon_small");
        messageNotification = _UIPack.findRegion("message_notification");
        orangeLine = _UIPack.findRegion("orange_line");
        greyLine = _UIPack.findRegion("grey_line");
        chatContainer = _UIPack.findRegion("chat_container");
        textCursor = _UIPack.findRegion("cursor_black");
        expandIcon = _UIPack.findRegion("expand_icon");
        collapsedIcon = _UIPack.findRegion("collapsed_icon");
        whiteLine = _UIPack.findRegion("white_line");
        selectBox = _UIPack.findRegion("select_box");
        unselectBox = _UIPack.findRegion("unselect_box");
        loading = _UIPack.findRegion("loading");
        webImageLoading = _UIPack.findRegion("loading_image");
        invitedIcon = _UIPack.findRegion("invited_icon");
        bulletIcon = _UIPack.findRegion("bullet");
        micIcon = _UIPack.findRegion("mic");
        voiceIcon = _UIPack.findRegion("voice_icon");
        micBig = _UIPack.findRegion("mic_big");
        micButton = _UIPack.findRegion("mic_btn");
        keyboardButton = _UIPack.findRegion("keyboard_btn");
        thinOrangeBar = _UIPack.findRegion("thin_orange_bar");
        closeKeyboardIcon = _UIPack.findRegion("close_keyboard_icon");
        transWhite = _UIPack.findRegion("trans_white");
        loggingInMascots = _UIPack.findRegion("logging_in");
        woodSeparatorHorizontal = _UIPack.findRegion("wood_separator_horizontal");
        woodBgFat = _UIPack.findRegion("wood_bg_fat");
        kickIcon = _UIPack.findRegion("kick_icon");
        verticalSeparator = _UIPack.findRegion("vertical_separator");
    }

    private TextureRegion blueBg, autumnBg, sunrise, sunray, logoNoWeapon, logoPotatoWeapon,
            logoTomatoWeapon, greenGround, autumnGround, uprightEggButton, playIcon, empty,
            tomatoHi, potatoHi, facebookIcon, loginIcon, tick, cross, woodBgSmall, woodBgTall, woodBgFat, woodBgNormal,
            woodBgTitle, downwardEggButton, topBarBg, quitIcon, gameListHighlight, gameListTitleSeparator,
            gameListTitleBg, btnWhiteRound, blackBg, ratingIcon, settingsIcon, tomatoIcon,
            potatoIcon, unknownIcon, backIcon, infoIcon, importantIcon, comingSoon, noImage,
            pointLeftIcon, closeButton, downloadIconSmall, messageNotification, orangeLine, greyLine,
            chatContainer, textCursor, unselectBox, selectBox, whiteLine, expandIcon, collapsedIcon, loading,
            webImageLoading, invitedIcon, bulletIcon, micIcon, voiceIcon, micBig, micButton, thinOrangeBar, keyboardButton,
            closeKeyboardIcon, transWhite, loggingInMascots, woodSeparatorHorizontal, kickIcon, verticalSeparator;

    public TextureRegion getVerticalSeparator() {
        return verticalSeparator;
    }

    public TextureRegion getKickIcon() {
        return kickIcon;
    }

    public TextureRegion getWoodBgFat() {
        return woodBgFat;
    }

    public TextureRegion getWoodSeparatorHorizontal() {
        return woodSeparatorHorizontal;
    }

    public TextureRegion getLoggingInMascots() {
        return loggingInMascots;
    }

    public TextureRegion getCloseKeyboardIcon() {
        return closeKeyboardIcon;
    }

    public TextureRegion getTransWhite() {
        return transWhite;
    }

    public TextureRegion getKeyboardButton() {
        return keyboardButton;
    }

    public TextureRegion getThinOrangeBar() {
        return thinOrangeBar;
    }

    public TextureRegion getMicButton() {
        return micButton;
    }

    public TextureRegion getMicBig() {
        return micBig;
    }

    public TextureRegion getVoiceIcon() {
        return voiceIcon;
    }

    public TextureRegion getMicIcon() {
        return micIcon;
    }

    public TextureRegion getBulletIcon() {
        return bulletIcon;
    }

    public TextureRegion getInvitedIcon() {
        return invitedIcon;
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

    public TextureRegion getFacebookIcon() {
        return facebookIcon;
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

    public TextureRegion getUnknownIcon() {
        return unknownIcon;
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

    public TextureAtlas getUIPack() {
        return _UIPack;
    }
}
