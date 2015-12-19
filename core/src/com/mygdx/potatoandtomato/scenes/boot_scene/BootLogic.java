package com.mygdx.potatoandtomato.scenes.boot_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class BootLogic extends LogicAbstract {

    BootScene _bootScene;
    boolean _fbStepPast;

    @Override
    public SceneAbstract getScene() {
        return _bootScene;
    }

    public BootLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _services.getGamingKit().addListener(new ConnectionChangedListener() {
            @Override
            public void onChanged(Status st) {
                if(st == Status.CONNECTED){
                    if(_services.getProfile().getMascotEnum() == null){
                        _screen.toScene(SceneEnum.MASCOT_PICK);
                    }
                    else{
                        _screen.toScene(SceneEnum.GAME_LIST);
                    }
                }
                else{
                    retrieveUserFailed();
                }
            }
        });

    }

    @Override
    public void onCreate() {
        super.onCreate();

        _services.getGamingKit().disconnect();
        _fbStepPast = false;
        _bootScene = new BootScene(_services, _screen);
        _bootScene.getPlayButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showLoginBox();
            }
        });
    }

    private void attachClickListenerToSocial(){
        _bootScene.getTickIcon().addListener(new ClickListener(){
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

    public void showLoginBox(){
        _bootScene.showLoginBox();
        attachClickListenerToSocial();
        if(_services.getPreferences().get(Terms.FACEBOOK_USERID) != null){    //user already logged in facebook before, log in again now
            loginFacebook();
        }
    }

    public void loginFacebook(){
        _bootScene.showSocialLoginProcessing();
        Broadcaster.getInstance().subscribeOnceWithTimeout(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, 10000, new BroadcastListener<JsonObj>() {
            @Override
            public void onCallback(JsonObj obj, Status st) {
                //login success
                if(st == Status.SUCCESS){
                    if(obj != null){
                        String fbUserId = obj.getString(Terms.FACEBOOK_USERID);
                        if(fbUserId != null){
                            _services.getPreferences().put(Terms.FACEBOOK_USERID, fbUserId);
                            loginPT();
                            return;
                        }
                    }
                }

                //user canceled facebook login / login failed
                _bootScene.showSocialLoginFailed();
            }
        });
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_FACEBOOK_REQUEST);
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
        _bootScene.showLoggingIn();
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
                        loginPTSuccess();
                    }
                }
            }
        });
    }

    public void createNewUser(){
        _bootScene.showCreatingUser();
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
                    loginPTSuccess();
                }
            }
        });
    }

    public void retrieveUserFailed(){
        _bootScene.showRetrieveUserFailed();
    }

    public void loginPTSuccess(){
        String fbUserId = _services.getPreferences().get(Terms.FACEBOOK_USERID);
        if(fbUserId != null){
            _services.getProfile().setFacebookUserId(fbUserId);
            _services.getDatabase().updateProfile(_services.getProfile());
        }

        _services.getGamingKit().connect(_services.getProfile());
    }





}
