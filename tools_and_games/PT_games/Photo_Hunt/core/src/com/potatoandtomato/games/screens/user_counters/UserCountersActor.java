package com.potatoandtomato.games.screens.user_counters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class UserCountersActor extends Table {

    private Table _this;
    private MyAssets assets;
    private Services services;
    private GameCoordinator gameCoordinator;
    private HashMap<String, Table> userIdToTableMap;
    private Label.LabelStyle counterLabelStyle;

    public UserCountersActor(Services services, GameCoordinator gameCoordinator) {
        _this = this;
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.assets = services.getAssets();

        this.userIdToTableMap = new HashMap();

        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                counterLabelStyle = new Label.LabelStyle();
                counterLabelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
                counterLabelStyle.fontColor = Color.BLACK;


                Label.LabelStyle nameLabelStyle = new Label.LabelStyle();
                nameLabelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_XS_REGULAR);
                nameLabelStyle.fontColor = Color.BLACK;

                ConcurrentHashMap<Integer, Player> playerHashMap = gameCoordinator.getIndexToPlayersConcurrentMap();

                for (int i = 0; i < 8 ; i++) {
                    Table userTable = new Table();
                    userTable.setClip(true);
                    userTable.getColor().a = 0.8f;
                    userTable.setBackground(new TextureRegionDrawable(assets.getTextures().getPlayerCountBg(i)));

                    Table counterTable = new Table();
                    counterTable.setFillParent(true);
                    new DummyButton(counterTable, assets);
                    counterTable.setName("counterTable");
                    Label counterLabel = new Label("0", counterLabelStyle);
                    counterTable.add(counterLabel);

                    userTable.addActor(counterTable);

                    if(playerHashMap.containsKey(i)){
                        userIdToTableMap.put(playerHashMap.get(i).getUserId(), userTable);

                        Table userNameTable = new Table();
                        userNameTable.pad(3);
                        userNameTable.align(Align.left);
                        userNameTable.setFillParent(true);
                        userNameTable.setName("userNameTable");
                        Label userNameLabel = new Label(Strings.cutOff(playerHashMap.get(i).getName(), 6), nameLabelStyle);
                        userNameLabel.setAlignment(Align.center);
                        userNameLabel.setWrap(true);
                        userNameTable.add(userNameLabel).expand().fill();
                        userNameTable.setVisible(false);

                        userTable.addActor(userNameTable);

                    }
                    _this.add(userTable).uniform().padRight(10);
                    if(i == 3) _this.row();
                }

            }
        });


    }

    public void updateCounter(final String userId, final int updatedCounter){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table userTable = userIdToTableMap.get(userId);
                if(userTable == null) return;

                Table counterTable = userTable.findActor("counterTable");

                final Actor oldLabel = counterTable.getChildren().get(counterTable.getChildren().size - 1);
                if(oldLabel != null){
                    oldLabel.addAction(sequence(Actions.moveBy(0, 15, 0.1f), new RunnableAction(){
                        @Override
                        public void run() {
                            oldLabel.remove();
                        }
                    }));
                }

                Label newLabel = new Label(String.valueOf(updatedCounter), counterLabelStyle);
                newLabel.setPosition(counterTable.getWidth() / 2 - newLabel.getPrefWidth() / 2, -20);

                counterTable.addActor(newLabel);

                newLabel.addAction(sequence(Actions.moveTo(newLabel.getX(), counterTable.getHeight() / 2 - newLabel.getPrefHeight() /2, 0.1f)));
            }
        });
    }

    public void updateSorting(final ArrayList<String> userIdsSortedByRecord){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                HashMap<Integer, Vector2> originalPositions = new HashMap();
                for(int i = 0; i < _this.getCells().size; i++){
                    Actor actor = _this.getCells().get(i).getActor();
                    originalPositions.put(i, new Vector2(actor.getX(), actor.getY()));
                }

                for(int i =0; i < userIdsSortedByRecord.size(); i++){

                    final Table userTable = (Table) _this.getCells().get(i).getActor();
                    final Table movingTable = userIdToTableMap.get(userIdsSortedByRecord.get(i));

                    if(movingTable == null) continue;

                    int movingIndex = 0;
                    for(int q = 0; q < _this.getCells().size; q++){
                        if(_this.getCells().get(q).getActor() == movingTable){
                            movingIndex = q;
                            break;
                        }
                    }

                    _this.getCells().get(i).setActor(null);
                    _this.getCells().get(movingIndex).setActor(null);
                    _this.getCells().get(i).setActor(movingTable);
                    _this.getCells().get(movingIndex).setActor(userTable);

                    if(movingTable != userTable){
                        Vector2 finalPosition = originalPositions.get(i);
                        movingTable.addAction(sequence(Actions.moveTo(finalPosition.x, finalPosition.y, 0.8f, Interpolation.exp10Out)));

                        Vector2 finalPosition2 = originalPositions.get(movingIndex);
                        userTable.addAction(sequence(Actions.moveTo(finalPosition2.x, finalPosition2.y, 0.8f, Interpolation.exp10Out)));
                    }

                }
            }
        });
    }

    public void setUserNameVisibility(final String userId, final boolean visible){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table userTable = userIdToTableMap.get(userId);
                Table counterTable = userTable.findActor("counterTable");
                Table userNameTable = userTable.findActor("userNameTable");

                counterTable.setVisible(!visible);
                userNameTable.setVisible(visible);
            }
        });

    }

    public HashMap<String, Table> getUserIdToTableMap() {
        return userIdToTableMap;
    }
}
