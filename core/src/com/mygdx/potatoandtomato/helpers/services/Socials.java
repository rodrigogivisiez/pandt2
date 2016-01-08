package com.mygdx.potatoandtomato.helpers.services;

import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 21/12/2015.
 */
public class Socials {

    Preferences _pref;

    public Socials(Preferences _pref) {
        this._pref = _pref;
    }

    public void loginFacebook(final FacebookListener listener){
        Broadcaster.getInstance().subscribeOnceWithTimeout(BroadcastEvent.LOGIN_FACEBOOK_CALLBACK, 10000, new BroadcastListener<JsonObj>() {
            @Override
            public void onCallback(JsonObj obj, Status st) {
                //login success
                if(st == Status.SUCCESS){
                    if(obj != null){
                        String fbUserId = obj.getString(Terms.FACEBOOK_USERID);
                        String fbUsername = obj.getString(Terms.FACEBOOK_USERNAME);
                        if(fbUserId != null){
                            _pref.put(Terms.FACEBOOK_USERID, fbUserId);
                            _pref.put(Terms.FACEBOOK_USERNAME, fbUsername);
                            listener.onLoginComplete(FacebookListener.Result.SUCCESS);
                            return;
                        }
                    }
                }

                //user canceled facebook login / login failed
                listener.onLoginComplete(FacebookListener.Result.FAILED);
            }
        });
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_FACEBOOK_REQUEST);
    }

    public void logoutFacebook(final FacebookListener listener){
        Broadcaster.getInstance().subscribeOnceWithTimeout(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST, 10000, new BroadcastListener<JsonObj>() {
            @Override
            public void onCallback(JsonObj obj, Status st) {
                //logout success success
                if(st == Status.SUCCESS){
                    _pref.delete(Terms.FACEBOOK_USERID);
                    _pref.delete(Terms.FACEBOOK_USERNAME);
                    listener.onLogoutComplete(FacebookListener.Result.SUCCESS);
                    return;
                }

                listener.onLogoutComplete(FacebookListener.Result.FAILED);
            }
        });
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGOUT_FACEBOOK_REQUEST);
    }

    public boolean isFacebookLogon(){
        return (_pref.get(Terms.FACEBOOK_USERID) != null);
    }

    public FacebookProfile getFacebookProfile(){
        if(!isFacebookLogon()) return null;
        else{
            return new FacebookProfile(_pref.get(Terms.FACEBOOK_USERNAME), _pref.get(Terms.FACEBOOK_USERID));
        }
    }

}
