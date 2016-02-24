package com.mygdx.potatoandtomato.scenes.boot_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Status;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootLogic extends LogicAbstract {

    BootScene _bootScene;
    boolean _fbStepPast;
    boolean _logined;

    @Override
    public SceneAbstract getScene() {
        return _bootScene;
    }

    public BootLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
    }

    @Override
    public void onShow() {

        _services.getSoundsWrapper().playThemeMusic();
        _screen.showRotateSunrise();
        publishBroadcast(BroadcastEvent.DESTROY_ROOM);

        _services.getDatabase().offline();
        _services.getGamingKit().disconnect();
        _fbStepPast = false;
        _logined = false;
        dispose();

        _services.getDatabase().online();
        _bootScene = new BootScene(_services, _screen);
        _bootScene.getPlayButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _services.getSoundsWrapper().playSoundEffect(Sounds.Name.TOGETHER_CHEERS);
                showLoginBox();
            }
        });

        _services.getGamingKit().addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(ConnectStatus st) {
                if(!_logined){
                    if(st == ConnectStatus.CONNECTED){
                        _screen.hideRotateSunrise();
                        if(_services.getProfile().getGameName() == null){
                            _screen.toScene(SceneEnum.INPUT_NAME);
                        }
                        else{
                            _screen.toScene(SceneEnum.GAME_LIST);
                        }
                        _logined = true;
                    }
                    else{
                        retrieveUserFailed();
                    }
                }
                else{
                    if(st == ConnectStatus.DISCONNECTED){
                        publishBroadcast(BroadcastEvent.DESTROY_ROOM);
                        _screen.backToBoot();
                        _confirm.show(_texts.noConnection(), Confirm.Type.YES, null);
                    }
                }

            }
        });

        super.onShow();
    }

    public void showLoginBox(){
        _bootScene.showSocialLogin();
        attachClickListenerToSocial();
        if(_services.getSocials().isFacebookLogon()){    //user already logged in facebook before, log in again now
            loginFacebook();
        }
    }

    private void attachClickListenerToSocial(){
        _bootScene.getTickIcon().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_fbStepPast) loginFacebook();
                else loginPT();
            }
        });

        _bootScene.getCrossIcon().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_fbStepPast) loginPT();
                else Gdx.app.exit();
            }
        });
    }


    public void loginFacebook() {
        _bootScene.showSocialLoggingIn();

        _services.getSocials().loginFacebook(new FacebookListener() {
            @Override
            public void onLoginComplete(Result result) {
                if(result == Result.SUCCESS){
                    loginPT();
                }
                else{
                    _bootScene.showSocialLoginFailed();
                }
            }
        });
    }

    public void loginPT(){
        _fbStepPast = true;
        String userId = _services.getPreferences().get(Terms.USERID);
        if(userId != null){
            loginPTWithExistingUser(userId);
        }
        else{
            createNewUser();
        }
    }

    public void loginPTWithExistingUser(String userId){
         _bootScene.showPTLoggingIn();
        _services.getDatabase().getProfileByUserId(userId, new DatabaseListener<Profile>(Profile.class) {
            @Override
            public void onCallback(Profile obj, Status st) {
                if(st == Status.FAILED) retrieveUserFailed();
                else{
                    if(obj == null){        //user doesnt exist in database, create a new one
                        _services.getPreferences().delete(Terms.USERID);
                        createNewUser();
                    }
                    else{
                        _services.setProfile(obj);
                        loginGCM();
                    }
                }
            }
        });
    }

    public void createNewUser(){
        _bootScene.showPTCreatingUser();
        _services.getDatabase().loginAnonymous(new DatabaseListener<Profile>() {
            @Override
            public void onCallback(Profile obj, Status st) {
                if(st == Status.FAILED || obj == null) retrieveUserFailed();
                else{
                    createUserByUserId(obj.getUserId());
                }
            }
        });
    }

    public void createUserByUserId(String userId){
        _services.getDatabase().createUserByUserId(userId, new DatabaseListener<Profile>() {
            @Override
            public void onCallback(Profile obj, Status st) {
                if(st == Status.FAILED || obj == null) retrieveUserFailed();
                else{
                    _services.getPreferences().put(Terms.USERID, obj.getUserId());
                    _services.setProfile(obj);
                    loginGCM();
                }
            }
        });
    }

    public void retrieveUserFailed(){
        _bootScene.showPTLogInFailed();
    }

    public void loginGCM(){
        subscribeBroadcastOnceWithTimeout(BroadcastEvent.LOGIN_GCM_CALLBACK, 10000, new BroadcastListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if (st == Status.SUCCESS) {
                    _services.getProfile().setGcmId(obj);
                    loginPTSuccess();
                } else {
                    retrieveUserFailed();
                }
            }
        });
        publishBroadcast(BroadcastEvent.LOGIN_GCM_REQUEST);
    }


    public void loginPTSuccess(){
        FacebookProfile facebookProfile = _services.getSocials().getFacebookProfile();
        if(facebookProfile != null){
            _services.getProfile().setFacebookUserId(facebookProfile.getUserId());
            _services.getProfile().setFacebookName(facebookProfile.getName());
        }
        else{
            _services.getProfile().setFacebookUserId(null);
            _services.getProfile().setFacebookName(null);
        }
        _services.getDatabase().updateProfile(_services.getProfile(), null);
        _services.getGamingKit().connect(_services.getProfile());
        _services.getDatabase().onDcSetGameStateDisconnected(_services.getProfile(), null);
        _services.getChat().setUserId(_services.getProfile().getUserId());
    }
}
