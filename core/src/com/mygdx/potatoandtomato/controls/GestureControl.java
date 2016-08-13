package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.enums.GestureType;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * Created by SiongLeng on 26/7/2016.
 */
public class GestureControl extends Table {

    private Table _this;
    private GestureType gestureType;
    private Assets assets;
    private int distanceX, distanceY;

    public GestureControl(GestureType gestureType, int distanceX, int distanceY, Assets assets) {
        _this = this;
        this.gestureType = gestureType;
        this.assets = assets;
        _this.setTouchable(Touchable.disabled);
        this.distanceX = distanceX;
        this.distanceY = distanceY;

        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(gestureType == GestureType.PointRight){
                    Image handImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_HAND));
                    handImage.setPosition(-26, 35);
                    _this.addActor(handImage);

                    _this.setSize(30, 45);
                    handImage.setRotation(-90);
                }
                else if(gestureType == GestureType.PointUp){
                    Image handImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_HAND));
                    handImage.setPosition(7, -26);
                    _this.addActor(handImage);

                    handImage.addAction(forever(sequence(Actions.moveBy(0, 2f, 0.3f),
                            Actions.moveBy(0, -2f, 0.3f))));

                    _this.setSize(30, 45);
                }
                else if(gestureType == GestureType.Tap){
                    Image buttonImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_BUTTON));
                    final Image traceImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_TRACE));
                    Image handImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_HAND));
                    handImage.setPosition(7, -26);

                    traceImage.getColor().a = 0f;
                    _this.add(traceImage);
                    _this.row();
                    _this.add(buttonImage);
                    _this.row();
                    _this.addActor(handImage);

                    _this.setSize(30, 45);

                    handImage.addAction(forever(sequence(
                            Actions.moveBy(0, -4f, 0f), new RunnableAction(){
                                @Override
                                public void run() {
                                    traceImage.getColor().a = 0f;
                                }
                            },
                            Actions.moveBy(0, 4f, 0.3f), new RunnableAction() {
                        @Override
                        public void run() {
                            traceImage.getColor().a = 1f;
                        }
                    }, delay(1f))));

                }
                else if(gestureType == GestureType.Swipe){
                    Image handImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_HAND));
                    final Image line = new Image(assets.getTextures().get(Textures.Name.WHITE_HORIZONTAL_LINE));
                    line.setColor(Color.BLACK);
                    line.setSize(10, 4);
                    line.setPosition(17, 31);
                    handImage.setPosition(10, -10);

                    _this.addActor(line);
                    _this.addActor(handImage);
                    _this.setSize(30, 45);

                    handImage.addAction(forever(sequence(
                            parallel(Actions.moveTo(10, -10, 0f), new RunnableAction(){
                                @Override
                                public void run() {
                                    line.getColor().a = 1f;
                                    line.setSize(10, 2);
                                }
                            }),
                            parallel(
                            Actions.moveBy(distanceX, distanceY, 0.3f), new RunnableAction(){
                                        @Override
                                        public void run() {
                                            line.addAction(sequence(Actions.sizeTo(distanceX, 2, 0.3f), fadeOut(0.3f)));
                                        }
                                    }),
                            delay(1f))));

                }
                else if(gestureType == GestureType.Drag){
                    Image handImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_HAND));
                    final Image buttonImage = new Image(assets.getTextures().get(Textures.Name.TAP_GESTURE_BUTTON));
                    buttonImage.setPosition(13, 27);
                    buttonImage.setColor(Color.WHITE);
                    handImage.setPosition(10, -10);

                    _this.addActor(buttonImage);

                    _this.addActor(handImage);
                    _this.setSize(30, 45);

                    handImage.addAction(forever(sequence(
                            Actions.moveTo(10, -10, 0f), new RunnableAction(){
                                @Override
                                public void run() {
                                    buttonImage.setPosition(13, 27);
                                    buttonImage.setColor(Color.WHITE);
                                }
                            }, delay(0.3f), new RunnableAction(){
                                @Override
                                public void run() {
                                    buttonImage.setColor(Color.BLACK);
                                }
                            }, delay(0.3f),
                            parallel(
                            Actions.moveBy(distanceX, distanceY, 0.3f), new RunnableAction(){
                                        @Override
                                        public void run() {
                                            buttonImage.addAction(sequence(Actions.moveBy(distanceX, distanceY, 0.3f), delay(0.2f),
                                                                    new RunnableAction(){
                                                                        @Override
                                                                        public void run() {
                                                                            buttonImage.setColor(Color.WHITE);
                                                                        }
                                                                    }));
                                        }
                                    }),
                            delay(1f))));

                }
            }
        });
    }

}
