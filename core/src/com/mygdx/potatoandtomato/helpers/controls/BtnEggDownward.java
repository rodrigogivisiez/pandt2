package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.services.Shaders;
import com.mygdx.potatoandtomato.helpers.services.SoundsWrapper;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.common.assets.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class BtnEggDownward extends Table {

    Button _button;
    Assets _assets;
    Vector2 _size;
    Image _contentImg;
    Label _textLabel;
    Shaders _shaders;
    ShaderProgram _shader;
    boolean _enabled;
    SoundsWrapper _soundsWrapper;

    public BtnEggDownward(Assets assets, SoundsWrapper soundsWrapper) {
        this(assets, soundsWrapper, null);
    }

    public BtnEggDownward(Assets assets, SoundsWrapper soundsWrapper, Shaders shaders) {
        this._soundsWrapper = soundsWrapper;
        this._assets = assets;
        this._shaders = shaders;
        this._button = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        this._enabled = true;
        _button.setFillParent(true);
        this.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.DOWNWARD_EGG_BUTTON)));
        _size = Sizes.resize(100, _assets.getTextures().get(Textures.Name.DOWNWARD_EGG_BUTTON));
        this.setSize(_size.x, _size.y);

        _button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animate();
                _soundsWrapper.playSoundEffect(Sounds.Name.BUTTON_CLICKED);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public void setContent(TextureRegion textureRegion){
        _contentImg = new Image(textureRegion);
        this.align(Align.center);
        this.add(_contentImg).padLeft(15).expandY();
    }

    public void setText(String text){
        this.clear();
        Label.LabelStyle textLabelStyle = new Label.LabelStyle();

        textLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XL_REGULAR_S_a05e00_1_1);

        _textLabel = new Label(text, textLabelStyle);
        _textLabel.setWrap(true);
        _textLabel.setAlignment(Align.center);
        this.add(_textLabel).expand().fill().padBottom(8);
        this.addActor(_button);
    }

    public void setEnabled(final boolean enabled){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _shader = enabled ? null : _shaders.getBlackOverlay();
            }
        });
        _enabled = enabled;

    }

    public void animate(){
        if(this._enabled){
            Vector2 originalPosition = new Vector2(this.getX(), this.getY());
            this.setSize(_size.x, _size.y - 40);
            this.setPosition(originalPosition.x, originalPosition.y + 10);
            float duration = 0.2f;
            Threadings.renderFor(duration + 0.1f);
            this.addAction(parallel(
                    fadeIn(duration, Interpolation.sineIn),
                    sizeTo(_size.x, _size.y, duration, Interpolation.bounceOut),
                    moveTo(originalPosition.x, originalPosition.y, duration)
            ));
        }
    }

    public float getWidth() {
        return _size.x;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(_shader!=null){
            batch.setShader(_shader);
            if (!_shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + _shader.getLog());
        }

        super.draw(batch, parentAlpha);
        if(_shader!=null) batch.setShader(null);
    }
}
