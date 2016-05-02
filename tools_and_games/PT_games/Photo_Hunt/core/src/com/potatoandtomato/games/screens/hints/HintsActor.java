package com.potatoandtomato.games.screens.hints;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 8/4/2016.
 */
public class HintsActor extends Table {

    private MyAssets myAssets;
    private Services services;
    private Image hintOnImage1, hintOnImage2, hintOnImage3;
    private Image hintOffImage1, hintOffImage2, hintOffImage3;
    private Image hintBlock;
    private Image recoverHintLightImage;

    public HintsActor(Services services) {
        this.services = services;
        this.myAssets = this.services.getAssets();
        populate();
        new DummyButton(this, services.getAssets());
    }

    public void populate(){
        int width = 28, height = 30;

        hintOnImage1 = new Image(myAssets.getTextures().get(Textures.Name.HINT_ON_ICON));
        hintOnImage1.setOrigin(Align.center);
        hintOnImage1.setSize(width, height);

        hintOnImage2 = new Image(myAssets.getTextures().get(Textures.Name.HINT_ON_ICON));
        hintOnImage2.setOrigin(Align.center);
        hintOnImage2.setSize(width, height);

        hintOnImage3 = new Image(myAssets.getTextures().get(Textures.Name.HINT_ON_ICON));
        hintOnImage3.setOrigin(Align.center);
        hintOnImage3.setSize(width, height);

        hintOffImage1 = new Image(myAssets.getTextures().get(Textures.Name.HINT_OFF_ICON));
        hintOffImage1.setSize(width, height);

        hintOffImage2 = new Image(myAssets.getTextures().get(Textures.Name.HINT_OFF_ICON));
        hintOffImage2.setSize(width, height);

        hintOffImage3 = new Image(myAssets.getTextures().get(Textures.Name.HINT_OFF_ICON));
        hintOffImage3.setSize(width, height);

        Table hintTable1 = new Table();
        hintTable1.addActor(hintOffImage1);
        hintTable1.addActor(hintOnImage1);

        Table hintTable2 = new Table();
        hintTable2.addActor(hintOffImage2);
        hintTable2.addActor(hintOnImage2);

        Table hintTable3 = new Table();
        hintTable3.addActor(hintOffImage3);
        hintTable3.addActor(hintOnImage3);


        this.add(hintTable1).size(width, height).padLeft(8);
        this.add(hintTable2).size(width, height).padLeft(-6);
        this.add(hintTable3).size(width, height).padLeft(-6).padRight(8);

        hintBlock = new Image(myAssets.getTextures().get(Textures.Name.HINT_BLOCK));
        hintBlock.setPosition(-hintBlock.getWidth(), -3.5f);
        this.addActor(hintBlock);
    }

    public void setHintBlockVisible(final boolean visible){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(visible){
                    hintBlock.addAction(moveTo(0, hintBlock.getY(), 1f));
                }
                else{
                    hintBlock.addAction(moveTo(-hintBlock.getWidth(), hintBlock.getY(), 1f));
                }
                services.getSoundsWrapper().playSounds(Sounds.Name.MOVE_ROCK);
            }
        });
    }

    public void refreshDesign(final int leftHints){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                boolean off1 = false, off2 = false, off3 = false;
                if(leftHints == 0){
                    off1 = off2 = off3 = true;
                }
                else if(leftHints == 1){
                    off2 = off3 = true;
                }
                else if(leftHints == 2){
                    off3 = true;
                }

                if(off1){
                    hintOnImage1.addAction(sequence(Actions.scaleTo(0, 0, 0.1f)));
                }
                else{
                    hintOnImage1.setScale(1, 1);
                }

                if(off2){
                    hintOnImage2.addAction(sequence(Actions.scaleTo(0, 0, 0.1f)));
                }
                else{
                    hintOnImage2.setScale(1, 1);
                }

                if(off3){
                    hintOnImage3.addAction(sequence(Actions.scaleTo(0, 0, 0.1f)));
                }
                else{
                    hintOnImage3.setScale(1, 1);
                }
            }
        });
    }


    public Image getHintBlock() {
        return hintBlock;
    }
}
