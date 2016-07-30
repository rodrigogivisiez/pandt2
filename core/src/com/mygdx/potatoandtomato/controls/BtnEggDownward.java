package com.mygdx.potatoandtomato.controls;

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
import com.mygdx.potatoandtomato.services.Shaders;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.assets.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class BtnEggDownward extends Table {

    BtnEggDownward _this;
    Button _button;
    Assets _assets;
    Vector2 _size;
    Image _contentImg;
    Label _textLabel;
    Shaders _shaders;
    ShaderProgram _shader;
    boolean _enabled;
    SoundsPlayer _soundsWrapper;

    public BtnEggDownward(Assets assets, SoundsPlayer soundsWrapper) {
        this(assets, soundsWrapper, null);
    }

    public BtnEggDownward(Assets assets, SoundsPlayer soundsWrapper, Shaders shaders) {
        _this = this;
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
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        animate();
                    }
                });

                if(_enabled) _soundsWrapper.playSoundEffect(Sounds.Name.BUTTON_CLICKED);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public void setContent(final TextureRegion textureRegion){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.clearChildren();
                _contentImg = new Image(textureRegion);
                _this.align(Align.center);
                _this.add(_contentImg).padLeft(15).expandY();
            }
        });
    }

    public void setText(final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.clearChildren();
                Label.LabelStyle textLabelStyle = new Label.LabelStyle();

                textLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XL_REGULAR_S_a05e00_1_1);

                _textLabel = new Label(text, textLabelStyle);
                _textLabel.setWrap(true);
                _textLabel.setAlignment(Align.center);
                _this.add(_textLabel).expand().fill().padBottom(8);
                _this.addActor(_button);
            }
        });
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
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_this._enabled){
                    Vector2 originalPosition = new Vector2(_this.getX(), _this.getY());
                    _this.setSize(_size.x, _size.y - 40);
                    _this.setPosition(originalPosition.x, originalPosition.y + 10);
                    float duration = 0.2f;
                    _this.addAction(parallel(
                            fadeIn(duration, Interpolation.sineIn),
                            sizeTo(_size.x, _size.y, duration, Interpolation.bounceOut),
                            moveTo(originalPosition.x, originalPosition.y, duration)
                    ));
                }
            }
        });
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
