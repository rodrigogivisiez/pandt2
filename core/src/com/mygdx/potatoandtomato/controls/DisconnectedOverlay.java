package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 3/6/2016.
 */
public class DisconnectedOverlay {
    private Stage stage;
    private SpriteBatch batch;
    private Assets assets;
    private Table root;
    private Broadcaster broadcaster;
    private Label labelMessage;
    private Texts texts;
    private IPTGame iptGame;
    private boolean visible;

    public DisconnectedOverlay(SpriteBatch batch, Assets assets, Broadcaster broadcaster, Texts texts, IPTGame game) {
        this.batch = batch;
        this.assets = assets;
        this.broadcaster = broadcaster;
        this.texts = texts;
        this.iptGame = game;

        invalidate();
        populate();

        broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                invalidate();
            }
        });
    }

    public void invalidate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(root == null){
                    root = new Table();
                    new DummyButton(root, assets);
                    root.setFillParent(true);
                    root.setBackground(new TextureRegionDrawable(
                                assets.getTextures().get(assets.getTextures().get(Textures.Name.FULL_BLACK_BG))));

                    root.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {

                        }
                    });
                }

                if (stage != null) {
                    stage.dispose();
                    root.remove();
                    iptGame.removeInputProcessor(stage);
                }

                StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                stage = new Stage(viewPort, batch);

                stage.addActor(root);
            }
        });
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                labelMessage = new Label(texts.lostConnection(),
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD), Color.WHITE));
                labelMessage.setWrap(true);
                labelMessage.setAlignment(Align.center);
                root.add(labelMessage).expand().fill().pad(50);
            }
        });
    }

    public void showResumingGameText(final int remainingMiliSecs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                labelMessage.setText(String.format(texts.connectionRecovered(), remainingMiliSecs / 1000));
            }
        });
    }

    public void resetText(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                labelMessage.setText(texts.lostConnection());
            }
        });
    }


    public void render(float delta){
        if(visible){
            stage.act(delta);
            stage.draw();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;

        if(visible && stage != null){
            iptGame.addInputProcessor(stage, 12);
        }
        else{
            iptGame.removeInputProcessor(stage);
        }
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

}
