package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


/**
 * Created by SiongLeng on 13/6/2016.
 */
public class CoinMachineControl {

    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private Table root, coinInsertAnimationTable, coinInsertRootTable;
    private Stage stage;
    private Image coinAngleImage, coinFlatImage;
    private IPTGame iptGame;



    public CoinMachineControl(Assets assets, SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.iptGame = iptGame;

        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                root = new Table();
                root.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.COIN_MACHINE_ROOT_BG)));
                root.setSize(174, 107);
                root.setPosition(Positions.getWidth() - root.getWidth(), 150);

                ///////////////////////////////
                //Left Users table
                ///////////////////////////////
                Table usersTable = new Table();


                ///////////////////////////////
                //Right coin insert Table
                ///////////////////////////////
                coinInsertRootTable = new Table();
                coinInsertRootTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.COIN_MACHINE_BG)));
                new DummyButton(coinInsertRootTable, assets);

                Table coinTable = new Table();
                coinTable.align(Align.left);
                Image blinkingImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_BLINK_BG));
                blinkingImage.setFillParent(true);
                blinkingImage.addAction(forever(sequence(fadeIn(1f), fadeOut(1f))));

                Image holeImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_HOLE));

                Color labelColor = Color.valueOf("c7b7a9");
                Table labelsTable = new Table();
                Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.IMPACT_M_REGULAR), labelColor);

                Label insertLabel = new Label(texts.insert().toUpperCase(), labelStyle);
                Image separatorImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_SEPARATOR));
                separatorImage.setColor(labelColor);

                Label coinLabel = new Label(texts.coin().toUpperCase(), labelStyle);
                labelsTable.add(insertLabel);
                labelsTable.row();
                labelsTable.add(separatorImage);
                labelsTable.row();
                labelsTable.add(coinLabel);

                coinInsertAnimationTable = new Table();
                coinInsertAnimationTable.setClip(true);
                coinInsertAnimationTable.setSize(20, 60);
                coinInsertAnimationTable.setPosition(0, 7);

                coinAngleImage = new Image(assets.getTextures().get(Textures.Name.COIN_WITH_ANGLE));
                coinAngleImage.getColor().a = 0f;
                coinFlatImage = new Image(assets.getTextures().get(Textures.Name.COIN_FLAT));
                coinFlatImage.setX(9);
                coinFlatImage.getColor().a = 0f;
                coinInsertAnimationTable.addActor(coinAngleImage);
                coinInsertAnimationTable.addActor(coinFlatImage);

                coinTable.addActor(blinkingImage);
                coinTable.add(holeImage).pad(2, 2, 2, 0);
                coinTable.add(labelsTable).expand().fill();
                coinTable.addActor(coinInsertAnimationTable);

                coinInsertRootTable.add(coinTable).expand().fill().pad(8);

                /////////////////////////////
                //root populations
                //////////////////////////

                root.add(usersTable).uniformX().expand().fill();
                root.add(coinInsertRootTable).uniformX().expand().fill();
                root.pad(5, 5, 13, 5);

                stage = new Stage(new StretchViewport(Positions.getWidth(), Positions.getHeight()));
                stage.addActor(root);

                iptGame.addInputProcessor(stage, 11);
            }
        });
    }

    public void putCoin(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                coinAngleImage.addAction(sequence(fadeIn(0f), moveBy(1f, 0f, 0.1f), fadeOut(0.1f), new RunnableAction(){
                    @Override
                    public void run() {
                        coinFlatImage.addAction(sequence(fadeIn(0f), moveBy(0f, -70f, 0.5f, Interpolation.exp5Out), new RunnableAction(){
                            @Override
                            public void run() {
                                coinFlatImage.getColor().a = 0f;
                                coinFlatImage.setY(0);
                                coinAngleImage.setX(0);
                            }
                        }));
                    }
                }));
            }
        });
    }

    public void render(float delta){
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height){
    }


    public Table getCoinInsertRootTable() {
        return coinInsertRootTable;
    }
}
