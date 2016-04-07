package com.potatoandtomato.games.services;

import com.firebase.client.*;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.Status;
import com.potatoandtomato.games.absintf.DatabaseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SiongLeng on 22/2/2016.
 */
public class Database {

    private GameCoordinator _coordinator;
    private Firebase _ref;


    public Database(GameCoordinator _coordinator) {
        this._coordinator = _coordinator;
        _ref = _coordinator.getFirebase();
    }

    private Firebase getInfoTable(){
        return  _ref.child(_coordinator.getRoomId()).child("info");
    }

    private Firebase getMatchHistoriesTable(){
        return  _ref.child("matchHistories");
    }

    public void saveGameData(String jsonData){
        getInfoTable().setValue(jsonData);
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
                listener.onCallback(results, Status.SUCCESS);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(0, Status.FAILED);
            }
        });
    }





}
