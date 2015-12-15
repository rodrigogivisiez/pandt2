package com.mygdx.potatoandtomato.helpers.services;

import com.firebase.client.*;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class FirebaseDB implements IDatabase {

    Firebase _ref;
    private String _tableTesting = "testing";
    private String _tableUsers = "users";
    private String _tableGames = "games";

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
        getSingleData(getTable(_tableUsers).child(userId), listener);
    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {
        Query queryRef = getTable(_tableUsers).orderByChild("facebookUserId").equalTo(facebookUserId);
        getSingleData(queryRef, listener);
    }

    @Override
    public void updateProfile(Profile profile) {
        getTable(_tableUsers).child(profile.getUserId()).setValue(profile);
    }

    @Override
    public void createUserByUserId(final String userId, final DatabaseListener<Profile> listener) {

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("userId", userId);
        getTable(_tableUsers).child(userId).setValue(userMap, new Firebase.CompletionListener() {
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
    public void getAllGames(DatabaseListener<ArrayList<Game>> listener) {
        getData(getTable(_tableGames), listener);
    }

    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        getDataCount(getTable(_tableTesting), listener);
    }

    private Firebase getTable(String _tableName){
        Firebase r = _ref.child(_tableName);;
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
                    listener.onCallback(snapshot.getValue(listener.getType()), DatabaseListener.Status.SUCCESS);
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

    private void getData(final Query ref, final DatabaseListener listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Object> results = new ArrayList<Object>();
                for(DataSnapshot postSnapShot : snapshot.getChildren()){
                    Object newPost = postSnapShot.getValue(listener.getType());
                    results.add(newPost);
                }
                listener.onCallback(results, DatabaseListener.Status.SUCCESS);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(0, DatabaseListener.Status.FAILED);
            }
        });
    }

}
