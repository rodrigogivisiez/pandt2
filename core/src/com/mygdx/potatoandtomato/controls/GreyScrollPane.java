package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;

/**
 * Created by SiongLeng on 27/6/2016.
 */
public class GreyScrollPane extends ScrollPane {

    private Actor actor;
    private Assets assets;
    public GreyScrollPane(Actor widget, Assets assets) {
        super(widget);

        this.assets = assets;
        this.actor = widget;
        populate();
    }

    public void populate(){
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SCROLLBAR_GREY_HANDLE));
        scrollPaneStyle.vScroll = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SCROLLBAR_GREY_BG));
        this.setStyle(scrollPaneStyle);
        this.setFadeScrollBars(false);
        this.setScrollingDisabled(true, false);
    }

}
