package com.mygdx.potatoandtomato.screens;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.abstractions.ScreenAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootScreen extends ScreenAbstract {

    Image _logoImg, _tomatoWeaponImg, _potatoWeaponImg, _greenGroundImg,
                _autumnGroundImg, _bgBlueImg, _bgAutumnImg, _sunriseImg, _sunrayImg;
    Stage _stage;

    public BootScreen(LogicAbstract logic) {
        super(logic);
    }

    @Override
    public void show() {
        _stage = new Stage();

        //Logo Image START////////////////////////////////////////////////////
        _logoImg = new Image(_textures.getLogoNoWeapon());
        Vector2 logoSize = Sizes.resize(260, _textures.getLogoNoWeapon());
        _logoImg.setSize(logoSize.x * 2.5f, logoSize.y * 2.5f);
        _logoImg.setPosition(Positions.centerX(logoSize.x * 2.5f), 250);
        _logoImg.getColor().a = 0;

        _tomatoWeaponImg = new Image(_textures.getLogoTomatoWeapon());
        _potatoWeaponImg = new Image(_textures.getLogoPotatoWeapon());
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

                _autumnGroundImg.addAction(fadeIn(0.5f));
                _sunriseImg.addAction(fadeIn(0.5f));
                _bgAutumnImg.addAction(sequence(fadeIn(0.5f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _sunrayImg.addAction(parallel(
                                fadeIn(1f),
                                                        forever(rotateBy(3, 0.15f))
                                                ));
                        return true;
                    }
                }));


                return true;
            }
        };
        float duration = 0.4f;
        _logoImg.addAction(sequence(parallel(
                                        fadeIn(duration),
                                        sizeTo(logoSize.x, logoSize.y, duration, Interpolation.sineIn),
                                        moveTo(Positions.centerX(logoSize.x), 290, duration, Interpolation.sineIn)
                                    ), completeAction)
                            );
        //Logo Image END////////////////////////////////////////////////////

        //Ground Texture START////////////////////////////////////////////
        _greenGroundImg = new Image(_textures.getGreenGround());
        _autumnGroundImg = new Image(_textures.getAutumnGround());
        _autumnGroundImg.getColor().a = 0;
        //Ground Texture END//////////////////////////////////////////////


        //Background Texture START
        _bgBlueImg = new Image(_textures.getBlueBg());
        _bgBlueImg.setSize(Positions.getWidth(), Positions.getHeight());

        _bgAutumnImg = new Image(_textures.getAutumnBg());
        _bgAutumnImg.setSize(Positions.getWidth(), Positions.getHeight());
        _bgAutumnImg.getColor().a = 0;

        _sunriseImg = new Image(_textures.getSunrise());
        _sunriseImg.getColor().a = 0;

        _sunrayImg = new Image(_textures.getSunray());
        _sunrayImg.setPosition(Positions.centerX(1200), -470);
        _sunrayImg.setOrigin(599f, 601f);
        _sunrayImg.setSize(1200, 1200);
        _sunrayImg.getColor().a = 0;
        //Background Texture END




        _stage.addActor(_bgBlueImg);
        _stage.addActor(_bgAutumnImg);
        _stage.addActor(_sunrayImg);
        _stage.addActor(_sunriseImg);
        _stage.addActor(_greenGroundImg);
        _stage.addActor(_autumnGroundImg);
        _stage.addActor(_potatoWeaponImg);
        _stage.addActor(_tomatoWeaponImg);
        _stage.addActor(_logoImg);



    }

    @Override
    public void render(float delta) {
        super.render(delta);
        _stage.act(delta);
        _stage.draw();
    }


}
