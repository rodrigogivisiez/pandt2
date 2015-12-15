package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Mascot extends Image {

    MascotEnum _mascotEnum;
    Textures _textures;
    TextureRegion _textureRegion;
    float _prefWidth, _prefHeight;

    public Mascot(MascotEnum _mascotEnum, Textures _textures) {
        this._mascotEnum = _mascotEnum;
        this._textures = _textures;
        build();
    }

    public void build(){
        if(this._mascotEnum == MascotEnum.TOMATO){
            _textureRegion = _textures.getTomatoIcon();
        }
        else{
            _textureRegion = _textures.getPotatoIcon();
        }

        this.setDrawable(new TextureRegionDrawable(_textureRegion));
    }

    public void resizeTo(float maxWidth, float maxHeight){
        Vector2 sizes = Sizes.resize(maxWidth, _textureRegion);
        if(sizes.y > maxHeight){
            sizes = Sizes.resizeByH(maxHeight, _textureRegion);
        }
        _prefWidth = sizes.x;
        _prefHeight = sizes.y;
    }

    public float getPrefWidth() {
        return _prefWidth;
    }

    public float getPrefHeight() {
        return _prefHeight;
    }
}
