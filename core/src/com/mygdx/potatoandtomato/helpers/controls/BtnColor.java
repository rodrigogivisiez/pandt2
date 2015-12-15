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
import com.mygdx.potatoandtomato.helpers.services.Fonts;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class BtnColor extends Table {

    public enum ColorChoice {
        GREEN, RED
    }

    private Textures _textures;
    private ColorChoice _colorChoice;
    private Fonts _fonts;

    public BtnColor(ColorChoice colorChoice, Fonts fonts, Textures textures) {
        _textures = textures;
        _colorChoice = colorChoice;
        _fonts = fonts;
        Table buttonYesTable = new Table();
        this.setBackground(new NinePatchDrawable(getColorNinePatch()));
    }

    public void setImage(TextureRegion textureRegion, float width){
        Image img = new Image(textureRegion);
        Vector2 sizes = Sizes.resize(width, textureRegion);
        this.add(img).size(sizes.x, sizes.y);
        new DummyButton(this, _textures);
    }

    public void setText(String msg){
        Label.LabelStyle labelStyle = new Label.LabelStyle(_fonts.getArialBold(17, Color.WHITE), Color.WHITE);
        Label lblMessage = new Label(msg, labelStyle);
        lblMessage.setAlignment(Align.center);
        lblMessage.setWrap(true);
        this.add(lblMessage).expandX().fillX();
        new DummyButton(this, _textures);
    }

    private NinePatch getColorNinePatch(){
        if(_colorChoice == ColorChoice.GREEN){
            return _textures.getButtonGreen();
        }
        else{
            return  _textures.getButtonRed();
        }
    }

}
