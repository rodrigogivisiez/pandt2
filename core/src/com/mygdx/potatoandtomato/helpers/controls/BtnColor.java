package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class BtnColor extends Table {

    public enum ColorChoice {
        GREEN, RED
    }

    private Assets _assets;
    private ColorChoice _colorChoice;

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
        Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getWhiteBold3GrayS(), Color.WHITE);
        Label lblMessage = new Label(msg, labelStyle);
        lblMessage.setAlignment(Align.center);
        lblMessage.setWrap(true);
        this.add(lblMessage).expandX().fillX();
        new DummyButton(this, _assets);
    }

    private NinePatch getColorNinePatch(){
        if(_colorChoice == ColorChoice.GREEN){
            return _assets.getButtonGreen();
        }
        else{
            return  _assets.getButtonRed();
        }
    }

}
