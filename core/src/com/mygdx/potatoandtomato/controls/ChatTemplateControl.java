package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.controls.ChatTemplateSelectedListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.services.Preferences;
import com.mygdx.potatoandtomato.statics.ChatTemplate;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.ArrayUtils;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 11/5/2016.
 */
public class ChatTemplateControl extends Table {

    public Table _this;
    public Assets assets;
    public Preferences preferences;
    public ChatTemplateSelectedListener chatTemplateSelectedListener;
    public Image userTemplatesTab, pickTemplatesTab, closeButton;
    public Table userTemplatesTable, pickTemplatesTable;
    public ScrollPane userTemplatesScroll, pickTemplatesScroll;

    public ChatTemplateControl(Assets assets, Preferences preferences) {
        _this = this;
        this.assets = assets;
        this.preferences = preferences;

        this.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.ORANGE_ROUNDED_BG)));
        this.setSize(150, 150);

        populate();
        setListeners();
        switchTab(true);
        setShowing(false);
    }

    public void populate(){

        new DummyButton(this, assets);

        Table tabsTable = new Table();
        tabsTable.align(Align.left);
        Table contentTable = new Table();

        userTemplatesTab = new Image(assets.getTextures().get(Textures.Name.FULL_BLACK_BG));
        pickTemplatesTab = new Image(assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG));
        closeButton = new Image(assets.getTextures().get(Textures.Name.CROSS_ICON));
        tabsTable.add(userTemplatesTab).size(20, 30);
        tabsTable.add(pickTemplatesTab).size(20, 30);
        tabsTable.add(closeButton).size(20, 20).expandX().right();

        this.add(tabsTable).expandX().fillX();
        this.row();
        this.add(contentTable).expand().fill();

        userTemplatesTable = new Table();
        userTemplatesTable.align(Align.topLeft);
        userTemplatesScroll = new ScrollPane(userTemplatesTable);
        userTemplatesScroll.setFillParent(true);
        userTemplatesScroll.setVisible(false);

        contentTable.addActor(userTemplatesScroll);

        pickTemplatesTable = new Table();
        pickTemplatesTable.align(Align.topLeft);
        pickTemplatesScroll = new ScrollPane(pickTemplatesTable);
        pickTemplatesScroll.setFillParent(true);
        pickTemplatesScroll.setVisible(false);

        contentTable.addActor(pickTemplatesScroll);

        ArrayList<String> userTemplates = getUserTemplates();
        for(String msg : ChatTemplate.getAllTemplates()){
            Table templateDesignTable = generateTemplateDesign(msg, true, userTemplates.contains(msg));
            pickTemplatesTable.add(templateDesignTable).expandX().fillX();
            pickTemplatesTable.row();
            setListenerForPickTemplate(templateDesignTable);
        }

    }

    public void switchTab(boolean toUserTemplates){
        if(toUserTemplates){
            refreshUserTemplates();
            userTemplatesScroll.setVisible(true);
            pickTemplatesScroll.setVisible(false);
        }
        else{
            userTemplatesScroll.setVisible(false);
            pickTemplatesScroll.setVisible(true);
        }
    }

    public void refreshUserTemplates(){
        ArrayList<String> userTemplates = getUserTemplates();

        userTemplatesTable.clear();
        for(String msg : userTemplates){
            Table templateDesignTable = generateTemplateDesign(msg, false, false);
            userTemplatesTable.add(templateDesignTable).expandX().fillX();
            userTemplatesTable.row();
            setListenerForUserTemplate(templateDesignTable, msg);
        }
    }

    public Table generateTemplateDesign(String msg, boolean isSelectable, boolean isSelected){
        Label.LabelStyle templateLabelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR), null);

        Table templateTable = new Table();
        Label templateLabel = new Label(msg, templateLabelStyle);
        templateLabel.setName("templateLabel");
        templateLabel.setAlignment(Align.left);
        templateTable.add(templateLabel).expandX().fillX().pad(4).left();

        if(isSelectable){
            Image selectBoxImage = new Image(new TextureRegionDrawable(assets.getTextures().get(
                                isSelected ? Textures.Name.SELECT_BOX : Textures.Name.UNSELECT_BOX)));
            selectBoxImage.setName("selectBoxImage");
            templateTable.add(selectBoxImage).size(10, 10).padRight(4);
            templateTable.setName(isSelected ? "selected" : "notSelected");
        }

        new DummyButton(templateTable, assets);
        return templateTable;
    }

    public void saveTemplatePreferences(){
        ArrayList<String> selectedTemplates = new ArrayList();
        for(Actor actor : pickTemplatesTable.getChildren()){
            if(actor instanceof Table){
                Table templateTable = (Table) actor;
                if(templateTable.getName() != null && templateTable.getName().equals("selected")){
                    Label templateLabel = templateTable.findActor("templateLabel");
                    selectedTemplates.add(templateLabel.getText().toString());
                }
            }
        }

        ArrayList<Integer> result = ChatTemplate.getIdsByTemplates(selectedTemplates);
        preferences.put(Terms.PREF_CHAT_TEMPLATE, Strings.joinArr(ArrayUtils.integerArrayToStringArray(result), ","));
    }

    public ArrayList<String> getUserTemplates(){
        String idsString = preferences.get(Terms.PREF_CHAT_TEMPLATE);

        ArrayList<String> templateIds;
        if(Strings.isEmpty(idsString)){
            templateIds = new ArrayList();
            templateIds.add(String.valueOf(1));
            templateIds.add(String.valueOf(2));
        }
        else{
            templateIds = Strings.split(idsString, ",");
        }

        ArrayList<String> result = ChatTemplate.getTemplatesByIds(ArrayUtils.stringArrayToIntegerArray(templateIds));

        return result;
    }

    public void screenTouched(float x, float y){
        if(x < this.getX() || x > this.getX() + this.getWidth()){
            setShowing(false);
        }
        if(y < this.getY() || y > this.getY() + this.getHeight()){
            setShowing(false);
        }
    }

    public void setListenerForPickTemplate(final Table templateTable){
        templateTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Image selectBoxImage = templateTable.findActor("selectBoxImage");

                if(templateTable.getName() != null && templateTable.getName().equals("selected")){
                    templateTable.setName("notSelected");
                    selectBoxImage.setDrawable(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.UNSELECT_BOX)));
                }
                else{
                    templateTable.setName("selected");
                    selectBoxImage.setDrawable(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SELECT_BOX)));
                }
                saveTemplatePreferences();
            }
        });
    }

    public void setListenerForUserTemplate(final Table templateTable, final String msg){
        templateTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(chatTemplateSelectedListener != null) chatTemplateSelectedListener.onSelected(msg);
            }
        });
    }

    public void setListeners(){
        userTemplatesTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchTab(true);
            }
        });

        pickTemplatesTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchTab(false);
            }
        });

        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setShowing(false);
            }
        });
    }

    public void setShowing(final boolean isShowing){
        setShowing(null, isShowing);
    }

    public void setShowing(final Actor relatedActor, final boolean isShowing){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.setVisible(isShowing);
                if(isShowing && relatedActor != null){
                    Vector2 positionOnStage = Positions.actorLocalToStageCoord(relatedActor);
                    _this.setPosition(positionOnStage.x, 10);
                }
                _this.setTouchable(isShowing ? Touchable.enabled : Touchable.disabled);
            }
        });
    }

    public void setChatTemplateSelectedListener(ChatTemplateSelectedListener chatTemplateSelectedListener) {
        this.chatTemplateSelectedListener = chatTemplateSelectedListener;
    }
}
