package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Patches{

    public Patches() {

    }


    public void onLoaded(TextureAtlas UIPack) {
        popupBg =  UIPack .createPatch("popup_bg");
        buttonGreen =  UIPack .createPatch("btn_green");
        buttonBlue = UIPack.createPatch("btn_blue");
        progressBarInner =  UIPack .createPatch("progress_bar_inner");
        progressBarBg =  UIPack .createPatch("progress_bar_bg");
        whiteRoundedBg =  UIPack .createPatch("white_rounded_bg");
        yellowRoundedBg =  UIPack .createPatch("yellow_rounded_bg");
        greenRoundedBg =  UIPack .createPatch("green_rounded_bg");
        blackRoundedBg =  UIPack .createPatch("black_rounded_bg");;
        chatBox =  UIPack .createPatch("chat_box");
        yellowGradientBox =  UIPack .createPatch("yellow_gradient_box");
        yellowGradientBoxRounded =  UIPack .createPatch("yellow_gradient_box_rounded");
        scrollVerticalHandle =  UIPack .createPatch("scrollbar_handle");
        irregularBg =  UIPack .createPatch("irregular_bg");
        expandTitleBg = UIPack.createPatch("expandable_title_bg");
        woodBgSmallPatch = UIPack.createPatch("wood_bg_small_patch");
        gameListBg = UIPack.createPatch("gamelist_bg");
        textFieldBg = UIPack.createPatch("text_field_bg");
        woodBgFatPatch = UIPack.createPatch("wood_bg_fat_nine");

    }

    private NinePatch popupBg, buttonGreen, buttonBlue, progressBarInner, progressBarBg, whiteRoundedBg,
            yellowRoundedBg,  greenRoundedBg,
            blackRoundedBg, chatBox, yellowGradientBox, yellowGradientBoxRounded, scrollVerticalHandle, irregularBg, expandTitleBg,
            woodBgSmallPatch, gameListBg, textFieldBg, woodBgFatPatch;

    public NinePatch getYellowGradientBoxRounded() {
        return yellowGradientBoxRounded;
    }

    public NinePatch getWoodBgFatPatch() {
        return woodBgFatPatch;
    }

    public NinePatch getGameListBg() {
        return gameListBg;
    }

    public NinePatch getTextFieldBg() {
        return textFieldBg;
    }

    public NinePatch getWoodBgSmallPatch() {
        return woodBgSmallPatch;
    }

    public NinePatch getExpandTitleBg() {
        return expandTitleBg;
    }

    public NinePatch getPopupBg() {
        return popupBg;
    }

    public NinePatch getButtonBlue() {
        return buttonBlue;
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

}
