package com.potatoandtomato.games.screens;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.services.Texts;
import com.potatoandtomato.games.statics.Global;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 24/2/2016.
 */
public class SplashActor extends Table {

    private SplashActor _this;
    private Assets _assets;
    private Texts _texts;
    private GameCoordinator _coordinator;

    public SplashActor(GameCoordinator gameCoordinator, Assets assets, Texts texts) {
        _this = this;
        this._coordinator = gameCoordinator;
        this._assets = assets;
        this._texts = texts;
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SPLASH_BG)));
                _this.align(Align.top);

                /////////////////////////////
                //VS labels
                /////////////////////////////
                Label labelVs = new Label(_texts.vs(), new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR), null));

                Player yellowPlayer = _coordinator.getPlayerByUniqueIndex(0);
                Player redPlayer = _coordinator.getPlayerByUniqueIndex(1);

                String yellowName = _texts.getRandomBotName();
                String redName = _texts.getRandomBotName();

                if(yellowPlayer != null) yellowName = Strings.cutOff(_coordinator.getPlayerByUniqueIndex(0).getName(), 9);
                if(redPlayer != null) redName = Strings.cutOff(_coordinator.getPlayerByUniqueIndex(1).getName(), 9);

                Label labelYellowUser = new Label(yellowName, new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_MAX_BlACKCONDENSEDITALIC_B_ffffff_f0c266_2_S_000000_1_1), null));

                Label labelRedUser = new Label(redName, new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_MAX_BlACKCONDENSEDITALIC_B_ffffff_f46767_2_S_000000_1_1), null));


                Table tableVs = new Table();
                tableVs.getColor().a = 0f;
                tableVs.addAction(sequence(delay(3f, fadeIn(0.3f))));

                tableVs.add(labelYellowUser).padRight(10).uniformX();
                tableVs.add(labelVs);
                tableVs.add(labelRedUser).padLeft(10).uniformX();

                _this.add(tableVs).expandX().fillX().padTop(180);

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

                _this.addActor(yellowWolf);
                _this.addActor(redWolf);

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

                _this.addActor(yellowTiger);
                _this.addActor(redTiger);

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

                _this.addActor(yellowLion);
                _this.addActor(redLion);

                yellowLion.addAction(sequence(delay(0.6f), Actions.moveBy(lionX + lionSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-1, 0, 0.1f), Actions.moveBy(1, 0, 0.1f)))));
                redLion.addAction(sequence(delay(0.6f), Actions.moveBy(-lionX - lionSize, 0, 0.5f, Interpolation.fade), forever(sequence(Actions.moveBy(-1, 0, 0.1f), Actions.moveBy(1, 0, 0.1f)))));
            }
        });
    }

    public void fadeOutActor(final Runnable runnable){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.addAction(sequence(delay(Global.NO_ENTRANCE ? 0f : 2f), fadeOut(Global.NO_ENTRANCE ? 0f : 1f), new RunnableAction(){
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }));
            }
        });
    }
}
