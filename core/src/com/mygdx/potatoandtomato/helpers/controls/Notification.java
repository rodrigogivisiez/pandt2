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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.IPTGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

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

    public Notification(SpriteBatch _batch, Assets _assets, IPTGame _game) {
        this._batch = _batch;
        this._assets = _assets;
        this._game = _game;

        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _root = new Table();
        _root.setHeight(Positions.getHeight());
        _root.setWidth(200);
        _root.setPosition(Positions.getWidth() - 200, 0);

        _stage.addActor(_root);
        _game.addInputProcessor(_stage, 20);
    }

    public void important(String msg){
        showNotification(msg, Color.RED);
    }

    public void info(String msg){
        showNotification(msg, Color.BLUE);
    }

    private void showNotification(String msg, Color color){
        final Table childTable = new Table();
        childTable.setBackground(new NinePatchDrawable(_assets.getWhiteRoundedBg()));
        childTable.setSize(200, 40);
        childTable.pad(10);
        childTable.padRight(20);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        if(color == Color.RED){
            labelStyle.font = _assets.getRedBold2();
        }
        else if(color == Color.BLUE){
            labelStyle.font = _assets.getBlueBold2();
        }

        Label labelMsg = new Label(msg, labelStyle);
        labelMsg.setWrap(true);
        childTable.add(labelMsg).expand().fill();
        childTable.setPosition(210, Positions.getHeight() - 40 - 40);

        for(Actor actor : _root.getChildren()){
            actor.addAction(moveBy(0, -60, 0.2f));
        }

        _root.addActor(childTable);

        childTable.addAction(sequence(moveBy(-200, 0, 0.2f), delay(5), moveBy(200, 0, 0.2f), new Action() {
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
                childTable.addAction(sequence(moveBy(200, 0, 0.2f), new Action() {
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

}
