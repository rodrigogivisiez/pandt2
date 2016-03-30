package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;

/**
 * Created by SiongLeng on 14/3/2016.
 */
public class FlowContainer extends Table {

    private Actor actor;
    private float widthLimit;
    private float heightLimit;
    private Assets assets;
    private float speed = 0;

    public FlowContainer(Actor actor, Assets assets) {
        this.actor = actor;
        this.assets = assets;

        this.add(actor).expand().fill();
    }

    public void setSizeLimit(float widthLimit, float heightLimit) {
        this.widthLimit = widthLimit;
        this.heightLimit = heightLimit;
        sizeChange();
    }


    public void sizeChange(){
        this.layout();
        if(this.getPrefWidth() > widthLimit){
            this.clear();
            Image image = new Image(assets.getTextures().get(Textures.Name.EMPTY));
            this.add(image).width(widthLimit).height(heightLimit);
            actor.setPosition(0, (heightLimit / 2) - (actor.getHeight() / 2));
            this.addActor(actor);
            this.setClip(true);
            setListener();
        }
    }


    public void setListener(){
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(speed == 0) speed = -0.3f;
                else{
                    speed = 0;
                    actor.setX(0);
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(speed != 0){
            actor.moveBy(speed, 0);
            labelPositionChanged();
        }

    }

    private void labelPositionChanged(){
        if(actor.getX() >= 10 || (actor.getX() < 0 && Math.abs(actor.getX()) + widthLimit >= actor.getWidth())){
            speed = -speed;
        }
    }

}
