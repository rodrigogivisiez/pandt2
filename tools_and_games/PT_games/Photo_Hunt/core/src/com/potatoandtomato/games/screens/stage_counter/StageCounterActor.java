package com.potatoandtomato.games.screens.stage_counter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public class StageCounterActor extends Table {

    private Services services;
    private MyAssets assets;
    private Label stageLabel;
    private Table specialStageTable;
    private Image specialStageImage;

    public StageCounterActor(Services services) {
        this.services = services;
        this.assets = services.getAssets();
        populate();
    }

    public void populate(){
        this.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SHIELD_SWORD)));

        Label.LabelStyle stageLabelStyle = new Label.LabelStyle(
                                    assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR_B_ffffff_8e4403_2), Color.valueOf("ffe400"));

        stageLabel = new Label("", stageLabelStyle);
        stageLabel.setAlignment(Align.center);
        this.add(stageLabel).expandX().fillX().padLeft(1);

        specialStageTable = new Table();
        specialStageTable.setFillParent(true);
        specialStageTable.getColor().a = 0f;
        specialStageImage = new Image(assets.getTextures().get(Textures.Name.STAR));
        specialStageImage.setOrigin(Align.center);
        specialStageTable.add(specialStageImage);
        this.addActor(specialStageTable);
    }

    public void refreshStageNumber(final int newNumber, StageType stageType){
        stageLabel.clearActions();
        stageLabel.addAction(fadeOut(0.1f));

        specialStageImage.clearActions();
        specialStageTable.clearActions();
        specialStageTable.addAction(fadeOut(0.1f));

        if(stageType == StageType.Normal){
            stageLabel.addAction(sequence(delay(0.1f), new RunnableAction(){
                @Override
                public void run() {
                    stageLabel.setText(String.valueOf(newNumber));
                }
            },fadeIn(0.3f)));
        }
        else if(stageType == StageType.Bonus){
            specialStageTable.addAction(sequence(delay(0.1f), fadeIn(0.3f), new RunnableAction(){
                @Override
                public void run() {
                    specialStageImage.addAction(sequence(forever(Actions.rotateBy(3f, 0.01f))));
                }
            }));

        }
    }

}
