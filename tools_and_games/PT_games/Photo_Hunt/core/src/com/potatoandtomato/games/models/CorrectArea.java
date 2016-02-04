package com.potatoandtomato.games.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by SiongLeng on 1/2/2016.
 */
public class CorrectArea {

    public Vector2 topLeft, topRight, bottomLeft, bottomRight;

    public CorrectArea(Vector2 topLeft, Vector2 topRight, Vector2 bottomLeft, Vector2 bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }


    public Rectangle toRectangle(){
        return new Rectangle(topLeft.x, topLeft.y, topRight.x - topLeft.x, bottomLeft.y - topLeft.y);
    }

}
