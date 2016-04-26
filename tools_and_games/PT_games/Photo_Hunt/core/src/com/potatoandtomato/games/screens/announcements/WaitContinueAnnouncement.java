package com.potatoandtomato.games.screens.announcements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.potatoandtomato.games.absintf.Announcement;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 26/4/2016.
 */
public class WaitContinueAnnouncement extends Announcement {

    private MyAssets assets;
    private Services services;
    private Label labelBig, labelSmall;


    public WaitContinueAnnouncement(Services services) {
        this.assets = services.getAssets();
        this.services = services;


        Label.LabelStyle labelStyle1 = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_MAX_REGULAR), Color.WHITE);
        labelBig = new Label(services.getTexts().gameContinue(), labelStyle1);
        labelBig.getColor().a = 0f;

        labelSmall = new Label(services.getTexts().verySoon(), labelStyle1);
        labelSmall.getColor().a = 0f;

        this.add(labelBig);
        this.row();
        this.add(labelSmall);


    }


    @Override
    public void run() {
        labelBig.addAction(sequence(fadeIn(2f), new RunnableAction(){
            @Override
            public void run() {
                labelSmall.addAction(fadeIn(0.8f));
            }
        }));
    }
}
