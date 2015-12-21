package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import android.app.AlertDialog;
import com.badlogic.gdx.utils.Json;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class FacebookConnector {

    Activity _activity;
    CallbackManager _callbackManager;

    public FacebookConnector(Activity activity) {
        this._activity = activity;
        FacebookSdk.sdkInitialize(_activity);
        _callbackManager = CallbackManager.Factory.create();

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOGIN_FACEBOOK_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                registerCallBack();
                com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(
                        _activity,
                        Arrays.asList("user_friends"));
            }
        });

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                LoginManager.getInstance().logOut();
                Broadcaster.getInstance().broadcast(BroadcastEvent.LOGOUT_FACEBOOK_CALLBACK, null, Status.SUCCESS);
            }
        });

    }

    private void registerCallBack(){

        LoginManager.getInstance().registerCallback(_callbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        if(Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    mProfileTracker.stopTracking();
                                    successRetrieved(loginResult.getAccessToken().getUserId(), profile2.getName());
                                }
                            };
                            mProfileTracker.startTracking();
                        }
                        else {
                            Profile profile = Profile.getCurrentProfile();
                            successRetrieved(loginResult.getAccessToken().getUserId(), profile.getName());
                        }



                    }

                    private void successRetrieved(String fbUserId, String fbUsername){
                        JsonObj json = new JsonObj();
                        json.put(Terms.FACEBOOK_USERID, fbUserId);
                        json.put(Terms.FACEBOOK_USERNAME, fbUsername);
                        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK,
                                json, BroadcastListener.Status.SUCCESS);
                    }

                    @Override
                    public void onCancel() {
                        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, null, BroadcastListener.Status.SUCCESS);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, null, BroadcastListener.Status.FAILED);
                    }
                });


    }

    public CallbackManager getCallbackManager() {
        return _callbackManager;
    }
}
