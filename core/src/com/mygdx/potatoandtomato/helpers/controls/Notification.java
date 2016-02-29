package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 7/1/2016.
 */
public class Notification {

    private Stage _stage;
    private SpriteBatch _batch;
    private Assets _assets;
    private IPTGame _game;
    private int _showingNotification;
    private Table _root;
    private Broadcaster _broadcaster;

    public Notification(SpriteBatch _batch, Assets _assets, IPTGame _game, Broadcaster _broadcaster) {
        this._batch = _batch;
        this._assets = _assets;
        this._game = _game;
        this._broadcaster = _broadcaster;

        _root = new Table();

       invalidate();

        _broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                invalidate();
            }
        });
    }

    public void invalidate(){
        if(_stage != null){
            _game.removeInputProcessor(_stage);
            _stage.dispose();
            _root.remove();
        }

        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _root.setHeight(Positions.getHeight());
        _root.align(Align.top);
        _root.setWidth(200);
        _root.setPosition(Positions.getWidth() - _root.getWidth(), 0);
        _root.invalidate();

        _stage.addActor(_root);
        _game.addInputProcessor(_stage, 20);

        reposition();
    }

    public void important(String msg){
        showNotification(msg, Color.RED);
    }

    public void info(String msg){
        showNotification(msg, Color.BLUE);
    }

    private void showNotification(String msg, Color color){

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        if(color == Color.RED){
            labelStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD, Fonts.FontSize.S, Fonts.FontColor.RED);
        }
        else if(color == Color.BLUE){
            labelStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD, Fonts.FontSize.S, Fonts.FontColor.BLUE);
        }

        final Label labelMsg = new Label(msg, labelStyle);
        labelMsg.setWrap(true);
        labelMsg.setWidth(_root.getWidth());
        labelMsg.layout();
        float tableHeight = labelMsg.getPrefHeight() + 30;

        final Table childTable = new Table();
        childTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.WHITE_ROUNDED_BG)));
        childTable.setTransform(true);
        childTable.setWidth(labelMsg.getWidth());
        childTable.setHeight(tableHeight);
        childTable.pad(10);
        childTable.padRight(20);
        childTable.setPosition(labelMsg.getWidth() + 10, _root.getHeight() - tableHeight);

        childTable.add(labelMsg).expand().fill().center();

        _root.addActor(childTable);
        reposition();

        childTable.addAction(sequence(delay(5), moveBy(labelMsg.getWidth(), 0, 0.2f), new Action() {
            @Override
            public boolean act(float delta) {
                childTable.remove();
                _showingNotification--;
                return true;
            }
        }));

        childTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                childTable.clearActions();
                childTable.addAction(sequence(moveBy(labelMsg.getWidth(), 0, 0.2f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        childTable.remove();
                        _showingNotification--;
                        return true;
                    }
                }));
            }
        });

        _showingNotification++;
    }

    private void reposition(){
        float y = 0;
        int spacing = 20;

        for(int i = _root.getChildren().size - 1; i >= 0; i--){
            Actor actor = _root.getChildren().get(i);
            if(y == 0){
                y = _root.getHeight() - actor.getHeight() - spacing;
            }
            else{
                y -= spacing;        //space between notification
                y -= actor.getHeight();
            }
            actor.addAction(moveTo(10, y, 0.2f));
        }
    }

    public void render(float delta){
        if(_showingNotification > 0){
            try{
                _stage.act(delta);
                _stage.draw();
            }
            catch (Exception e){

            }
        }

    }

    public void resize(int width, int height){
        _stage.getViewport().update(width, height);
    }

}
