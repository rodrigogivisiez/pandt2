package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.potatoandtomato.common.utils.JsonObj;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 21/12/2015.
 */
public class Socials {

    Preferences _pref;
    Broadcaster _broadcaster;
    String fbUserId, fbUsername, fbToken;

    public Socials(Preferences _pref, Broadcaster _broadcaster) {
        this._broadcaster = _broadcaster;
        this._pref = _pref;
        fbUserId = _pref.get(Terms.FACEBOOK_USERID);
    }

    public void loginFacebook(final FacebookListener listener){
        _broadcaster.subscribeOnceWithTimeout(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, 10000, new BroadcastListener<JsonObj>() {
            @Override
            public void onCallback(JsonObj obj, Status st) {
                //login success
                if (st == Status.SUCCESS) {
                    if (obj != null) {
                        fbUserId = obj.getString(Terms.FACEBOOK_USERID);
                        fbUsername = obj.getString(Terms.FACEBOOK_USERNAME);
                        fbToken = obj.getString(Terms.FACEBOOK_TOKEN);
                        _pref.put(Terms.FACEBOOK_USERID, fbUserId);
                        if (fbUserId != null) {
                            listener.onLoginComplete(FacebookListener.Result.SUCCESS);
                            return;
                        }
                    }
                }

                //user canceled facebook login / login failed
                _pref.delete(Terms.FACEBOOK_USERID);
                fbUserId = null;
                listener.onLoginComplete(FacebookListener.Result.FAILED);
            }
        });
        _broadcaster.broadcast(BroadcastEvent.LOGIN_FACEBOOK_REQUEST);
    }

    public void logoutFacebook(final FacebookListener listener){
        _broadcaster.subscribeOnceWithTimeout(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST, 10000, new BroadcastListener<JsonObj>() {
            @Override
            public void onCallback(JsonObj obj, Status st) {
                //logout success success
                if (st == Status.SUCCESS) {
                    fbUserId = null;
                    fbUsername = null;
                    _pref.delete(Terms.FACEBOOK_USERID);
                    listener.onLogoutComplete(FacebookListener.Result.SUCCESS);
                    return;
                }

                listener.onLogoutComplete(FacebookListener.Result.FAILED);
            }
        });
        _broadcaster.broadcast(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST);
    }

    public boolean isFacebookLogon(){
        return (fbUserId != null && !fbUserId.equals(""));
    }

    public FacebookProfile getFacebookProfile(){
        if(!isFacebookLogon()) return null;
        else{
            return new FacebookProfile(fbUsername, fbUserId, fbToken);
        }
    }

}
