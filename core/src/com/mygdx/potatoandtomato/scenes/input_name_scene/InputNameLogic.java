package com.mygdx.potatoandtomato.scenes.input_name_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.Confirm;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 10/1/2016.
 */
public class InputNameLogic extends LogicAbstract {

    private InputNameScene _scene;

    public InputNameLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        setSaveToStack(false);

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
        name = name.trim();
        if(!name.equals("")){
            loading();
            final String finalName = name;
            _services.getDatabase().getProfileByGameNameLower(name, new DatabaseListener<Profile>() {
                @Override
                public void onCallback(Profile obj, Status st) {
                    if (st == Status.SUCCESS) {
                        if (obj == null) {
                            _services.getProfile().setGameName(finalName);
                            _services.getDatabase().updateProfile(_services.getProfile(), null);
                            _screen.toScene(SceneEnum.GAME_LIST);
                        } else {
                            _services.getConfirm().show(_texts.duplicateNameError(), Confirm.Type.YES, null);
                            clearLoading();
                        }
                    } else {
                        _services.getConfirm().show(_texts.generalError(), Confirm.Type.YES, null);
                        clearLoading();
                    }
                }
            });


        }
    }

    public void loading(){
        _scene.getRoot().setTouchable(Touchable.disabled);
        _scene.getBtnConfirm().loading();
    }

    public void clearLoading(){
        _scene.getRoot().setTouchable(Touchable.enabled);
        _scene.getBtnConfirm().clearLoading();
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
