package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Mascot extends Image {

    MascotEnum _mascotEnum;
    Assets _assets;
    TextureRegion _textureRegion;
    float _prefWidth, _prefHeight;
    float _padWidth;

    public Mascot(MascotEnum _mascotEnum, Assets _assets) {
        this._mascotEnum = _mascotEnum;
        this._assets = _assets;
        build();
    }

    public void build(){
        if(this._mascotEnum == MascotEnum.TOMATO){
            _textureRegion = _assets.getTomatoIcon();
        }
        else if(this._mascotEnum == MascotEnum.UNKNOWN){
            _textureRegion = _assets.getUnknownMascotIcon();
        }
        else{
            _textureRegion = _assets.getPotatoIcon();
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
        _padWidth = maxWidth - _prefWidth;
    }

    public float getPadWidth() {
        return _padWidth;
    }

    public float getPrefWidth() {
        return _prefWidth;
    }

    public float getPrefHeight() {
        return _prefHeight;
    }
}
