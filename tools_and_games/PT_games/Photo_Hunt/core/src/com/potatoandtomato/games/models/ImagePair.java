package com.potatoandtomato.games.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class ImagePair implements Disposable {

    private Texture imageOne;
    private Texture imageTwo;
    private String metaJson;
    private String id;

    public ImagePair(Texture imageOne, Texture imageTwo, String metaJson, String id) {
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
        this.metaJson = metaJson;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetaJson() {
        return metaJson;
    }

    public Texture getImageTwo() {
        return imageTwo;
    }

    public Texture getImageOne() {
        return imageOne;
    }


    @Override
    public void dispose() {
        imageOne.dispose();
        imageTwo.dispose();
    }
}
