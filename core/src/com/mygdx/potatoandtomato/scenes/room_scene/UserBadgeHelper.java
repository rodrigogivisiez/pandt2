package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by SiongLeng on 23/3/2016.
 */
public class UserBadgeHelper implements Disposable {

    private Services _services;
    private RoomScene _roomScene;
    private Game _game;
    private ArrayList<RoomUser> _roomUsers;
    private HashMap<String, SafeThread> _runningThreads;
    private HashMap<String, Streak> _streaksMap;
    private HashMap<String, Integer> _rankMap;
    private HashMap<String, RoomScene.BadgeType> _currentBadge;
    private ArrayList<LeaderboardRecord> _records;
    private boolean paused;

    public UserBadgeHelper(Services _services, RoomScene _roomScene, Game _game) {
        this._services = _services;
        this._roomScene = _roomScene;
        this._game = _game;
        this._runningThreads= new HashMap<String, SafeThread>();
        this._streaksMap = new HashMap<String, Streak>();
        this._rankMap = new HashMap<String, Integer>();
        this._roomUsers = new ArrayList<RoomUser>();
        this._currentBadge = new HashMap<String, RoomScene.BadgeType>();
        refresh();
    }

    public void addRoomUsersIfNotExist(Collection<RoomUser> roomUsers){
        for(RoomUser roomUser : roomUsers){
            boolean found = false;
            for(RoomUser currentRoomUser : _roomUsers){
                if(currentRoomUser.getProfile().getUserId().equals(roomUser.getProfile().getUserId())){
                    found = true;
                    break;
                }
            }
            if(!found){
                _roomUsers.add(roomUser);
            }
        }
    }

    public void usersJoinedRoom(Collection<RoomUser> newRoomUsers){
        for(RoomUser roomUser : newRoomUsers){
            _roomUsers.add(roomUser);
        }
        roomUsersChanged();
    }

    public void usersLeftRoom(Collection<RoomUser> leftRoomUsers){
        ArrayList<Integer> removingIndexes = new ArrayList<Integer>();
        for(RoomUser leftRoomUser : leftRoomUsers){
            String leftUserId = leftRoomUser.getProfile().getUserId();
            for(int i = 0; i < _roomUsers.size(); i++){
                if(_roomUsers.get(i).getProfile().getUserId().equals(leftUserId)){
                    removingIndexes.add(i);
                }
            }

            _runningThreads.get(leftUserId).kill();
            _runningThreads.remove(leftUserId);
            _streaksMap.remove(leftUserId);
            _rankMap.remove(leftUserId);
            _currentBadge.remove(leftUserId);
        }

        Collections.reverse(removingIndexes);
        for(Integer index : removingIndexes){
            _roomUsers.remove(_roomUsers.get(index));
        }

        roomUsersChanged();
    }

    public void roomUsersChanged(){
        fillRankMap();
        fillStreaksMap();
        fillRunningThreadsMap();
    }

    public void fillRunningThreadsMap(){
        for(final RoomUser roomUser : _roomUsers){
            if(!_runningThreads.containsKey(roomUser.getProfile().getUserId())){
                final SafeThread safeThread = new SafeThread();
                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        boolean showingRank = true;

                        while (true){
                            if(safeThread.isKilled()) break;

                            if(isPaused()){
                                Threadings.sleep(10 * 1000);
                                continue;
                            }

                            final String userId = roomUser.getProfile().getUserId();
                            boolean reRun = false;

                            if(!showingRank && _records != null){
                                if(_streaksMap.containsKey(userId)){
                                    reRun = !setBadge(userId, RoomScene.BadgeType.Streak, _streaksMap.get(userId).getStreakCount());
                                }
                            }
                            else{
                                if(_rankMap.containsKey(userId)){
                                    reRun =  !setBadge(userId, RoomScene.BadgeType.Rank, _rankMap.get(userId));
                                }
                            }

                            if(!_streaksMap.containsKey(userId) && !_rankMap.containsKey(userId)){
                                reRun = !setBadge(userId, RoomScene.BadgeType.Normal, 0);
                            }

                            Threadings.sleep(_records == null || reRun? 1 * 1000 : 7 * 1000);
                            if(!reRun) showingRank = !showingRank;
                        }
                    }
                });
                _runningThreads.put(roomUser.getProfile().getUserId(), safeThread);
            }
        }
    }

    private boolean setBadge(final String userId, final RoomScene.BadgeType type, final int num){
        if(_currentBadge.containsKey(userId) && _currentBadge.get(userId) == type){
            return true;
        }
        else{
            boolean success = _roomScene.getPlayersMaps().containsKey(userId);
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _roomScene.setPlayerBadge(userId, type, num);
                }
            });

            if(success){
                _currentBadge.put(userId, type);
            }
            return success;
        }
    }

    public void fillRankMap(){
        int i = 1;
        if(_records == null) return;

        for(LeaderboardRecord record : _records){
            for(RoomUser roomUser : _roomUsers){
                if(record.containUser(roomUser.getProfile().getUserId())){
                    if(!_rankMap.containsKey(roomUser.getProfile().getUserId())){
                        _rankMap.put(roomUser.getProfile().getUserId(), i);
                    }
                }
            }
            i++;
        }
    }

    public void fillStreaksMap(){
        for(final RoomUser roomUser : _roomUsers){
            if(!_streaksMap.containsKey(roomUser.getProfile().getUserId())){
                _services.getDatabase().getUserStreak(_game, roomUser.getProfile().getUserId(), new DatabaseListener<Streak>(Streak.class) {
                    @Override
                    public void onCallback(Streak obj, Status st) {
                        if(st == Status.SUCCESS && obj != null){
                            if(obj.hasValidStreak()){
                                _streaksMap.put(roomUser.getProfile().getUserId(), obj);
                            }
                        }
                    }
                });
            }
        }
    }

    public void refresh(){
        dispose();
        getLeaderboardRecords(new Runnable() {
            @Override
            public void run() {
                fillRankMap();
            }
        });
        fillStreaksMap();
        fillRunningThreadsMap();
    }

    public void getLeaderboardRecords(final Runnable onFinish){
        _services.getDatabase().getLeaderBoardAndStreak(_game, 200, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                if(st == Status.SUCCESS){
                    _records = records;
                    onFinish.run();
                }
            }
        });
    }


    public void dispose(){
        _streaksMap.clear();
        _rankMap.clear();
        _currentBadge.clear();
        for(SafeThread thread : _runningThreads.values()){
            thread.kill();
        }
        _runningThreads.clear();
    }


    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
