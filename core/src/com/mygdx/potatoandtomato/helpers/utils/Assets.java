package com.mygdx.potatoandtomato.helpers.utils;

import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Texts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Assets {

    Fonts _fonts;
    Texts _texts;
    Textures _textures;

    public Assets(Textures textures, Fonts fonts, Texts texts) {
        _fonts = fonts;
        _texts = texts;
        _textures = textures;
    }

    public Fonts getFonts() {
        return _fonts;
    }

    public Texts getTexts() {
        return _texts;
    }

    public Textures getTextures() {
        return _textures;
    }
}
