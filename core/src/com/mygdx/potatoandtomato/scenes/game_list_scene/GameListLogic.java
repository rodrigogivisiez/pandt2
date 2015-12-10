package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListLogic extends LogicAbstract {

    GameListScene _scene;

    public GameListLogic(PTScreen screen, Assets assets) {
        super(screen, assets);
        _scene = new GameListScene(assets);

        for(int i = 0; i<20; i++){
            final Actor clicked = _scene.addNewGameRow();
            clicked.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    _scene.gameRowHighlight(clicked.getName());
                }
            });
        }

    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
