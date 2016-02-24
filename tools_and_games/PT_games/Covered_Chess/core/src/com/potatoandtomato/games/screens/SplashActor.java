package com.potatoandtomato.games.screens;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Strings;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.statics.Global;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * Created by SiongLeng on 24/2/2016.
 */
public class SplashActor extends Table {

    private Assets _assets;
    private Texts _texts;
    private GameCoordinator _coordinator;

    public SplashActor(GameCoordinator gameCoordinator, Assets assets, Texts texts) {
        this._coordinator = gameCoordinator;
        this._assets = assets;
        this._texts = texts;
    }

    public void populate(){
        this.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SPLASH_BG)));
        this.align(Align.top);

        /////////////////////////////
        //VS labels
        /////////////////////////////
        Label labelVs = new Label(_texts.vs(), new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontName.PIZZA, Fonts.FontSize.XXL, Fonts.FontColor.WHITE), null));

        Label labelYellowUser = new Label(Strings.cut(_coordinator.getPlayerByUniqueIndex(0).getName(), 8), new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontName.HELVETICA, Fonts.FontSize.MAX, Fonts.FontColor.WHITE,
                        Fonts.FontStyle.BlACK_CONDENSED_ITALIC, Fonts.FontBorderColor.ORANGE, Fonts.FontShadowColor.BLACK), null));

        Label labelRedUser = new Label(Strings.cut(_coordinator.getPlayerByUniqueIndex(1).getName(), 8), new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontName.HELVETICA, Fonts.FontSize.MAX, Fonts.FontColor.WHITE,
                        Fonts.FontStyle.BlACK_CONDENSED_ITALIC, Fonts.FontBorderColor.RED, Fonts.FontShadowColor.BLACK), null));


        Table tableVs = new Table();
        tableVs.getColor().a = 0f;
        tableVs.addAction(sequence(delay(3f, fadeIn(0.3f))));

        tableVs.add(labelYellowUser).padRight(10).uniformX();
        tableVs.add(labelVs);
        tableVs.add(labelRedUser).padLeft(10).uniformX();


        this.add(tableVs).expandX().fillX().padTop(180);



        /////////////////////////////
        //Chesses
        /////////////////////////////
        Image yellowWolf = new Image(_assets.getTextures().get(Textures.Name.YELLOW_WOLF_SPLASH));
        Image redWolf = new Image(_assets.getTextures().get(Textures.Name.RED_WOLF_SPLASH));
        int wolfSize = 105;
        int wolfX = 10;
        yellowWolf.setSize(wolfSize, wolfSize);
        redWolf.setSize(wolfSize, wolfSize);
        yellowWolf.setPosition(-wolfSize, 100);
        redWolf.setPosition(_coordinator.getGameWidth(), 100);

        this.addActor(yellowWolf);
        this.addActor(redWolf);

        yellowWolf.addAction(sequence(delay(0.2f), Actions.moveBy(wolfX + wolfSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-2, 0, 0.2f), Actions.moveBy(2, 0, 0.2f)))));
        redWolf.addAction(sequence(delay(0.2f), Actions.moveBy(-wolfX - wolfSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-2, 0, 0.2f), Actions.moveBy(2, 0, 0.2f)))));

        Image yellowTiger = new Image(_assets.getTextures().get(Textures.Name.YELLOW_TIGER_SPLASH));
        Image redTiger = new Image(_assets.getTextures().get(Textures.Name.RED_TIGER_SPLASH));
        int tigerSize = 90;
        int tigerX = 45;
        yellowTiger.setSize(tigerSize, tigerSize);
        redTiger.setSize(tigerSize, tigerSize);
        yellowTiger.setPosition(-tigerSize, 205);
        redTiger.setPosition(_coordinator.getGameWidth(), 205);

        this.addActor(yellowTiger);
        this.addActor(redTiger);

        yellowTiger.addAction(sequence(delay(0.3f), Actions.moveBy(tigerX + tigerSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-2, 0, 0.1f), Actions.moveBy(2, 0, 0.1f)))));
        redTiger.addAction(sequence(delay(0.3f), Actions.moveBy(-tigerX - tigerSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-2, 0, 0.1f), Actions.moveBy(2, 0, 0.1f)))));

        Image yellowLion = new Image(_assets.getTextures().get(Textures.Name.YELLOW_LION_SPLASH));
        Image redLion = new Image(_assets.getTextures().get(Textures.Name.RED_LION_SPLASH));
        int lionSize = 80;
        int lionX = 70;
        yellowLion.setSize(lionSize, lionSize);
        redLion.setSize(lionSize, lionSize);
        yellowLion.setPosition(-lionSize, 295);
        redLion.setPosition(_coordinator.getGameWidth(), 295);

        this.addActor(yellowLion);
        this.addActor(redLion);

        yellowLion.addAction(sequence(delay(0.6f), Actions.moveBy(lionX + lionSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-1, 0, 0.1f), Actions.moveBy(1, 0, 0.1f)))));
        redLion.addAction(sequence(delay(0.6f), Actions.moveBy(-lionX - lionSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-1, 0, 0.1f), Actions.moveBy(1, 0, 0.1f)))));
    }

    public void fadeOutActor(final Runnable runnable){
        this.addAction(sequence(delay(Global.NO_ENTRANCE ? 0f : 2f), fadeOut(Global.NO_ENTRANCE ? 0f : 1f), new Action(){

            @Override
            public boolean act(float delta) {
                runnable.run();
                return true;
            }
        }));
    }



}
