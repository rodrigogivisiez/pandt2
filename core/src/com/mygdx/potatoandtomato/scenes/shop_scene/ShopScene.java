package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Animations;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;

import java.lang.reflect.AccessibleObject;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopScene extends SceneAbstract {

    private Image arcadeWorldImage;

    public ShopScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        TopBar topBar = new TopBar(_root, _services.getTexts().shopTitle(), false, _assets, _screen, _services.getCoins());
        topBar.setDarkTheme();

        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));

        Image floorImage = new Image(_assets.getTextures().get(Textures.Name.FLOOR_BG));
        _root.addActor(floorImage);

        _root.align(Align.top);

        Image arcadeBgImage = new Image(_assets.getTextures().get(Textures.Name.ARCADE_BG));
        arcadeBgImage.setPosition(0, 405);
        _root.addActor(arcadeBgImage);

        arcadeWorldImage = new Image(_assets.getTextures().get(Textures.Name.SHOP_ARCADE_WORLD));
        arcadeWorldImage.getColor().a = 0.5f;

        arcadeWorldImage.addAction(forever(sequence(fadeIn(1f), alpha(0.5f, 1f))));

        Table mascotsTable = new Table();
        Image tomatoImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_SUNGLASS));
        tomatoImage.setOrigin(Align.bottom);
        Image potatoImage = new Image(_assets.getTextures().get(Textures.Name.POTATO_SUNGLASS));
        potatoImage.setOrigin(Align.bottom);
        mascotsTable.add(potatoImage).size(potatoImage.getPrefWidth(), potatoImage.getPrefHeight()).padRight(8);
        mascotsTable.add(tomatoImage).size(tomatoImage.getPrefWidth(), tomatoImage.getPrefHeight());

        tomatoImage.addAction(forever(sequence(Actions.rotateBy(-1f, 0.5f), Actions.rotateBy(1f, 0.5f))));
        potatoImage.addAction(forever(sequence(Actions.rotateBy(-0.5f, 0.4f), Actions.rotateBy(0.5f, 0.4f))));

        Table shopTable = new Table();
        shopTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SHOP_TABLE)));

        final Animator potatoFlipCoinAnimator = new Animator(0.025f, _assets.getAnimations().get(Animations.Name.POTATO_FLIP_COIN), false);
        potatoFlipCoinAnimator.setPosition(83, 358);
        potatoFlipCoinAnimator.setPaused(true);
        potatoFlipCoinAnimator.addAction(forever(sequence(Actions.rotateBy(-1f, 0.4f), Actions.rotateBy(1f, 0.4f))));

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Threadings.sleep(MathUtils.random(6, 20) * 1000);
                    potatoFlipCoinAnimator.setPaused(false);
                    potatoFlipCoinAnimator.replay();
                }
            }
        });

        Image tomatoLeftHandImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_SUNGLASS_LEFT_HAND));
        tomatoLeftHandImage.setPosition(173, 338);
        tomatoLeftHandImage.setOrigin(Align.center);

        Image tomatoRightHandImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_SUNGLASS_RIGHT_HAND));
        tomatoRightHandImage.setOrigin(Align.bottom);
        tomatoRightHandImage.setPosition(241, 341);

        tomatoLeftHandImage.addAction(forever(sequence(parallel(Actions.moveBy(-1f, 2f, 1f), Actions.rotateBy(2f, 1f)),
                parallel(Actions.moveBy(1f, -2f, 0.3f), Actions.rotateBy(-2f, 0.3f)))));
        tomatoRightHandImage.addAction(forever(sequence(Actions.rotateBy(-2f, 0.4f), Actions.rotateBy(2f, 0.4f))));

        _root.add(arcadeWorldImage).padTop(13);
        _root.row();
        _root.add(mascotsTable).expandX().fillX().padTop(59);
        _root.row();
        _root.add(shopTable).expand().fill().padTop(-8);
        _root.addActor(potatoFlipCoinAnimator);
        _root.addActor(tomatoLeftHandImage);
        _root.addActor(tomatoRightHandImage);

    }
}
