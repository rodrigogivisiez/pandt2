package com.potatoandtomato.common.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.potatoandtomato.common.absints.IAssetFragment;
import com.potatoandtomato.common.absints.PTAssetsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public abstract class FontAssets implements IAssetFragment {

    private PTAssetsManager _manager;
    private HashMap<String, BitmapFont> _storage;
    private ArrayList<MyFreetypeFontLoader.FreeTypeFontLoaderParameter> _preloadParameters;
    protected FontDetailsGenerator fontDetailsGenerator;

    public FontAssets(PTAssetsManager _manager) {
        this._manager = _manager;
        this._preloadParameters = new ArrayList<MyFreetypeFontLoader.FreeTypeFontLoaderParameter>();
        this._storage = new HashMap<String, BitmapFont>();
        setFontDetailsGenerator();
    }

    public abstract void loadFonts();

    public abstract void setFontDetailsGenerator();

    protected void addPreloadParameter(Object object){
        MyFreetypeFontLoader.FreeTypeFontLoaderParameter parameter = EnumToFontParameter.convert(object, this.fontDetailsGenerator);
        parameter.setId(object.toString());
        _preloadParameters.add(parameter);
    }


    @Override
    public void load() {
        FileHandleResolver resolver = _manager.getFileHandleResolver();
        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new MyFreetypeFontLoader(resolver));

        loadFonts();

        for(MyFreetypeFontLoader.FreeTypeFontLoaderParameter parameter : _preloadParameters){
            loadParameterFont(parameter);
        }
    }

    @Override
    public void onLoaded() {

        for(MyFreetypeFontLoader.FreeTypeFontLoaderParameter parameter : _preloadParameters){
            _storage.put(parameter.getId(), _manager.get(parameter.getId()+".ttf", BitmapFont.class));
        }

    }

    @Override
    public void dispose() {
        _storage.clear();
        _preloadParameters.clear();
    }

    public BitmapFont get(Object key){
        return _storage.get(key.toString());
    }

    private void loadParameterFont(MyFreetypeFontLoader.FreeTypeFontLoaderParameter parameter){
        parameter.fontParameters.genMipMaps = true;
        parameter.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.fontParameters.magFilter = Texture.TextureFilter.Linear;

        _manager.load(parameter.getId()+".ttf", BitmapFont.class, parameter);
    }


}

