package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
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
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.services.Preferences;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
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

    private Table _this;
    private Assets assets;
    private Preferences preferences;
    private SoundsPlayer soundsPlayer;
    private ChatTemplateSelectedListener chatTemplateSelectedListener;
    private Table userTemplatesTab, pickTemplatesTab;
    private Table userTemplatesTable, pickTemplatesTable;
    private ScrollPane userTemplatesScroll, pickTemplatesScroll;
    private Texts texts;

    public ChatTemplateControl(Assets assets, Preferences preferences, Texts texts, SoundsPlayer soundsPlayer) {
        _this = this;
        this.assets = assets;
        this.preferences = preferences;
        this.texts = texts;
        this.soundsPlayer = soundsPlayer;

        populate();
        setInternalListeners();
        switchTab(true);
    }

    public void populate(){

        new DummyButton(this, assets);

        Table tabsTable = new Table();
        Table contentTable = new Table();

        userTemplatesTab = getTabDesign(texts.favouritesTitle(), true);
        pickTemplatesTab = getTabDesign(texts.settingsTitle(), false);

        tabsTable.add(userTemplatesTab).uniformX().fillX().expandX();
        tabsTable.add(pickTemplatesTab).uniformX().fillX().expandX();

        this.add(tabsTable).expandX().fillX();
        this.row();
        this.add(contentTable).expand().fill().padRight(3).padBottom(5);

        userTemplatesTable = new Table();
        userTemplatesTable.align(Align.topLeft);
        userTemplatesTable.pad(10, 5, 10, 5);
        userTemplatesScroll = new GreyScrollPane(userTemplatesTable, assets);
        userTemplatesScroll.setFillParent(true);
        userTemplatesScroll.setVisible(false);

        contentTable.addActor(userTemplatesScroll);

        pickTemplatesTable = new Table();
        pickTemplatesTable.align(Align.topLeft);
        pickTemplatesTable.pad(10, 5, 10, 5);
        pickTemplatesScroll = new GreyScrollPane(pickTemplatesTable, assets);
        pickTemplatesScroll.setFillParent(true);
        pickTemplatesScroll.setVisible(false);

        contentTable.addActor(pickTemplatesScroll);

        ArrayList<String> userTemplates = getUserTemplates();
        for(String msg : ChatTemplate.getAllTemplates()){
            Table templateDesignTable = generateTemplateDesign(msg, true, userTemplates.contains(msg));
            pickTemplatesTable.add(templateDesignTable).expandX().fillX();
            pickTemplatesTable.row();
        }

    }

    public void switchTab(boolean toUserTemplates){
        Table selectedTab;
        Table notSelectedTab;
        if(toUserTemplates){
            refreshUserTemplates();
            selectedTab = userTemplatesTab;
            notSelectedTab = pickTemplatesTab;
            userTemplatesScroll.setVisible(true);
            pickTemplatesScroll.setVisible(false);
        }
        else{
            selectedTab = pickTemplatesTab;
            notSelectedTab = userTemplatesTab;
            userTemplatesScroll.setVisible(false);
            pickTemplatesScroll.setVisible(true);
        }

        selectedTab.findActor("tabBackground").setVisible(false);
        notSelectedTab.findActor("tabBackground").setVisible(true);

        ((Label) selectedTab.findActor("tabLabel")).getStyle().fontColor = Color.valueOf("ff0000");
        ((Label) notSelectedTab.findActor("tabLabel")).getStyle().fontColor = Color.valueOf("a2a2a2");
    }

    public void refreshUserTemplates(){
        ArrayList<String> userTemplates = getUserTemplates();

        userTemplatesTable.clear();
        for(String msg : userTemplates){
            Table templateDesignTable = generateTemplateDesign(msg, false, false);
            userTemplatesTable.add(templateDesignTable).expandX().fillX();
            userTemplatesTable.row();
        }
    }

    public Table generateTemplateDesign(String msg, boolean isSelectable, boolean isSelected){
        Label.LabelStyle templateLabelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_L_REGULAR),
                isSelected ? Color.valueOf("ff0000") : Color.valueOf("969696"));

        Table templateTable = new Table();
        Label templateLabel = new Label(msg, templateLabelStyle);
        templateLabel.setName("templateLabel");
        templateLabel.setAlignment(Align.left);

        Image separatorImage = new Image(assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
        separatorImage.getColor().a = 0.3f;

        templateTable.add(templateLabel).expandX().fillX().pad(4).left();
        if(isSelectable){
            Table checkBoxTable = new Table();

            Image uncheckBoxImage = new Image(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_CHECKBOX)));
            uncheckBoxImage.setName("uncheckBoxImage");
            uncheckBoxImage.setVisible(false);
            uncheckBoxImage.setPosition(-8, -7);

            Image checkedBoxImage = new Image(
                    new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_CHECKBOX_CHECKED)));
            checkedBoxImage.setName("checkedBoxImage");
            checkedBoxImage.setVisible(false);
            checkedBoxImage.setPosition(-8, -7);

            checkBoxTable.addActor(uncheckBoxImage);
            checkBoxTable.addActor(checkedBoxImage);

            if(isSelected) checkedBoxImage.setVisible(true);
            else uncheckBoxImage.setVisible(true);

            templateTable.add(checkBoxTable).padRight(8);
            templateTable.setName(isSelected ? "selected" : "notSelected");
        }

        templateTable.row();
        templateTable.add(separatorImage).expandX().fillX().colspan(2);
        templateTable.row();

        new DummyButton(templateTable, assets);

        if(isSelectable){
            setListenerForPickTemplate(templateTable);
        }
        else{
            setListenerForUserTemplate(templateTable, msg);
        }

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

    private Table getTabDesign(String heading, boolean left){
        Label.LabelStyle tabLabelStyle = new Label.LabelStyle();
        tabLabelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD);
        tabLabelStyle.fontColor = Color.valueOf("a2a2a2");

        Table tabTable = new Table();
        Image tabBackground = new Image(
                                new NinePatchDrawable(left ? assets.getPatches().get(Patches.Name.LEFT_CURVE_TAB_HEADER) :
                                assets.getPatches().get(Patches.Name.RIGHT_CURVE_TAB_HEADER))
                                );
        tabBackground.setFillParent(true);
        tabBackground.setName("tabBackground");

        Label tabLabel = new Label(heading, tabLabelStyle);
        tabLabel.setName("tabLabel");

        tabTable.addActor(tabBackground);
        tabTable.add(tabLabel).pad(5);

        return tabTable;
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

    public void setListenerForPickTemplate(final Table templateTable){
        templateTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                Label templateLabel = templateTable.findActor("templateLabel");
                Image uncheckBoxImage = templateTable.findActor("uncheckBoxImage");
                Image checkedBoxImage = templateTable.findActor("checkedBoxImage");
                uncheckBoxImage.setVisible(false);
                checkedBoxImage.setVisible(false);

                if(templateTable.getName() != null && templateTable.getName().equals("selected")){
                    templateTable.setName("notSelected");
                    uncheckBoxImage.setVisible(true);
                    templateLabel.getStyle().fontColor = Color.valueOf("969696");
                }
                else{
                    templateTable.setName("selected");
                    checkedBoxImage.setVisible(true);
                    templateLabel.getStyle().fontColor = Color.valueOf("ff0000");
                }
                saveTemplatePreferences();

                soundsPlayer.playSoundEffect(Sounds.Name.CHECKBOX_SOUND);
            }
        });
    }

    public void setListenerForUserTemplate(final Table templateTable, final String msg){
        templateTable.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Label templateLabel = templateTable.findActor("templateLabel");
                templateLabel.getStyle().fontColor = Color.valueOf("969696");
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Label templateLabel = templateTable.findActor("templateLabel");
                templateLabel.getStyle().fontColor = Color.valueOf("ff0000");
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(chatTemplateSelectedListener != null) chatTemplateSelectedListener.onSelected(msg);
            }
        });
    }

    public void setInternalListeners(){
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
    }


    public void setChatTemplateSelectedListener(ChatTemplateSelectedListener chatTemplateSelectedListener) {
        this.chatTemplateSelectedListener = chatTemplateSelectedListener;
    }
}
