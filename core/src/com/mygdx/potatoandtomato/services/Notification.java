package com.mygdx.potatoandtomato.services;

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
 * Created by SiongLeng on 7/1/2016.
 */
public class Notification {

    private Stage stage;
    private SpriteBatch batch;
    private Assets assets;
    private Broadcaster broadcaster;
    private SoundsPlayer soundsPlayer;
    private IPTGame game;

    private int showingNotification;
    private Table root;

    public Notification(SpriteBatch _batch, Assets _assets, IPTGame _game, Broadcaster _broadcaster, SoundsPlayer soundsPlayer) {
        this.batch = _batch;
        this.assets = _assets;
        this.game = _game;
        this.soundsPlayer = soundsPlayer;
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
                    game.addInputProcessor(stage, 20, false);
                    stage.addActor(root);
                }
                else{
                    if(stage.getViewport().getWorldWidth() != Positions.getWidth()
                            || stage.getViewport().getWorldHeight() != Positions.getHeight()){
                        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                        viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                        stage.setViewport(viewPort);
                    }
                }


                root.setHeight(Positions.getHeight());
                root.setPosition(Positions.getWidth() - root.getWidth(), 0);
                reposition();

            }
        });
    }

    public void important(String msg){
        showNotification(msg, Color.RED);
    }

    public void info(String msg){
        showNotification(msg, Color.BLUE);
    }

    private void showNotification(final String msg, final Color color){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label.LabelStyle labelStyle = new Label.LabelStyle();
                labelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
                labelStyle.fontColor = (color == Color.RED ?  Color.valueOf("e40404") : Color.valueOf("11b1bf"));

                final Label labelMsg = new Label(msg, labelStyle);
                labelMsg.setWrap(true);
                labelMsg.setWidth(root.getWidth());
                labelMsg.pack();
                labelMsg.setWidth(root.getWidth());
                float tableHeight = labelMsg.getHeight() + 15;

                final Table childTable = new Table();
                childTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.WHITE_ROUNDED_BG)));
                childTable.setTransform(true);
                childTable.setWidth(labelMsg.getWidth());
                childTable.setHeight(tableHeight);
                childTable.pad(10);
                childTable.padRight(20);
                childTable.setPosition(labelMsg.getWidth() + 10, root.getHeight() - tableHeight);

                childTable.add(labelMsg).expand().fill().center();

                root.addActor(childTable);
                reposition();

                childTable.addAction(sequence(delay(5), moveBy(labelMsg.getWidth(), 0, 0.2f), new RunnableAction() {
                    @Override
                    public void run() {
                        childTable.remove();
                        showingNotification--;
                    }
                }));

                childTable.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        childTable.clearActions();
                        childTable.addAction(sequence(moveBy(labelMsg.getWidth(), 0, 0.2f), new RunnableAction() {
                            @Override
                            public void run() {
                                childTable.remove();
                                showingNotification--;
                            }
                        }));
                    }
                });

                showingNotification++;
                soundsPlayer.playSoundEffect(Sounds.Name.NOTIFICATION);
            }
        });
    }

    private void reposition(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                float y = 0;
                int spacing = 20;

                for(int i = root.getChildren().size - 1; i >= 0; i--){
                    Actor actor = root.getChildren().get(i);
                    if(y == 0){
                        y = root.getHeight() - actor.getHeight() - spacing;
                    }
                    else{
                        y -= spacing;        //space between notification
                        y -= actor.getHeight();
                    }
                    actor.addAction(moveTo(10, y, 0.2f));
                }
            }
        });
    }

    public void render(float delta){
        if(showingNotification > 0){
            try{
                stage.act(delta);
                stage.draw();
            }
            catch (Exception e){

            }
        }

    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

}
