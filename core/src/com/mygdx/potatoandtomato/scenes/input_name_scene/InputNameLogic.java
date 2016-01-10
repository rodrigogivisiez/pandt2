package com.mygdx.potatoandtomato.scenes.input_name_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 10/1/2016.
 */
public class InputNameLogic extends LogicAbstract {

    private InputNameScene _scene;

    public InputNameLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new InputNameScene(_services, _screen);
    }

    @Override
    public void onInit() {
        super.onInit();

        _scene.getDisplayNameTextField().setText(_services.getProfile().getFacebookName());

        _scene.getBtnConfirm().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                saveNameIfValid(_scene.getDisplayNameTextField().getText());
            }
        });
    }

    public void saveNameIfValid(String name){
        if(!name.trim().equals("")){
            _services.getProfile().setGameName(name);
            _services.getDatabase().updateProfile(_services.getProfile(), null);
            _screen.toScene(SceneEnum.GAME_LIST);
        }
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
