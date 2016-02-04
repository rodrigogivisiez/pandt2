package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.Threadings;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class FacebookConnector {

    Activity _activity;
    CallbackManager _callbackManager;
    AccessToken _accessToken;
    Broadcaster _broadcaster;

    public FacebookConnector(Activity activity, Broadcaster broadcaster) {
        this._activity = activity;
        this._broadcaster = broadcaster;
        FacebookSdk.sdkInitialize(_activity);
        _callbackManager = CallbackManager.Factory.create();

        _broadcaster.subscribe(BroadcastEvent.LOGIN_FACEBOOK_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                registerCallBack();
                com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(
                        _activity,
                        Arrays.asList("user_friends"));
            }
        });

        _broadcaster.subscribe(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                LoginManager.getInstance().logOut();
                _broadcaster.broadcast(BroadcastEvent.LOGOUT_FACEBOOK_CALLBACK, null, Status.SUCCESS);
            }
        });

        _broadcaster.subscribe(BroadcastEvent.FACEBOOK_GET_FRIENDS_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                getAllFriends();
            }
        });


    }

    private void getAllFriends(){
        final GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        _accessToken,
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                ArrayList<FacebookProfile> friendsList = new ArrayList();
                                for(int i=0; i<jsonArray.length();i++){
                                    JSONObject c= null;
                                    try {
                                        c = jsonArray.getJSONObject(i);
                                        FacebookProfile profile = new FacebookProfile(c.getString("name"), c.getString("id"));
                                        friendsList.add(profile);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                _broadcaster.broadcast(BroadcastEvent.FACEBOOK_GET_FRIENDS_RESPONSE,
                                        friendsList, Status.SUCCESS);
                            }


                        })

        );

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                batch.executeAndWait();
            }
        });


    }

    private void registerCallBack(){

        LoginManager.getInstance().registerCallback(_callbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        _accessToken = loginResult.getAccessToken();
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
                        _broadcaster.broadcast(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK,
                                json, Status.SUCCESS);
                    }

                    @Override
                    public void onCancel() {
                        _broadcaster.broadcast(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, null, Status.SUCCESS);
                    }

                    @Override
                    public void onError(FacebookException error) {

                        if (error instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                                _broadcaster.broadcast(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST);
                            }
                        }
                        else{
                            _broadcaster.broadcast(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, null, Status.FAILED);
                        }

                    }
                });


    }

    public CallbackManager getCallbackManager() {
        return _callbackManager;
    }
}
