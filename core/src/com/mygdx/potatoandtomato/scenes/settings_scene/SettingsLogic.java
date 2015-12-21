package com.mygdx.potatoandtomato.scenes.settings_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public class SettingsLogic extends LogicAbstract {

    SettingsScene _scene;

    public SettingsLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new SettingsScene(services, screen);
        _scene.getFacebookBtn().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _scene.getFacebookConfirm().show();
                _scene.getFacebookConfirm().setListener(new ConfirmResultListener() {
                    @Override
                    public void onResult(Result result) {
                        if(result == Result.YES){
                            facebookRequest();
                        }
                    }
                });


            }
        });
    }

    public void facebookRequest(){
        if(!_services.getSocials().isFacebookLogon()){
            _services.getSocials().loginFacebook(new FacebookListener() {
                @Override
                public void onLoginComplete(Result result) {
                    if(result == Result.SUCCESS){
                        _screen.backToBoot();
                    }
                    else{
                        _scene.showFacebookRequestFailed();
                    }
                }
            });
        }
        else{
            _services.getSocials().logoutFacebook(new FacebookListener() {
                @Override
                public void onLogoutComplete(Result result) {
                    _services.getProfile().setFacebookName(null);
                    _services.getProfile().setFacebookUserId(null);     //update profile on hide
                    _screen.backToBoot();   //always success
                }
            });
        }
    }

    @Override
    public void onShow() {
        super.onShow();
    }


    private void updateProfile(){
        String newName = _scene.getDisplayNameTextField().getText();
        if(_services.getProfile().getFacebookName() == null || !_services.getProfile().getFacebookName().equals(newName)){
            _services.getProfile().setGameName(newName);
        }
        _services.getDatabase().updateProfile(_services.getProfile());
    }

    @Override
    public void onHide() {
        updateProfile();
        super.onHide();
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
