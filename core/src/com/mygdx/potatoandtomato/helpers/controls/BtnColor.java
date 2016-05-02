package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.assets.Animations;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class BtnColor extends Table {

    public enum ColorChoice {
        GREEN, RED, BLUE
    }

    private BtnColor _this;
    private Assets _assets;
    private ColorChoice _colorChoice;
    private Table _loadingTable;

    public BtnColor(ColorChoice colorChoice, Assets assets) {
        _this = this;
        _assets = assets;
        _colorChoice = colorChoice;
        Table buttonYesTable = new Table();
        this.setBackground(new NinePatchDrawable(getColorNinePatch()));
    }

    public void setImage(final TextureRegion textureRegion, final float width){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Image img = new Image(textureRegion);
                Vector2 sizes = Sizes.resize(width, textureRegion);
                _this.add(img).size(sizes.x, sizes.y);
                new DummyButton(_this, _assets);
            }
        });

    }

    public void setText(final String msg){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label.LabelStyle labelStyle = new Label.LabelStyle();
                labelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_XL_BOLD_S_000000_1_1);

                Label lblMessage = new Label(msg, labelStyle);
                lblMessage.setAlignment(Align.center);
                lblMessage.setWrap(true);
                _this.add(lblMessage).expandX().fillX();
                new DummyButton(_this, _assets);
            }
        });
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
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(!(_loadingTable == null)) _loadingTable.remove();
                _loadingTable = new Table();
                _loadingTable.setFillParent(true);
                _loadingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
                Animator loadingAnimator = new Animator(0.1f, _assets.getAnimations().get(Animations.Name.LOADING));
                _loadingTable.add(loadingAnimator).size(20, 20);
                _this.addActor(_loadingTable);
            }
        });
    }

    public void clearLoading(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _loadingTable.remove();
                _loadingTable = null;
            }
        });
    }



}
