package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.BtnColor;
import com.mygdx.potatoandtomato.controls.DummyButton;
import com.mygdx.potatoandtomato.controls.GestureControl;
import com.potatoandtomato.common.enums.GestureType;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.absints.ITutorials;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.controls.DisposableActor;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 28/3/2016.
 */
public class Tutorials implements ITutorials {

    private Assets assets;
    private SoundsPlayer soundsWrapper;
    private SpriteBatch batch;
    private IPTGame game;
    private Broadcaster broadcaster;
    private Preferences preferences;
    private Texts texts;

    private Stage stage;
    private Table root;
    private Table tutorialRoot;
    private Table msgTable;
    private BtnColor startButton;
    private BtnColor skipButton;
    private boolean showing;
    private TutorialPartListener tutorialPartListener;
    private ActorData focusingActor;
    private GestureControl gestureControl;
    private boolean blockRootClick;
    private String currentTutorialId;
    private ArrayList<String> completedTutorialsId;

    public Tutorials(IPTGame _game, SpriteBatch _batch, SoundsPlayer _soundsWrapper, Assets _assets, Broadcaster broadcaster,
                     Preferences preferences, Texts texts) {
        this.game = _game;
        this.batch = _batch;
        this.soundsWrapper = _soundsWrapper;
        this.assets = _assets;
        this.preferences = preferences;
        this.broadcaster = broadcaster;
        this.texts = texts;
        this.completedTutorialsId = new ArrayList();

        populate();
        invalidate();
        setListeners();

        this.broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
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
                root.setVisible(false);
                new DummyButton(root, assets);

                Image overlayImage = new Image(assets.getTextures().get(Textures.Name.LESS_TRANS_BLACK_BG));
                overlayImage.getColor().a = 0.5f;
                overlayImage.setFillParent(true);

                root.addActor(overlayImage);
                root.setFillParent(true);

                tutorialRoot = new Table();
                root.addActor(tutorialRoot);

                startButton = new BtnColor(BtnColor.ColorChoice.GREEN, assets);
                startButton.setText(texts.btnTextStartTutorial());
                startButton.setSize(150, 60);
                startButton.setVisible(false);
                root.addActor(startButton);

                skipButton = new BtnColor(BtnColor.ColorChoice.RED, assets);
                skipButton.setText(texts.btnTextSkipTutorial());
                skipButton.setSize(150, 60);
                skipButton.setPosition(Positions.getWidth() - skipButton.getWidth(), 0);
                skipButton.setVisible(false);
                root.addActor(skipButton);


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
                    game.addInputProcessor(stage, 19, false);
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

                tutorialRoot.setY(Positions.getHeight() / 2 - tutorialRoot.getHeight() / 2);

            }
        });
    }

    @Override
    public void showMessage(final DisposableActor actor, final String text) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                tutorialRoot.clear();
                tutorialRoot.align(Align.top);
                tutorialRoot.pad(10);
                tutorialRoot.padLeft(20);
                tutorialRoot.setWidth(200);

                tutorialRoot.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.TUTORIAL_BG)));
                Image mascotsImage = new Image(assets.getTextures().get(Textures.Name.TUTORIAL_MASCOT));

                Label.LabelStyle labelStyle = new Label.LabelStyle();
                labelStyle.fontColor = Color.valueOf("573801");
                labelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_XL_REGULAR);

                Label textLabel = new Label(text, labelStyle);
                textLabel.setWrap(true);

                tutorialRoot.add(mascotsImage).colspan(2).padTop(-75).right().padRight(10);
                tutorialRoot.row();
                if(actor != null) tutorialRoot.add(actor).size(actor.getWidth(), actor.getHeight());
                tutorialRoot.row();
                tutorialRoot.add(textLabel).width(tutorialRoot.getWidth());
                tutorialRoot.pack();

                tutorialRoot.setY(Positions.getHeight() / 2 - tutorialRoot.getHeight() / 2);
                tutorialRoot.addAction(sequence(
                        moveTo(-tutorialRoot.getWidth(), tutorialRoot.getY()), moveTo(-10, tutorialRoot.getY(), 0.4f)));

                soundsWrapper.playSoundEffect(Sounds.Name.TUTORIAL);

                blockRootClick = false;
            }
        });
    }

    private void hideMsg(final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                tutorialRoot.clearActions();
                tutorialRoot.addAction(sequence(moveTo(-tutorialRoot.getWidth(), tutorialRoot.getY(), 0.2f), new RunnableAction() {
                    @Override
                    public void run() {
                        if(onFinish != null) onFinish.run();
                    }
                }));
            }
        });
    }

    public void expectGestureOnActor(final GestureType gestureType, final Actor actor, final String text,
                                                final int offsetX, final int offsetY){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                focusingActor = new ActorData(actor);

                actor.setTouchable(Touchable.disabled);
                Vector2 position = Positions.actorLocalToStageCoord(actor);
                actor.setPosition(position.x, position.y);
                root.addActor(actor);

                if(gestureType == GestureType.Tap){
                    actor.setTouchable(Touchable.enabled);

                    actor.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            goToNextTutorialPart();
                        }
                    });

                    blockRootClick = true;
                }
                else{
                    blockRootClick = false;
                }

                expectGestureOnPosition(gestureType, text, 0, (int) actor.getHeight() / 2,
                        position.x + actor.getWidth() / 2 + offsetX,
                        position.y + actor.getHeight() / 2 + offsetY, 0, 0);

            }
        });
    }

    @Override
    public boolean completedTutorialBefore(String id) {
        if(completedTutorialsId.contains(id)){
            return true;
        }

        String completed = preferences.get(id);
        if(Strings.isEmpty(completed) || !completed.equals("1")){
            return false;
        }
        else{
            completedTutorialsId.add(completed);
            return true;
        }
    }

    @Override
    public void startTutorialIfNotCompleteBefore(String id, boolean canSkip, TutorialPartListener listener) {
        if(completedTutorialBefore(id)){
            return;
        }

        root.setVisible(true);
        showing = true;
        this.tutorialPartListener = listener;
        goToNextTutorialPart();
        currentTutorialId = id;
        if(canSkip){
            showButtons();
        }
    }

    @Override
    public void expectGestureOnPosition(final GestureType gestureType, final String text,
                                        final int gestureAndTextDistanceX, final int gestureAndTextDistanceY,
                                        final float x, final float y, final int gestureActionDistanceX,
                                        final int gestureActionDistanceY) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(!Strings.isEmpty(text)){
                    Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(
                            Fonts.FontId.PIZZA_XXL_REGULAR_B_ffffff_000000_2), Color.WHITE);

                    msgTable = new Table();

                    Label msgLabel = new Label(text, labelStyle);
                    msgLabel.setAlignment(Align.center);
                    msgLabel.setWrap(true);
                    msgTable.add(msgLabel).expand().fill().width(200);

                    msgTable.pack();
                    float positionX = x - msgTable.getWidth() /2 + gestureAndTextDistanceX + gestureActionDistanceX / 2;
                    float positionY = y + gestureAndTextDistanceY + gestureActionDistanceY / 2;

                    if(positionX < 5) positionX = 5;
                    if(positionX + msgTable.getWidth() > Positions.getWidth() - 10){
                        positionX = Positions.getWidth() - 10 - msgTable.getWidth();
                    }

                    if(gestureType == GestureType.PointRight){
                        positionY += 10;
                    }

                    if(positionY + msgTable.getHeight() > Positions.getHeight() - 10){
                        positionY = y -  msgTable.getHeight() - gestureAndTextDistanceY;
                    }


                    msgTable.setPosition(positionX, positionY);

                    root.addActor(msgTable);
                }


                if(gestureType != GestureType.None){
                    gestureControl = new GestureControl(gestureType, gestureActionDistanceX, gestureActionDistanceY, assets);
                    gestureControl.setPosition(x - gestureControl.getWidth() / 2, y - gestureControl.getHeight()/2);
                    root.addActor(gestureControl);
                }

                if(gestureType == GestureType.Tap){
                    blockRootClick = true;
                }
                else if(gestureType == GestureType.Swipe){
                    blockRootClick = false;
                }
                else{
                    blockRootClick = false;
                }

                soundsWrapper.playSoundEffect(Sounds.Name.NEXT_TUTORIAL);
            }
        });
    }

    @Override
    public void completeTutorial() {
        preferences.put(currentTutorialId, "1");
        completedTutorialsId.add(currentTutorialId);
        hide();
    }

    private void goToNextTutorialPart(){
        clearTutorials();
        blockRootClick = true;
        tutorialPartListener.nextTutorial();
        hideButtons();
    }

    private void clearTutorials(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(focusingActor != null){
                    focusingActor.revert();
                }
                if(msgTable != null){
                    msgTable.remove();
                }
                if(gestureControl != null){
                    gestureControl.remove();
                }
                hideMsg(null);
            }
        });
    }

    @Override
    public void hide(){
        currentTutorialId = "";
        if(showing){
            clearTutorials();
            hideMsg(new Runnable() {
                @Override
                public void run() {
                    root.setVisible(false);
                    showing = false;
                }
            });
        }
    }

    private void showButtons(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                startButton.setVisible(true);
                skipButton.setVisible(true);
            }
        });
    }

    private void hideButtons(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                startButton.setVisible(false);
                skipButton.setVisible(false);
            }
        });
    }

    public void render(float delta){
        if(showing){
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    public void setListeners(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                skipButton.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        completeTutorial();
                        blockRootClick = true;
                    }
                });

                root.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(!blockRootClick){
                            goToNextTutorialPart();
                        }
                    }
                });



            }
        });
    }

    public boolean isShowing() {
        return showing;
    }

    private class ActorData{
        private Group parent;
        private Vector2 originalPosition;
        private Actor actor;
        private Touchable touchable;

        public ActorData(Actor actor) {
            this.parent = actor.getParent();
            this.originalPosition = new Vector2(actor.getX(), actor.getY());
            this.actor = actor;
            this.touchable = actor.getTouchable();
        }

        public void revert(){
            actor.setPosition(originalPosition.x, originalPosition.y);
            actor.setTouchable(this.touchable);
            parent.addActor(actor);
        }

    }

}
