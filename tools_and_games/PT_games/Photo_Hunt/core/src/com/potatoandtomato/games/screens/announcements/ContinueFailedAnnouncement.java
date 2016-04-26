package com.potatoandtomato.games.screens.announcements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.games.absintf.Announcement;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 26/4/2016.
 */
public class ContinueFailedAnnouncement extends Announcement {

    private MyAssets assets;
    private Services services;
    private Label labelBig;


    public ContinueFailedAnnouncement(Services services) {
        this.assets = services.getAssets();
        this.services = services;


        Label.LabelStyle labelStyle1 = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_MAX_REGULAR), Color.WHITE);
        labelBig = new Label(services.getTexts().gameContinueFailed(), labelStyle1);
        labelBig.setWrap(true);
        labelBig.setAlignment(Align.center);
        labelBig.getColor().a = 0f;

        this.add(labelBig).expandX().fillX().pad(10);
    }


    @Override
    public void run() {
        labelBig.addAction(sequence(fadeIn(2f)));
    }
}

