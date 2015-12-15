package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class BtnEggUpright extends Table {

    Button _button;
    Textures _textures;
    Vector2 _size;
    Image _contentImg;

    public BtnEggUpright(Textures textures) {
        this._textures = textures;
        this._button = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        _button.setFillParent(true);
        this.setBackground(new TextureRegionDrawable(_textures.getUprightEggButton()));
        _size = Sizes.resize(120, _textures.getUprightEggButton());
        this.setSize(_size.x, _size.y);

        _button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animate();
                return super.touchDown(event, x, y, pointer, button);
            }
        });


    }

    public void setContent(TextureRegion textureRegion){
        _contentImg = new Image(textureRegion);
        this.align(Align.center);
        this.add(_contentImg).padLeft(15).expandY();
        this.addActor(_button);
    }

    public void animate(){
        this.setSize(_size.x, _size.y - 40);
        Vector2 originalPosition = new Vector2(this.getX(), this.getY());
        this.setPosition(originalPosition.x, originalPosition.y + 10);
        float duration = 0.2f;
        this.addAction(parallel(
                            fadeIn(duration, Interpolation.sineIn),
                            sizeTo(_size.x, _size.y, duration, Interpolation.bounceOut),
                            moveTo(originalPosition.x, originalPosition.y, duration)
                        ));

    }

    public float getWidth() {
        return _size.x;
    }

}
