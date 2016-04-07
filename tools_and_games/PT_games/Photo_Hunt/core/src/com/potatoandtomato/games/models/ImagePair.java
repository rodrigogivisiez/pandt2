package com.potatoandtomato.games.models;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by SiongLeng on 7/4/2016.
 */
public class ImagePair {

    ImageDetails imageDetails;
    Texture imageOne;
    Texture imageTwo;
    Integer orderIndex;

    public ImagePair(ImageDetails imageDetails, Texture imageOne, Texture imageTwo, Integer orderIndex) {
        this.imageDetails = imageDetails;
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
        this.orderIndex = orderIndex;
    }

    public ImageDetails getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(ImageDetails imageDetails) {
        this.imageDetails = imageDetails;
    }

    public Texture getImageOne() {
        return imageOne;
    }

    public void setImageOne(Texture imageOne) {
        this.imageOne = imageOne;
    }

    public Texture getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(Texture imageTwo) {
        this.imageTwo = imageTwo;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
