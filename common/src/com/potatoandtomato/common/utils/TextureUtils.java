package com.potatoandtomato.common.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by SiongLeng on 9/5/2016.
 */
public class TextureUtils {

    public static Texture bytesToTexture(byte[] textureBytes) {
        if(textureBytes != null){
            try {
                Pixmap pixmap = new Pixmap(textureBytes, 0, textureBytes.length);
                Texture texture = new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                return texture;

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return null;
    }


}
