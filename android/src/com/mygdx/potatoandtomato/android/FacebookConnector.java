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
                com.facebook.login.LoginManager.getInstance().logInWithPublishPermissions(
                        _activity,
                        Arrays.asList("publish_actions"));
            }
        });

    }

    private void registerCallBack(){

        LoginManager.getInstance().registerCallback(_callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        JsonObj json = new JsonObj();
                        json.put(Terms.FACEBOOK_USERID, loginResult.getAccessToken().getUserId());
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



}
