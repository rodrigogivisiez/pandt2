package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.services.SoundsPlayer;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.assets.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class BtnEggUpright extends Table {

    BtnEggUpright _this;
    Button _button;
    Assets _assets;
    Vector2 _size;
    Image _contentImg;
    SoundsPlayer _soundsWrapper;

    public BtnEggUpright(Assets assets, SoundsPlayer soundsWrapper){
        this(assets, soundsWrapper, 120);
    }

    public BtnEggUpright(Assets assets, SoundsPlayer soundsWrapper, int width) {
        _this = this;
        this._assets = assets;
        this._soundsWrapper = soundsWrapper;
        this._button = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        _button.setFillParent(true);
        this.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.UPRIGHT_EGG_BUTTON)));
        _size = Sizes.resize(width, _assets.getTextures().get(Textures.Name.UPRIGHT_EGG_BUTTON));
        this.setSize(_size.x, _size.y);

        _button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        animate();
                    }
                });
                _soundsWrapper.playSoundEffect(Sounds.Name.BUTTON_CLICKED);
                return super.touchDown(event, x, y, pointer, button);
            }
        });


    }

    public void setContent(final TextureRegion textureRegion){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _contentImg = new Image(textureRegion);
                _this.align(Align.center);
                _this.add(_contentImg).padLeft(15).expandY();
                _this.addActor(_button);
            }
        });
    }

    public void animate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.setSize(_size.x, _size.y - 40);
                Vector2 originalPosition = new Vector2(_this.getX(), _this.getY());
                _this.setPosition(originalPosition.x, originalPosition.y + 10);
                float duration = 0.2f;
                Threadings.renderFor(duration + 0.1f);
                _this.addAction(parallel(
                        Actions.fadeIn(duration, Interpolation.sineIn),
                        Actions.sizeTo(_size.x, _size.y, duration, Interpolation.bounceOut),
                        Actions.moveTo(originalPosition.x, originalPosition.y, duration)
                ));
            }
        });
    }

    public float getWidth() {
        return _size.x;
    }

}
