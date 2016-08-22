package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.controls.ConfirmStateChangedListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.game_file_checker.GameFileCheckerListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.RoomInfoListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.absintflis.services.IChatRoomUsersConnectionRefresher;
import com.mygdx.potatoandtomato.helpers.Analytics;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.*;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.VersionControl;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.absints.CoinListener;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.enums.GestureType;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomLogic extends LogicAbstract implements IChatRoomUsersConnectionRefresher, TutorialPartListener {

    RoomScene scene;
    Room room;
    ConcurrentHashMap<String, String> noGameClientUsers;
    int currentPercentage, previousSentPercentage;
    SafeThread downloadThread, checkReadyThread, roomLogicSafeThread;
    boolean starting;
    boolean forceQuit;
    boolean gameStarted;
    boolean isContinue;
    boolean quiting;
    boolean finishGameFileCheck;
    int tutorialStep;
    RoomError roomErrorOccured;
    UserBadgeHelper userBadgeHelper;
    GameFileChecker gameFileChecker;
    ConcurrentHashMap<String, SafeThread> addUserSafeThreadMap;
    ConcurrentHashMap<String, SafeThread> waitRoomUserStateResponseSafeThreadMap;

    public Room getRoom() {
        return room;
    }

    public RoomLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        room = (Room) objs[0];
        isContinue = (Boolean) objs[1];
        scene = new RoomScene(services, screen, room);
        userBadgeHelper = new UserBadgeHelper(_services, scene, room.getGame());
        noGameClientUsers = new ConcurrentHashMap();
        addUserSafeThreadMap = new ConcurrentHashMap();
        waitRoomUserStateResponseSafeThreadMap = new ConcurrentHashMap();
        roomLogicSafeThread = new SafeThread();

        scene.populateGameDetails(room.getGame());
        if(isHost()) scene.setStartButtonText(_texts.btnTextStartGame());
    }

    @Override
    public void onInit() {
        super.onInit();

        Logs.LAST_GAME = room.getGame().getAbbr();

        _services.getConnectionWatcher().joinedRoom(room);
        _services.getChat().initChat(room, _services.getProfile().getUserId());
        _services.getChat().resetAllChat();

        setCloseRoomOnDisconnectIfHost();
        initRoomDbMonitor(new OneTimeRunnable(new Runnable() {
            @Override
            public void run() {
                sendJoinRoomAndSetupGameReadyMonitorThread();
            }
        }));
        refreshRoomDesign();
        setAppwarpListener();
        setupFileChecker();
        startFirebaseOnDisconnectBugFixThread();

        if(!isContinue){
            onNewRoom();
        }



    }


    @Override
    public void onShow() {
        super.onShow();
        _services.getSoundsPlayer().playMusic(Sounds.Name.THEME_MUSIC);

        if(roomErrorOccured != null){
            return;
        }

        _services.getChat().setMode(1);
        _services.getChat().showChat();

        toggleConfirmStateListener(true);

        //come back from game end
        if(gameStarted){
            gameStarted = false;
            userBadgeHelper.refresh();
            if(!isContinue && isHost()){
                _services.getDatabase().updateRoomPlayingAndOpenState(room, false, true, null);
            }
            refreshChatRoomUsersConnectStatus();
            coinMachineUsersChanged();
            sendIsReadyUpdate(true);
        }

        if(!isContinue) {
            checkHostInRoom();
            selfUpdateRoomStatePush();
        }

        starting = false;

        if(isContinue){
            onContinue();
        }

    }

    @Override
    public void onHide() {
        super.onHide();
        toggleConfirmStateListener(false);
        _services.getChat().hideChat();
        _services.getCoins().hideCoinMachine();
        if(!quiting)  sendIsReadyUpdate(false);
    }

    @Override
    public void onShown() {
        super.onShown();
        if(roomErrorOccured != null){
            errorOccurred(roomErrorOccured);
        }
        _services.getTutorials().startTutorialIfNotCompleteBefore(Terms.PREF_BASIC_TUTORIAL, false, this);

    }

    @Override
    public void onChangedScene(SceneEnum toScene) {
        super.onChangedScene(toScene);
        if(toScene != SceneEnum.GAME_SANDBOX){
            sendUpdateRoomMates(UpdateRoomMatesCode.PLAYER_CANCEL_START_GAME, "");
        }
    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        if(!forceQuit){
            _confirm.show(ConfirmIdentifier.Room, isHost() ? _texts.confirmHostLeaveRoom() : _texts.confirmLeaveRoom(), Confirm.Type.YESNO, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result == Result.YES) {
                        forceQuit = true;
                        quiting = true;
                        listener.onResult(OnQuitListener.Result.YES);
                    } else {
                        listener.onResult(OnQuitListener.Result.NO);
                    }
                }
            });
        }
        else{
            quiting = true;
            super.onQuit(listener);
        }
    }

    @Override
    public void onBack() {
        super.onBack();
        _services.getCoins().reset();
    }

    public void onNewRoom(){
        if(isHost()){
            userJoinLeftAddChat(_services.getProfile(), true);
        }
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            userBadgeHelper.userJoinedRoom(roomUser);
        }
    }

    public void onContinue(){
        refreshRoomPlayersIfIsHost();
        continueGame();
    }

    public void refreshRoomPlayersIfIsHost(){
        if(isHost()){
            _services.getGamingKit().getRoomInfo(room.getWarpRoomId(), getClassTag());
        }
    }

    public void refreshRoomDesign(){
        scene.updateRoom(room, new RunnableArgs<Boolean>() {
            @Override
            public void run() {
                boolean refreshed = this.getFirstArg();
                if(refreshed){
                    int i = 0;
                    for(final Table t : scene.getSlotsTable()){
                        final int finalI = i;
                        t.clearListeners();
                        t.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                if(room.getRoomUserBySlotIndex(finalI) == null){
                                    sendUpdateRoomMates(UpdateRoomMatesCode.MOVE_SLOT, String.valueOf(finalI));
                                }
                            }

                            @Override
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                if(room.getRoomUserBySlotIndex(finalI) == null){
                                    scene.playerTableTouchedDown(t);
                                }
                                return super.touchDown(event, x, y, pointer, button);
                            }


                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                if(room.getRoomUserBySlotIndex(finalI) == null){
                                    scene.playerTableTouchedUp(t);
                                }
                                super.touchUp(event, x, y, pointer, button);
                            }
                        });

                        i++;
                    }

                    if(isHost()){
                        for(final String userId : room.getRoomUsersMap().keySet()){
                            Table table = scene.getPlayerTableByUserId(userId);
                            if(table != null && !userId.equals(_services.getProfile().getUserId())){
                                Actor kickButton = table.findActor("kickDummy");
                                if(kickButton != null){
                                    kickButton.setName("");
                                    kickButton.addListener(new ClickListener() {
                                        @Override
                                        public void clicked(InputEvent event, float x, float y) {
                                            super.clicked(event, x, y);
                                            _confirm.show(ConfirmIdentifier.Room,
                                                    String.format(_texts.confirmKick(), room.getProfileByUserId(userId).getDisplayName(0)),
                                                    Confirm.Type.YESNO, new ConfirmResultListener() {
                                                        @Override
                                                        public void onResult(Result result) {
                                                            if (result == Result.YES) {
                                                                JsonObj jsonObj = new JsonObj();
                                                                jsonObj.put("userId", userId);
                                                                jsonObj.put("name", room.getProfileByUserId(userId).getDisplayName(0));
                                                                sendUpdateRoomMates(UpdateRoomMatesCode.KICK_USER, jsonObj.toString());
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public void sendUpdateRoomMates(int code, String msg){
        _services.getGamingKit().updateRoomMates(code, msg);
    }

    public void sendPrivateUpdateRoomMates(String toUserId, int code, String msg){
        _services.getGamingKit().privateUpdateRoomMates(toUserId, code, msg);
    }

    public void receivedUpdateRoomMates(int code, final String msg, final String senderId){

        if(code == UpdateRoomMatesCode.JOIN_ROOM){
            if(isHost()){
                addUserToRoom(senderId, null, -1);
            }
            sendMyCurrentIsReadyToUser(senderId);
        }
        else if(code == UpdateRoomMatesCode.LEFT_ROOM){
            if(isHost())  removeUserFromRoom(senderId);
        }
        else if(code == UpdateRoomMatesCode.MOVE_SLOT){
            if(isHost()) moveSlot(senderId,  Integer.valueOf(msg), true);
        }
        else if(code == UpdateRoomMatesCode.UPDATE_USER_READY){
            userIsReadyChanged(senderId, msg.equals("1"));
        }
        else if(code == UpdateRoomMatesCode.KICK_USER){
            JsonObj jsonObj = new JsonObj(msg);
            String userId = jsonObj.getString("userId");
            String name = jsonObj.getString("name");
            removeUserFromRoom(userId);
            _services.getChat().newMessage(new ChatMessage(String.format(_texts.chatMsgUserKicked(), name), ChatMessage.FromType.SYSTEM, null, ""));
            if(userId.equals(_services.getProfile().getUserId())){
                errorOccurred(RoomError.Kicked);
                Threadings.delayNoPost(1000, new Runnable() {
                    @Override
                    public void run() {
                        _services.getGamingKit().leaveRoom();
                    }
                });
            }
        }
        else if(code == UpdateRoomMatesCode.INVTE_USERS){
            if(isHost()){
                final String[] userIds = msg.split(",");
                for(final String userId : userIds){
                    room.addInvitedUserId(userId);
                }
                _services.getDatabase().setInvitedUsers(room.getInvitedUserIds(), room, null);
            }
        }
        else if(code == UpdateRoomMatesCode.UPDATE_DOWNLOAD){
            downloadUpdateReceived(msg, senderId);
        }
        else if(code == UpdateRoomMatesCode.START_GAME){
            startPutCoins();
        }
        else if(code == UpdateRoomMatesCode.GAME_OUTDATED){
            errorOccurred(RoomError.GameVersionOutdated);
        }
        else if(code == UpdateRoomMatesCode.GAME_STARTED){
            if(!gameStarted) gameStarted();
        }
        else if(code == UpdateRoomMatesCode.REQUEST_ROOM_STATE){
            receivedRoomUserStateRequest(senderId);
        }
        else if(code == UpdateRoomMatesCode.ROOM_STATE_RESPONSE){
            receivedRoomUserStateResponse(senderId, RoomUserState.valueOf(msg));
        }
        else if(code == UpdateRoomMatesCode.PLAYER_CANCEL_START_GAME){
            RoomUser roomUser = room.getRoomUserByUserId(senderId);
            if(roomUser != null){
                cancelPutCoins(roomUser.getProfile());
            }
        }
    }

    public void addUserToRoom(final String userId, final Profile profile, final int slotIndex){

        if(addUserSafeThreadMap.containsKey(userId)){
            addUserSafeThreadMap.get(userId).kill();
        }
        final SafeThread safeThread = new SafeThread();
        addUserSafeThreadMap.put(userId, safeThread);

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                if (room.getRoomUserByUserId(userId) != null) return;

                final boolean[] added = {false};
                if (isHost()) {
                    getProfileByUserId(userId, new RunnableArgs<Profile>() {
                        @Override
                        public void run() {
                            if(safeThread.isKilled()) return;

                            room.addRoomUser(this.getFirstArg(), slotIndex, RoomUserState.NotReady);
                            _services.getDatabase().addUserToRoom(room, this.getFirstArg(),
                                    room.getSlotIndexByUserId(userId), RoomUserState.NotReady, null);
                            added[0] = true;
                        }
                    });
                } else {
                    if (profile == null) return;

                    room.addRoomUser(profile, slotIndex, RoomUserState.NotReady);
                    added[0] = true;
                }

                while (!added[0]) {
                    Threadings.sleep(100);
                    if (roomLogicSafeThread.isKilled()) return;
                }

                RoomUser roomUser = room.getRoomUserByUserId(userId);
                if(roomUser == null) return;
                userJoinLeftAddChat(roomUser.getProfile(), true);
                cancelPutCoins(roomUser.getProfile());
                refreshRoomDesign();
                userBadgeHelper.userJoinedRoom(roomUser);
                coinMachineUsersChanged();
                selfUpdateRoomStatePush();
                refreshChatRoomUsersConnectStatus();
            }
        });
    }

    public void removeUserFromRoom(String userId){
        if(addUserSafeThreadMap.containsKey(userId)){
            addUserSafeThreadMap.get(userId).kill();
        }

        RoomUser roomUser = room.getRoomUserByUserId(userId);
        if(roomUser != null){
            if(isHost()){
                _services.getDatabase().removeUserFromRoom(room, room.getProfileByUserId(userId), null);
            }

            room.removeUserByUserId(userId);
            cancelPutCoins(roomUser.getProfile());

            if(!isDisposing()){
                userJoinLeftAddChat(roomUser.getProfile(), false);

                checkHostInRoom();
                refreshRoomDesign();
                userBadgeHelper.userLeftRoom(roomUser);
                coinMachineUsersChanged();
                selfUpdateRoomStatePush();
                refreshChatRoomUsersConnectStatus();
            }
        }
    }

    public void moveSlot(String userId, int toSlot, boolean updateDb){
        if(room.getRoomUserByUserId(userId) != null && !starting){
            Profile profile = room.getRoomUserByUserId(userId).getProfile();
            int fromSlot = room.getSlotIndexByUserId(userId);

            if(fromSlot != toSlot){
                room.changeSlotIndex(toSlot, profile);
                refreshRoomDesign();

                if(isHost() && updateDb){
                    _services.getDatabase().setRoomUserSlotIndex(room, userId, room.getSlotIndexByUserId(userId), null);
                }
            }
        }
    }

    public void downloadUpdateReceived(String msg, String senderId){
        if(Integer.valueOf(msg) < 100){
            noGameClientUsers.put(senderId, msg);
            RoomUser roomUser = room.getRoomUserByUserId(senderId);
            if(roomUser != null && roomUser.getRoomUserState() != RoomUserState.NotReady){
                roomUserStateChanged(senderId, RoomUserState.NotReady);
            }
        }
        else{
            noGameClientUsers.remove(senderId);
        }
        if(noGameClientUsers.size() > 0){
            Map.Entry<String, String> entry = noGameClientUsers.entrySet().iterator().next();  //first item
            cancelPutCoins(room.getProfileByUserId(entry.getKey()));
        }
        scene.updateDownloadPercentage(senderId, Integer.valueOf(msg));
    }

    public void userIsReadyChanged(String userId, boolean isReady){
        RoomUser roomUserModel = room.getRoomUserByUserId(userId);
        if(roomUserModel != null){
            RoomUserState currentRoomUserState = roomUserModel.getRoomUserState();
            RoomUserState newRoomUserState = isReady ? RoomUserState.Normal : RoomUserState.NotReady;
            if(currentRoomUserState != newRoomUserState){
                roomUserStateChanged(userId, newRoomUserState);
            }
        }
    }

    public void roomUserStateChanged(String userId, RoomUserState roomUserState){
        if(room.getRoomUserByUserId(userId) != null){
            room.setRoomUserState(userId, roomUserState);
            if(isHost()){
                _services.getDatabase().setRoomUserState(room, userId, roomUserState, null);
            }
            refreshRoomDesign();

            if(roomUserState == RoomUserState.TemporaryDisconnected){
                cancelPutCoins(room.getRoomUserByUserId(userId).getProfile());
            }
            refreshChatRoomUsersConnectStatus();
        }
    }

    public boolean checkHostInRoom(){
        if(forceQuit || roomErrorOccured != null) return false;
        if(gameStarted) return true;

        boolean found = false;
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            if(roomUser.getProfile().equals(room.getHost())){
                found = true;
                break;
            }
        }
        if(!found) {
            errorOccurred(RoomError.HostLeft);
        }
        return found;
    }

    private void sendIsReadyUpdate(boolean isReady){
        if((finishGameFileCheck && isReady && isSceneVisible()) || !isReady){
            sendUpdateRoomMates(UpdateRoomMatesCode.UPDATE_USER_READY, isReady ? "1" : "0");
        }
    }

    private void sendMyCurrentIsReadyToUser(String toUser){
        RoomUser myRoomUserModel = room.getRoomUserByUserId(_services.getProfile().getUserId());
        if(myRoomUserModel != null){
            sendPrivateUpdateRoomMates(toUser, UpdateRoomMatesCode.UPDATE_USER_READY,
                    myRoomUserModel.getRoomUserState() == RoomUserState.Normal ? "1" : "0");

        }
    }

    public void selfUpdateRoomStatePush(){
        if(quiting) return;;

        PushNotification push = new PushNotification();
        push.setId(PushCode.UPDATE_ROOM);
        push.setSticky(true);
        push.setTitle(_texts.PUSHRoomUpdateTitle());
        push.setMessage(String.format(_texts.PUSHRoomUpdateContent(), room.getRoomUsersCount(), room.getGame().getMaxPlayers()));
        push.setSilentNotification(true);
        push.setSilentIfInGame(false);

        int returnCode = startGameCheck(false);
        boolean canStart = (returnCode == 0 || returnCode == 3 || returnCode == 2);
        if(canStart && !gameStarted && isHost() && !isContinue){
            push.setTitle(_texts.PUSHRoomUpdateGameReadyTitle());
            push.setSilentIfInGame(true);
            push.setSilentNotification(false);
        }

        publishBroadcast(BroadcastEvent.UPDATE_ROOM, push);
    }

    public void hostSendGameStartingPush(){
        if(isHost()){       //only host can send push notification to update room state
            PushNotification push = new PushNotification();
            push.setId(PushCode.UPDATE_ROOM);
            push.setSticky(true);
            push.setTitle(_texts.PUSHRoomUpdateGameStartingTitle());
            push.setMessage(String.format(_texts.PUSHRoomUpdateContent(), room.getRoomUsersCount(), room.getGame().getMaxPlayers()));
            push.setSilentNotification(false);
            push.setSilentIfInGame(true);
            for(RoomUser roomUser : room.getRoomUsersMap().values()){
                _services.getGcmSender().send(roomUser.getProfile(), push);
            }
        }
    }

    public void errorOccurred(RoomError roomError){
        if(tutorialStep > 0){
            return;
        }

        //host left doens't necessary to dispose at here and should not to
        if(roomError != RoomError.HostLeft){
            disposeEarly();
        }

        if(roomErrorOccured == null){
            roomErrorOccured = roomError;
        }
        else{
            if(roomError != roomErrorOccured) return;
        }

        if(!isSceneFullyVisible()){
            return;
        }

        if(forceQuit) return;
        else{
            forceQuit = true;
            _screen.back();
            String message = "";
            switch (roomError){
                case Kicked:
                    message = _texts.confirmYouAreKicked();
                    break;
                case GameClientOutdated:
                    message = _texts.confirmGameClientOutdated();
                    break;
                case GameVersionOutdated:
                    message = _texts.confirmGameVersionOutdated();
                    break;
                case FailedRetrieveGameData:
                    message = _texts.confirmFailedRetriveGameData();
                    break;
                case HostLeft:
                    message = _texts.confirmHostLeft();
                    _services.getDatabase().updateRoomPlayingAndOpenState(room, null, false, null);
                    break;
                case UnknownError:
                    message = _texts.confirmRoomError();
                    break;
                case FullRoom:
                    message = _texts.confirmFullRoom();
                    break;
            }


            _confirm.show(ConfirmIdentifier.Room, message, Confirm.Type.YES, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                }
            });
        }
    }

    public int startGameCheck(boolean showMessage){
        if(tutorialStep > 0){       //in tutorial always allow start game
            return 0;
        }

        if(!room.checkAllTeamHasMinPlayers()){
            if(showMessage){
                _confirm.show(ConfirmIdentifier.Room,
                        String.format(_services.getTexts().confirmNotEnoughPlayers(),
                                room.getGame().getTeamMinPlayers()), Confirm.Type.YES, null);
            }
            return 1;
        }
        else if(room.getGame().getMustFairTeam() && !room.checkAllFairTeam()){
            if(showMessage){
                _confirm.show(ConfirmIdentifier.Room,
                        _services.getTexts().confirmFairTeamNeeded(), Confirm.Type.YES, null);
            }
            return 4;
        }
        else if(noGameClientUsers.size() > 0){
            if(showMessage){
                _confirm.show(ConfirmIdentifier.Room,
                        _services.getTexts().confirmStillDownloadingClient(), Confirm.Type.YES, null);
            }
            return 2;
        }
        else if(room.getTemporaryDisconnectedCount() > 0){
            if(showMessage){
                _confirm.show(ConfirmIdentifier.Room,
                        _services.getTexts().confirmWaitTemporaryDisconnectedUsers(), Confirm.Type.YES, null);
            }
            return 3;
        }
        else if(room.getNotYetReadyCount() > 0){
            if(showMessage){
                _confirm.show(ConfirmIdentifier.Room,
                        _services.getTexts().confirmWaitAllUsersReady(), Confirm.Type.YES, null);
            }
            return 3;
        }
        else{
            return 0;
        }
    }

    public void hostSendStartGame(){
        if(isHost()){
            if(startGameCheck(true) == 0){
                sendUpdateRoomMates(UpdateRoomMatesCode.START_GAME, "");
            }
            else{
                starting = false;
            }
        }
    }

    public void startPutCoins(){
        starting = true;

        _services.getSoundsPlayer().playSoundEffect(Sounds.Name.COUNT_DOWN);
        _services.getChat().newMessage(new ChatMessage(String.format(_texts.chatMsgGameStarting(), room.getRoomUsersCount()),
                ChatMessage.FromType.IMPORTANT, null, ""));

        initCoinMachine();
        setCoinListener();
        _services.getCoins().showCoinMachine(true);
        hostSendGameStartingPush();
    }

    public void cancelPutCoins(Profile profile){
        if(!starting || gameStarted || tutorialStep > 0) return;

        starting = false;

        _services.getCoins().hideCoinMachine();
        _services.getCoins().cancelPutCoins();
        if(profile != null){
            _services.getChat().newMessage(new ChatMessage(String.format(_texts.chatMsgGameStartStop(),
                    profile.getDisplayName(15)), ChatMessage.FromType.SYSTEM, null, ""));
        }
    }


    public void gameStarted(){
        if(gameStarted) return;

        gameStarted = true;
        room.setOpen(false);
        room.setPlaying(true);
        room.setRoundCounter(room.getRoundCounter()+1);
        room.setTeams(room.convertRoomUsersToTeams());
        _services.getDatabase().saveRoom(room, true, null);
        _services.getDatabase().savePlayedHistory(_services.getProfile(), room, null);
        _services.getChat().newMessage(new ChatMessage(_texts.chatMsgGameStarted(), ChatMessage.FromType.SYSTEM, null, ""));

        _screen.toScene(SceneEnum.GAME_SANDBOX, room, false);
    }

    public void continueGame() {
        if (gameStarted) return;

        selfUpdateRoomStatePush();

        Threadings.delay(500, new Runnable() {
            @Override
            public void run() {
                _screen.toScene(SceneEnum.GAME_SANDBOX, room, true);
            }
        });

        gameStarted = true;
        isContinue = false;
    }

    public void toggleConfirmStateListener(boolean on){
        _confirm.setStateChangedListener(null);

        if(on){
            _confirm.setStateChangedListener(new ConfirmStateChangedListener() {
                @Override
                public void onShow() {
                    sendIsReadyUpdate(false);
                }

                @Override
                public void onHide() {
                    sendIsReadyUpdate(true);
                }
            });
        }
    }


    public void downloadingGameNotify(){
        if(downloadThread == null){
            downloadThread = new SafeThread();

            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(previousSentPercentage != currentPercentage){
                            sendUpdateRoomMates(UpdateRoomMatesCode.UPDATE_DOWNLOAD, String.valueOf(currentPercentage));
                            previousSentPercentage = currentPercentage;
                        }
                        Threadings.sleep(2000);
                        if(downloadThread.isKilled()){
                            gameFileChecker.killDownloads();
                            previousSentPercentage = currentPercentage = 0;
                            return;
                        }
                        if(previousSentPercentage >= 100) return;
                    }
                }
            });

        }
    }

    //request room state from all users in room, in no reply, deem as temporary disconnected
    public void broadcastRoomUserStateRequest(){
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            if(!roomUser.getProfile().getUserId().equals(_services.getProfile().getUserId())){
                setupRoomUserRoomStateWaitThread(roomUser.getProfile().getUserId());
            }
        }
        sendUpdateRoomMates(UpdateRoomMatesCode.REQUEST_ROOM_STATE, "");
    }

    public void receivedRoomUserStateRequest(String fromUserId){
        RoomUser roomUser = room.getRoomUserByUserId(_services.getProfile().getUserId());
        if(roomUser != null &&
                roomUser.getRoomUserState() != RoomUserState.TemporaryDisconnected &&
                !fromUserId.equals(_services.getProfile().getUserId())) {
            sendPrivateUpdateRoomMates(fromUserId, UpdateRoomMatesCode.ROOM_STATE_RESPONSE, roomUser.getRoomUserState().name());
        }
    }

    public void receivedRoomUserStateResponse(String fromUserId, RoomUserState roomUserState){
        if(waitRoomUserStateResponseSafeThreadMap.containsKey(fromUserId)){
            waitRoomUserStateResponseSafeThreadMap.get(fromUserId).kill();
        }
        roomUserStateChanged(fromUserId, roomUserState);
    }

    private void setupRoomUserRoomStateWaitThread(final String forUserId){
        if(room.getRoomUserByUserId(forUserId) != null){
            if(waitRoomUserStateResponseSafeThreadMap.containsKey(forUserId)){
                waitRoomUserStateResponseSafeThreadMap.get(forUserId).kill();
            }

            final SafeThread safeThread = new SafeThread();
            waitRoomUserStateResponseSafeThreadMap.put(forUserId, safeThread);

            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    int i = 5;
                    while (i > 0){
                        Threadings.sleep(1000);
                        i--;
                        if(safeThread.isKilled()) return;
                    }
                    roomUserStateChanged(forUserId, RoomUserState.TemporaryDisconnected);
                }
            });
        }
    }

    public void setAppwarpListener(){
        _services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                receivedUpdateRoomMates(code, msg, senderId);
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });

        _services.getGamingKit().addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ClientConnectionStatus st) {
                if(!_services.getProfile().getUserId().equals(userId)){
                    if(st == ClientConnectionStatus.DISCONNECTED){
                        if(userId.equals(room.getHost().getUserId())){
                            room.removeUserByUserId(userId);
                            checkHostInRoom();
                        }
                        else{
                            if(isHost()) removeUserFromRoom(userId);
                        }
                    }
                    else if(st == ClientConnectionStatus.DISCONNECTED_BUT_RECOVERABLE){
                        roomUserStateChanged(userId, RoomUserState.TemporaryDisconnected);
                    }
                    else if(st == ClientConnectionStatus.CONNECTED_FROM_RECOVER || st == ClientConnectionStatus.CONNECTED){
                        if(gameStarted){
                            Player player = room.getPlayerByUserId(userId);
                            int slotIndex = -1;
                            if(player != null){
                                slotIndex = player.getSlotIndex();
                            }

                            if(isHost()){
                                addUserToRoom(userId, null, slotIndex);
                            }
                            else{
                                if(userId.equals(room.getHost().getUserId())){  //add back host to room
                                    addUserToRoom(userId, room.getHost(), slotIndex);
                                }
                            }
                        }
                    }
                }
            }
        });

        _services.getConnectionWatcher().addConnectionWatcherListener(getClassTag(), new ConnectionWatcherListener() {
            @Override
            public void onConnectionResume() {
                if(isHost()){
                    refreshRoomPlayersIfIsHost();
                }
                else{
                    broadcastRoomUserStateRequest();
                }
                sendIsReadyUpdate(false);
                setCloseRoomOnDisconnectIfHost();   //need to re-set it again since it should alrdy fired when disconnection occured
            }

            @Override
            public void onConnectionHalt() {
                roomUserStateChanged(_services.getProfile().getUserId(), RoomUserState.TemporaryDisconnected);
            }
        });

        _services.getGamingKit().addListener(this.getClassTag(), new RoomInfoListener(room.getWarpRoomId(), getClassTag()) {
            @Override
            public void onRoomInfoRetrievedSuccess(String[] inRoomUserIds) {
                ArrayList<String> inRoomUserIdsArr = new ArrayList<String>(Arrays.asList(inRoomUserIds));
                for(RoomUser roomUser : room.getRoomUsersMap().values()){
                    if(!inRoomUserIdsArr.contains(roomUser.getProfile().getUserId())){
                        removeUserFromRoom(roomUser.getProfile().getUserId());
                    }
                }

                for(String roomUserId : inRoomUserIdsArr){
                    if(room.getRoomUserByUserId(roomUserId) == null){
                        Player player = room.getPlayerByUserId(roomUserId);
                        addUserToRoom(roomUserId, null, player == null ? -1 : player.getSlotIndex());
                    }
                    else{
                        userBadgeHelper.userJoinedRoom(room.getRoomUserByUserId(roomUserId));
                    }
                }
                broadcastRoomUserStateRequest();
            }

            @Override
            public void onRoomInfoFailed() {

            }
        });

    }

    public void setupFileChecker(){
        if(!isContinue){
            gameFileChecker = new GameFileChecker(room.getGame(), _services.getPreferences(),
                    _services.getDownloader(), _services.getDatabase(), new VersionControl(),
                    new GameFileCheckerListener() {
                        @Override
                        public void onCallback(GameFileChecker.GameFileResult result, Status st) {
                            finishGameFileCheck = true;
                            if(st == Status.FAILED && !quiting){
                                switch (result){
                                    case FAILED_RETRIEVE:
                                        errorOccurred(RoomError.FailedRetrieveGameData);
                                        break;

                                    case GAME_OUTDATED:
                                        sendUpdateRoomMates(UpdateRoomMatesCode.GAME_OUTDATED, "");
                                        break;

                                    case CLIENT_OUTDATED:
                                        errorOccurred(RoomError.GameClientOutdated);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onStep(double percentage) {
                            super.onStep(percentage);
                            currentPercentage = (int) percentage;
                            downloadingGameNotify();
                        }
                    });
        }
        else{
            finishGameFileCheck = true;
        }
    }

    public void sendJoinRoomAndSetupGameReadyMonitorThread(){
        checkReadyThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(checkReadyThread.isKilled()) return;

                    if(isSceneVisible() && !_confirm.isVisible()){
                        RoomUser roomUser = room.getRoomUserByUserId(_services.getProfile().getUserId());
                        if(roomUser != null && roomUser.getSlotIndex() != -1){
                            if(roomUser.getRoomUserState() != RoomUserState.Normal){
                                sendIsReadyUpdate(true);
                            }
                        }
                        else{
                            if(!isHost()){
                                if(room.hasNewUserSlot(_services.getProfile().getUserId())){
                                    sendUpdateRoomMates(UpdateRoomMatesCode.JOIN_ROOM, "");
                                }
                            }
                        }
                    }
                    Threadings.sleep(3000);
                }
            }
        });

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(checkReadyThread.isKilled()) return;

                    RoomUser roomUser = room.getRoomUserByUserId(_services.getProfile().getUserId());
                    if(roomUser != null && roomUser.getSlotIndex() != -1){
                        break;
                    }

                    if(!room.hasNewUserSlot(_services.getProfile().getUserId())){
                        errorOccurred(RoomError.FullRoom);
                        break;
                    }

                    Threadings.sleep(500);
                }
            }
        });

    }

    public void initRoomDbMonitor(final OneTimeRunnable onSuccess){
        _services.getDatabase().monitorRoomById(room.getId(), getClassTag(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room roomObj, Status st) {
                if(st == Status.SUCCESS && room != null){
                    ArrayList<RoomUser> justJoinedUsers = room.getJustJoinedUsers(roomObj);
                    ArrayList<RoomUser> justLeftUsers = room.getJustLeftUsers(roomObj);
                    ArrayList<RoomUser> changedSlotUsers = room.getSlotIndexChangedUsers(roomObj);

                    for(RoomUser u : justJoinedUsers){
                        addUserToRoom(u.getProfile().getUserId(), u.getProfile(), u.getSlotIndex());
                    }
                    for(RoomUser u : justLeftUsers){
                        removeUserFromRoom(u.getProfile().getUserId());
                    }

                    for(RoomUser u : changedSlotUsers){
                        moveSlot(u.getProfile().getUserId(), u.getSlotIndex(), false);
                    }

                    room.setOpen(roomObj.isOpen());
                    room.setInvitedUserIds(roomObj.getInvitedUserIds());

                    onSuccess.run();
                }
                else{
                    errorOccurred(RoomError.UnknownError);
                }
            }
        });

        _services.getDatabase().monitorRoomInvitations(room.getId(), getClassTag(), new DatabaseListener<ArrayList<HashMap>>() {
            @Override
            public void onCallback(ArrayList<HashMap> maps, Status st) {
                if(st == Status.SUCCESS && maps != null){
                    for(HashMap map : maps){
                        for(Object key : map.keySet()){
                            String userId = key.toString();
                            final String statusCode = map.get(key).toString();

                            _services.getDatabase().getProfileByUserId(userId, new DatabaseListener<Profile>(Profile.class) {
                                @Override
                                public void onCallback(Profile obj, Status st) {
                                    if(statusCode.equals("0")){
                                        _services.getChat().newMessage(new ChatMessage(String.format(_texts.chatMsgInvitationRejected(), obj.getDisplayName(0)),
                                                ChatMessage.FromType.IMPORTANT, null, ""));
                                    }
                                    else if(statusCode.equals("1")){
                                        _services.getChat().newMessage(new ChatMessage(String.format(_texts.chatMsgInvitationAccepted(), obj.getDisplayName(0)),
                                                ChatMessage.FromType.IMPORTANT, null, ""));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void setCloseRoomOnDisconnectIfHost(){
        if(isHost()) _services.getDatabase().setOnDisconnectCloseRoom(room);
    }

    @Override
    public void setListeners() {
        super.setListeners();

        scene.getStartButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(tutorialStep > 0){
                    startPutCoins();
                }
                else{
                    if (!starting) {
                        starting = true;
                        hostSendStartGame();
                    }
                }
            }
        });

        scene.getInviteButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.INVITE, room);
            }
        });

        if(scene.getLeaderboardButton() != null){
            scene.getLeaderboardButton().addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    _screen.toScene(SceneEnum.SINGLE_GAME_LEADER_BOARD, room.getGame());
                }
            });
        }

    }

    public void setCoinListener(){
        _services.getCoins().setCoinListener(new CoinListener() {
            @Override
            public void onEnoughCoins() {
                sendUpdateRoomMates(UpdateRoomMatesCode.GAME_STARTED, "");
            }

            @Override
            public void onDismiss(String dismissUserId) {
                RoomUser dismissRoomUser = room.getRoomUserByUserId(dismissUserId);
                if(dismissRoomUser != null){
                    cancelPutCoins(dismissRoomUser.getProfile());
                }
            }
        });

    }

    @Override
    public boolean disposeEarly() {
        if(super.disposeEarly()){
            if(isHost()) {
                removeUserFromRoom(_services.getProfile().getUserId());
                _services.getDatabase().updateRoomPlayingAndOpenState(room, null, false, null);
            }
            else{
                sendUpdateRoomMates(UpdateRoomMatesCode.LEFT_ROOM, "");
            }

            _services.getConnectionWatcher().leftRoom();
            _services.getGamingKit().leaveRoom();
            publishBroadcast(BroadcastEvent.DESTROY_ROOM);
            userBadgeHelper.dispose();
            Gdx.files.local("records").deleteDirectory();
            if(roomLogicSafeThread != null) roomLogicSafeThread.kill();
            if(checkReadyThread != null) checkReadyThread.kill();
            _services.getChat().setMode(1);
            _services.getChat().resetChat();
            if(gameFileChecker != null) gameFileChecker.dispose();
            _services.getRecorder().reset();
        }
        return true;
    }


    @Override
    public boolean dispose() {
        if(super.dispose()){
            Analytics.log(AnalyticEvent.LeavingRoom, "msgSent", String.valueOf(_services.getGamingKit().getMsgSentCount()));
        };
        return true;
    }

    public void userJoinLeftAddChat(Profile profile, boolean joined){
        if(_services.getChat().getCurrentMode() == 1){
            _services.getChat().newMessage(new ChatMessage(String.format(
                    joined ? _services.getTexts().chatMsgUserHasJoinedRoom() : _services.getTexts().chatMsgUserHasLeftRoom(),
                    profile.getDisplayName(0)), ChatMessage.FromType.SYSTEM, null, ""));
        }
    }

    public void getProfileByUserId(String userId, final RunnableArgs<Profile> toRun){
        _services.getDatabase().getProfileByUserId(userId, new DatabaseListener<Profile>(Profile.class) {
            @Override
            public void onCallback(Profile obj, Status st) {
                if(st == Status.SUCCESS){
                    toRun.run(obj);
                }
            }
        });
    }

    public void startFirebaseOnDisconnectBugFixThread(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(roomLogicSafeThread.isKilled()) return;
                    if(!room.isOpen() && isSceneFullyVisible() && isHost() && !starting && !gameStarted && !isContinue
                                && tutorialStep ==0){
                        _services.getDatabase().updateRoomPlayingAndOpenState(room, false, true, null);
                    }
                    Threadings.sleep(2000);
                }
            }
        });
    }

    public void coinMachineUsersChanged(){
        ArrayList<Pair<String, String>> userIdToNamePairs = new ArrayList();
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            userIdToNamePairs.add(new Pair<String, String>(roomUser.getProfile().getUserId(), roomUser.getProfile().getDisplayName(99)));
        }
        _services.getCoins().sync(userIdToNamePairs);
    }

    public void initCoinMachine(){
        ArrayList<Pair<String, String>> userIdToNamePairs = new ArrayList();
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            userIdToNamePairs.add(new Pair<String, String>(roomUser.getProfile().getUserId(), roomUser.getProfile().getDisplayName(99)));
        }

        Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> pair = _texts.getRandomMascotsSpeechAboutStartGame();

        _services.getCoins().initCoinMachine("Start game", room.getRoomUsersCount(), room.getId() + "_" + room.getRoundCounter() + "_start",
                userIdToNamePairs, false, pair.getFirst(), pair.getSecond(), _texts.cancel());
    }

    @Override
    public void refreshChatRoomUsersConnectStatus() {
        if(this.isSceneVisible()){
            ArrayList<Pair<String, ConnectionStatusAndCountryModel>> userIdToConnectStatusPairs = new ArrayList();
            for(RoomUser roomUser : room.getRoomUsersMap().values()){
                if(roomUser.getRoomUserState() == RoomUserState.TemporaryDisconnected){
                    userIdToConnectStatusPairs.add(new Pair<String, ConnectionStatusAndCountryModel>(roomUser.getProfile().getDisplayName(99),
                            new ConnectionStatusAndCountryModel(roomUser.getProfile().getCountry(), GameConnectionStatus.Disconnected_No_CountDown)));
                }
                else{
                    userIdToConnectStatusPairs.add(new Pair<String, ConnectionStatusAndCountryModel>(roomUser.getProfile().getDisplayName(99),
                            new ConnectionStatusAndCountryModel(roomUser.getProfile().getCountry(), GameConnectionStatus.Connected)));
                }
            }

            _services.getChat().refreshRoomUsersConnectionStatus(userIdToConnectStatusPairs);
        }
    }
    @Override
    public SceneAbstract getScene() {
        return scene;
    }

    private boolean isHost(){
        return room.getHost().equals(_services.getProfile());
    }


    @Override
    public void nextTutorial() {
        tutorialStep++;
        if(tutorialStep == 1){
            _services.getTutorials().showMessage(null, _texts.tutorialAboutRoom());
            _services.getCoins().setDisableSpeech(true);
        }
        else if(tutorialStep == 2){
            _services.getTutorials().expectGestureOnActor(GestureType.Tap,
                    scene.getStartButton(), _texts.tutorialAboutStartGame(), 0, 0);
        }
        else if(tutorialStep == 3){
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    _services.getTutorials().showMessage(null, _texts.tutorialAboutCoinMachine());
                }
            });
        }
        else if(tutorialStep == 4){
            _services.getTutorials().expectGestureOnActor(GestureType.PointUp,
                    _services.getCoins().getCoinMachineControl().getToInsertCoinsRootTable(),
                    _texts.tutorialAboutCoinCount(), 0, -20);
        }
        else if(tutorialStep == 5){
            _services.getCoins().setTutorialMode(true);
            _services.getTutorials().expectGestureOnActor(GestureType.Tap,
                    _services.getCoins().getCoinMachineControl().getCoinInsertRootTable(),
                    _texts.tutorialAboutInsertCoin(), 0, 0);
        }
        else if(tutorialStep == 6){
            _services.getCoins().setTutorialMode(false);
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    _services.getTutorials().showMessage(null,
                            !_services.getCoins().checkUserHasCoin(_services.getProfile().getUserId()) ?
                                    _texts.tutorialAboutNoCoin() :
                                    _texts.tutorialAboutHasCoin());
                }
            });
        }
        else if(tutorialStep == 7){
            _services.getTutorials().expectGestureOnActor(GestureType.Tap,
                    _services.getCoins().getCoinMachineControl().getRetrieveCoinsTabButton(),
                    _texts.tutorialAboutTapMumPurse(), 0, 0);
        }
        else if(tutorialStep == 8){
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    _services.getTutorials().expectGestureOnActor(GestureType.Tap,
                            _services.getCoins().getCoinMachineControl().getRetrieveCoinsButton(),
                            _texts.tutorialAboutTapGetFreeCoins(), 0, 0);
                }
            });
        }
        else if(tutorialStep == 9){
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    _services.getTutorials().showMessage(null, _texts.tutorialAboutGetCoinsSuccess());
                }
            });
        }
        else if(tutorialStep == 10){
            _services.getCoins().hideCoinMachine();
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    _services.getTutorials().showMessage(null, _texts.tutorialAboutInviteMsg());
                    _services.getTutorials().expectGestureOnActor(GestureType.PointUp,
                            scene.getInviteButton(),"", 0, 0);
                }
            });
        }
        else if(tutorialStep == 11){
            forceQuit = true;
            _services.getCoins().setDisableSpeech(false);
            _screen.back();
        }
       
    }
}
