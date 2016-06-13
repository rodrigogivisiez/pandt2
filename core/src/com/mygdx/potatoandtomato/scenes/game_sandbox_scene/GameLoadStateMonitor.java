package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.GameLoadStateMonitorListener;
import com.mygdx.potatoandtomato.absintflis.scenes.GameLoaderListener;
import com.mygdx.potatoandtomato.enums.LoadState;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.utils.ThreadsPool;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public class GameLoadStateMonitor implements Disposable {

    private Room room;
    private Services services;
    private GameLoadStateMonitorListener gameLoadStateMonitorListener;
    private ConcurrentHashMap<String, LoadState> userLoadStateMap;
    private GameLoader gameLoader;
    private GameCoordinator gameCoordinator;
    private SafeThread safeThread;
    private boolean failedLoad;

    //if teams is null, mean only need to monitor self loading, example in continue case
    public GameLoadStateMonitor(Room room, Services services,
                                ArrayList<Team> teams, PTScreen ptScreen, IGameSandBox gameSandBox) {
        this.room = room;
        this.services = services;
        userLoadStateMap = new ConcurrentHashMap();
        gameLoader = new GameLoader(room, services, ptScreen, gameSandBox);
        safeThread = new SafeThread();

        init(teams);

        setListeners();
        gameLoader.load();
    }

    public void init(ArrayList<Team> teams){
        if(teams == null){
            userLoadStateMap.put(services.getProfile().getUserId(), LoadState.Loading);
        }
        else{
            for(Team team : teams){
                for(Player player : team.getPlayers()){
                    userLoadStateMap.put(player.getUserId(), LoadState.Loading);
                }
            }
        }
    }

    public void userLoadedSuccess(String userId){
        if(userLoadStateMap.get(userId) == LoadState.Loaded) return;

        userLoadStateMap.put(userId, LoadState.Loaded);
        if(gameLoadStateMonitorListener != null) gameLoadStateMonitorListener.onPlayerReady(room.getPlayerByUserId(userId));

        //check all loaded
        for(LoadState loadState : userLoadStateMap.values()){
            if(loadState != LoadState.Loaded){
                return;
            }
        }

        if(userLoadStateMap.size() == 1){       //only one player and thats me
            allPlayersLoaded();
        }
        else{
            if(services.getProfile().getUserId().equals(room.getHost().getUserId())){
                notifyAllLoaded();
            }
        }

    }

    public void userLoadedFailed(String userId){
        if(!failedLoad){
            failedLoad = true;
            if(gameLoadStateMonitorListener != null) gameLoadStateMonitorListener.onFailed(room.getPlayerByUserId(userId));

            dispose();
        }
    }

    public void allPlayersLoaded(){
        if(gameLoadStateMonitorListener != null) gameLoadStateMonitorListener.onAllSuccess(gameCoordinator);
        dispose();
    }

    public void notifyLoadState(boolean isSuccess, String userId){
        if(userLoadStateMap.containsKey(userId)){
            services.getGamingKit().updateRoomMates(isSuccess ? UpdateRoomMatesCode.USER_IS_READY : UpdateRoomMatesCode.LOAD_FAILED, userId);
        }
    }

    public void notifyAllLoaded(){
        services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS, "");
    }

    public ArrayList<String> getNotLoadedUsers(){
        ArrayList<String> result = new ArrayList();
        for(String userId : userLoadStateMap.keySet()){
            if(userLoadStateMap.get(userId) != LoadState.Loaded){
                result.add(userId);
            }
        }
        return result;
    }


    public void setListeners(){
        gameLoader.setGameLoaderListener(new GameLoaderListener() {
            @Override
            public void onFinished(GameCoordinator coordinator, Status status) {
                if(status == Status.SUCCESS){
                    gameCoordinator = coordinator;
                    loadLeaderboardRecordToTeam(new Runnable() {
                        @Override
                        public void run() {
                            Threadings.runInBackground(new Runnable() {
                                @Override
                                public void run() {
                                    while (true){
                                        if(safeThread.isKilled()) return;
                                        else{
                                            if(gameCoordinator.isFinishLoading()){
                                                break;
                                            }
                                        }
                                        Threadings.sleep(300);
                                    }
                                    notifyLoadState(true, services.getProfile().getUserId());
                                }
                            });


                        }
                    });
                }
                else{
                    notifyLoadState(false, services.getProfile().getUserId());
                }
            }
        });

        services.getGamingKit().addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ConnectStatus st) {
                if(st == ConnectStatus.DISCONNECTED || st == ConnectStatus.DISCONNECTED_BUT_RECOVERABLE){
                    notifyLoadState(false, userId);
                }
            }
        });

        services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                if(!userLoadStateMap.containsKey(senderId)) return;

                switch (code) {
                    case UpdateRoomMatesCode.ASK_FOR_USER_READY:
                        if(userLoadStateMap.get(services.getProfile().getUserId()) != LoadState.Loading){
                            notifyLoadState(userLoadStateMap.get(services.getProfile().getUserId()) == LoadState.Loaded,
                                                services.getProfile().getUserId());
                        }
                        break;
                    case UpdateRoomMatesCode.USER_IS_READY:
                        userLoadedSuccess(msg);
                        break;
                    case UpdateRoomMatesCode.LOAD_FAILED:
                        userLoadedFailed(msg);
                        break;
                    case UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS:
                        allPlayersLoaded();
                        break;
                }
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });


        //host is responsible to repetitively asking room users whether they are ready, to prevent package loss not ready problem
        if(userLoadStateMap.size() > 1){
            if(room.getHost().equals(services.getProfile())){

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Threadings.sleep(3000);
                            if (safeThread.isKilled()) return;

                            ArrayList<String> userIds = getNotLoadedUsers();
                            for (String userId : userIds) {
                                services.getGamingKit().privateUpdateRoomMates(userId, UpdateRoomMatesCode.ASK_FOR_USER_READY, "");
                            }
                        }
                    }
                });
            }
        }

        //timeout thread, if loading exceed 60sec, deem as fail
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 60;
                while (true){
                    if(safeThread.isKilled()) return;

                    i--;

                    if(i == 0){
                        ArrayList<String> notLoadedUserIDs = getNotLoadedUsers();
                        notLoadedUserIDs.add(services.getProfile().getUserId());
                        notifyLoadState(false, notLoadedUserIDs.get(0));
                    }
                    Threadings.sleep(1000);

                }
            }
        });
    }


    private void loadLeaderboardRecordToTeam(final Runnable onFinish){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ThreadsPool threadsPool = new ThreadsPool();
                for(final Team team : room.getTeams()){
                    final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();
                    services.getDatabase().getTeamHighestLeaderBoardRecordAndStreak(room.getGame(), team.getPlayersUserIds(),
                            new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
                                @Override
                                public void onCallback(LeaderboardRecord record, Status st) {
                                    if (st == Status.SUCCESS && record != null && team.matchedUsers(record.getUserIds())) {
                                        team.setLeaderboardRecord(record);
                                    }
                                    threadFragment.setFinished(true);
                                }
                            });
                    threadsPool.addFragment(threadFragment);
                }

                //the purpose of above function is to complement with this part, we cant use
                //this function only to get leaderboard and streak because user might not be in the leaderboard
                final Threadings.ThreadFragment allLeaderboardFragment = new Threadings.ThreadFragment();
                services.getDatabase().getLeaderBoardAndStreak(room.getGame(), Global.LEADERBOARD_COUNT, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
                    @Override
                    public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                        if(st == Status.SUCCESS){
                            for(int i = records.size() - 1; i >= 0 ;i--){
                                gameCoordinator.getGameLeaderboardRecords().add(0, records.get(i));
                                for(Team team : room.getTeams()){
                                    if(team.matchedUsers(records.get(i).getUserIds())){
                                        team.setRank(i + 1);
                                    }
                                }
                            }
                        }
                        allLeaderboardFragment.setFinished(true);
                    }
                });
                threadsPool.addFragment(allLeaderboardFragment);

                while (!threadsPool.allFinished()){
                    Threadings.sleep(300);
                    if(safeThread.isKilled()) return;
                }
                onFinish.run();
            }
        });

    }


    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
        gameLoader.dispose();
        gameLoadStateMonitorListener = null;
        services.getGamingKit().removeListenersByClassTag(getClassTag());
    }

    public void setGameLoadStateMonitorListener(GameLoadStateMonitorListener gameLoadStateMonitorListener) {
        this.gameLoadStateMonitorListener = gameLoadStateMonitorListener;
    }

    public GameCoordinator getGameCoordinator() {
        return gameCoordinator;
    }

    public String getClassTag(){
        return this.getClass().getName();
    }

}
