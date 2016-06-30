package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.mygdx.potatoandtomato.absintflis.controls.ChatPopupListener;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 27/6/2016.
 */
public class ChatPopup extends Table {

    private ChatPopup _this;
    private float originalX, originalY;
    private Table container;
    private Assets assets;
    private Image closeButton;
    private ArrayList<ChatPopupListener> chatPopupListeners;
    private boolean visible;

    public ChatPopup(Assets assets, int arrowPositionX) {
        this.setVisible(false);
        _this = this;
        this.assets = assets;
        this.chatPopupListeners = new ArrayList();
        container = new Table();
        this.add(container).expand().fill();
        populate(arrowPositionX);
        setListeners();
    }

    public void populate(int arrowPositionX){
        this.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.CHAT_POPUP_BG)));

        Image arrowImage = new Image(assets.getTextures().get(Textures.Name.CHAT_POPUP_ARROW));
        arrowImage.setPosition(arrowPositionX, -5);
        closeButton = new Image(assets.getTextures().get(Textures.Name.CHAT_POPUP_CLOSE_BTN));

        this.addActor(arrowImage);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if(this.originalX == 0 && this.originalY == 0){
            this.originalX = x;
            this.originalY = y;
        }
    }

    public void hide(){
        if(!visible) return;

        visible = false;
        this.clearActions();
        this.setPosition(originalX, originalY);
        this.getColor().a = 1f;

        this.addAction(sequence(parallel(Actions.moveBy(0, -5, 0.1f), fadeOut(0.1f)), new RunnableAction(){
            @Override
            public void run() {
                _this.setVisible(false);
            }
        }));

        for(ChatPopupListener chatPopupListener : chatPopupListeners){
            chatPopupListener.onVisibleChanged(false);
        }
    }

    public void show(){
        if(visible) return;

        visible = true;
        this.clearActions();
        this.setPosition(originalX, originalY - 5);
        this.getColor().a = 0f;
        this.setVisible(true);

        this.addAction(sequence(parallel(Actions.moveBy(0, 5, 0.1f), fadeIn(0.1f))));

        for(ChatPopupListener chatPopupListener : chatPopupListeners){
            chatPopupListener.onVisibleChanged(true);
        }
    }

    public void setActor(Actor actor, float minHeight){


        container.clear();

        if(minHeight == actor.getHeight()){
            container.add(actor).expand().fill().height(actor.getHeight()).width(actor.getWidth()).padTop(-1);
        }
        else{
            container.add(actor).expand().fill().maxHeight(actor.getHeight()).width(actor.getWidth()).padTop(-1);
        }

        refreshSize();
    }

    public void refreshSize(){
        closeButton.setPosition(this.getPrefWidth() - closeButton.getPrefWidth() /2 - 4,
                this.getPrefHeight()  - closeButton.getPrefHeight() /2 - 6);
        container.addActor(closeButton);

        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }

    public void setListeners(){
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                hide();
            }
        });
    }

    public void addChatPopupListeners(ChatPopupListener chatPopupListener){
        this.chatPopupListeners.add(chatPopupListener);
    }

    public void removeChatPopupListeners(ChatPopupListener chatPopupListener){
        this.chatPopupListeners.remove(chatPopupListener);
    }


    public boolean getVisible() {
        return visible;
    }
}
