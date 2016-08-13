package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
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
 * Created by SiongLeng on 2/8/2016.
 */
public class Loggings {

    private Stage stage;
    private SpriteBatch batch;
    private Assets assets;
    private Broadcaster broadcaster;
    private IPTGame game;
    private boolean enabled;
    private ScrollPane scrollPane;

    private Table root;

    public Loggings(SpriteBatch _batch, Assets _assets, IPTGame _game, Broadcaster _broadcaster) {
        this.batch = _batch;
        this.assets = _assets;
        this.game = _game;
        this.broadcaster = _broadcaster;

        populate();
        invalidate();

        _broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
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
                root.align(Align.top);
                root.setWidth(200);
                root.setDebug(true);

                scrollPane = new ScrollPane(root);
                scrollPane.setVisible(false);

            }
        });
    }

    public void invalidate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {


                if(stage == null){
                    StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                    viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                    stage = new Stage(viewPort, batch);
                    game.addInputProcessor(stage, 25, false);
                    stage.addActor(scrollPane);
                }
                else{
                    if(stage.getViewport().getWorldWidth() != Positions.getWidth()
                            || stage.getViewport().getWorldHeight() != Positions.getHeight()){
                        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                        viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                        stage.setViewport(viewPort);
                    }
                }


                scrollPane.setHeight(200);
                scrollPane.setWidth(root.getWidth());
                scrollPane.setPosition(Positions.getWidth() - root.getWidth(), Positions.getHeight() - scrollPane.getHeight() - 20);

            }
        });
    }

    public void add(final String msg){
        if(!enabled) return;

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD),
                        Color.RED);
                Label label = new Label(msg, labelStyle);
                label.setWrap(true);
                label.setAlignment(Align.left);

                root.add(label).expandX().fillX().padTop(10);
                root.row();

                scrollPane.setScrollPercentY(100);
            }
        });
    }

    public void render(float delta){
        if(enabled){
            try{
                stage.act(delta);
                stage.draw();
            }
            catch (Exception e){

            }
        }

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        scrollPane.setVisible(enabled);
        add("Loggings enabled");
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

}
