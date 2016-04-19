package com.potatoandtomato.games.screens.announcements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.potatoandtomato.games.absintf.Announcement;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by SiongLeng on 18/4/2016.
 */
public class BonusAnnouncement extends Announcement {

    private MyAssets assets;
    private Services services;
    private Label labelBig, labelSmall;


    public BonusAnnouncement(Services services, BonusType bonusType) {
        this.assets = services.getAssets();
        this.services = services;


        Label.LabelStyle labelStyle1 = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_MAX_REGULAR), Color.WHITE);
        labelBig = new Label(bonusType.name(), labelStyle1);
        labelBig.getColor().a = 0f;

        this.add(labelBig);

    }


    @Override
    public void run() {
        labelBig.addAction(fadeIn(0.6f));
    }
}
