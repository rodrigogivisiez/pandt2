package com.potatoandtomato.common.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 19/1/2016.
 */
public class MyFreetypeFontLoader extends AsynchronousAssetLoader<BitmapFont, MyFreetypeFontLoader.FreeTypeFontLoaderParameter> {
    public MyFreetypeFontLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public static class FreeTypeFontLoaderParameter extends AssetLoaderParameters<BitmapFont> {
        public String id;
        /** the name of the TTF file to be used to load the font **/
        public String fontFileName;
        /** the parameters used to generate the font, e.g. size, characters, etc. **/
        public FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    public void loadAsync (AssetManager manager, String fileName, FileHandle file, FreeTypeFontLoaderParameter parameter) {
        if(parameter == null) throw new RuntimeException("FreetypeFontParameter must be set in AssetManager#load to point at a TTF file!");
    }

    @Override
    public BitmapFont loadSync (AssetManager manager, String fileName, FileHandle file, FreeTypeFontLoaderParameter parameter) {
        if(parameter == null) throw new RuntimeException("FreetypeFontParameter must be set in AssetManager#load to point at a TTF file!");
        FreeTypeFontGenerator generator = manager.get(parameter.fontFileName + ".gen", FreeTypeFontGenerator.class);
        generator.scaleForPixelHeight((int)Math.ceil(parameter.fontParameters.size));
        BitmapFont font = generator.generateFont(parameter.fontParameters);
        return font;
    }

    @Override
    public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, FreeTypeFontLoaderParameter parameter) {
        Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        deps.add(new AssetDescriptor<FreeTypeFontGenerator>(parameter.fontFileName + ".gen", FreeTypeFontGenerator.class));
        return deps;
    }


    public static FreeTypeFontLoaderParameter getParameter(String path, Color fontColor, int size, int borderWidth,
                                                           Color borderColor, int shadowOffsetX, int shadowOffsetY, Color shadowColor,
                                                           int spaceX, int spaceY){
        MyFreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new MyFreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontParameters.size = size;
        size2Params.fontParameters.characters = "";
        size2Params.fontParameters.incremental = true;

        size2Params.fontFileName = path;

        size2Params.fontParameters.color = fontColor;
        size2Params.fontParameters.genMipMaps = true;
        size2Params.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        size2Params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        if(borderWidth != 0){
            size2Params.fontParameters.borderWidth = borderWidth;
            size2Params.fontParameters.borderColor = borderColor;
        }
        if(shadowOffsetX != 0 || shadowOffsetY != 0){
            size2Params.fontParameters.shadowColor = shadowColor;
            size2Params.fontParameters.shadowOffsetX = shadowOffsetX;
            size2Params.fontParameters.shadowOffsetY = shadowOffsetY;
        }

//        if(spaceX != 0){
//            size2Params.fontParameters.spaceX = spaceX;
//        }
//
//        if(spaceY != 0){
//            size2Params.fontParameters.spaceY = spaceY;
//        }

        return size2Params;
    }





















}