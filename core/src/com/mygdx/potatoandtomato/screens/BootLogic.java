package com.mygdx.potatoandtomato.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootLogic extends LogicAbstract {

    BootScreen _bootScreen;

    public BootLogic(PTGame game, Textures textures) {
        super(game, textures);
        _bootScreen = new BootScreen(this);
    }



    @Override
    public Screen getScreen() {
        return _bootScreen;
    }


}
