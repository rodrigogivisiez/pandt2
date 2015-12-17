package com.mygdx.potatoandtomato.scenes.mascot_pick_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.helpers.controls.Animator;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class MascotPickScene extends SceneAbstract {

    Label _questionLabel;
    Table _potatoTable, _tomatoTable;
    Image _potatoHiImg, _tomatoHiImg;
    Button _potatoButton, _tomatoButton;
    Animator _potatoHiAnimator, _tomatoHiAnimator;
    Button _nextSceneButton;

    public MascotPickScene(Services services) {
        super(services);
    }

    @Override
    public void populateRoot() {

        _nextSceneButton = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        _nextSceneButton.setFillParent(true);
        _nextSceneButton.setVisible(false);

        //Label Question START
        Label.LabelStyle lblPizzaStyle = new Label.LabelStyle();
        lblPizzaStyle.font = _fonts.getPizzaFont(28, Color.WHITE, 0, Color.BLACK, 2, Color.GRAY);
        _questionLabel = new Label(_texts.mascotQuestion() ,lblPizzaStyle);
        _questionLabel.setAlignment(Align.center);
        //Label Question END

        //Potato Mascot START
        _potatoTable = new Table();
        _potatoTable.setBackground(new TextureRegionDrawable(_textures.getWoodBgTall()));
        _potatoHiImg = new Image(_textures.getPotatoHi());
        Vector2 potatoSizes = Sizes.resize(140, _textures.getPotatoHi());
        _potatoTable.add(_potatoHiImg).size(potatoSizes.x, potatoSizes.y).align(Align.bottom).expand().padBottom(60);
        _potatoButton = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        _potatoButton.setFillParent(true);
        _potatoTable.addActor(_potatoButton);
        //Potato Mascot END

        //Tomato Mascot START
        _tomatoTable = new Table();
        _tomatoTable.setBackground(new TextureRegionDrawable(_textures.getWoodBgTall()));
        _tomatoHiImg = new Image(_textures.getTomatoHi());
        Vector2 tomatoSizes = Sizes.resize(140, _textures.getTomatoHi());
        _tomatoTable.add(_tomatoHiImg).size(tomatoSizes.x, tomatoSizes.y).align(Align.bottom).expand().padBottom(60);
        _tomatoButton = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        _tomatoButton.setFillParent(true);
        _tomatoTable.addActor(_tomatoButton);
        //Tomato Mascot END

        _root.add(_questionLabel).colspan(2).padBottom(20);
        _root.row();
        _root.add(_potatoTable).height(250).width(160).padRight(10);
        _root.add(_tomatoTable).height(250).width(160);
        _root.addActor(_nextSceneButton);
    }

    public Button getPotatoButton() {
        return _potatoButton;
    }

    public Button getTomatoButton() {
        return _tomatoButton;
    }

    public Button getNextSceneButton() {
        return _nextSceneButton;
    }

    public void choosedMascot(MascotEnum mascot){
        final String msg;
        if(mascot == MascotEnum.POTATO){
            _tomatoTable.addAction(sequence(moveBy(300, 0, 0.5f), new Action() {
                @Override
                public boolean act(float delta) {
                    _nextSceneButton.setVisible(true);
                    _tomatoTable.getColor().a = 0;
                    return true;
                }
            }));
            _potatoTable.addAction(moveBy(90, 0, 0.3f));
            msg = _texts.mascotPotato();
            _potatoHiImg.remove();
            _potatoHiAnimator = new Animator(0.2f, _textures.getPotatoHiAnimation());
            Vector2 size = Sizes.resize(140, _textures.getPotatoHiAnimation().first());
            _potatoTable.add(_potatoHiAnimator).size(size.x, size.y).align(Align.bottom).padBottom(60).padRight(10);
        }
        else{
            _potatoTable.addAction(sequence(moveBy(-300, 0, 0.5f), new Action() {
                @Override
                public boolean act(float delta) {
                    _nextSceneButton.setVisible(true);
                    _potatoTable.getColor().a = 0;
                    return true;
                }
            }));
            _tomatoTable.addAction(moveBy(-90, 0, 0.3f));
            msg = _texts.mascotTomato();
            _tomatoHiImg.remove();
            _tomatoHiAnimator = new Animator(0.2f, _textures.getTomatoHiAnimation());
            Vector2 size = Sizes.resize(140, _textures.getTomatoHiAnimation().first());
            _tomatoTable.add(_tomatoHiAnimator).size(size.x, size.y).align(Align.bottom).padBottom(60).padRight(10);
        }

        _questionLabel.setText(msg);
        _questionLabel.addAction(forever(sequence(fadeIn(1f), fadeOut(1f))));
    }


}
