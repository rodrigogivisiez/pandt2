package com.potatoandtomato.games.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.SimpleRectangle;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public class Circle extends Image {

    private Services services;
    private GameCoordinator gameCoordinator;
    private Color userColor;

    public Circle(GameCoordinator gameCoordinator, Services services, String userId, SimpleRectangle simpleRectangle) {
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.userColor = ((userId == null) ? Color.WHITE :gameCoordinator.getPlayerByUserId(userId).getUserColor());

        this.setColor(userColor);
        this.setTouchable(Touchable.disabled);

        if(simpleRectangle.getHeight() > (simpleRectangle.getWidth() + simpleRectangle.getWidth() * 20 / 100)){
            this.setDrawable(new TextureRegionDrawable(services.getAssets().getTextures().get(Textures.Name.CIRCLE_TALL)));
        }
        else if(simpleRectangle.getWidth() > (simpleRectangle.getHeight() + simpleRectangle.getHeight() * 20 / 100)){
            this.setDrawable(new TextureRegionDrawable(services.getAssets().getTextures().get(Textures.Name.CIRCLE_FAT)));
        }
        else if(simpleRectangle.getWidth() < 30){
            this.setDrawable(new TextureRegionDrawable(services.getAssets().getTextures().get(Textures.Name.CIRCLE_SMALL)));
        }
        else if(simpleRectangle.getWidth() < 60){
            this.setDrawable(new TextureRegionDrawable(services.getAssets().getTextures().get(Textures.Name.CIRCLE_MEDIUM)));
        }
        else{
            this.setDrawable(new TextureRegionDrawable(services.getAssets().getTextures().get(Textures.Name.CIRCLE_BIG)));
        }



    }
}
