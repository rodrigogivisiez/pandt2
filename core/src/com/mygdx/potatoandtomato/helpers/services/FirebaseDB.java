package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.utils.Array;
import com.firebase.client.*;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.models.*;

import java.util.*;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class FirebaseDB implements IDatabase {

    Firebase _ref;
    private String _tableTesting = "testing";
    private String _tableUsers = "users";
    private String _tableGames = "games";
    private String _tableRooms = "rooms";
    private String _tableHistories = "histories";
    private String _tableRoomNotifications = "roomNotifications";
    private Array<ListenerModel> _listenerModels;

    public FirebaseDB(String url){
        _ref = new Firebase(url);
        _listenerModels = new Array<>();
    }

    @Override
    public void offline() {
        Firebase.goOffline();
    }

    @Override
    public void online() {
        Firebase.goOnline();
    }

    @Override
    public void clearListenersByClass(Class clss) {
        ArrayList<Integer> toRemove = new ArrayList<>();
        for(int i = 0; i< _listenerModels.size; i++){
            if(_listenerModels.get(i).getClassName().equals(clss.getName())){
                toRemove.add(i);
            }
        }

        Collections.reverse(toRemove);
        for(Integer i : toRemove){
            ListenerModel listenerModel = _listenerModels.get(i);
            if(listenerModel.getValue() == null && listenerModel.getChild() == null){
                ((Firebase) listenerModel.getRef()).onDisconnect().cancel();
            }
            else if(listenerModel.getValue() != null){
                listenerModel.getRef().removeEventListener(listenerModel.getValue());
            }
            else if(listenerModel.getChild() != null){
                listenerModel.getRef().removeEventListener(listenerModel.getChild());
            }
            _listenerModels.removeIndex(i);
        }
    }

    @Override
    public void savePlayedHistory(Profile profile, Room room, DatabaseListener<String> listener) {
        for(RoomUser u : room.getRoomUsers().values()){
            if(!u.getProfile().equals(profile)){
                GameHistory history = new GameHistory();
                history.setPlayedWith(u.getProfile());
                history.setNameOfGame(room.getGame().getName());
                save(getTable(_tableHistories).child(profile.getUserId()).child(u.getProfile().getUserId()), history, listener);
            }
        }
    }

    @Override
    public void getPlayedHistories(Profile profile, final DatabaseListener<ArrayList<GameHistory>> listener) {
        DatabaseListener<ArrayList<GameHistory>> intermediateListener = new DatabaseListener<ArrayList<GameHistory>>(GameHistory.class) {
            @Override
            public void onCallback(final ArrayList<GameHistory> obj, Status st) {
                if(st == Status.SUCCESS){
                    Collections.reverse(obj);
                    final int[] count = {0};
                    for(final GameHistory history : obj){
                        //profile might be outdated, need to refresh
                        getProfileByUserId(history.getPlayedWith().getUserId(), new DatabaseListener<Profile>(Profile.class) {
                            @Override
                            public void onCallback(Profile obj2, Status st) {
                                if(st == Status.SUCCESS){
                                    history.setPlayedWith(obj2);
                                    count[0]++;
                                    if(count[0] == obj.size()){
                                        listener.onCallback(obj, st);
                                    }
                                }
                                else{
                                    listener.onCallback(null, st);
                                }
                            }
                        });
                    }
                }
                else{
                    listener.onCallback(null, st);
                }

            }
        };
        getData(getTable(_tableHistories).child(profile.getUserId()).orderByChild("creationDate"), intermediateListener);
    }

    @Override
    public void getPendingInvitationsCount(final Profile profile, final DatabaseListener<Integer> listener) {
        getData(getTable(_tableRooms).orderByChild("open").equalTo(true), new DatabaseListener<ArrayList<Room>>(Room.class) {
            @Override
            public void onCallback(ArrayList<Room> obj, Status st) {
                if(st == Status.SUCCESS){
                    int result = 0;
                    for(Room room : obj){
                        if(room.getInvitedUserByUserId(profile.getUserId()) != null) result++;
                    }
                    listener.onCallback(result, Status.SUCCESS);
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
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
    public void saveRoom(Room room, DatabaseListener<String> listener) {
        if(room.getId() == null){
            Firebase ref = getTable(_tableRooms).push();
            room.setId(ref.getKey());
            save(ref, room, listener);
            ref.child("open").onDisconnect().setValue(false);
            _listenerModels.add(new ListenerModel(ref.child("open"), Logs.getCallerClassName()));
            String notifyKey = notifyRoomChanged(room);
            getTable(_tableRoomNotifications).child(notifyKey).onDisconnect().setValue(room.getId() + "_DC");
            _listenerModels.add(new ListenerModel(getTable(_tableRoomNotifications).child(notifyKey), Logs.getCallerClassName()));
        }
        else{
            save(getTable(_tableRooms).child(room.getId()), room, listener);
            notifyRoomChanged(room);
        }
    }

    @Override
    public void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener) {
        getTable(_tableRooms).child(room.getId()).child("roomUsers").child(user.getUserId()).child("slotIndex").setValue(newIndex);
    }

    @Override
    public void monitorRoomById(String id, DatabaseListener<Room> listener) {
        Query queryRef = getTable(_tableRooms).child(id);
        getSingleDataMonitor(queryRef, listener);
    }

    @Override
    public void getRoomById(String id, DatabaseListener<Room> listener) {
        Query queryRef = getTable(_tableRooms).child(id);
        getSingleData(queryRef, listener);
    }

    @Override
    public void monitorAllRooms(final ArrayList<Room> rooms, final SpecialDatabaseListener<ArrayList<Room>, Room> listener) {
        getData(getTable(_tableRooms).orderByChild("open").equalTo(true), new DatabaseListener<ArrayList<Room>>(Room.class) {
            @Override
            public void onCallback(ArrayList<Room> obj, Status st) {
                if(st == Status.SUCCESS){
                    for(Room r : obj){
                        rooms.add(r);
                    }
                    listener.onCallbackTypeOne(rooms, SpecialDatabaseListener.Status.SUCCESS);
                }
                else{
                    listener.onCallbackTypeOne(null, SpecialDatabaseListener.Status.FAILED);
                    return;
                }
            }
        });


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                String changedRoomId = (String) snapshot.getValue();
                if(changedRoomId.endsWith("_DC")) changedRoomId = changedRoomId.substring(0, changedRoomId.length() - 3);
                roomChanged(changedRoomId);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String changedRoomId = (String) dataSnapshot.getValue();
                if(changedRoomId.endsWith("_DC")) changedRoomId = changedRoomId.substring(0, changedRoomId.length() - 3);
                roomChanged(changedRoomId);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

            private void roomChanged(final String roomId){
                getRoomById(roomId, new DatabaseListener<Room>(Room.class) {
                    @Override
                    public void onCallback(Room obj, Status st) {
                        if (st == Status.SUCCESS && obj != null) {
                            for (int i = 0; i < rooms.size(); i++) {
                                if (rooms.get(i).getId().equals(roomId)) {
                                    rooms.set(i, obj);
                                    break;
                                }
                            }
                            listener.onCallbackTypeTwo(obj, SpecialDatabaseListener.Status.SUCCESS);
                        }
                    }
                });
            }

        };

        getTable(_tableRoomNotifications).limitToLast(1).addChildEventListener(childEventListener);
        _listenerModels.add(new ListenerModel(getTable(_tableRoomNotifications), Logs.getCallerClassName(), childEventListener));

    }

    @Override
    public String notifyRoomChanged(Room room) {
        Firebase ref = getTable(_tableRoomNotifications).push();
        ref.setValue(room.getId());
        return ref.getKey();
    }

    @Override
    public void removeUserFromRoomOnDisconnect(Room room, Profile user, final DatabaseListener<String> listener) {
        final boolean[] roomUserSuccess = new boolean[1];
        final boolean[] roomNotificationSuccess = new boolean[1];
        final boolean[] failed = new boolean[1];
       for (Map.Entry<String, RoomUser> entry : room.getRoomUsers().entrySet()) {
           String index = entry.getKey();
           RoomUser roomUser = entry.getValue();
            if(roomUser.getProfile().getUserId().equals(user.getUserId())){
                Firebase ref = getTable(_tableRooms).child(room.getId()).child("roomUsers").child(user.getUserId());
                ref.onDisconnect().removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError error, Firebase firebase) {
                        if (error != null) {
                            if(!failed[0]){
                                failed[0] = true;
                                listener.onCallback(error.getMessage(), DatabaseListener.Status.FAILED);
                            }
                        }
                        else{
                            roomUserSuccess[0] = true;
                            if(roomNotificationSuccess[0] && !failed[0]) listener.onCallback(null, DatabaseListener.Status.SUCCESS);
                        }
                    }
                });
                _listenerModels.add(new ListenerModel(ref, Logs.getCallerClassName()));

                Firebase ref2 = getTable(_tableRoomNotifications).child(room.getId() + "_" + System.currentTimeMillis());
                ref2.onDisconnect().setValue(room.getId(), new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError error, Firebase firebase) {
                        if (error != null) {
                            if(!failed[0]){
                                failed[0] = true;
                                listener.onCallback(error.getMessage(), DatabaseListener.Status.FAILED);
                            }
                        }
                        else{
                            roomNotificationSuccess[0] = true;
                            if(roomUserSuccess[0] && !failed[0]) listener.onCallback(null, DatabaseListener.Status.SUCCESS);
                        }
                    }
                });
                _listenerModels.add(new ListenerModel(ref2, Logs.getCallerClassName()));
            }
        }
    }

    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        getDataCount(getTable(_tableTesting), listener);
    }

    private Firebase getTable(String _tableName){
        Firebase r = _ref.child(_tableName);
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

    private void getSingleDataMonitor(Query ref, final DatabaseListener listener){

        ValueEventListener valueEventListener = new ValueEventListener() {
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
        };
        ref.addValueEventListener(valueEventListener);

        _listenerModels.add(new ListenerModel(ref, Logs.getCallerClassName(), valueEventListener));
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

    private void getDataMonitor(final Query ref, final DatabaseListener listener){

        ValueEventListener valueEventListener = new ValueEventListener() {
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
        };

        ref.addValueEventListener(valueEventListener);

        _listenerModels.add(new ListenerModel(ref, Logs.getCallerClassName(), valueEventListener));

    }


    private void save(Firebase ref, Object value, final DatabaseListener listener){
        ref.setValue(value, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    if(listener != null) listener.onCallback(firebaseError.getMessage(), DatabaseListener.Status.FAILED);
                } else {
                    if(listener != null) listener.onCallback(null, DatabaseListener.Status.SUCCESS);
                }
            }
        });
    }


    private class ListenerModel{

        Query ref;
        String className;
        ChildEventListener child;
        ValueEventListener value;

        public ListenerModel(Query ref, String className, ChildEventListener child) {
            this.ref = ref;
            this.className = className;
            this.child = child;
        }

        public ListenerModel(Query ref, String className, ValueEventListener value) {
            this.value = value;
            this.className = className;
            this.ref = ref;
        }

        public ListenerModel(Query ref, String className) {
            this.className = className;
            this.ref = ref;
        }

        public Query getRef() {
            return ref;
        }

        public String getClassName() {
            return className;
        }

        public ChildEventListener getChild() {
            return child;
        }

        public ValueEventListener getValue() {
            return value;
        }
    }

}
