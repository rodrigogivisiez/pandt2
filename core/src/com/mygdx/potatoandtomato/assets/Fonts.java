package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;
import com.mygdx.potatoandtomato.absintflis.assets.MyFreetypeFontLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Fonts implements IAssetFragment {

    private AssetManager _manager;
    private HashMap<String, BitmapFont> _storage;
    private ArrayList<Font> _preloadFonts;

    public Fonts(AssetManager _manager) {
        this._manager = _manager;
        this._preloadFonts = new ArrayList<Font>();
        this._storage = new HashMap<String, BitmapFont>();
    }

    @Override
    public void load() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new MyFreetypeFontLoader(resolver));

        loadFonts();
    }

    private void loadFonts(){

        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.XS, FontColor.WHITE, FontStyle.SEMI_BOLD,
                FontBorderColor.BLACK, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.BLACK, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.BLACK, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.BLUE, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.RED, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.BLACK, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.BLUE, FontStyle.SEMI_BOLD,
                FontBorderColor.WHITE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.RED, FontStyle.SEMI_BOLD,
                FontBorderColor.WHITE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.WHITE, FontStyle.SEMI_BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.WHITE, FontStyle.SEMI_BOLD,
                FontBorderColor.BLACK, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.WHITE, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.WHITE, FontStyle.ITALIC,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.DARK_BROWN, FontStyle.ITALIC,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.S, FontColor.DARK_BROWN, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.M, FontColor.DARK_BROWN, FontStyle.SEMI_BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.M, FontColor.WHITE, FontStyle.SEMI_BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.M, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.M, FontColor.BLACK, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.L, FontColor.WHITE, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.MYRIAD, FontSize.XL, FontColor.WHITE, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.BLACK));

        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.XS, FontColor.GREEN, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.XS, FontColor.GRAY, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.XS, FontColor.BLACK, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.XS, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.GRAY, FontShadowColor.GRAY));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.M, FontColor.DARK_BROWN, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.M, FontColor.BLACK, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.M, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.M, FontColor.WHITE, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.M, FontColor.BLACK, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.L, FontColor.WHITE, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.L, FontColor.BLACK, FontStyle.BOLD,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.L, FontColor.WHITE, FontStyle.HEAVY,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.XL, FontColor.WHITE, FontStyle.HEAVY_ITALIC,
                FontBorderColor.DARK_BROWN, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.HELVETICA, FontSize.XXL, FontColor.DARK_BROWN, FontStyle.CONDENSED,
                FontBorderColor.NONE, FontShadowColor.DARK_ORANGE));

        _preloadFonts.add(new Font(FontName.PIZZA, FontSize.M, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.BLACK, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.PIZZA, FontSize.XL, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.DARK_ORANGE));
        _preloadFonts.add(new Font(FontName.PIZZA, FontSize.XXL, FontColor.WHITE, FontStyle.REGULAR,
                FontBorderColor.DARK_BLUE, FontShadowColor.DARK_BLUE));
        _preloadFonts.add(new Font(FontName.PIZZA, FontSize.XXL, FontColor.TEAL, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.DARK_ORANGE));
        _preloadFonts.add(new Font(FontName.PIZZA, FontSize.XXXL, FontColor.ORANGE, FontStyle.REGULAR,
                FontBorderColor.NONE, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.PIZZA, FontSize.MAX, FontColor.BLACK, FontStyle.REGULAR,
                FontBorderColor.LIGHT_ORANGE, FontShadowColor.NONE));

        _preloadFonts.add(new Font(FontName.CARTER, FontSize.S, FontColor.YELLOW, FontStyle.REGULAR,
                FontBorderColor.BLACK, FontShadowColor.NONE));
        _preloadFonts.add(new Font(FontName.CARTER, FontSize.L, FontColor.YELLOW, FontStyle.REGULAR,
                FontBorderColor.BLACK, FontShadowColor.NONE));

        for(Font font : _preloadFonts){
            loadOneFont(font);
        }
    }

    @Override
    public void onLoaded() {

        for(Font font : _preloadFonts){
            _storage.put(font.toString(), _manager.get(font.getName(), BitmapFont.class));
        }

    }


    @Override
    public void dispose() {

    }

















    public BitmapFont get(FontName fontName){
        return get(fontName, FontSize.M);
    }

    public BitmapFont get(FontName fontName, FontStyle fontStyle){
        return get(fontName, FontSize.M, FontColor.WHITE, fontStyle);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize){
        return get(fontName, fontSize, FontColor.BLACK);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontStyle fontStyle){
        return get(fontName, fontSize, FontColor.WHITE, fontStyle);
    }

    public BitmapFont get(FontName fontName, FontColor fontColor, FontStyle fontStyle){
        return get(fontName, FontSize.M, fontColor, fontStyle);
    }

    public BitmapFont get(FontName fontName, FontColor fontColor){
        return get(fontName, FontSize.M, fontColor);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontColor fontColor){
        return get(fontName, fontSize, fontColor, FontStyle.REGULAR, FontBorderColor.NONE, FontShadowColor.NONE);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontColor fontColor, FontStyle fontStyle){
        return get(fontName, fontSize, fontColor, fontStyle, FontBorderColor.NONE, FontShadowColor.NONE);
    }

    public BitmapFont get(FontName fontName, FontColor fontColor,  FontBorderColor fontBorderColor){
        return get(fontName, FontSize.M, fontColor, fontBorderColor);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontStyle fontStyle, FontBorderColor fontBorderColor){
        return get(fontName, fontSize, FontColor.WHITE, fontStyle, fontBorderColor, FontShadowColor.NONE);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontColor fontColor,  FontBorderColor fontBorderColor){
        return get(fontName, fontSize, fontColor, FontStyle.REGULAR, fontBorderColor, FontShadowColor.NONE);
    }


    public BitmapFont get(FontName fontName, FontColor fontColor,  FontShadowColor fontShadowColor){
        return get(fontName, FontSize.M, fontColor, fontShadowColor);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontStyle fontStyle, FontShadowColor fontShadowColor){
        return get(fontName, fontSize, FontColor.WHITE, fontStyle, FontBorderColor.NONE, fontShadowColor);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontColor fontColor,  FontShadowColor fontShadowColor){
        return get(fontName, fontSize, fontColor, FontStyle.REGULAR, FontBorderColor.NONE, fontShadowColor);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontColor fontColor,
                          FontStyle fontStyle, FontShadowColor fontShadowColor) {
        return get(fontName, fontSize, fontColor, fontStyle, FontBorderColor.NONE, fontShadowColor);
    }

    public BitmapFont get(FontName fontName, FontSize fontSize, FontColor fontColor,
                          FontStyle fontStyle, FontBorderColor fontBorderColor, FontShadowColor fontShadowColor){
        Font font = new Font(fontName, fontSize, fontColor, fontStyle, fontBorderColor, fontShadowColor);
        if(_storage.containsKey(font.toString())){
            return _storage.get(font.toString());
        }
        else{
            throw new NullPointerException("Cannot find Bitmapfont "+ font.toString() +" in pre-loaded fonts storage.");
        }
    }

    private void loadOneFont(Font font){
        int borderSize = 0;
        if(font.hasBorder()){
            if(font.fontSize == FontSize.MAX) borderSize = 4;
            if(font.fontSize == FontSize.XL) borderSize = 2;
            else borderSize = 1;
        }

        loadOneFont(font.getPath(), font.getName(), font.getColor(), font.getSize(),
                borderSize, font.getBorderColor(), font.hasShadow() ? 1 : 0, font.getShadowColor());

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

    public enum FontStyle {
        SEMI_BOLD, REGULAR, BOLD, CONDENSED, ITALIC, HEAVY, HEAVY_ITALIC
    }

    public enum FontColor{
        WHITE, BLACK, DARK_BROWN, TEAL, GRAY, GREEN, ORANGE, BLUE, RED, YELLOW
    }

    public enum FontName{
        PIZZA, MYRIAD, HELVETICA, CARTER
    }

    public enum FontSize{
        XS, S, M, L, XL, XXL, XXXL, MAX
    }

    public enum FontShadowColor{
        GRAY, DARK_ORANGE, BLACK, DARK_BLUE, NONE
    }

    public enum FontBorderColor{
        WHITE, GRAY, BLACK, LIGHT_ORANGE, DARK_BLUE, DARK_BROWN, NONE
    }

    private class Font{

        private FontName fontName;
        private FontSize fontSize;
        private FontColor fontColor;
        private FontStyle fontStyle;
        private FontShadowColor fontShadowColor;
        private FontBorderColor fontBorderColor;

        public Font(FontName fontName, FontSize fontSize,
                    FontColor fontColor, FontStyle fontStyle,
                    FontBorderColor fontBorderColor,
                    FontShadowColor fontShadowColor) {
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.fontStyle = fontStyle;
            this.fontShadowColor = fontShadowColor;
            this.fontBorderColor = fontBorderColor;
        }

        public String getPath(){
            String path = "";
            switch (fontName){
                case MYRIAD:
                    path = "fonts/MyriadPro-%s.otf";
                    break;
                case PIZZA:
                    path = "fonts/Pizza-%s.otf";
                    break;
                case HELVETICA:
                    path = "fonts/Helvetica-%s.otf";
                    break;
                case CARTER:
                    path = "fonts/CarterOne-%s.otf";
                    break;
            }

            String styleName = "";
            switch (fontStyle){
                case SEMI_BOLD:
                    styleName = "Semibold";
                    break;
                case REGULAR:
                    styleName = "Regular";
                    break;
                case BOLD:
                    styleName = "Bold";
                    break;
                case CONDENSED:
                    styleName = "Condensed";
                    break;
                case ITALIC:
                    styleName = "It";
                    break;
                case HEAVY:
                    styleName = "Heavy";
                    break;
                case HEAVY_ITALIC:
                    styleName = "HvIt";
            }

            return String.format(path, styleName);
        }

        public String getName(){
            return toString() + ".ttf";
        }

        public Color getColor(){
            switch (fontColor){
                case BLACK:
                    return Color.BLACK;
                case DARK_BROWN:
                    return Color.valueOf("573801");
                case WHITE:
                    return Color.WHITE;
                case TEAL:
                    return Color.valueOf("fff6d8");
                case GRAY:
                    return Color.valueOf("898887");
                case GREEN:
                    return Color.valueOf("51bf1b");
                case ORANGE:
                    return Color.valueOf("f05837");
                case BLUE:
                    return Color.valueOf("11b1bf");
                case RED:
                    return Color.valueOf("e40404");
                case YELLOW:
                    return Color.valueOf("fff600");
            }
            return null;
        }

        public Color getShadowColor(){
            switch (fontShadowColor){
                case GRAY:
                    return Color.valueOf("898887");
                case DARK_ORANGE:
                    return Color.valueOf("a05e00");
                case BLACK:
                    return Color.BLACK;
                case DARK_BLUE:
                    return Color.valueOf("0e516c");
            }
            return null;
        }

        public Color getBorderColor(){
            switch (fontBorderColor){
                case WHITE:
                    return Color.WHITE;
                case GRAY:
                    return Color.GRAY;
                case BLACK:
                    return Color.BLACK;
                case LIGHT_ORANGE:
                    return Color.valueOf("fed778");
                case DARK_BLUE:
                    return Color.valueOf("0f5673");
                case DARK_BROWN:
                    return Color.valueOf("81562c");
            }
            return null;
        }

        public int getSize(){
            switch (fontSize){
                case XS:
                    return 9;
                case S:
                    return 11;
                case M:
                    return 13;
                case L:
                    return 15;
                case XL:
                    return 17;
                case XXL:
                    return 20;
                case XXXL:
                    return 22;
                case MAX:
                    return 30;
            }
            return 0;
        }

        public boolean hasBorder(){
            return fontBorderColor != FontBorderColor.NONE;
        }

        public boolean hasShadow(){
            return fontShadowColor != FontShadowColor.NONE;
        }

        @Override
        public String toString() {
            String returnString = "";
            returnString += fontColor.name() + "_";
            returnString += fontName.name() + "_";
            returnString += fontSize.name() + "_";
            returnString += fontStyle.name() + "_";
            returnString += fontShadowColor.name() + "_";
            returnString += fontBorderColor.name();
            return returnString;
        }
    }

}

