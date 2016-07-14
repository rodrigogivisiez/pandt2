package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.services.ClientInternalCoinListener;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;
import com.potatoandtomato.common.utils.ArrayUtils;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
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
    private HashMap<String, Integer> _streaksMap;
    private HashMap<String, Integer> _rankMap;
    private ArrayList<LeaderboardRecord> _records;
    private boolean paused;

    public UserBadgeHelper(Services _services, RoomScene _roomScene, Game _game) {
        this._services = _services;
        this._roomScene = _roomScene;
        this._game = _game;
        this._streaksMap = new HashMap<String, Integer>();
        this._rankMap = new HashMap<String, Integer>();
        this._roomUsers = new ArrayList<RoomUser>();
        setListeners();
        refresh();
    }

    public synchronized void userJoinedRoom(RoomUser newRoomUser){
        _roomUsers.add(newRoomUser);
        roomUsersChanged();
    }

    public synchronized void userLeftRoom(RoomUser leftRoomUser){
        ArrayList<Integer> removingIndexes = new ArrayList<Integer>();
        String leftUserId = leftRoomUser.getProfile().getUserId();
        for(int i = 0; i < _roomUsers.size(); i++){
            if(_roomUsers.get(i).getProfile().getUserId().equals(leftUserId)){
                removingIndexes.add(i);
            }
        }

        _streaksMap.remove(leftUserId);
        _rankMap.remove(leftUserId);

        Collections.reverse(removingIndexes);
        for(Integer index : removingIndexes){
            _roomUsers.remove(_roomUsers.get(index));
        }

        roomUsersChanged();
    }

    public void roomUsersChanged(){
        fillRankMap();
        fillStreaksMap();
        handleNoCoinsBadge();
    }

    public void fillRankMap(){
        int i = 1;
        if(_records == null) return;

        for(LeaderboardRecord record : _records){
            for(RoomUser roomUser : _roomUsers){
                if(record.containUser(roomUser.getProfile().getUserId()) && !_rankMap.containsKey(roomUser.getProfile().getUserId())){
                    _rankMap.put(roomUser.getProfile().getUserId(), i);
                    addPlayerBadge(roomUser.getProfile().getUserId(), BadgeType.Rank, i);
                }
            }
            i++;
        }
    }

    public void fillStreaksMap(){
        for(final RoomUser roomUser : _roomUsers){
            userHasCoinChangedUpdateBadge(roomUser.getProfile().getUserId(),
                    _services.getCoins().checkUserHasCoin(roomUser.getProfile().getUserId()));
        }
    }

    public void handleNoCoinsBadge(){
        for(final RoomUser roomUser : _roomUsers){
            if(!_streaksMap.containsKey(roomUser.getProfile().getUserId())){
                _services.getDatabase().getTeamStreak(_game, ArrayUtils.stringsToArray(roomUser.getProfile().getUserId()), new DatabaseListener<Streak>(Streak.class) {
                    @Override
                    public void onCallback(Streak streak, Status st) {
                        if (st == Status.SUCCESS && streak != null) {
                            if (streak.hasValidStreak()) {
                                _streaksMap.put(roomUser.getProfile().getUserId(), streak.getStreakCount());
                                addPlayerBadge(roomUser.getProfile().getUserId(), BadgeType.Streak, streak.getStreakCount());
                            }
                        }
                    }
                });
            }
        }
    }

    public void addPlayerBadge(final String userId, final BadgeType badgeType, final int num){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 5){
                    if(_roomScene.getPlayerTableByUserId(userId) == null){
                        Threadings.sleep(1000);
                    }
                    else{
                        break;
                    }
                    i++;
                }
                _roomScene.addPlayerBadge(userId, badgeType, num);
            }
        });
    }

    public void userHasCoinChangedUpdateBadge(String userId, boolean hasCoin){
        if(hasCoin){
            _roomScene.removePlayerBadge(userId, BadgeType.NoCoin);
        }
        else{
            _roomScene.addPlayerBadge(userId, BadgeType.NoCoin, 0);
        }
    }

    public synchronized void refresh(){
        reset();
        getLeaderboardRecords(new Runnable() {
            @Override
            public void run() {
                fillRankMap();
                fillStreaksMap();
                handleNoCoinsBadge();
            }
        });

    }

    public void getLeaderboardRecords(final Runnable onFinish){
        _services.getDatabase().getLeaderBoardAndStreak(_game, Global.LEADERBOARD_COUNT, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                if(st == Status.SUCCESS){
                    _records = records;
                    onFinish.run();
                }
            }
        });
    }

    public void setListeners(){
        _services.getCoins().addCoinsListener(getClassTag(), new ClientInternalCoinListener() {
            @Override
            public void userHasCoinChanged(String userId, boolean userHasCoin) {
                userHasCoinChangedUpdateBadge(userId, userHasCoin);
            }
        });
    }

    public void reset(){
        _streaksMap.clear();
        _rankMap.clear();
        for(final RoomUser roomUser : _roomUsers){
            _roomScene.removeAllPlayerBadges(roomUser.getProfile().getUserId());
        }
    }

    public void dispose(){
        reset();
        _services.getCoins().removeCoinsListenersByClassTag(getClassTag());
    }

    public String getClassTag(){
        return this.getClass().getName();
    }

}
