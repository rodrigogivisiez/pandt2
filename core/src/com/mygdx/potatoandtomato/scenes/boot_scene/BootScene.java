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
import com.mygdx.potatoandtomato.helpers.controls.BtnEggUpright;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootScene extends SceneAbstract {

    Image _logoImg, _tomatoWeaponImg, _potatoWeaponImg;
    BtnEggUpright _playButton;
    Table _socialTable;
    Label _socialLoginLabel;
    Image _socialIcon, _tickIcon, _crossIcon;

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
        _logoImg = new Image(_assets.getLogoNoWeapon());
        Vector2 logoSize = Sizes.resize(260, _assets.getLogoNoWeapon());
        _logoImg.setSize(logoSize.x * 2.5f, logoSize.y * 2.5f);
        _logoImg.setPosition(Positions.centerX(logoSize.x * 2.5f), 250);
        _logoImg.getColor().a = 0;

        _tomatoWeaponImg = new Image(_assets.getLogoTomatoWeapon());
        _potatoWeaponImg = new Image(_assets.getLogoPotatoWeapon());
        _potatoWeaponImg.setOrigin(Align.bottomRight);
        _tomatoWeaponImg.setPosition(230, 380);
        _potatoWeaponImg.setPosition(150, 380);
        _tomatoWeaponImg.setVisible(false);
        _potatoWeaponImg.setVisible(false);

        Action completeAction = new Action(){
            public boolean act( float delta ) {
                _tomatoWeaponImg.setVisible(true);
                _potatoWeaponImg.setVisible(true);

                _tomatoWeaponImg.addAction(parallel(
                        moveTo(260, 400, 0.4f),
                        forever(sequence(
                                rotateBy(3, 1f),
                                rotateBy(-3, 1f)
                        ))
                ));

                _potatoWeaponImg.addAction(parallel(
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
        _logoImg.addAction(sequence(parallel(
                        fadeIn(duration),
                        sizeTo(logoSize.x, logoSize.y, duration, Interpolation.sineOut),
                        moveTo(Positions.centerX(logoSize.x), 290, duration, Interpolation.sineOut)
                ), completeAction)
        );
        //Logo Image END////////////////////////////////////////////////////

        //Play Button START
        _playButton = new BtnEggUpright(_assets, _services.getSounds());
        _playButton.setPosition(Positions.centerX(_playButton.getWidth()), 160);
        _playButton.getColor().a = 0;
        _playButton.setContent(_assets.getPlayIcon());
        //Play Button END

        _root.addActor(_potatoWeaponImg);
        _root.addActor(_tomatoWeaponImg);
        _root.addActor(_logoImg);
        _root.addActor(_playButton);
    }

    public void showLoginBox(){

        _playButton.addAction(sequence(fadeOut(0.3f), new Action() {
            @Override
            public boolean act(float delta) {
                _socialTable.addAction(fadeIn(0.3f));
                return true;
            }
        }));

        _socialTable = new Table();
        _socialTable.setBackground(new TextureRegionDrawable(_assets.getWoodBgNormal()));
        _socialTable.setSize(300, 230);
        _socialTable.setPosition(Positions.centerX(300), 40);
        _socialTable.getColor().a = 0;

        _socialIcon = new Image(_assets.getSocialIcon());

        Label.LabelStyle socialLoginStyle = new Label.LabelStyle(_assets.getWhiteBold3GrayS(), Color.WHITE);
        _socialLoginLabel = new Label(_texts.socialLogin(), socialLoginStyle);
        _socialLoginLabel.setWrap(true);

        _tickIcon = new Image(_assets.getTick());
        _crossIcon = new Image(_assets.getCross());
        Vector2 tickSize = Sizes.resizeByH(40, _assets.getTick());
        Vector2 crossSize = Sizes.resizeByH(40, _assets.getCross());

        _socialTable.add(_socialIcon).padLeft(20).padRight(10);
        _socialTable.add(_socialLoginLabel).expandX().fillX().padRight(20).height(150);
        _socialTable.row();
        _socialTable.add(_tickIcon).size(tickSize.x, tickSize.y).uniformX();
        _socialTable.add(_crossIcon).size(crossSize.x, crossSize.y).uniformX();

        _root.addActor(_socialTable);
    }

    public void showSocialLoginProcessing(){
        _socialLoginLabel.addAction(forever(sequence(fadeOut(0.5f), fadeIn(0.5f))));
        _socialLoginLabel.setText(_texts.socialLoginProcessing());
        _socialLoginLabel.setAlignment(Align.center);
        _tickIcon.setVisible(false);
        _crossIcon.setVisible(false);
    }

    public void showSocialLoginFailed(){
        _socialLoginLabel.clearActions();
        _socialLoginLabel.getColor().a = 1;
        _socialLoginLabel.setText(_texts.socialLoginFailed());
        _socialLoginLabel.setAlignment(Align.left);
        _tickIcon.setVisible(true);
        _crossIcon.setVisible(true);
    }

    public void showLoggingIn(){
        _socialIcon.setDrawable(new TextureRegionDrawable(_assets.getLoginIcon()));
        _socialLoginLabel.addAction(forever(sequence(fadeOut(0.5f), fadeIn(0.5f))));
        _socialLoginLabel.setText(_texts.loginProcessing());
        _socialLoginLabel.setAlignment(Align.center);
        _tickIcon.setVisible(false);
        _crossIcon.setVisible(false);
    }

    public void showCreatingUser(){
        _socialIcon.setDrawable(new TextureRegionDrawable(_assets.getLoginIcon()));
        _socialLoginLabel.addAction(forever(sequence(fadeOut(0.5f), fadeIn(0.5f))));
        _socialLoginLabel.setText(_texts.creatingUser());
        _socialLoginLabel.setAlignment(Align.center);
        _tickIcon.setVisible(false);
        _crossIcon.setVisible(false);
    }

    public void showRetrieveUserFailed(){
        _socialIcon.setDrawable(new TextureRegionDrawable(_assets.getLoginIcon()));
        _socialLoginLabel.clearActions();
        _socialLoginLabel.getColor().a = 1;
        _socialLoginLabel.setText(_texts.failedRetrieveProfile());
        _socialLoginLabel.setAlignment(Align.center);
        _tickIcon.setVisible(true);
        _crossIcon.setVisible(true);
    }



}
