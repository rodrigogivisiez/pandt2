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
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

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
        _services.getDatabase().clearAllListeners();
        _services.getChat().hideChat();
        _services.getCoins().dispose();
        _services.getProfile().reset();
        _services.getSoundsPlayer().playMusic(Sounds.Name.THEME_MUSIC);
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
                    checkContainsSecondaryUserId();
                }
                else{
                    _bootScene.showSocialLoginFailed();
                }
            }
        });
    }

    public void checkContainsSecondaryUserId(){
        String userId = _services.getPreferences().get(Terms.USERID);
        if(Strings.isEmpty(userId)){
            String userId2 = _services.getPreferences().get(Terms.USERID_2);
            if(!Strings.isEmpty(userId2)){
                _services.getPreferences().put(Terms.USERID, _services.getPreferences().get(Terms.USERID_2));
                _services.getPreferences().put(Terms.USER_SECRET, _services.getPreferences().get(Terms.USER_SECRET_2));
            }
        }
        afterFacebookPhase();
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

        _services.getRestfulApi().loginUser(userId, userSecret, _services.getSocials().getFacebookProfile(), new RestfulApiListener<String>() {
            @Override
            public void onCallback(String result, Status st) {
                if(st == Status.FAILED && result.equals("USER_NOT_FOUND")){
                    _services.getPreferences().delete(Terms.USERID);
                    _services.getPreferences().delete(Terms.USER_SECRET);
                    createNewUser();
                }
                else if(st == Status.FAILED && result.equals("FAIL_CONNECT")){
                    _bootScene.showPTDown();
                }
                else if(st == Status.FAILED){
                    retrieveUserFailed();
                }
                else{
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        LoginReturnData loginReturnData = objectMapper.readValue(result, LoginReturnData.class);
                        if(!loginReturnData.getUserId().equals(userId)){
                            _services.getPreferences().put(Terms.USERID_2, userId);
                            _services.getPreferences().put(Terms.USER_SECRET_2, userSecret);
                        }

                        _services.getPreferences().put(Terms.USERID, loginReturnData.getUserId());
                        _services.getPreferences().put(Terms.USER_SECRET, loginReturnData.getSecret());
                        loginPTWithToken(loginReturnData.getToken());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                        checkContainsSecondaryUserId();
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
                    _services.getProfile().copyToThis(obj);
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
        _services.getCoins().profileReady();
        _services.getGamingKit().connect(_services.getProfile());
        _services.getBroadcaster().broadcast(BroadcastEvent.USER_READY, _services.getProfile());
    }

    private void checkCrashedBefore(){
        String msg = Logs.getAndDeleteLogMsg();
        if(!Strings.isEmpty(msg)){
            _services.getDatabase().saveLog(msg);
            _services.getConfirm().show(ConfirmIdentifier.CrashedReportSent, _texts.appsCrashed(), Confirm.Type.YES, null);
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
                else checkContainsSecondaryUserId();
            }
        });

        _bootScene.getCrossIcon().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_fbStepPast) checkContainsSecondaryUserId();
                else Gdx.app.exit();
            }
        });

        _services.getGamingKit().addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ConnectStatus st) {
                if(_services.getProfile() != null && userId != null && userId.equals(_services.getProfile().getUserId())){
                    if(!_logined){
                        if(st == ConnectStatus.CONNECTED){
                            _screen.hideRotateSunrise();
                            if(Strings.isEmpty(_services.getProfile().getGameName())){
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
