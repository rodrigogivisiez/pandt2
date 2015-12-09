package com.mygdx.potatoandtomato.helpers.assets;

import com.firebase.client.*;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class FirebaseDB implements IDatabase {

    Firebase _ref;

    public FirebaseDB() {
        _ref = new Firebase("https://glaring-inferno-8572.firebaseIO.com");
    }

    @Override
    public void loginAnonymous(final DatabaseListener<Profile> listener) {
        _ref.authAnonymously(new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Profile profile = new Profile();
                profile.setUserId(authData.getUid());
                listener.onCallback(profile, DatabaseListener.Status.SUCCESS);
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onCallback(null, DatabaseListener.Status.FAILED);
            }
        });
    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {
        getSingleData(getTable_Users().child(userId), listener);
    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {
        Query queryRef = getTable_Users().orderByChild("facebookUserId").equalTo(facebookUserId);
        getSingleData(queryRef, listener);
    }

    @Override
    public void updateProfile(Profile profile) {
        getTable_Users().child(profile.getUserId()).setValue(profile);
    }

    @Override
    public void createUserByUserId(final String userId, final DatabaseListener<Profile> listener) {

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("userId", userId);
        getTable_Users().child(userId).setValue(userMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    Profile profile = new Profile();
                    profile.setUserId(userId);
                    listener.onCallback(profile, DatabaseListener.Status.SUCCESS);
                } else {
                    listener.onCallback(null, DatabaseListener.Status.FAILED);
                }
            }
        });
    }

    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        getDataCount(getTable_Test(), listener);
    }

    private Firebase getTable_Test(){
        Firebase r = _ref.child("testing");;
        r.onDisconnect().cancel();
        return r;
    }

    private Firebase getTable_Users(){
        Firebase r = _ref.child("users");;
        r.onDisconnect().cancel();
        return r;
    }

    private void getDataCount(Query ref, final DatabaseListener<Integer> listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.onCallback((int) snapshot.getChildrenCount(), DatabaseListener.Status.SUCCESS);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(0, DatabaseListener.Status.FAILED);
            }
        });
    }

    private void getSingleData(Query ref, final DatabaseListener listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listener.onCallback(snapshot.getValue(listener.getMyType()), DatabaseListener.Status.SUCCESS);
                }
                else{
                    listener.onCallback(null, DatabaseListener.Status.SUCCESS);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(0, DatabaseListener.Status.FAILED);
            }
        });
    }


}
