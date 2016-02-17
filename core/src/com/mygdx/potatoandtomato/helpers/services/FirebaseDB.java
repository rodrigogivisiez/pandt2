package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.utils.Array;
import com.firebase.client.*;
import com.firebase.client.snapshot.DoubleNode;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.Status;
import com.shaded.fasterxml.jackson.databind.DeserializationFeature;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

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
    private String _tableGameBelongData = "gameBelongData";
    private String _tableServerTimeInfo = ".info/serverTimeOffset";
    private Array<ListenerModel> _listenerModels;

    public FirebaseDB(String url){
        _ref = new Firebase(url);
        _listenerModels = new Array();
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
    public void clearAllListeners(){
        for(ListenerModel listenerModel : _listenerModels){
            removeListenerModel(listenerModel);
        }
        _listenerModels.clear();
    }

    @Override
    public void clearListenersByClassTag(String classTag) {
        ArrayList<Integer> toRemove = new ArrayList();
        for(int i = 0; i< _listenerModels.size; i++){
            if(_listenerModels.get(i).getClassName().equals(classTag)){
                toRemove.add(i);
            }
        }

        Collections.reverse(toRemove);
        for(Integer i : toRemove){
            ListenerModel listenerModel = _listenerModels.get(i);
            removeListenerModel(listenerModel);
            _listenerModels.removeIndex(i);
        }
    }

    private void removeListenerModel(ListenerModel listenerModel){
        if(listenerModel.getValue() == null && listenerModel.getChild() == null){
            ((Firebase) listenerModel.getRef()).onDisconnect().cancel();
        }
        else if(listenerModel.getValue() != null){
            listenerModel.getRef().removeEventListener(listenerModel.getValue());
        }
        else if(listenerModel.getChild() != null){
            listenerModel.getRef().removeEventListener(listenerModel.getChild());
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

                    if(obj.size() == 0){
                        listener.onCallback(obj, Status.SUCCESS);
                        return;
                    }

                    Collections.reverse(obj);
                    listener.onCallback(obj, st);

//                    final int[] count = {0};
//                    for(final GameHistory history : obj){
//                        //profile might be outdated, need to refresh
//                        getProfileByUserId(history.getPlayedWith().getUserId(), new DatabaseListener<Profile>(Profile.class) {
//                            @Override
//                            public void onCallback(Profile obj2, Status st) {
//                                if(st == Status.SUCCESS){
//                                    history.setPlayedWith(obj2);
//                                    count[0]++;
//                                    if(count[0] == obj.size()){
//                                        listener.onCallback(obj, st);
//                                    }
//                                }
//                                else{
//                                    listener.onCallback(null, st);
//                                }
//                            }
//                        });
//                    }
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
                listener.onCallback(profile, Status.SUCCESS);
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onCallback(null, Status.FAILED);
            }
        });
    }

    @Override
    public void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener) {
        getSingleDataMonitor(getTable(_tableUsers).child(userId), classTag, listener);
    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {
        getSingleData(getTable(_tableUsers).child(userId), listener);
    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, final DatabaseListener<Profile> listener) {
        Query queryRef = getTable(_tableUsers).orderByChild("facebookUserId").equalTo(facebookUserId);
        DatabaseListener<ArrayList<Profile>> intermediate = new DatabaseListener<ArrayList<Profile>>(Profile.class) {
            @Override
            public void onCallback(ArrayList<Profile> obj, Status st) {
                if(st == Status.SUCCESS && obj.size() >= 1){
                    listener.onCallback(obj.get(0), Status.SUCCESS);
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
            }
        };
        getData(queryRef, intermediate);
    }

    @Override
    public void updateProfile(Profile profile, DatabaseListener listener) {
        save(getTable(_tableUsers).child(profile.getUserId()), profile, listener);
    }

    @Override
    public void createUserByUserId(final String userId, final DatabaseListener<Profile> listener) {

        HashMap<String, String> userMap = new HashMap();
        userMap.put("userId", userId);
        getTable(_tableUsers).child(userId).setValue(userMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    Profile profile = new Profile();
                    profile.setUserId(userId);
                    listener.onCallback(profile, Status.SUCCESS);
                } else {
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
    }

    @Override
    public void getAllGames(final DatabaseListener<ArrayList<Game>> listener) {
        getData(getTable(_tableGames).orderByChild("createTimestamp"), new DatabaseListener<ArrayList<Game>>(Game.class) {
                    @Override
                    public void onCallback(ArrayList<Game> result, Status st) {
                        if(st == Status.SUCCESS){
                            Collections.reverse(result);
                        }
                        listener.onCallback(result, st);
                    }
                });
    }

    @Override
    public void saveRoom(Room room, boolean notify, DatabaseListener<String> listener) {
        if(room.getId() == null){
            Firebase ref = getTable(_tableRooms).push();
            room.setId(ref.getKey());
            save(ref, room, listener);
            ref.child("open").onDisconnect().setValue(false);
            String notifyKey = notifyRoomChanged(room);
            Firebase ref2 = getTable(_tableRooms).push();

            getTable(_tableRoomNotifications).child(ref2.getKey()).onDisconnect().setValue(new RoomNotification(room.getId()));
        }
        else{
            save(getTable(_tableRooms).child(room.getId()), room, listener);
            if(notify) notifyRoomChanged(room);
        }
    }

    @Override
    public void addUserToRoom(Room room, Profile user, DatabaseListener<String> listener) {
        RoomUser roomUser = new RoomUser();
        roomUser.setReady(true);
        roomUser.setSlotIndex(-1);
        roomUser.setProfile(user);
        save(getTable(_tableRooms).child(room.getId()).child("roomUsers").child(user.getUserId()), roomUser, listener);
        notifyRoomChanged(room);
    }

    @Override
    public void onDcSetGameStateDisconnected(Profile profile,  final DatabaseListener listener) {
        getTable(_tableUsers).child(profile.getUserId()).child("userPlayingState").child("connected").onDisconnect().setValue(false, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(listener != null)  listener.onCallback(null, Status.SUCCESS);
            }
        });
    }

    @Override
    public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
        getSingleData(getTable(_tableGames).child(abbr), listener);
    }

    @Override
    public Object getGameBelongDatabase(String abbr) {
        return getTable(_tableGameBelongData).child(abbr);
    }

    @Override
    public void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener) {
        getTable(_tableRooms).child(room.getId()).child("roomUsers").child(user.getUserId()).child("slotIndex").setValue(newIndex);
    }

    @Override
    public void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener) {
        Query queryRef = getTable(_tableRooms).child(id);
        getSingleDataMonitor(queryRef, classTag, listener);
    }

    @Override
    public void getRoomById(String id, DatabaseListener<Room> listener) {
        Query queryRef = getTable(_tableRooms).child(id);
        getSingleData(queryRef, listener);
    }

    @Override
    public void monitorAllRooms(final ArrayList<Room> rooms, final String classTag, final SpecialDatabaseListener<ArrayList<Room>, Room> listener) {
        getData(getTable(_tableRooms).orderByChild("open").equalTo(true), new DatabaseListener<ArrayList<Room>>(Room.class) {
            @Override
            public void onCallback(ArrayList<Room> obj, Status st) {
                if(st == Status.SUCCESS){
                    for(Room r : obj){
                        rooms.add(r);
                    }
                    listener.onCallbackTypeOne(rooms, Status.SUCCESS);
                }
                else{
                    listener.onCallbackTypeOne(null, Status.FAILED);
                    return;
                }
            }
        });


        final ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                String roomId = "";
                for (DataSnapshot shot1: snapshot.getChildren()) {
                    if(shot1.getKey().equals("roomInfo")){
                        for (DataSnapshot shot2: shot1.getChildren()) {
                            if(shot2.getKey().equals("roomId")){
                                roomId = (String) shot2.getValue();
                                break;
                            }
                        }
                    }
                }

                roomChanged(roomId);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                            listener.onCallbackTypeTwo(obj, Status.SUCCESS);
                        }
                    }
                });
            }

        };

        getServerCurrentTime(new DatabaseListener<Double>() {
            @Override
            public void onCallback(Double result, Status st) {
                getTable(_tableRoomNotifications).orderByChild("timestamp").startAt(result).addChildEventListener(childEventListener);
                _listenerModels.add(new ListenerModel(getTable(_tableRoomNotifications).orderByChild("timestamp").startAt(result),
                                    classTag, childEventListener));
            }
        });



    }

    private void getServerCurrentTime(final DatabaseListener<Double> listener){
        getSingleData(getTable(_tableServerTimeInfo), new DatabaseListener<Double>(Double.class) {
            @Override
            public void onCallback(Double result, Status st) {
                if(st == Status.SUCCESS){
                    listener.onCallback(System.currentTimeMillis() + result, Status.SUCCESS);
                }
            }
        });
    }

    @Override
    public String notifyRoomChanged(Room room) {
        Firebase ref = getTable(_tableRoomNotifications).push();
        ref.setValue(new RoomNotification(room.getId()));
        return ref.getKey();
    }


    @Override
    public void removeUserFromRoomOnDisconnect(String roomId, Profile user, final DatabaseListener<String> listener) {
        final boolean[] roomUserSuccess = new boolean[1];
        final boolean[] roomNotificationSuccess = new boolean[1];
        final boolean[] failed = new boolean[1];
        Firebase ref = getTable(_tableRooms).child(roomId).child("roomUsers").child(user.getUserId());
        ref.onDisconnect().removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError error, Firebase firebase) {
                if (error != null) {
                    if(!failed[0]){
                        failed[0] = true;
                        listener.onCallback(error.getMessage(), Status.FAILED);
                    }
                }
                else{
                    roomUserSuccess[0] = true;
                    if(roomNotificationSuccess[0] && !failed[0]) listener.onCallback(null, Status.SUCCESS);
                }
            }
        });
        //_listenerModels.add(new ListenerModel(ref, Logs.getCallerClassName()));

        Firebase ref2 = getTable(_tableRoomNotifications).child(roomId + "_" + System.currentTimeMillis());
        ref2.onDisconnect().setValue(roomId, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError error, Firebase firebase) {
                if (error != null) {
                    if(!failed[0]){
                        failed[0] = true;
                        listener.onCallback(error.getMessage(), Status.FAILED);
                    }
                }
                else{
                    roomNotificationSuccess[0] = true;
                    if(roomUserSuccess[0] && !failed[0]) listener.onCallback(null, Status.SUCCESS);
                }
            }
        });
               // _listenerModels.add(new ListenerModel(ref2, Logs.getCallerClassName()));
    }

    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        getDataCount(getTable(_tableTesting), listener);
    }

    @Override
    public void getProfileByGameNameLower(String gameName, final DatabaseListener<Profile> listener) {
        getData(getTable(_tableUsers).orderByChild("gameNameLower").startAt(gameName.toLowerCase()).endAt(gameName.toLowerCase()), new DatabaseListener<ArrayList<Profile>>(Profile.class) {
            @Override
            public void onCallback(ArrayList<Profile> obj, Status st) {
                if(st == Status.SUCCESS){
                    if(obj.size() > 0){
                        listener.onCallback(obj.get(0), Status.SUCCESS);
                    }
                    else{
                        listener.onCallback(null, Status.SUCCESS);
                    }
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
    }

    private Firebase getTable(String _tableName){
        Firebase r = _ref.child(_tableName);
        return r;
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

    private void getSingleDataMonitor(Query ref, String classTag, final DatabaseListener listener){

        ValueEventListener valueEventListener = new ValueEventListener() {
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
        };
        ref.addValueEventListener(valueEventListener);

        _listenerModels.add(new ListenerModel(ref, classTag, valueEventListener));
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

    private void getDataMonitor(final Query ref, String classTag, final DatabaseListener listener){

        ValueEventListener valueEventListener = new ValueEventListener() {
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
        };

        ref.addValueEventListener(valueEventListener);

        _listenerModels.add(new ListenerModel(ref, classTag, valueEventListener));

    }


    private void save(Firebase ref, Object value, final DatabaseListener listener){
        ref.setValue(value, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    if(listener != null) listener.onCallback(firebaseError.getMessage(), Status.FAILED);
                } else {
                    if(listener != null) listener.onCallback(null, Status.SUCCESS);
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
