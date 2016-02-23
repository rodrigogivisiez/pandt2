package com.potatoandtomato.games.helpers;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.Status;
import com.potatoandtomato.games.absint.DatabaseListener;

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

    public void saveGameData(String jsonData){
        getInfoTable().setValue(jsonData);
    }

    public void getGameData(final DatabaseListener listener){
        getInfoTable().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listener.onCallback(snapshot.getValue(listener.getType()), Status.SUCCESS);
                } else {
                    listener.onCallback(null, Status.SUCCESS);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(0, Status.FAILED);
            }
        });
    }

}
