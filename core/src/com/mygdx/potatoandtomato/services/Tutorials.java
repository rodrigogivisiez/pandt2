package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.absints.ITutorials;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.controls.DisposableActor;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 28/3/2016.
 */
public class Tutorials implements ITutorials {

    private Assets _assets;
    private SoundsPlayer _soundsWrapper;
    private Stage _stage;
    private SpriteBatch _batch;
    private IPTGame _game;
    private Table _root;
    private Broadcaster _broadcaster;
    private ArrayList<TutorialItem> _queueItems;
    private boolean _showing;


    public Tutorials(IPTGame _game, SpriteBatch _batch, SoundsPlayer _soundsWrapper, Assets _assets, Broadcaster broadcaster) {
        this._game = _game;
        this._batch = _batch;
        this._soundsWrapper = _soundsWrapper;
        this._assets = _assets;
        this._root = new Table();
        this._broadcaster = broadcaster;
        _queueItems = new ArrayList<TutorialItem>();

        invalidate();

        _broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
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
                if(_stage != null){
                    _game.removeInputProcessor(_stage);
                    _stage.dispose();
                    _root.remove();
                }

                StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                _stage = new Stage(viewPort, _batch);

                _root.setHeight(Positions.getHeight());
                _root.setWidth(Global.IS_POTRAIT ? 300 : 500);
                _root.setPosition(-_root.getWidth(), 0);
                _root.invalidate();

                _stage.addActor(_root);
                _game.addInputProcessor(_stage, 19);
            }
        });
    }


    @Override
    public void show(DisposableActor actor, String text, float duration) {
        _queueItems.add(new TutorialItem(actor, text, duration));
        run();
    }

    public void run(){
        if(_showing) return;

        _showing = true;

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (_queueItems.size() > 0) {
                    final boolean[] waiting = {true};
                    final TutorialItem tutorialItem = _queueItems.get(0);
                    _queueItems.remove(tutorialItem);

                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {

                            _root.clear();

                            Table tutorialRoot = new Table();
                            tutorialRoot.pad(10);
                            tutorialRoot.padLeft(20);
                            tutorialRoot.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.TUTORIAL_BG)));
                            Image mascotsImage = new Image(_assets.getTextures().get(Textures.Name.TUTORIAL_MASCOT));

                            Label.LabelStyle labelStyle = new Label.LabelStyle();
                            labelStyle.fontColor = Color.valueOf("573801");
                            labelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_XL_REGULAR);

                            Label textLabel = new Label(tutorialItem.getText(), labelStyle);
                            textLabel.setWrap(true);

                            tutorialRoot.add(mascotsImage).colspan(2).padTop(-75).right().padRight(10);
                            tutorialRoot.row();
                            tutorialRoot.add(tutorialItem.getActor()).size(tutorialItem.getActor().getWidth(), tutorialItem.getActor().getHeight());
                            tutorialRoot.add(textLabel).expandX().fillX();


                            _root.add(tutorialRoot).expandX().fillX();

                            _root.addAction(sequence(moveTo(-_root.getWidth(), 0), moveTo(-10, 0, 0.4f), delay(tutorialItem.getDuration()),
                                    moveTo(-_root.getWidth(), 0, 0.2f), new RunnableAction() {
                                        @Override
                                        public void run() {
                                            waiting[0] = false;
                                        }
                                    }));

                            _soundsWrapper.playSoundEffect(Sounds.Name.TUTORIAL);

                        }
                    });

                    while (waiting[0]) {
                        Threadings.sleep(300);
                    }

                }
                _showing = false;
            }
        });
    }

    public void render(float delta){
        if(_showing){
            _stage.act(delta);
            _stage.draw();
        }
    }

    public void resize(int width, int height){
        _stage.getViewport().update(width, height);
    }


    private class TutorialItem{

        DisposableActor actor;
        String text;
        float duration;

        public TutorialItem(DisposableActor actor, String text, float duration) {
            this.actor = actor;
            this.text = text;
            this.duration = duration;
        }

        public DisposableActor getActor() {
            return actor;
        }

        public String getText() {
            return text;
        }

        public float getDuration() {
            return duration;
        }
    }

}
