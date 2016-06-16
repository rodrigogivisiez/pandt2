package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


/**
 * Created by SiongLeng on 13/6/2016.
 */
public class CoinMachineControl {

    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private Table root, coinInsertAnimationTable, coinInsertRootTable, usersTable;
    private Stage stage;
    private Image coinAngleImage, coinFlatImage;
    private IPTGame iptGame;
    private SpriteBatch batch;
    private boolean visible;


    public CoinMachineControl(Broadcaster broadcaster,
                              Assets assets, SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame, SpriteBatch batch) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.iptGame = iptGame;
        this.batch = batch;

        populate();

        invalidate();

        broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                invalidate();
            }
        });
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                root = new Table();
                root.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.COIN_MACHINE_ROOT_BG)));
                root.setSize(190, Sizes.resize(190, assets.getTextures().get(Textures.Name.COIN_MACHINE_ROOT_BG)).y);

                ///////////////////////////////
                //Left Users table
                ///////////////////////////////
                Table usersCoinsRootTable = new Table();
                usersCoinsRootTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.COIN_MACHINE_USERS_BG)));

                Label coinInsertedLabel = new Label(texts.coinsInsertedTitle().toUpperCase(),
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.HELVETICA_XXS_REGULAR), Color.valueOf("f7f7f7")));
                coinInsertedLabel.setAlignment(Align.center);

                Image usersSeparatorImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_SEPARATOR));
                usersSeparatorImage.setColor(Color.valueOf("7b7b7b"));

                usersTable = new Table();
                usersTable.align(Align.top);
                ScrollPane scrollPane = new ScrollPane(usersTable);
                scrollPane.setScrollingDisabled(true, false);

                usersCoinsRootTable.add(coinInsertedLabel).expandX().fillX();
                usersCoinsRootTable.row();
                usersCoinsRootTable.add(usersSeparatorImage).expandX().fillX().padTop(1).padBottom(1);
                usersCoinsRootTable.row();
                usersCoinsRootTable.add(scrollPane).expand().fill();

                usersCoinsRootTable.pad(3);

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

                root.add(usersCoinsRootTable).uniformX().expand().fill().pad(8, 10, 19, 8);
                root.add(coinInsertRootTable).uniformX().expand().fill().pad(8, 0, 18, 8);

            }
        });
    }

    public void invalidate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(stage != null){
                    stage.dispose();
                    iptGame.removeInputProcessor(stage);
                }
                root.remove();

                root.setPosition(Positions.getWidth() - root.getWidth(), Positions.getHeight() / 4);
                stage = new Stage(new StretchViewport(Positions.getWidth(), Positions.getHeight()), batch);
                stage.addActor(root);

                if(visible){
                    iptGame.addInputProcessor(stage, 11);
                }
            }
        });
    }

    public void show(){
        if(visible) return;

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                visible = true;
                root.clearActions();
                root.setX(Positions.getWidth() + 10);
                root.addAction(moveTo(Positions.getWidth() - root.getWidth(), root.getY(), 0.3f));

                iptGame.addInputProcessor(stage, 11);
            }
        });

    }

    public void hide(){
        if(!visible) return;

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                root.clearActions();
                root.addAction(sequence(moveTo(Positions.getWidth() + 10, root.getY(), 0.3f), new RunnableAction(){
                    @Override
                    public void run() {
                        visible = false;
                    }
                }));

                iptGame.removeInputProcessor(stage);
            }
        });

    }

    public void updateUserTable(final String userId, final String userName, final int insertedCoins, final boolean hasCoin){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Actor existUserTable = usersTable.findActor(userId);

                Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.HELVETICA_XS_REGULAR),
                        Color.valueOf("f7f7f7"));

                Table userTable = new Table();
                userTable.setName(userId);
                Label usernameLabel = new Label(Strings.cutOff(userName, 10), labelStyle);

                Table coinTable = new Table();
                if(hasCoin || insertedCoins > 0){
                    Image coinImage = new Image(assets.getTextures().get(Textures.Name.COIN_ICON_SMALL));
                    Label coinCountLabel = new Label(String.format("x%s", insertedCoins), labelStyle);
                    coinTable.add(coinImage).padRight(1);
                    coinTable.add(coinCountLabel);
                }
                else{
                    Image coinImage = new Image(assets.getTextures().get(Textures.Name.NO_COIN_ICON_SMALL));
                    coinTable.add(coinImage);
                }

                userTable.add(usernameLabel).expandX().fillX().padRight(2);
                userTable.add(coinTable);

                if(existUserTable != null){
                    Cell cell = usersTable.getCell(existUserTable);
                    cell.setActor(userTable);
                }
                else{
                    usersTable.add(userTable).expandX().fillX().padLeft(3).padRight(3);
                    usersTable.row();
                }
            }
        });
    }

    public void putCoinAnimation(final Runnable onFinish){
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
                                if(onFinish!= null) onFinish.run();
                            }
                        }));
                    }
                }));
            }
        });
    }

    public void render(float delta){
        if(visible){
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }


    public Table getCoinInsertRootTable() {
        return coinInsertRootTable;
    }
}
