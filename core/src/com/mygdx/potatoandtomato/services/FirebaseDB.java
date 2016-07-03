package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.utils.Array;
import com.firebase.client.*;
import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.enums.RoomUserState;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.utils.ThreadsPool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class
        FirebaseDB implements IDatabase {

    Firebase _ref;
    private String _tableUsers = "users";
    private String _tableGames = "games";
    private String _tableRooms = "rooms";
    private String _tableRoomInvitations = "roomInvitations";
    private String _tableHistories = "histories";
    private String _tableRoomNotifications = "roomNotifications";
    private String _tableGameBelongData = "gameBelongData";
    private String _tableLeaderboard = "leaderboard";
    private String _tableUserLeaderboardLog = "userLeaderboardLog";
    private String _tableStreak = "streaks";
    private String _tableCoins = "coins";
    private String _tableUpdatedScores = "updatedScores";
    private String _tableCoinsProducts = "coinsProducts";
    private String _tableServerTimeInfo = ".info/serverTimeOffset";
    private String _tableLogs = "logs";
    private Array<ListenerModel> _listenerModels;

    public FirebaseDB(String url){
        _ref = new Firebase(url);
        _listenerModels = new Array();
    }

    @Override
    public void unauth() {
        _ref.unauth();
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
    public void clearAllOnDisconnectListenerModel() {
        ArrayList<Integer> toRemove = new ArrayList();
        for(int i = 0; i< _listenerModels.size; i++){
            if(_listenerModels.get(i).getValue() == null && _listenerModels.get(i).getChild() == null){
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

    @Override
    public void clearListenersByTag(String tag) {
        ArrayList<Integer> toRemove = new ArrayList();
        for(int i = 0; i< _listenerModels.size; i++){
            if(_listenerModels.get(i).getTag().equals(tag)){
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
        for(RoomUser u : room.getRoomUsersMap().values()){
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
    public void authenticateUserByToken(String token, final DatabaseListener<Profile> listener) {
        _ref.authWithCustomToken(token, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                if(authData.getUid() != null) getProfileByUserId(authData.getUid(), listener);
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
    public void getUsernameByUserId(String userId, DatabaseListener<String> listener) {
        getSingleData(getTable(_tableUsers).child(userId).child("gameName"), listener);
    }

    @Override
    public void getUsernamesByUserIds(final ArrayList<String> userIds, final DatabaseListener<HashMap<String, String>> listener) {
        final HashMap<String, String> result = new HashMap<String, String>();
        final int[] count = {0};
        for(final String userId : userIds){
            getUsernameByUserId(userId, new DatabaseListener<String>(String.class) {
                @Override
                public void onCallback(String name, Status st) {
                    if(st == Status.SUCCESS && name != null){
                        result.put(userId, name);
                    }
                    count[0]++;
                    if(count[0] == userIds.size()){
                        listener.onCallback(result, Status.SUCCESS);
                    }
                }
            });
        }
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
    public void monitorUserCoinsCount(String userId, DatabaseListener<Integer> listener) {
        getSingleDataMonitor(getTable(_tableCoins).child(userId).child("count"), userId, listener);
    }

    @Override
    public void deductUserCoins(String userId, int finalCoins, final DatabaseListener listener) {
        getTable(_tableCoins).child(userId).child("count").setValue(finalCoins, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError != null){
                    listener.onCallback(null, Status.FAILED);
                }
                else{
                    listener.onCallback(null, Status.SUCCESS);
                }
            }
        });
    }

    @Override
    public void updateProfile(Profile profile, DatabaseListener listener) {
        save(getTable(_tableUsers).child(profile.getUserId()), profile, listener);
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
    public void getAllProducts(DatabaseListener<ArrayList<CoinProduct>> listener) {
        getData(getTable(_tableCoinsProducts).orderByChild("count"), listener);
    }

    @Override
    public void updateRoomPlayingAndOpenState(final Room room, Boolean isPlaying, Boolean isOpen, @Nullable final DatabaseListener<String> listener) {

        if(isPlaying != null){
            getTable(_tableRooms).child(room.getId()).child("playing").setValue(isPlaying, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                }
            });
        }

        if(isOpen != null){
            getTable(_tableRooms).child(room.getId()).child("open").setValue(isOpen, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if(firebaseError == null){
                        notifyRoomChanged(room);
                        if(listener != null) listener.onCallback(null, Status.SUCCESS);
                    }
                    else{
                        if(listener != null)  listener.onCallback(null, Status.FAILED);
                    }
                }
            });
        }

    }

    @Override
    public void saveRoom(Room room, boolean notify, DatabaseListener<String> listener) {
        if(room.getId() == null){
            Firebase ref = getTable(_tableRooms).push();
            room.setId(ref.getKey());
            save(ref, room, listener);
            notifyRoomChanged(room);
         }
        else{
            save(getTable(_tableRooms).child(room.getId()), room, listener);
            if(notify) notifyRoomChanged(room);
        }
    }

    @Override
    public void setOnDisconnectCloseRoom(Room room) {
        //multiple ondisconnect fire bugs may occur when using firebase onDisconnect!!!!!

        Firebase ref1 = getTable(_tableRooms).child(room.getId()).child("open");
        ref1.onDisconnect().setValue(false);                                 //may have wifi issue
       // Firebase ref2 = getTable(_tableRooms).child(room.getId()).child("roomUsersMap").child(room.getHost().getUserId());
        //ref2.onDisconnect().setValue(null);
        Firebase ref3 = getTable(_tableRoomNotifications).child(getTable(_tableRoomNotifications).push().getKey());
        ref3.onDisconnect().setValue(new RoomNotification(room.getId()));  //may have wifi issue
//        Firebase ref4 = getTable(_tableRooms).child(room.getId()).child("closeRoom");
//        ref4.onDisconnect().setValue("yes");

        _listenerModels.add(new ListenerModel(ref1, ""));
        //_listenerModels.add(new ListenerModel(ref2, ""));
        _listenerModels.add(new ListenerModel(ref3, ""));
       // _listenerModels.add(new ListenerModel(ref4, ""));
    }

    @Override
    public void addUserToRoom(Room room, Profile user, int slotIndex, RoomUserState roomUserState, DatabaseListener<String> listener) {
        RoomUser roomUser = new RoomUser();
        roomUser.setRoomUserState(roomUserState);
        roomUser.setSlotIndex(slotIndex);
        roomUser.setProfile(user);
        save(getTable(_tableRooms).child(room.getId()).child("roomUsersMap").child(user.getUserId()), roomUser, listener);
        notifyRoomChanged(room);
    }

    @Override
    public void removeUserFromRoom(final Room room, Profile user, final DatabaseListener listener) {
        getTable(_tableRooms).child(room.getId()).child("roomUsersMap").child(user.getUserId()).setValue(null, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(listener != null) listener.onCallback("", firebaseError == null ? Status.SUCCESS : Status.FAILED);
                notifyRoomChanged(room);
            }
        });
    }

    @Override
    public void setInvitedUsers(ArrayList<String> invitedUserIds, final Room room, final DatabaseListener listener) {
        getTable(_tableRooms).child(room.getId()).child("invitedUserIds").setValue(invitedUserIds, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(listener != null) listener.onCallback("", firebaseError == null ? Status.SUCCESS : Status.FAILED);
                notifyRoomChanged(room);
            }
        });
    }

    @Override
    public void setRoomUserState(Room room, String userId, RoomUserState roomUserState, DatabaseListener listener) {
        save(getTable(_tableRooms).child(room.getId()).child("roomUsersMap").child(userId).child("roomUserState"),
                roomUserState, listener);
    }

    @Override
    public void setRoomUserSlotIndex(Room room, String userId, int slotIndex, DatabaseListener listener) {
        save(getTable(_tableRooms).child(room.getId()).child("roomUsersMap").child(userId).child("slotIndex"),
                slotIndex, listener);
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
    public void getLeaderBoardAndStreak(final Game game, int expectedCount, final DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
        getData(getTable(_tableLeaderboard).child(game.getAbbr()).limitToLast(expectedCount).orderByPriority(), new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(final ArrayList<LeaderboardRecord> leaderboardRecords, Status st) {
                if(st == Status.SUCCESS){

                    if(leaderboardRecords.size() == 0){
                        listener.onCallback(new ArrayList<LeaderboardRecord>(), Status.SUCCESS);
                        return;
                    }

                    for(int i = leaderboardRecords.size() -1; i >=0; i--){
                        LeaderboardRecord record = leaderboardRecords.get(i);
                        if(record.getScore() == 0){
                            leaderboardRecords.remove(record);
                        }
                    }

                    Collections.reverse(leaderboardRecords);

                    final int[] count = {0};
                    final ThreadsPool threadsPool = new ThreadsPool();

                    for(final LeaderboardRecord record : leaderboardRecords){

                        final Threadings.ThreadFragment fragment1 = new Threadings.ThreadFragment();
                        Threadings.runInBackground(new Runnable() {
                            @Override
                            public void run() {
                                getUsernamesByUserIds(record.getUserIds(), new DatabaseListener<HashMap<String, String>>(String.class) {
                                    @Override
                                    public void onCallback(HashMap<String, String> obj, Status st) {
                                        if (st == Status.SUCCESS) {
                                            record.setUserIdToNameMap(new ConcurrentHashMap<String, String>(obj));
                                        }
                                        fragment1.setFinished(true);
                                    }
                                });
                            }
                        });

                        final Threadings.ThreadFragment fragment2 = new Threadings.ThreadFragment();
                        Threadings.runInBackground(new Runnable() {
                            @Override
                            public void run() {
                                getTeamStreak(game, record.getUserIds(), new DatabaseListener<Streak>(Streak.class) {
                                    @Override
                                    public void onCallback(Streak streak, Status st) {
                                        if(st == Status.SUCCESS){
                                            record.setStreak(streak);
                                        }
                                        fragment2.setFinished(true);
                                    }
                                });
                            }
                        });

                        threadsPool.addFragment(fragment1);
                        threadsPool.addFragment(fragment2);
                    }

                    Threadings.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            while (!threadsPool.allFinished()){
                                Threadings.sleep(300);
                            }
                            listener.onCallback(leaderboardRecords, Status.SUCCESS);
                        }
                    });

                }
                else{
                    listener.onCallback(null, st);
                }
            }
        });
    }

    @Override
    public void getTeamStreak(Game game, ArrayList<String> userIds, DatabaseListener<Streak> listener) {
        if(userIds != null && userIds.size() > 0){
            getSingleData(getTable(_tableStreak).child(game.getAbbr()).child(userIdsToKey(userIds)), listener);
        }
        else{
            listener.onCallback(null, Status.SUCCESS);
        }
    }

    @Override
    public void getTeamHighestLeaderBoardRecordAndStreak(final Game game, ArrayList<String> teamUserIds, final DatabaseListener<LeaderboardRecord> listener) {
        getLeaderBoardRecordAndStreakById(game, userIdsToKey(teamUserIds), listener);
    }

    @Override
    public void getUserHighestLeaderBoardRecordAndStreak(final Game game, String userId, final DatabaseListener<LeaderboardRecord> listener) {
        getSingleData(getTable(_tableUserLeaderboardLog).child(game.getAbbr()).child(userId).orderByValue().limitToLast(1), new DatabaseListener<HashMap<String, String>>(HashMap.class) {
            @Override
            public void onCallback(final HashMap<String, String> record, Status st) {
                if (st == Status.SUCCESS && record != null && record.keySet().size() > 0) {
                    for(String recordId : record.keySet()){
                        getLeaderBoardRecordAndStreakById(game, recordId, listener);
                        break;
                    }
                } else {
                    listener.onCallback(null, st);
                }

            }
        });
    }

    @Override
    public void getLeaderBoardRecordAndStreakById(final Game game, String leaderboardId, final DatabaseListener<LeaderboardRecord> listener) {
        getSingleData(getTable(_tableLeaderboard).child(game.getAbbr()).child(leaderboardId), new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
            @Override
            public void onCallback(final LeaderboardRecord record, Status st) {
                if(st == Status.SUCCESS && record != null){

                    final ThreadsPool threadsPool = new ThreadsPool();
                    final Threadings.ThreadFragment fragment1 = new Threadings.ThreadFragment();
                    getUsernamesByUserIds(record.getUserIds(), new DatabaseListener<HashMap<String, String>>(String.class) {
                        @Override
                        public void onCallback(HashMap<String, String> obj, Status st) {
                            if (st == Status.SUCCESS) {
                                record.setUserIdToNameMap(new ConcurrentHashMap<String, String>(obj));
                            }
                            fragment1.setFinished(true);
                        }
                    });

                    final Threadings.ThreadFragment fragment2 = new Threadings.ThreadFragment();
                    getTeamStreak(game, record.getUserIds(), new DatabaseListener<Streak>(Streak.class) {
                        @Override
                        public void onCallback(Streak streak, Status st) {
                            if (st == Status.SUCCESS) {
                                record.setStreak(streak);
                            }
                            fragment2.setFinished(true);
                        }
                    });

                    threadsPool.addFragment(fragment1);
                    threadsPool.addFragment(fragment2);

                    Threadings.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            while (!threadsPool.allFinished()){
                                Threadings.sleep(300);
                            }
                            listener.onCallback(record, Status.SUCCESS);
                        }
                    });

                }
                else{
                    listener.onCallback(record, st);
                }
            }
        });
    }

    private String userIdsToKey(ArrayList<String> userIds){
        ArrayList<String> userIdsClone = (ArrayList<String>) userIds.clone();
        Collections.sort(userIdsClone);
        String key = Strings.joinArr(userIdsClone, ",");
        return key;
    }


    @Override
    public void deleteLeaderBoard(final Game game, final DatabaseListener listener) {
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                getTable(_tableLeaderboard).child(game.getAbbr()).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                    }
                });

                getTable(_tableUserLeaderboardLog).child(game.getAbbr()).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        listener.onCallback(null, Status.SUCCESS);
                    }
                });

            }
        });
    }

    @Override
    public void checkScoreUpdated(Room room, final DatabaseListener<Boolean> listener) {
        getSingleData(getTable(_tableUpdatedScores).child(room.getId()).child(String.valueOf(room.getRoundCounter())), new DatabaseListener<String>(String.class) {
            @Override
            public void onCallback(String result, Status st) {
                if(st == Status.SUCCESS){
                    listener.onCallback((result != null && result.equals("1")), Status.SUCCESS);
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
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

    @Override
    public void monitorRoomInvitations(String roomId, String classTag,  final DatabaseListener listener) {
        final boolean[] firstDataDispose = new boolean[1];
        getDataMonitor(getTable(_tableRoomInvitations).child(roomId), classTag, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                if(firstDataDispose[0]){
                    listener.onCallback(obj, st);
                }
                else{
                    if(st == Status.SUCCESS){
                        firstDataDispose[0] = true;
                    }
                }
            }
        });
    }

    @Override
    public void checkRoomInvitationResponseExist(String roomId, String userId, DatabaseListener<Boolean> listener) {
        checkExist(getTable(_tableRoomInvitations).child(roomId).child(userId), listener);
    }

    @Override
    public void getPendingInvitationRoomIds(final Profile profile, final DatabaseListener<ArrayList<String>> listener) {
        getData(getTable(_tableRooms).orderByChild("open").equalTo(true), new DatabaseListener<ArrayList<Room>>(Room.class) {
            @Override
            public void onCallback(final ArrayList<Room> rooms, Status st) {
                if(st == Status.SUCCESS){
                    Threadings.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            ThreadsPool threadsPool = new ThreadsPool();

                            final ArrayList<String> result =new ArrayList<String>();
                            for(final Room room : rooms){
                                if(room.getUserIsInvited(profile.getUserId())){
                                    final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();
                                    checkRoomInvitationResponseExist(room.getId(), profile.getUserId(), new DatabaseListener<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean responded, Status st) {
                                            if(!responded){
                                                result.add(room.getId());
                                            }
                                            threadFragment.setFinished(true);
                                        }
                                    });
                                    threadsPool.addFragment(threadFragment);
                                }
                            }

                            while (!threadsPool.allFinished()){
                                Threadings.sleep(300);
                            }

                            listener.onCallback(result, Status.SUCCESS);
                        }
                    });
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
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
    public void saveLog(String msg) {
        String key = getTable(_tableLogs).push().getKey();
        getTable(_tableLogs).child(key).setValue(msg);
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

    public void checkExist(Firebase ref, final DatabaseListener<Boolean> listener) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listener.onCallback(snapshot.getValue() != null, Status.SUCCESS);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(false, Status.FAILED);
            }
        });
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
                    if(listener != null) listener.onCallback(snapshot.getValue(listener.getType()), Status.SUCCESS);
                }
                else{
                    if(listener != null) listener.onCallback(null, Status.SUCCESS);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(listener != null) listener.onCallback(0, Status.FAILED);
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
                listener.onCallback(null, Status.FAILED);
            }
        });
    }

    private void getDataMonitor(final Query ref, String classTag, final DatabaseListener listener){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Object> results = new ArrayList<Object>();
                for(DataSnapshot postSnapShot : snapshot.getChildren()){
                    Object newPost;
                    if(listener.getType() == null){
                        HashMap map = new HashMap();
                        map.put(postSnapShot.getKey(), postSnapShot.getValue());
                        newPost = map;
                    }
                    else{
                        newPost = postSnapShot.getValue(listener.getType());
                    }
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
        String tag;
        ChildEventListener child;
        ValueEventListener value;

        public ListenerModel(Query ref, String tag, ChildEventListener child) {
            this.ref = ref;
            this.tag = tag;
            this.child = child;
        }

        public ListenerModel(Query ref, String tag, ValueEventListener value) {
            this.value = value;
            this.tag = tag;
            this.ref = ref;
        }

        public ListenerModel(Query ref, String tag) {
            this.tag = tag;
            this.ref = ref;
        }

        public Query getRef() {
            return ref;
        }

        public String getTag() {
            return tag;
        }

        public ChildEventListener getChild() {
            return child;
        }

        public ValueEventListener getValue() {
            return value;
        }
    }

}
