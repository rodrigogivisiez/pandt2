package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.MyFileResolver;
import com.potatoandtomato.games.abs.assets.IAssetElement;
import com.potatoandtomato.games.abs.assets.MyFreetypeFontLoader;

/**
 * Created by SiongLeng on 5/2/2016.
 */
public class Fonts implements IAssetElement {

    private String _fontBoldPath = "fonts/helvetica_bold.ttf";
    private AssetManager _manager;
    private GameCoordinator _coordinator;

    public Fonts(AssetManager _manager, GameCoordinator coordinator) {
        this._manager = _manager;
        this._coordinator = coordinator;
    }
    @Override
    public void preLoad(){
        MyFileResolver resolver = new MyFileResolver(_coordinator);
        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new MyFreetypeFontLoader(resolver));

        loadOneFont(_fontBoldPath, "whiteBold3.ttf", Color.valueOf("f05837"), 22);
    }

    @Override
    public void dispose() {

    }

    private BitmapFont whiteBold3;

    @Override
    public void finishLoading(){
        whiteBold3 = _manager.get("whiteBold3.ttf", BitmapFont.class);
    }

    public BitmapFont getWhiteBold3() {
        return whiteBold3;
    }















    private void loadOneFont(String path, String name, Color color, int size){
        loadOneFont(path, name, color, size, 0, Color.BLACK, 0, Color.BLACK);
    }

    private void loadOneFont(String path, String name, Color color, int size, int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){
        MyFreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new MyFreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = path;
        size2Params.fontParameters.size = size;
        size2Params.fontParameters.color = color;
        size2Params.fontParameters.genMipMaps = true;
        size2Params.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        size2Params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        if(borderWidth != 0){
            size2Params.fontParameters.borderWidth = borderWidth;
            size2Params.fontParameters.borderColor = borderColor;
        }
        if(shadowOffset != 0){
            size2Params.fontParameters.shadowColor = shadowColor;
            size2Params.fontParameters.shadowOffsetX = shadowOffset;
            size2Params.fontParameters.shadowOffsetY = shadowOffset;
        }
        _manager.load(name, BitmapFont.class, size2Params);
    }

}
