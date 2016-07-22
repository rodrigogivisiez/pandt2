package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;

/**
 * Created by SiongLeng on 27/6/2016.
 */
public class PTTextField extends TextField {
    private Assets assets;
    private boolean asciiOnly;

    public PTTextField(Assets assets, boolean asciiOnly) {
        super("", new TextFieldStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR),
                Color.BLACK, new TextureRegionDrawable(assets.getTextures().get(Textures.Name.EMPTY)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.EMPTY)),
                        new TextureRegionDrawable(assets.getTextures().get(Textures.Name.EMPTY))));

        this.assets = assets;
        this.asciiOnly = asciiOnly;

        setStyle();
    }

    public void setStyle(){
        TextFieldStyle textFieldStyle = this.getStyle();
        textFieldStyle.selection = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.TRANS_BLACK_BG));
        textFieldStyle.font = assets.getFonts().get(asciiOnly ? Fonts.FontId.MYRIAD_M_REGULAR : Fonts.FontId.PT_M_REGULAR);
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CURSOR_BLACK));
        textFieldStyle.background = new NinePatchDrawable(assets.getPatches().get(Patches.Name.CHATBOX_UNFOCUS));
        textFieldStyle.focusedBackground = new NinePatchDrawable(assets.getPatches().get(Patches.Name.CHATBOX_FOCUS));
        textFieldStyle.background.setLeftWidth(textFieldStyle.background.getLeftWidth() + 5);
        textFieldStyle.background.setRightWidth(textFieldStyle.background.getRightWidth() + 5);
        textFieldStyle.focusedBackground.setLeftWidth(textFieldStyle.focusedBackground.getLeftWidth() + 5);
        textFieldStyle.focusedBackground.setRightWidth(textFieldStyle.focusedBackground.getRightWidth() + 5);
    }

}
