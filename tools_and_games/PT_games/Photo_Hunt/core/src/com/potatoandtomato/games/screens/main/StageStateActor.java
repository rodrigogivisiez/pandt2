package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.papyruses.PapyrusSceneAbstract;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 23/5/2016.
 */
public class StageStateActor extends Table {

    private Table _this;
    private Services services;
    private MyAssets assets;
    private Image papyrusStartImage, papyrusEndImage;
    private Table papyrusRootTable, papyrusContentTable;

    public StageStateActor(Services services) {
        this.services = services;
        assets = services.getAssets();
        _this = this;

        papyrusContentTable = new Table();
    }


    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                papyrusRootTable = new Table();
                papyrusRootTable.setClip(true);

                Image papyrusImage = new Image(assets.getTextures().get(Textures.Name.PAPYRUS));
                papyrusImage.setSize(_this.getWidth(), _this.getHeight());

                papyrusContentTable.setSize(_this.getWidth(), _this.getHeight());

                papyrusStartImage = new Image(assets.getTextures().get(Textures.Name.PAPYRUS_START));
                papyrusEndImage = new Image(assets.getTextures().get(Textures.Name.PAPYRUS_END));
                papyrusEndImage.setPosition(papyrusStartImage.getX() + papyrusStartImage.getPrefWidth(), 0);

                papyrusRootTable.setSize(papyrusStartImage.getPrefWidth() + papyrusEndImage.getPrefWidth(), _this.getHeight());
                papyrusRootTable.addActor(papyrusImage);
                papyrusRootTable.addActor(papyrusContentTable);

                _this.setVisible(false);
                _this.addActor(papyrusRootTable);
                _this.addActor(papyrusStartImage);
                _this.addActor(papyrusEndImage);
            }
        });
    }

    public void openPapyrus(final Actor toShowActor){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.clearActions();
                papyrusEndImage.clearActions();
                papyrusRootTable.clearActions();
                papyrusContentTable.clear();
                papyrusContentTable.add(toShowActor).expand().fill();

                _this.setX(-90);
                _this.setVisible(true);

                _this.addAction(sequence(Actions.moveTo(0, _this.getY(), 0.7f), new RunnableAction(){
                    @Override
                    public void run() {
                        services.getSoundsWrapper().playSounds(Sounds.Name.PAPYRUS);

                        papyrusEndImage.addAction(Actions.moveTo(_this.getWidth() - papyrusStartImage.getPrefWidth() + 5, 0, 1.3f, Interpolation.linear));
                        papyrusRootTable.addAction(sequence(Actions.sizeTo(_this.getWidth(), papyrusRootTable.getHeight(), 1.3f, Interpolation.linear), new RunnableAction(){
                            @Override
                            public void run() {
                            }
                        }));
                    }
                }));
            }
        });
    }

    public void switchPapyrus(final Actor toShowActor){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final Actor originalPapyrus = papyrusContentTable.findActor("papyrusScene");
                if(originalPapyrus != null){
                    originalPapyrus.addAction(sequence(fadeOut(0.5f), new RunnableAction(){
                        @Override
                        public void run() {
                            papyrusContentTable.clear();
                            toShowActor.getColor().a = 0f;
                            papyrusContentTable.add(toShowActor).expand().fill();
                            toShowActor.addAction(fadeIn(0.5f));

                            if(originalPapyrus instanceof PapyrusSceneAbstract){
                                ((PapyrusSceneAbstract) originalPapyrus).dispose();
                            }
                        }
                    }));
                }
                else{
                    papyrusContentTable.clear();
                    toShowActor.getColor().a = 0f;
                    papyrusContentTable.add(toShowActor).expand().fill();
                    toShowActor.addAction(fadeIn(0.5f));
                }
            }
        });
    }

    public void closePapyrus(final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.clearActions();
                papyrusEndImage.clearActions();
                papyrusRootTable.clearActions();

                services.getSoundsWrapper().playSounds(Sounds.Name.PAPYRUS);
                papyrusEndImage.addAction(Actions.moveTo(papyrusStartImage.getX() + papyrusStartImage.getPrefWidth(), 0, 1.3f, Interpolation.linear));
                papyrusRootTable.addAction(sequence(Actions.sizeTo(papyrusStartImage.getPrefWidth() + papyrusEndImage.getPrefWidth(),
                        papyrusRootTable.getHeight(), 1.3f, Interpolation.linear), new RunnableAction() {
                    @Override
                    public void run() {
                        _this.addAction(sequence(Actions.moveTo(-90, _this.getY(), 0.7f), new RunnableAction() {
                            @Override
                            public void run() {
                                _this.setVisible(false);
                                if(onFinish != null) onFinish.run();
                            }
                        }));
                    }
                }));
            }
        });
    }

    public Table getPapyrusContentTable() {
        return papyrusContentTable;
    }
}
