package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Positions {

    public static float centerY(float fullHeight, float textureHeight){
        return (float) fullHeight / 2 - textureHeight / 2;
    }

    public static float centerX(float fullWidth, float textureWidth){
        return (float) fullWidth / 2 - textureWidth / 2;
    }

    public static Vector2 actorLocalToStageCoord(Actor actor){
        Vector2 coords = new Vector2(0, 0);
        actor.localToStageCoordinates(/*in/out*/coords);
        return coords;
    }

}
