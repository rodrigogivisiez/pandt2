package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class BtnColor extends Table {

    public enum ColorChoice {
        GREEN, RED, BLUE
    }

    private Assets _assets;
    private ColorChoice _colorChoice;
    private Table _loadingTable;

    public BtnColor(ColorChoice colorChoice, Assets assets) {
        _assets = assets;
        _colorChoice = colorChoice;
        Table buttonYesTable = new Table();
        this.setBackground(new NinePatchDrawable(getColorNinePatch()));
    }

    public void setImage(TextureRegion textureRegion, float width){
        Image img = new Image(textureRegion);
        Vector2 sizes = Sizes.resize(width, textureRegion);
        this.add(img).size(sizes.x, sizes.y);
        new DummyButton(this, _assets);
    }

    public void setText(String msg){
        Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontName.MYRIAD,
                                            Fonts.FontSize.XL, Fonts.FontColor.WHITE, Fonts.FontStyle.BOLD, Fonts.FontShadowColor.BLACK), null);
        Label lblMessage = new Label(msg, labelStyle);
        lblMessage.setAlignment(Align.center);
        lblMessage.setWrap(true);
        this.add(lblMessage).expandX().fillX();
        new DummyButton(this, _assets);
    }

    private NinePatch getColorNinePatch(){
        if(_colorChoice == ColorChoice.GREEN){
            return _assets.getPatches().get(Patches.Name.BTN_GREEN);
        }
        else if(_colorChoice == ColorChoice.BLUE){
            return _assets.getPatches().get(Patches.Name.BTN_BLUE);
        }
        else{
            return  _assets.getPatches().get(Patches.Name.BTN_BLUE);
        }
    }

    public void loading(){
        if(!(_loadingTable == null)) _loadingTable.remove();
        _loadingTable = new Table();
        _loadingTable.setFillParent(true);
        _loadingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
        Animator loadingAnimator = new Animator(0.1f, _assets.getAnimations().getLoadingAnimation());
        _loadingTable.add(loadingAnimator).size(20, 20);
        this.addActor(_loadingTable);
    }

    public void clearLoading(){
        _loadingTable.remove();
        _loadingTable = null;
    }



}
