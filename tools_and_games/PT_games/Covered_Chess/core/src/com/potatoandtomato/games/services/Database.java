package com.potatoandtomato.games.services;

import com.firebase.client.*;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.utils.ThreadsPool;
import com.potatoandtomato.games.absint.DatabaseListener;
import com.potatoandtomato.games.models.MatchHistory;

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

    public void getLastMatchHistories(String userId, int count, final DatabaseListener<ArrayList<MatchHistory>> listener){
        getData(getMatchHistoriesTable().child(userId).orderByKey().limitToLast(count), listener);
    }

    public void getHeadToHeadMatchHistories(String fromUserId, String targetUserId, final DatabaseListener<ArrayList<MatchHistory>> listener){
        getData(getMatchHistoriesTable().child(fromUserId).orderByChild("opponentUserId")
                                        .startAt(targetUserId).endAt(targetUserId), listener);
    }

    public void saveMatchHistory(final String winnerUserId, final String loserUserId, final DatabaseListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                MatchHistory winnerMatchHistory = new MatchHistory(loserUserId, true);
                MatchHistory loserMatchHistory = new MatchHistory(winnerUserId, false);
                ThreadsPool threadsPool = new ThreadsPool();

                final Threadings.ThreadFragment fragment1 = new Threadings.ThreadFragment();
                saveSingleUserMatchHistory(winnerUserId, winnerMatchHistory, new DatabaseListener() {
                    @Override
                    public void onCallback(Object obj, Status st) {
                        fragment1.setFinished(true);
                    }
                });
                threadsPool.addFragment(fragment1);

                final Threadings.ThreadFragment fragment2 = new Threadings.ThreadFragment();
                saveSingleUserMatchHistory(loserUserId, loserMatchHistory, new DatabaseListener() {
                    @Override
                    public void onCallback(Object obj, Status st) {
                        fragment2.setFinished(true);
                    }
                });
                threadsPool.addFragment(fragment2);

                while (!threadsPool.allFinished()) {
                    Threadings.sleep(300);
                }

                if (listener != null) listener.onCallback(null, Status.SUCCESS);

            }
        });
    }

    private void saveSingleUserMatchHistory(String userId, MatchHistory matchHistory, final DatabaseListener listener){
        String key = getMatchHistoriesTable().child(userId).push().getKey();
        getMatchHistoriesTable().child(userId).child(key).setValue(matchHistory, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                listener.onCallback(null, firebaseError == null ? Status.SUCCESS : Status.FAILED);
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
