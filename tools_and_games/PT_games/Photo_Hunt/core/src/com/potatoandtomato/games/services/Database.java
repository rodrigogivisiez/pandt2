package com.potatoandtomato.games.services;

import com.badlogic.gdx.math.MathUtils;
import com.firebase.client.*;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.games.absintf.DatabaseListener;
import com.potatoandtomato.games.models.ImageDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SiongLeng on 22/2/2016.
 */
public class Database {

    private GameCoordinator _coordinator;
    private Firebase _ref;
    private final String _storageTable = "storage";

    public Database(Firebase ref) {
        _ref = ref;
    }

    public void getLastImageIndex(final DatabaseListener<Integer> listener) {
        getData(getTable(_storageTable).orderByChild("index").limitToLast(1), new DatabaseListener<ArrayList<ImageDetails>>(ImageDetails.class) {
            @Override
            public void onCallback(ArrayList<ImageDetails> obj, Status st) {
                if (st == Status.SUCCESS && obj != null && obj.size() > 0) {
                    listener.onCallback(obj.get(0).getIndex(), st);
                } else {
                    listener.onCallback(0, st);
                }
            }
        });
    }

    public void getImageDetailsByIndex(int index, final DatabaseListener<ImageDetails> listener) {
        getData(getTable(_storageTable).orderByChild("index").startAt(index).endAt(index), new DatabaseListener<ArrayList<ImageDetails>>(ImageDetails.class) {
            @Override
            public void onCallback(ArrayList<ImageDetails> obj, Status st) {
                if (obj != null && st == Status.SUCCESS && obj.size() > 0) {
                    int max = obj.size() - 1;
                    listener.onCallback(obj.get(MathUtils.random(0, max)), st);
                } else {
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
    }

    public void getImageDetailsById(String id, DatabaseListener<ImageDetails> listener) {
        getSingleData(getTable(_storageTable).child(id), listener);
    }

    public void addNewImageDetails(ImageDetails details, final DatabaseListener listener){
        final String key = getTable(_storageTable).push().getKey();
        details.setId(key);
        getTable(_storageTable).child(key).setValue(details, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                listener.onCallback(key, Status.SUCCESS);
            }
        });
    }

    public void getAllImageDetails(final DatabaseListener listener){
        getData(getTable(_storageTable), listener);
    }

    public void removeImageById(String id, final DatabaseListener listener) {
        getTable(_storageTable).child(id).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError != null) listener.onCallback(null, Status.FAILED);
                else{
                    getAllImageDetails(new DatabaseListener<ArrayList<ImageDetails>>(ImageDetails.class) {
                        @Override
                        public void onCallback(ArrayList<ImageDetails> obj, Status st) {
                            int i = 0;
                            for(ImageDetails details : obj){
                                getTable(_storageTable).child(details.getId()).child("index").setValue(i);
                                i++;
                            }
                            listener.onCallback(null, Status.SUCCESS);
                        }
                    });
                }

            }
        });
    }

    public void removeAllImages(){
        getTable(_storageTable).setValue(null);
    }

    private Firebase getTable(String table){
        return _ref.child(table);
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


    private void getSingleData(Query ref, final DatabaseListener listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listener.onCallback(snapshot.getValue(listener.getType()), Status.SUCCESS);
                }
                else{
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
