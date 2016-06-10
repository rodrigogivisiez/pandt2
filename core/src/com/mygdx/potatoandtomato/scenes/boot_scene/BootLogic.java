package com.mygdx.potatoandtomato.scenes.boot_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.UserIdSecretModel;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootLogic extends LogicAbstract {

    BootScene _bootScene;
    boolean _fbStepPast;
    boolean _logined;

    public BootLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _bootScene = new BootScene(_services, _screen);
    }

    @Override
    public void onShow() {
        super.onShow();

        _bootScene.reset();
        _services.getSoundsPlayer().playThemeMusic();
        _screen.showRotateSunrise();
        publishBroadcast(BroadcastEvent.DESTROY_ROOM);

        _services.getDatabase().offline();
        _services.getGamingKit().disconnect();
        _fbStepPast = false;
        _logined = false;
        _services.getDatabase().online();
        checkCrashedBefore();
    }

    public void showLoginBox(){
        _bootScene.showSocialLogin();
        if(_services.getSocials().isFacebookLogon()){    //user already logged in facebook before, log in again now
            loginFacebook();
        }
    }

    public void loginFacebook() {
        _bootScene.showSocialLoggingIn();

        _services.getSocials().loginFacebook(new FacebookListener() {
            @Override
            public void onLoginComplete(Result result) {
                if(result == Result.SUCCESS){
                    afterFacebookPhase();
                }
                else{
                    _bootScene.showSocialLoginFailed();
                }
            }
        });
    }

    public void afterFacebookPhase(){
        _fbStepPast = true;
        _bootScene.showPTLoggingIn();
        String userId = _services.getPreferences().get(Terms.USERID);

        if(!Strings.isEmpty(userId)){
            retrieveUserToken();
        }
        else{
            createNewUser();
        }
    }

    public void retrieveUserToken(){
        final String userId = _services.getPreferences().get(Terms.USERID);
        final String userSecret = _services.getPreferences().get(Terms.USER_SECRET);

        _services.getRestfulApi().loginUser(userId, userSecret, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String token, Status st) {
                if(st == Status.FAILED && token.equals("USER_NOT_FOUND")){
                    _services.getPreferences().delete(Terms.USERID);
                    _services.getPreferences().delete(Terms.USER_SECRET);
                    createNewUser();
                }
                else if(st == Status.FAILED && token.equals("FAIL_CONNECT")){
                    _bootScene.showPTDown();
                }
                else if(st == Status.FAILED){
                    retrieveUserFailed();
                }
                else{
                    loginPTWithToken(token);
                }
            }
        });
    }

    public void createNewUser(){
        _bootScene.showPTCreatingUser();
        if(_services.getSocials().isFacebookLogon()){
            _services.getRestfulApi().createNewUserWithFacebookProfile(_services.getSocials().getFacebookProfile(), new RestfulApiListener<UserIdSecretModel>() {
                @Override
                public void onCallback(UserIdSecretModel obj, Status st) {
                    if(st == Status.FAILED){
                        retrieveUserFailed();
                    }
                    else{
                        _services.getPreferences().put(Terms.USERID, obj.getUserId());
                        _services.getPreferences().put(Terms.USER_SECRET, obj.getSecret());
                        afterFacebookPhase();
                    }
                }
            });
        }
        else{
            _services.getRestfulApi().createNewUser(new RestfulApiListener<UserIdSecretModel>() {
                @Override
                public void onCallback(UserIdSecretModel obj, Status st) {
                    if (st == Status.FAILED) {
                        retrieveUserFailed();
                    } else {
                        _services.getPreferences().put(Terms.USERID, obj.getUserId());
                        _services.getPreferences().put(Terms.USER_SECRET, obj.getSecret());
                        retrieveUserToken();
                    }
                }
            });
        }
    }

    public void loginPTWithToken(final String token){
        _services.getDatabase().unauth();
        _services.getDatabase().authenticateUserByToken(token, new DatabaseListener<Profile>(Profile.class) {
            @Override
            public void onCallback(Profile obj, Status st) {
                if (st == Status.FAILED || obj == null){
                    retrieveUserFailed();
                }
                else {
                    obj.setToken(token);
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
        _services.getDatabase().updateProfile(_services.getProfile(), null);
        _services.getGamingKit().connect(_services.getProfile());
    }

    private void checkCrashedBefore(){
        String msg = Logs.getAndDeleteLogMsg();
        if(!Strings.isEmpty(msg)){
            _services.getDatabase().saveLog(msg);
            _services.getConfirm().show(_texts.appsCrashed(), Confirm.Type.YES, null);
        }
    }

    @Override
    public void setListeners() {
        _bootScene.getPlayButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_CHEERS);
                showLoginBox();
                //getBroadcaster().broadcast(BroadcastEvent.SHOW_REWARD_VIDEO);
            }
        });

        _bootScene.getTickIcon().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_fbStepPast) loginFacebook();
                else afterFacebookPhase();
            }
        });

        _bootScene.getCrossIcon().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_fbStepPast) afterFacebookPhase();
                else Gdx.app.exit();
            }
        });

        _services.getGamingKit().addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ConnectStatus st) {
                if(userId.equals(_services.getProfile().getUserId())){
                    if(!_logined){
                        if(st == ConnectStatus.CONNECTED){
                            _services.getDatabase().clearAllListeners();

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
                }
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _bootScene;
    }


}
