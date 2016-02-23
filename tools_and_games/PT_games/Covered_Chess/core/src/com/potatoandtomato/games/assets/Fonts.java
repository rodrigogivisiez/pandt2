package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.MyFileResolver;
import com.potatoandtomato.games.absint.IAssetFragment;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Fonts implements IAssetFragment {

    private AssetManager _manager;
    private String _fontBoldPath = "fonts/Helvetica-Bold.otf";
    private String _fontHeavyPath = "fonts/Helvetica-Heavy.otf";
    private String _fontRegularPath = "fonts/MyriadPro-Regular.otf";
    private String _fontPizzaPath = "fonts/pizza.ttf";
    private BitmapFont whiteBold2BlackS, blackNormal1,
            blackNormal1Green, greyPizza4BlackS,
            orangePizza5BlackS, greyPizza5BlackS,
            whitePizza2BlackS, darkBrownHeavy3, whiteNormal1;

    public Fonts(AssetManager _manager, GameCoordinator coordinator) {
        this._manager = _manager;
        FileHandleResolver resolver = new MyFileResolver(coordinator);
        _manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        _manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    @Override
    public void load() {
        loadOneFont(_fontBoldPath, "whiteBold2BlackS.ttf", Color.WHITE, 17, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontRegularPath, "blackNormal1.ttf", Color.BLACK, 13);
        loadOneFont(_fontRegularPath, "blackNormal1Green.ttf", Color.BLACK, 12, 1, Color.valueOf("588e54"), 0, Color.GRAY);
        loadOneFont(_fontPizzaPath, "greyPizza4BlackS.ttf", Color.valueOf("e9e9e9"), 30, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "greyPizza5BlackS.ttf", Color.valueOf("e9e9e9"), 50, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "orangePizza5BlackS.ttf", Color.valueOf("fbbb31"), 50, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontPizzaPath, "whitePizza2BlackS.ttf", Color.WHITE, 20, 1, Color.BLACK, 1, Color.GRAY);
        loadOneFont(_fontRegularPath, "whiteNormal1.ttf", Color.WHITE, 11, 0, Color.BLACK, 0, Color.GRAY);

        loadOneFont(_fontHeavyPath, "darkBrownHeavy3.ttf", Color.valueOf("56380a"), 20, 0, Color.BLACK, 0, Color.GRAY);
    }

    @Override
    public void onLoaded() {
        blackNormal1 = _manager.get("blackNormal1.ttf", BitmapFont.class);
        whiteBold2BlackS = _manager.get("whiteBold2BlackS.ttf", BitmapFont.class);
        blackNormal1Green = _manager.get("blackNormal1Green.ttf", BitmapFont.class);
        greyPizza4BlackS = _manager.get("greyPizza4BlackS.ttf", BitmapFont.class);
        greyPizza5BlackS = _manager.get("greyPizza5BlackS.ttf", BitmapFont.class);
        orangePizza5BlackS = _manager.get("orangePizza5BlackS.ttf", BitmapFont.class);
        whitePizza2BlackS = _manager.get("whitePizza2BlackS.ttf", BitmapFont.class);
        darkBrownHeavy3 = _manager.get("darkBrownHeavy3.ttf", BitmapFont.class);
        whiteNormal1 = _manager.get("whiteNormal1.ttf", BitmapFont.class);
    }

    public BitmapFont getWhiteNormal1() {
        return whiteNormal1;
    }

    public BitmapFont getDarkBrownHeavy3() {
        return darkBrownHeavy3;
    }

    public BitmapFont getWhitePizza2BlackS() {
        return whitePizza2BlackS;
    }

    public BitmapFont getGreyPizza5BlackS() {
        return greyPizza5BlackS;
    }

    public BitmapFont getOrangePizza5BlackS() {
        return orangePizza5BlackS;
    }

    public BitmapFont getGreyPizza4BlackS() {
        return greyPizza4BlackS;
    }

    public BitmapFont getWhiteBold2BlackS() {
        return whiteBold2BlackS;
    }

    public BitmapFont getBlackNormal1() {
        return blackNormal1;
    }

    public BitmapFont getBlackNormal1Green() {
        return blackNormal1Green;
    }


    @Override
    public void dispose() {

    }





    private void loadOneFont(String path, String name, Color color, int size){
        loadOneFont(path, name, color, size, 0, Color.BLACK, 0, Color.BLACK);
    }

    private void loadOneFont(String path, String name, Color color, int size, int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = path;
        size2Params.fontParameters.size = size;
        size2Params.fontParameters.color = color;
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
