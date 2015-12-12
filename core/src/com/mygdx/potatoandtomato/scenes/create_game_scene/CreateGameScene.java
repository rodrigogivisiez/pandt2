package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class CreateGameScene extends SceneAbstract {

    Table _gameList;

    public CreateGameScene(Assets assets) {
        super(assets);
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.createGameTitle(), false, _textures, _fonts);
        _root.align(Align.topLeft);

        //left game list START
        _gameList = new Table();
        _gameList.setBackground(new NinePatchDrawable(_textures.getIrregularBg()));
        //left game list END


        _root.add(_gameList).width(140).expandY().fillY().padTop(25).padBottom(100).padLeft(-5);

    }

    public void populateGameList(){

    }



}
