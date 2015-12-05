package com.mygdx.potatoandtomato.scenes.boot_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.abstractions.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.scenes.shared_actors.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootScene extends SceneAbstract {

    Image _logoImg, _tomatoWeaponImg, _potatoWeaponImg, _greenGroundImg,
                _autumnGroundImg;
    BtnEggUpright _playButton;

    public BootScene(LogicAbstract logic) {
        super(logic);

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

                _autumnGroundImg.addAction(sequence(fadeIn(0.5f), new Action() {
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

        //Ground Texture START////////////////////////////////////////////
        _greenGroundImg = new Image(_textures.getGreenGround());
        _autumnGroundImg = new Image(_textures.getAutumnGround());
        _autumnGroundImg.getColor().a = 0;
        //Ground Texture END//////////////////////////////////////////////

        //Play Button START
        _playButton = new BtnEggUpright(_textures);
        _playButton.setPosition(Positions.centerX(_playButton.getWidth()), 160);
        _playButton.getColor().a = 0;
        _playButton.setContent(_textures.getPlayIcon());
        //Play Button END


        _root.addActor(_greenGroundImg);
        _root.addActor(_autumnGroundImg);
        _root.addActor(_potatoWeaponImg);
        _root.addActor(_tomatoWeaponImg);
        _root.addActor(_logoImg);
        _root.addActor(_playButton);
    }

    public BtnEggUpright getPlayButton() {
        return _playButton;
    }
}
