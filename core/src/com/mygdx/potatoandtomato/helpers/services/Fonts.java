package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class Fonts implements Disposable {

    HashMap<String, FreeTypeFontGenerator> fontGenerators = new HashMap();

    private String getArialBoldPath() {
        return "fonts/arial_bold.ttf";
    }
    public BitmapFont getArialBold(int size, Color color){
        return getFont(getArialBoldPath(), size, color, 0, Color.BLACK, 0, Color.BLACK);
    }
    public BitmapFont getArialBold(int size, Color color, int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){
        return getFont(getArialBoldPath(), size, color, borderWidth, borderColor, shadowOffset, shadowColor);
    }

    private String getArialPath() {
        return "fonts/arial.ttf";
    }
    public BitmapFont getArial(int size, Color color, int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){
        return getFont(getArialPath(), size, color, borderWidth, borderColor, shadowOffset, shadowColor);
    }

    private String getPizzaPath() {
        return "fonts/pizza.ttf";
    }
    public BitmapFont getPizzaFont(int size, Color color, int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){
        return getFont(getPizzaPath(), size, color, borderWidth, borderColor, shadowOffset, shadowColor);
    }

    private BitmapFont getFont(String internalPath, int size, Color color,
                               int borderWidth, Color borderColor, int shadowOffset, Color shadowColor){

        FreeTypeFontGenerator generator;
        if(fontGenerators.containsKey(internalPath)){
            generator = fontGenerators.get(internalPath);
        }
        else{
            generator = new FreeTypeFontGenerator(Gdx.files.internal(internalPath));
            fontGenerators.put(internalPath, generator);
        }

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;
        parameter.size = size;
        parameter.borderColor = borderColor;
        parameter.borderWidth = borderWidth;
        if(shadowOffset > 0){
            parameter.shadowColor = shadowColor;
            parameter.shadowOffsetX = shadowOffset;
            parameter.shadowOffsetY = shadowOffset;
        }

        BitmapFont font = generator.generateFont(parameter);
        return font;
    }


    @Override
    public void dispose() {
        for (Map.Entry<String, FreeTypeFontGenerator> entry : fontGenerators.entrySet()) {
            FreeTypeFontGenerator generator = entry.getValue();
            generator.dispose();
        }
        fontGenerators.clear();
    }
}
