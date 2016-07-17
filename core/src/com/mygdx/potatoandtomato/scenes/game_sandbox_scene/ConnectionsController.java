package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.RoomInfoListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.ConnectionsControllerListener;
import com.mygdx.potatoandtomato.absintflis.scenes.PlayerConnectionStateListener;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.absintflis.services.IChatRoomUsersConnectionRefresher;
import com.mygdx.potatoandtomato.enums.ConnectionStatus;
import com.mygdx.potatoandtomato.enums.RoomUserState;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public class ConnectionsController implements Disposable, IChatRoomUsersConnectionRefresher {

    private ConnectionsControllerListener connectionsControllerListener;
    private ConcurrentHashMap<String, PlayerConnectionState> playerConnectionStatesMap;
    private Services services;
    private Room room;
    private boolean gameStarted = false;
    private SafeThread safeThread;
    private ArrayList<Runnable> onRefreshFinishedRunnables;
    private CopyOnWriteArrayList<Runnable> onGameStartedRunnables;

    public ConnectionsController(Room room, Services services) {
        this.services = services;
        this.room = room;
        playerConnectionStatesMap = new ConcurrentHashMap();
        onRefreshFinishedRunnables = new ArrayList();
        onGameStartedRunnables = new CopyOnWriteArrayList<Runnable>();
        safeThread = new SafeThread();

        init(room.getTeams());
        setListeners();
    }

    public void gameStarted(){
        gameStarted = true;
        services.getConnectionWatcher().addConnectionWatcherListener(getClassTag(), new ConnectionWatcherListener() {
            @Override
            public void onConnectionResume() {
                sendMeConnected();
                refreshAllConnectStates(null);
            }

            @Override
            public void onConnectionHalt() {
                setPlayerConnectionState(services.getProfile().getUserId(), ConnectionStatus.Disconnected, "");

                for(PlayerConnectionState playerConnectionState : playerConnectionStatesMap.values()){
                    playerConnectionState.stopDisconnectTimeoutThread();
                }
            }
        });

        services.getConnectionWatcher().gameStarted(room);

        updateMyPlayingState(true, false);

        for(Runnable runnable : onGameStartedRunnables){
            runnable.run();
        }

        onGameStartedRunnables.clear();

    }

    public void init(ArrayList<Team> teams){
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                playerConnectionStatesMap.put(player.getUserId(), new PlayerConnectionState(player, new PlayerConnectionStateListener() {
                    @Override
                    public void onPlayerDisconnectTimeout(String userId) {
                        sendUserAbandoned(userId);
                    }

                    @Override
                    public void onPlayerConnectionChanged(String userId, ConnectionStatus connectionStatus) {
                        connectionsControllerListener.userConnectionChanged(userId, connectionStatus);
                    }

                }));
            }
        }
    }

    public void receivedUserAbandoned(String userId, String fromUserId){
        setPlayerConnectionState(userId, ConnectionStatus.Abandoned, fromUserId);

        if(userId.equals(services.getProfile().getUserId())){
            updateMyPlayingState(false, true);
        }
    }


    public void sendUserAbandoned(String userId){
        services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_ABANDON, userId);

        if(userId.equals(services.getProfile().getUserId())){
            updateMyPlayingState(false, true);
        }
    }

    public void refreshAllConnectStates(Runnable onFinish){
        if(onFinish != null) onRefreshFinishedRunnables.add(onFinish);
        services.getGamingKit().getRoomInfo(room.getWarpRoomId(), getClassTag());
    }

    public void receivedRoomInfo(final String[] inRoomUserIds){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> connectedUserIds = new ArrayList<String>(Arrays.asList(inRoomUserIds));
                ArrayList<String> needRefreshUserIds = new ArrayList<String>();
                final ConcurrentHashMap<String, Profile> refreshedAllUsersProfile = new ConcurrentHashMap<String, Profile>();

                for(String userId : playerConnectionStatesMap.keySet()){
                    if(playerConnectionStatesMap.get(userId).getConnectionStatus() != ConnectionStatus.Abandoned
                            && !userId.equals(services.getProfile().getUserId())){
                        needRefreshUserIds.add(userId);
                    }
                }

                ThreadsPool threadsPool = new ThreadsPool();
                for(final String userId : needRefreshUserIds){
                    final Threadings.ThreadFragment fragment = new Threadings.ThreadFragment();
                    services.getDatabase().getProfileByUserId(userId, new DatabaseListener<Profile>(Profile.class) {
                        @Override
                        public void onCallback(Profile obj, Status st) {
                            refreshedAllUsersProfile.put(userId, obj);
                            fragment.setFinished(true);
                        }
                    });
                    threadsPool.addFragment(fragment);
                }

                while (!threadsPool.allFinished()){
                    Threadings.sleep(300);
                    if(safeThread.isKilled()) return;
                }

                for(Profile profile : refreshedAllUsersProfile.values()){
                    if(!profile.getUserPlayingState().canContinue(room) && profile.getUserPlayingState().getRoomId().equals("ABANDONED")){
                        playerConnectionStatesMap.get(profile.getUserId()).setConnectionStatus(ConnectionStatus.Abandoned);
                    }
                }

                for(String userId : playerConnectionStatesMap.keySet()){
                    PlayerConnectionState playerConnectionState = playerConnectionStatesMap.get(userId);
                    if(playerConnectionState.getConnectionStatus() == ConnectionStatus.Connected){
                        if(!connectedUserIds.contains(userId)){
                            playerConnectionState.setConnectionStatus(ConnectionStatus.Disconnected);
                        }
                    }
                    else if(playerConnectionState.getConnectionStatus() == ConnectionStatus.Disconnected){
                        if(connectedUserIds.contains(userId)){
                            playerConnectionState.setConnectionStatus(ConnectionStatus.Connected);
                        }
                    }
                }


                for(Runnable runnable : onRefreshFinishedRunnables){
                    runnable.run();
                }

                onRefreshFinishedRunnables.clear();

                refreshChatRoomUsersConnectStatus();
            }
        });
    }

    public void sendMeConnected(){
        services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_CONNECTED, "");
    }

    public void setPlayerConnectionState(String userId, ConnectionStatus connectionStatus, String senderId){
         if(playerConnectionStatesMap.containsKey(userId)){
            boolean changed = playerConnectionStatesMap.get(userId).setConnectionStatus(connectionStatus);

            if(gameStarted && changed){
                refreshChatRoomUsersConnectStatus();
                Player player = room.getPlayerByUserId(userId);
                if(player == null) return;

                boolean isSelf = userId.equals(services.getProfile().getUserId());

                if (connectionStatus == ConnectionStatus.Abandoned) {
                    //user abandoned
                    if(Strings.isEmpty(senderId)) senderId = userId;

                    if(senderId.equals(userId)){
                        services.getNotification().important(isSelf ? services.getTexts().notificationYouAbandon() :
                                String.format(services.getTexts().notificationAbandon(), player.getName()));
                    }
                    else{
                        services.getNotification().important(isSelf ? services.getTexts().notificationYouAbandonDueToTimeout() :
                                String.format(services.getTexts().notificationAbandonDueToTimeout(), player.getName()));
                    }


                } else if (connectionStatus == ConnectionStatus.Connected) {
                    //user connected back
                    services.getNotification().info(isSelf ? services.getTexts().notificationYouConnected() :
                                                String.format(services.getTexts().notificationConnected(), player.getName()));
                } else if (connectionStatus == ConnectionStatus.Disconnected) {
                    //user disconnected
                    services.getNotification().important(isSelf ? services.getTexts().notificationYouDisconnected() :
                                    String.format(services.getTexts().notificationDisconnected(), player.getName()));
                }
            }

        }
    }

    @Override
    public void refreshChatRoomUsersConnectStatus() {
        ArrayList<Pair<String, ConnectionStatus>> userIdToConnectStatusPairs = new ArrayList();

        for(String userId : playerConnectionStatesMap.keySet()){
            PlayerConnectionState playerConnectionState = playerConnectionStatesMap.get(userId);
            userIdToConnectStatusPairs.add(new Pair<String, ConnectionStatus>(playerConnectionState.getPlayer().getName(),
                                            playerConnectionState.getConnectionStatus()));
        }

        services.getChat().refreshRoomUsersConnectionStatus(userIdToConnectStatusPairs);
    }

    public void setListeners(){
        services.getGamingKit().addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ConnectStatus st) {
                if(!ConnectStatus.isConnected(st)){
                    setPlayerConnectionState(userId, ConnectionStatus.Disconnected, "");
                }
            }
        });

        services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(final int code, final String msg, final String senderId) {
                if(!gameStarted){
                    onGameStartedRunnables.add(new Runnable() {
                        @Override
                        public void run() {
                            onUpdateRoomMatesReceived(code, msg, senderId);
                        }
                    });
                    return;
                }

                if(code == UpdateRoomMatesCode.USER_ABANDON){
                    receivedUserAbandoned(msg, senderId);
                }
                else if(code == UpdateRoomMatesCode.USER_CONNECTED){
                    if(playerConnectionStatesMap.get(senderId).getConnectionStatus() == ConnectionStatus.Abandoned){
                        sendUserAbandoned(senderId);
                    }
                    else{
                        setPlayerConnectionState(senderId, ConnectionStatus.Connected, "");
                    }
                }
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });

        services.getGamingKit().addListener(getClassTag(), new RoomInfoListener(room.getWarpRoomId(), getClassTag()) {
            @Override
            public void onRoomInfoRetrievedSuccess(String[] inRoomUserIds) {
                receivedRoomInfo(inRoomUserIds);
            }

            @Override
            public void onRoomInfoFailed() {

            }
        });

    }

    public void updateMyPlayingState(boolean allowContinue, boolean isAbandon){
        UserPlayingState userPlayingState = new UserPlayingState();

        if(!allowContinue){
            userPlayingState.setRoomId(isAbandon ? "ABANDONED" : "");
            userPlayingState.setRoundCounter(0);
        }
        else{
            userPlayingState.setRoomId(room.getId());
            userPlayingState.setRoundCounter(room.getRoundCounter());
        }

        services.getProfile().setUserPlayingState(userPlayingState);
        services.getDatabase().updateProfile(services.getProfile(), null);
    }



    public void setConnectionsControllerListener(ConnectionsControllerListener connectionsControllerListener) {
        this.connectionsControllerListener = connectionsControllerListener;
    }

    public String getClassTag(){
        return this.getClass().getName();
    }

    @Override
    public void dispose() {
        for(PlayerConnectionState state : playerConnectionStatesMap.values()){
            state.dispose();
        }
        safeThread.kill();
        services.getGamingKit().removeListenersByClassTag(getClassTag());
        services.getConnectionWatcher().clearConnectionWatcherListenerByClassTag(getClassTag());
        services.getConnectionWatcher().gameEnded();
    }


}
