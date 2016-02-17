package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.math.MathUtils;
import com.firebase.client.*;
import com.potatoandtomato.common.Status;
import com.potatoandtomato.games.abs.database.DatabaseListener;
import com.potatoandtomato.games.abs.database.IDatabase;
import com.potatoandtomato.games.models.ImageData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class Database implements IDatabase {

    Firebase ref;
    private final String tableImages = "images";

    public Database(Firebase ref) {
        this.ref = ref;
    }

    @Override
    public void getTotalImagesCount(final DatabaseListener<Long> listener) {
        getData(getTableImages().orderByChild("index").limitToLast(1), new DatabaseListener<ArrayList<ImageData>>(ImageData.class) {
            @Override
            public void onCallback(ArrayList<ImageData> obj, Status st) {
                if(st == Status.SUCCESS && obj != null && obj.size() > 0){
                    listener.onCallback(obj.get(0).getIndex(), st);
                }
                else{
                    listener.onCallback((long) 0, st);
                }
            }
        });
    }

    @Override
    public void getImageIdByIndex(int index, final DatabaseListener<String> listener) {
        getData(getTableImages().orderByChild("index").startAt(index).endAt(index), new DatabaseListener<ArrayList<ImageData>>(ImageData.class) {
            @Override
            public void onCallback(ArrayList<ImageData> obj, Status st) {
                if (obj != null && st == Status.SUCCESS && obj.size() > 0) {
                    int max = obj.size() - 1;
                    listener.onCallback(obj.get(MathUtils.random(0, max)).getId(), st);
                } else {
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
    }

    @Override
    public void getImageDataById(String id, DatabaseListener<ImageData> listener) {
        getSingleData(getTableImages().child(id), listener);
    }

    @Override
    public void removeImageById(String id, final DatabaseListener listener) {
        getTableImages().child(id).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError != null) listener.onCallback(null, Status.FAILED);
                else{
                    getData(getTableImages(), new DatabaseListener<ArrayList<ImageData>>(ImageData.class) {
                        @Override
                        public void onCallback(ArrayList<ImageData> obj, Status st) {
                            int i = 0;
                            for(ImageData data : obj){
                                getTableImages().child(data.getId()).child("index").setValue(i);
                                i++;
                            }
                            listener.onCallback(null, Status.SUCCESS);
                        }
                    });
                }

            }
        });

    }

    public Firebase getTableImages(){
        return ref.child(tableImages);
    }

    private void getDataCount(Query ref, final DatabaseListener<Integer> listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.onCallback((int) snapshot.getChildrenCount(), Status.SUCCESS);
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
