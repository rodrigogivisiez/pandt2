package com.mygdx.potatoandtomato.scenes.mascot_pick_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class MascotPickLogic extends LogicAbstract {

    MascotPickScene _scene;
    boolean mascotChosen;

    public MascotPickLogic(PTScreen screen, Assets assets) {
        super(screen, assets);
        _scene = new MascotPickScene(assets);

        _scene.getPotatoButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!mascotChosen){
                    mascotChosen = true;
                    _scene.choosedMascot(MascotEnum.POTATO);
                }
            }
        });

        _scene.getTomatoButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!mascotChosen) {
                    mascotChosen = true;
                    _scene.choosedMascot(MascotEnum.TOMATO);
                }
            }
        });

        _scene.getNextSceneButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
