package com.potatoandtomato.common.assets;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public class EnumToFontParameter {

    public static MyFreetypeFontLoader.FreeTypeFontLoaderParameter convert(Object enumObj, FontDetailsGenerator generator){
        String[] tmp = enumObj.toString().split("_");
        String fontName = tmp[0];
        String fontSize = tmp[1];
        String fontStyle = tmp[2];
        int borderWidth = 0;
        String borderColor = null;
        int shadowOffsetX = 0, shadowOffsetY = 0;
        String shadowColor = null;
        String fontColor = null;
        Color finalBorderColor = Color.BLACK;
        Color finalShadowColor = Color.BLACK;
        Color finalFontColor = Color.WHITE;

        if(tmp.length > 3){
            if(tmp[3].equals("B")){
                fontColor = tmp[4];
                borderColor = tmp[5];
                borderWidth = Integer.valueOf(tmp[6]);
            }
            else if(tmp[3].equals("S")){
                shadowColor = tmp[4];
                shadowOffsetX = Integer.valueOf(tmp[5]);
                shadowOffsetY = Integer.valueOf(tmp[6]);
            }
        }

        if(tmp.length > 7){
            if(tmp[7].equals("B")){
                fontColor = tmp[8];
                borderColor = tmp[9];
                borderWidth = Integer.valueOf(tmp[10]);
            }
            else if(tmp[7].equals("S")){
                shadowColor = tmp[8];
                shadowOffsetX = Integer.valueOf(tmp[9]);
                shadowOffsetY = Integer.valueOf(tmp[10]);
            }
        }

        if(borderColor != null){
            finalBorderColor = Color.valueOf(borderColor);
        }

        if(shadowColor != null){
            finalShadowColor = Color.valueOf(shadowColor);
        }

        if(fontColor != null){
            finalFontColor = Color.valueOf(fontColor);
        }

        return MyFreetypeFontLoader.getParameter(generator.getPath(fontName, fontStyle), finalFontColor,
                                                    generator.getSize(fontSize), borderWidth, finalBorderColor,
                                                    shadowOffsetX, shadowOffsetY, finalShadowColor, 0, 0);

    }

}
