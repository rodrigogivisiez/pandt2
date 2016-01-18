package com.mygdx.potatoandtomato.scenes.invite_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.helpers.controls.DummyButton;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.helpers.utils.MultiHashMap;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;


/**
 * Created by SiongLeng on 23/12/2015.
 */
public class InviteScene extends SceneAbstract {

    BtnEggDownward _inviteButton;
    MultiHashMap<String, Table> _usersHashMap;
    Table _recentPlayedTable;
    Table _facebookFriendsTable;

    public InviteScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public Table getRecentPlayedTable() {
        return _recentPlayedTable;
    }

    public Table getFacebookFriendsTable() {
        return _facebookFriendsTable;
    }

    public BtnEggDownward getInviteButton() {
        return _inviteButton;
    }

    @Override
    public void populateRoot() {
        _usersHashMap = new MultiHashMap();
        new TopBar(_root, _texts.inviteTitle(), false, _assets, _screen);



        Table _invitesRootTable = new Table();
        _invitesRootTable.setBackground(new TextureRegionDrawable(_assets.getGameListBg()));
        _invitesRootTable.align(Align.top);

        Table invitesTable = new Table();
        invitesTable.align(Align.top);
        ScrollPane scrollPane = new ScrollPane(invitesTable);
        _invitesRootTable.add(scrollPane).expand().fill();

        _recentPlayedTable = getExpandableTitleTable(_texts.recentlyPlay());
        _facebookFriendsTable = getExpandableTitleTable(_texts.faebookFriends());


        invitesTable.add(_recentPlayedTable).expandX().fillX();
        invitesTable.row();
        invitesTable.add(_facebookFriendsTable).expandX().fillX();
        invitesTable.padBottom(10);


        _inviteButton = new BtnEggDownward(_assets, _services.getSounds());
        _inviteButton.setText("Invite");


        _root.add(_invitesRootTable).padLeft(15).padRight(15).expandX().fillX().height(400);
        _root.row();
        _root.add(_inviteButton).expandX().padTop(-10);
    }

    private Table getExpandableTitleTable(String title){
        final Table expandableRoot = new Table();
        final Table expandableTitle = new Table();
        final Table contentRoot = new Table();

        Table messageTable = new Table();
        final Table contentTable = new Table();
        messageTable.setName("msg");
        contentTable.setName("content");

        expandableTitle.pad(10);
        expandableTitle.setBackground(new NinePatchDrawable(_assets.getExpandTitleBg()));
        new DummyButton(expandableTitle, _assets);

        Label titleLabel = new Label(title, new Label.LabelStyle(_assets.getWhitePizza2BlackS(), Color.WHITE));
        final Image icon = new Image(_assets.getExpandIcon());

        expandableTitle.add(titleLabel).expandX().fillX();
        expandableTitle.add(icon).size(10, 10).right();
        expandableTitle.setName("expanded");

        contentRoot.add(messageTable).expandX().fillX();
        contentRoot.row();
        contentRoot.add(contentTable).expandX().fillX();
        contentRoot.row();

        expandableRoot.add(expandableTitle).expandX().fillX();
        expandableRoot.row();
        expandableRoot.add(contentRoot).expandX().fillX();
        expandableRoot.row();


        expandableTitle.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(expandableTitle.getName().equals("expanded")){
                    expandableTitle.setName("collapsed");
                    icon.setDrawable(new TextureRegionDrawable(_assets.getCollapsedIcon()));
                    contentRoot.remove();
                }
                else{
                    expandableTitle.setName("expanded");
                    icon.setDrawable(new TextureRegionDrawable(_assets.getExpandIcon()));
                    expandableRoot.add(contentRoot).expandX().fillX();
                    expandableRoot.row();
                }
            }
        });



        return expandableRoot;
    }

    public Table putUserToTable(final Profile profile, Table targetTable){

        Table messageTable = targetTable.findActor("msg");
        messageTable.clear();
        Table contentTable = targetTable.findActor("content");

        Table userTable = new Table();
        userTable.setName("unselected");
        userTable.padLeft(10).padRight(10).padTop(5);
        new DummyButton(userTable, _assets);

        Label nameLabel = new Label(profile.getDisplayName(30), new Label.LabelStyle(_assets.getWhiteBold3GrayS(), Color.WHITE));

        Image selectBoxImage = new Image(_assets.getUnselectBox());
        selectBoxImage.setName("selectbox");
        Image separator = new Image(_assets.getWhiteLine());

        userTable.add(nameLabel).expandX().fillX();
        userTable.add(selectBoxImage).size(35, 35).padRight(10);
        userTable.row();
        userTable.add(separator).expandX().fillX().padTop(5).colspan(2);

        contentTable.add(userTable).expandX().fillX();
        contentTable.row();

        _usersHashMap.put(profile.getUserId(), userTable);

        return userTable;
    }


    public boolean toggleUserSelection(Profile user){
        boolean result = false;
        for(Table userTable : _usersHashMap.get(user.getUserId())){
            Image selectBox =  userTable.findActor("selectbox");
            if(userTable.getName().equals("selected")){
                userTable.setName("unselected");
                selectBox.setDrawable(new TextureRegionDrawable(_assets.getUnselectBox()));
                result = false;
            }
            else{
                userTable.setName("selected");
                selectBox.setDrawable(new TextureRegionDrawable(_assets.getSelectBox()));
                result = true;
            }
        }
        return result;
    }

    public void putMessageToTable(String msg, Table targetTable){

        Table messageTable = targetTable.findActor("msg");
        messageTable.clear();

        Label msgLabel = new Label(msg, new Label.LabelStyle(_assets.getWhiteNormal3GrayS(), Color.WHITE));
        msgLabel.setWrap(true);
        msgLabel.setName("msg");
        messageTable.add(msgLabel).expandX().fillX().pad(10);
    }



}
