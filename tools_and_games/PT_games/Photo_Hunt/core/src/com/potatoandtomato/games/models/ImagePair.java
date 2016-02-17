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
    private long index;

    public ImagePair(Texture imageOne, Texture imageTwo, String metaJson, String id, Long index) {
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
        this.metaJson = metaJson;
        this.id = id;
        this.index = index;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
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

    public boolean imagesReady(){
        return (imageOne != null && imageTwo != null);
    }

    @Override
    public void dispose() {
        if(imageOne != null) imageOne.dispose();
        if(imageTwo != null) imageTwo.dispose();


    }
}
