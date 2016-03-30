package com.mygdx.potatoandtomato.scenes.boot_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggUpright;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.statics.Global;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootScene extends SceneAbstract {

    BtnEggUpright _playButton;
    Table _infoTable, _titleTable;
    Image _tickIcon, _crossIcon;

    public BootScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public BtnEggUpright getPlayButton() {
        return _playButton;
    }

    public Image getTickIcon() { return _tickIcon; }

    public Image getCrossIcon() { return _crossIcon; }

    @Override
    public void populateRoot() {
        //Logo Image START////////////////////////////////////////////////////
        Image logoImg = new Image(_assets.getTextures().get(Textures.Name.LOGO_NO_WEAPON));
        Vector2 logoSize = Sizes.resize(260, _assets.getTextures().get(Textures.Name.LOGO_NO_WEAPON));
        logoImg.setSize(logoSize.x * 2.5f, logoSize.y * 2.5f);
        logoImg.setPosition(Positions.centerX(logoSize.x * 2.5f), 250);
        logoImg.getColor().a = 0;

        final Image tomatoWeaponImg = new Image(_assets.getTextures().get(Textures.Name.LOGO_TOMATO_WEAPON));
        final Image potatoWeaponImg = new Image(_assets.getTextures().get(Textures.Name.LOGO_POTATO_WEAPON));
        potatoWeaponImg.setOrigin(Align.bottomRight);
        tomatoWeaponImg.setPosition(230, 380);
        potatoWeaponImg.setPosition(150, 380);
        tomatoWeaponImg.setVisible(false);
        potatoWeaponImg.setVisible(false);

        Action completeAction = new Action(){
            public boolean act( float delta ) {
                tomatoWeaponImg.setVisible(true);
                potatoWeaponImg.setVisible(true);

                tomatoWeaponImg.addAction(parallel(
                        moveTo(260, 400, 0.4f),
                        forever(sequence(
                                rotateBy(3, 1f),
                                rotateBy(-3, 1f)
                        ))
                ));

                potatoWeaponImg.addAction(parallel(
                        moveTo(55, 400, 0.4f),
                        forever(sequence(
                                rotateBy(-2, 1.3f),
                                rotateBy(2, 1.3f)
                        ))
                ));

                _playButton.addAction(sequence(delay(0.5f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _playButton.animate();
                        return true;
                    }
                }));


                return true;
            }
        };
        float duration = 0.4f;
        logoImg.addAction(sequence(parallel(
                        fadeIn(duration),
                        sizeTo(logoSize.x, logoSize.y, duration, Interpolation.sineOut),
                        moveTo(Positions.centerX(logoSize.x), 290, duration, Interpolation.sineOut)
                ), completeAction)
        );
        //Logo Image END////////////////////////////////////////////////////

        //Play Button START
        _playButton = new BtnEggUpright(_assets, _services.getSoundsWrapper(), 140);
        _playButton.setPosition(Positions.centerX(_playButton.getWidth()), 150);
        _playButton.getColor().a = 0;
        _playButton.setContent(_assets.getTextures().get(Textures.Name.PLAY_ICON));
        //Play Button END

        //Game Version START
        Label.LabelStyle versionStyle = new Label.LabelStyle();
        versionStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_XS_SEMIBOLD_B_ffffff_000000_1);
        Label versionLabel = new Label(String.format(_texts.build(), _services.getVersionControl().getClientVersion())
                            + (Global.DEBUG ? " " + _texts.debug() : ""), versionStyle);
        //Game Version END

        ///////////////////////////////////////
        //Info table
        ////////////////////////////////////////
        _infoTable = new Table();
        _infoTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));
        _infoTable.setSize(300, 230);
        _infoTable.setPosition(Positions.centerX(300), 40);
        _infoTable.getColor().a = 0;


        ///////////////////////////////////////////
        //Populate root
        ///////////////////////////////////////////
        _root.addActor(potatoWeaponImg);
        _root.addActor(tomatoWeaponImg);
        _root.addActor(logoImg);
        _root.addActor(_playButton);
        _root.add(versionLabel).expand().bottom().right().padRight(10).padBottom(10);
        _root.addActor(_infoTable);
    }

    public void showSocialLogin(){
        _playButton.addAction(sequence(fadeOut(0.3f), new Action() {
            @Override
            public boolean act(float delta) {
                _infoTable.addAction(fadeIn(0.3f));
                _playButton.setVisible(false);
                return true;
            }
        }));

        _infoTable.clear();
        _infoTable.padTop(10);

        ///////////////////////////////
        //Facebook title
        //////////////////////////////
        Image facebookImage = new Image(_assets.getTextures().get(Textures.Name.FACEBOOK_ICON));
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.fontColor = Color.BLACK;
        titleStyle.font = _assets.getFonts().get(Fonts.FontId.HELVETICA_L_HEAVY);
        Label titleLabel = new Label(_texts.socialLogin(), titleStyle);

        _titleTable = new Table();
        _titleTable.add(facebookImage);
        _titleTable.add(titleLabel).padLeft(10);

        ///////////////////////////////
        //Content
        ////////////////////////////////
        Table contentTable = new Table();
        contentTable.setName("contentTable");
        contentTable.align(Align.topLeft);
        Table tomatoTable = new Table();
        tomatoTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_WHITE_BG)));
        tomatoTable.pad(5);

        Label.LabelStyle contentStyle = new Label.LabelStyle();
        contentStyle.fontColor = Color.valueOf("573801");
        contentStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD);
        Label tomatoLabel = new Label(_texts.socialLoginTomato(), contentStyle);
        tomatoLabel.setWrap(true);
        Image tomatoHiImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_HI));
        tomatoTable.add(tomatoLabel).expandX().fillX().padLeft(10);
        tomatoTable.add(tomatoHiImage).height(40).width(50);

        Table potatoTable = new Table();
        potatoTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_WHITE_BG)));
        potatoTable.pad(5);

        Label potatoLabel = new Label(_texts.socialLoginPotato(), contentStyle);
        potatoLabel.setWrap(true);
        Image potatoHiImage = new Image(_assets.getTextures().get(Textures.Name.POTATO_HI));
        potatoTable.add(potatoHiImage).height(45).width(48).padRight(10);
        potatoTable.add(potatoLabel).expandX().fillX();

        contentTable.add(tomatoTable).expandX().fillX().padLeft(20).padRight(20);
        contentTable.row();
        contentTable.add(potatoTable).expandX().fillX().padLeft(20).padRight(20).padTop(5);

        //////////////////////////////////
        //Tick Cross Button
        /////////////////////////////////
        Table choicesTable = new Table();
        choicesTable.setName("choicesTable");
        _tickIcon = new Image(_assets.getTextures().get(Textures.Name.TICK_ICON));
        _crossIcon = new Image(_assets.getTextures().get(Textures.Name.CROSS_ICON));
        choicesTable.add(_tickIcon).size(50, 50).padRight(20);
        choicesTable.add(_crossIcon).size(50, 50).padLeft(20);

        //////////////////////////////
        //Populate info table
        //////////////////////////////

        _infoTable.add(_titleTable).expandX().fillX().height(37);
        _infoTable.row();
        _infoTable.add(contentTable).expand().fill();
        _infoTable.row();
        _infoTable.add(choicesTable).expandX().fillX().padBottom(15).padTop(5);

    }

    public void showSocialLoggingIn(){
        setLoading(_texts.socialLoginProcessing());
    }

    public void showSocialLoginFailed(){
        _infoTable.findActor("choicesTable").setVisible(true);
        setMessage(_texts.socialLoginFailed());
    }

    public void showPTLoggingIn(){
        _titleTable.setVisible(false);
        setLoading(_texts.loginProcessing());
    }

    public void showPTCreatingUser(){
        _titleTable.setVisible(false);
        setLoading(_texts.creatingUser());
    }

    public void showPTLogInFailed(){
        _infoTable.findActor("choicesTable").setVisible(true);
        setMessage(_texts.failedRetrieveProfile());
    }

    private void setLoading(String msg){
        _infoTable.findActor("choicesTable").setVisible(false);
        Table contentTable = _infoTable.findActor("contentTable");
        contentTable.setClip(true);
        contentTable.align(Align.center);
        contentTable.clear();

        Table loadingTable = new Table();
        loadingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_WHITE_BG)));
        loadingTable.pad(15);

        Label.LabelStyle contentStyle = new Label.LabelStyle();
        contentStyle.fontColor = Color.valueOf("573801");
        contentStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD);
        Label loadingLabel = new Label(msg, contentStyle);
        loadingTable.add(loadingLabel);

        Image loginMascotsImage = new Image(_assets.getTextures().get(Textures.Name.LOGGING_IN_MASCOTS));
        Vector2 sizes = Sizes.resize(100, _assets.getTextures().get(Textures.Name.LOGGING_IN_MASCOTS));
        loginMascotsImage.setSize(sizes.x, sizes.y);
        loginMascotsImage.setPosition(-100, 60);
        loginMascotsImage.addAction(forever(sequence(moveBy(400, 0, 3f), moveTo(-100, 60))));

        contentTable.add(loadingTable).expandX().fillX().padLeft(20).padRight(20).padTop(45);
        contentTable.addActor(loginMascotsImage);
    }

    private void setMessage(String msg){
        Table contentTable = _infoTable.findActor("contentTable");
        contentTable.clear();

        Table msgTable = new Table();
        msgTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_WHITE_BG)));
        msgTable.pad(15);

        Label.LabelStyle contentStyle = new Label.LabelStyle();
        contentStyle.fontColor = Color.valueOf("573801");
        contentStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD);   Label loadingLabel = new Label(msg, contentStyle);
        loadingLabel.setWrap(true);
        msgTable.add(loadingLabel).expand().fill();

        contentTable.add(msgTable).expandX().fillX().padLeft(20).padRight(20);
    }





}
