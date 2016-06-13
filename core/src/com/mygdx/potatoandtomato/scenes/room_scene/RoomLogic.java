package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.RoomError;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.VersionControl;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomLogic extends LogicAbstract {

    RoomScene scene;
    Room room;
    ConcurrentHashMap<String, String> noGameClientUsers;
    int currentPercentage, previousSentPercentage;
    SafeThread downloadThread, countDownThread, checkReadyThread, roomLogicSafeThread;
    boolean starting;
    boolean forceQuit;
    boolean gameStarted;
    boolean isContinue;
    boolean quiting;
    RoomError roomErrorOccured;
    UserBadgeHelper userBadgeHelper;
    GameFileChecker gameFileChecker;
    ConcurrentHashMap<String, SafeThread> addUserSafeThreadMap;

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
        roomLogicSafeThread = new SafeThread();

        scene.populateGameDetails(room.getGame());
        if(isHost()) scene.setStartButtonText(_texts.startGame());
    }

    @Override
    public void onInit() {
        super.onInit();

        _services.getChat().initChat(room, _services.getProfile().getUserId());
        _services.getChat().resetAllChat();

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
            _services.getSoundsPlayer().playThemeMusic();
        }

        userBadgeHelper.setPaused(false);

        if(!isContinue) {
            checkHostInRoom();
            selfUpdateRoomStatePush();
            sendIsReadyUpdate(true);
        }

        _services.getChat().setMode(1);
        _services.getChat().showChat();
        starting = false;

        if(isContinue){
            onContinue();
        }

    }

    @Override
    public void onHide() {
        super.onHide();
        userBadgeHelper.setPaused(true);
        toggleConfirmStateListener(false);
        _services.getChat().hideChat();
        if(!quiting)  sendIsReadyUpdate(false);
    }

    @Override
    public void onShown() {
        super.onShown();
        if(roomErrorOccured != null){
            errorOccurred(roomErrorOccured);
        }
    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        if(!forceQuit){
            _confirm.show(isHost() ? _texts.confirmHostLeaveRoom() : _texts.confirmLeaveRoom(), Confirm.Type.YESNO, new ConfirmResultListener() {
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
            _services.getGamingKit().getRoomInfo(room.getWarpRoomId());
        }
    }

    public void refreshRoomDesign(){
        scene.updateRoom(room);

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                for(final Table t : scene.getSlotsTable()){
                    final int finalI = i;
                    if(!t.getName().contains("disableclick")){
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
                    }

                    i++;
                }

                if(isHost()){
                    for (Map.Entry<String, Table> entry : scene.getPlayersMaps().entrySet()) {
                        final String userId = entry.getKey();
                        Table table = entry.getValue();
                        Actor kickButton = table.findActor("kickDummy");
                        if(kickButton != null && !userId.equals(_services.getProfile().getUserId())){
                            kickButton.setName("");
                            kickButton.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    _confirm.show(String.format(_texts.confirmKick(), room.getProfileByUserId(userId).getDisplayName(0)),
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
        });

    }

    public void sendUpdateRoomMates(int code, String msg){
        _services.getGamingKit().updateRoomMates(code, msg);
    }

    public void receivedUpdateRoomMates(int code, final String msg, final String senderId){

        if(code == UpdateRoomMatesCode.JOIN_ROOM){
            if(isHost()) addUserToRoom(senderId, null, -1);
        }
        else if(code == UpdateRoomMatesCode.LEFT_ROOM){
            if(isHost())  removeUserFromRoom(senderId);
        }
        else if(code == UpdateRoomMatesCode.MOVE_SLOT){
            if(isHost()) moveSlot(senderId,  Integer.valueOf(msg));
        }
        else if(code == UpdateRoomMatesCode.UPDATE_USER_READY){
            userIsReadyChanged(senderId, msg.equals("1"));
        }
        else if(code == UpdateRoomMatesCode.KICK_USER){
            JsonObj jsonObj = new JsonObj(msg);
            String userId = jsonObj.getString("userId");
            String name = jsonObj.getString("name");
            removeUserFromRoom(userId);
            _services.getChat().newMessage(new ChatMessage(String.format(_texts.userKicked(), name), ChatMessage.FromType.SYSTEM, null, ""));
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
                final int[] i = {0};
                for(final String userId : userIds){
                    _services.getDatabase().getProfileByUserId(userId, new DatabaseListener<Profile>(Profile.class) {
                        @Override
                        public void onCallback(Profile obj, Status st) {
                            if(st == Status.SUCCESS){
                                room.addInvitedUser(obj);

                            }
                            i[0]++;
                            if(i[0] == userIds.length){
                                _services.getDatabase().setInvitedUsers(room.getInvitedUsers(), room, null);
                            }
                        }
                    });
                }
            }
        }
        else if(code == UpdateRoomMatesCode.UPDATE_DOWNLOAD){
            if(Integer.valueOf(msg) < 100){
                noGameClientUsers.put(senderId, msg);
            }
            else{
                noGameClientUsers.remove(senderId);
            }
            if(noGameClientUsers.size() > 0){
                Map.Entry<String, String> entry = noGameClientUsers.entrySet().iterator().next();  //first item
                stopGameStartCountDown(room.getProfileByUserId(entry.getKey()));
            }
            scene.updateDownloadPercentage(senderId, Integer.valueOf(msg));
        }
        else if(code == UpdateRoomMatesCode.START_GAME){
            startGameCountDown();
        }
        else if(code == UpdateRoomMatesCode.GAME_OUTDATED){
            errorOccurred(RoomError.GameVersionOutdated);
        }
        else if(code == UpdateRoomMatesCode.GAME_STARTED){
            if(countDownThread != null) countDownThread.kill();
            if(!gameStarted) gameStarted();
        }
        else if(code == UpdateRoomMatesCode.PLAYER_CANCEL_START_GAME){
            stopGameStartCountDown(room.getProfileByUserId(msg));
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

                            room.addRoomUser(this.getFirstArg(), slotIndex, false);
                            _services.getDatabase().addUserToRoom(room, this.getFirstArg(), room.getSlotIndexByUserId(userId), null);
                            added[0] = true;
                        }
                    });
                } else {
                    if (profile == null) return;

                    room.addRoomUser(profile, slotIndex, false);
                    added[0] = true;
                }

                while (!added[0]) {
                    Threadings.sleep(100);
                    if (roomLogicSafeThread.isKilled()) return;
                }

                RoomUser roomUser = room.getRoomUserByUserId(userId);
                userJoinLeftAddChat(roomUser.getProfile(), true);
                stopGameStartCountDown(roomUser.getProfile());
                userBadgeHelper.userJoinedRoom(roomUser);

                if (isHost()) {
                    refreshRoomDesign();
                    selfUpdateRoomStatePush();
                }
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
            scene.getPlayersMaps().remove(userId);
            userJoinLeftAddChat(roomUser.getProfile(), false);
            stopGameStartCountDown(roomUser.getProfile());

            checkHostInRoom();
            userBadgeHelper.userLeftRoom(roomUser);

            if(isHost()){
                refreshRoomDesign();
                selfUpdateRoomStatePush();
            }
        }
    }

    public void moveSlot(String userId, int toSlot){
        if(room.getRoomUserByUserId(userId) != null){
            Profile profile = room.getRoomUserByUserId(userId).getProfile();
            int fromSlot = room.getSlotIndexByUserId(userId);

            if(fromSlot != toSlot){
                room.changeSlotIndex(toSlot, profile);
                refreshRoomDesign();

                if(isHost()){
                    _services.getDatabase().setRoomUserSlotIndex(room, userId, room.getSlotIndexByUserId(userId), null);
                }
            }
        }
    }

    public void userIsReadyChanged(String userId, boolean isReady){
        if(room.getRoomUserByUserId(userId) != null){
            room.setRoomUserReady(userId, isReady);
            if(isHost()){
                _services.getDatabase().setRoomUserIsReady(room, userId, isReady, null);
            }

            refreshRoomDesign();
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
        if(isReady && isSceneVisible() || !isReady){
            sendUpdateRoomMates(UpdateRoomMatesCode.UPDATE_USER_READY, isReady ? "1" : "0");
        }

        if(starting && !isReady){
            sendUpdateRoomMates(UpdateRoomMatesCode.PLAYER_CANCEL_START_GAME, _services.getProfile().getUserId());
        }
    }

    public void selfUpdateRoomStatePush(){
        if(quiting) return;;

        boolean canStart = (startGameCheck(false) == 0);
        PushNotification push = new PushNotification();
        push.setId(PushCode.UPDATE_ROOM);
        push.setSticky(true);
        push.setTitle(_texts.PUSHRoomUpdateTitle());
        push.setMessage(String.format(_texts.PUSHRoomUpdateContent(), room.getRoomUsersCount(), room.getGame().getMaxPlayers()));
        push.setSilentNotification(true);
        push.setSilentIfInGame(false);

        if(canStart && !gameStarted && isHost() && !isContinue){
            push.setTitle(_texts.PUSHRoomUpdateGameReadyTitle());
            push.setSilentIfInGame(true);
            push.setSilentNotification(false);
        }

        _services.getGcmSender().send(_services.getProfile(), push);
    }

    public void hostSendGameStartedPush(){
        if(isHost()){       //only host can send push notification to update room state
            PushNotification push = new PushNotification();
            push.setId(PushCode.UPDATE_ROOM);
            push.setSticky(true);
            push.setTitle(_texts.PUSHRoomUpdateGameStartedTitle());
            push.setMessage(String.format(_texts.PUSHRoomUpdateContent(), room.getRoomUsersCount(), room.getGame().getMaxPlayers()));
            push.setSilentNotification(false);
            push.setSilentIfInGame(true);
            for(RoomUser roomUser : room.getRoomUsersMap().values()){
                _services.getGcmSender().send(roomUser.getProfile(), push);
            }
        }
    }

    public void errorOccurred(RoomError roomError){
        if(!isSceneFullyVisible()){
            roomErrorOccured = roomError;
            return;
        }

        if(forceQuit) return;
        else{
            forceQuit = true;
            _screen.back();
            String message = "";
            switch (roomError){
                case Kicked:
                    message = _texts.youAreKicked();
                    break;
                case GameClientOutdated:
                    message = _texts.gameClientOutdated();
                    break;
                case GameVersionOutdated:
                    message = _texts.gameVersionOutdated();
                    break;
                case FailedRetrieveGameData:
                    message = _texts.failedRetriveGameData();
                    break;
                case HostLeft:
                    message = _texts.hostLeft();
                    _services.getDatabase().updateRoomPlayingAndOpenState(room, null, false, null);
                    break;
                case UnknownError:
                    message = _texts.roomError();
                    break;
            }


            _confirm.show(message, Confirm.Type.YES, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                }
            });
        }
    }

    public int startGameCheck(boolean showMessage){
        if(!room.checkAllTeamHasMinPlayers()){
            if(showMessage){
                _confirm.show(String.format(_services.getTexts().notEnoughPlayers(), room.getGame().getTeamMinPlayers()), Confirm.Type.YES, null);
            }
            return 1;
        }
        else if(noGameClientUsers.size() > 0){
            if(showMessage){
                _confirm.show(_services.getTexts().stillDownloadingClient(), Confirm.Type.YES, null);
            }
            return 2;
        }
        else if(room.getNotYetReadyCount() > 0){
            if(showMessage){
                _confirm.show(_services.getTexts().waitAllUsersReady(), Confirm.Type.YES, null);
            }
            return 3;
        }
        else if(room.getGame().getMustFairTeam() && !room.checkAllFairTeam()){
            if(showMessage){
                _confirm.show(_services.getTexts().fairTeamNeeded(), Confirm.Type.YES, null);
            }
            return 4;
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

    public void startGameCountDown(){
        countDownThread = new SafeThread();
        starting = true;
        scene.getTeamsRoot().setTouchable(Touchable.disabled);

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 3;
                while(i > 0){
                    _services.getSoundsPlayer().playSoundEffect(Sounds.Name.COUNT_DOWN);
                    _services.getChat().newMessage(new ChatMessage(String.format(_texts.gameStartingIn(), i),
                                            ChatMessage.FromType.IMPORTANT, null, ""));

                    Threadings.sleep(1500);

                    i--;
                    if(countDownThread == null || countDownThread.isKilled()){
                        countDownThread = null;
                        starting = false;
                        scene.getTeamsRoot().setTouchable(Touchable.enabled);
                        return;
                    }
                }
                countDownThread = null;
                if(isHost()){
                    sendUpdateRoomMates(UpdateRoomMatesCode.GAME_STARTED, "");
                }
            }
        });

    }

    public void stopGameStartCountDown(Profile profile){
        if(countDownThread != null){
            countDownThread.kill();
            if(profile != null){
                _services.getChat().newMessage(new ChatMessage(String.format(_texts.gameStartStop(),
                        profile.getDisplayName(15)), ChatMessage.FromType.SYSTEM, null, ""));
            }
        }
    }


    public void gameStarted(){
        if(gameStarted) return;

        gameStarted = true;
        hostSendGameStartedPush();
        room.setOpen(false);
        room.setPlaying(true);
        room.setRoundCounter(room.getRoundCounter()+1);
        room.convertRoomUsersToTeams();
        _services.getDatabase().saveRoom(room, true, null);
        _services.getDatabase().savePlayedHistory(_services.getProfile(), room, null);
        _services.getChat().newMessage(new ChatMessage(_texts.gameStarted(), ChatMessage.FromType.SYSTEM, null, ""));

        _screen.toScene(SceneEnum.GAME_SANDBOX, room, false);
        scene.getTeamsRoot().setTouchable(Touchable.enabled);
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
            public void onChanged(String userId, ConnectStatus st) {
                if(!_services.getProfile().getUserId().equals(userId)){
                    if(st == ConnectStatus.DISCONNECTED_BUT_RECOVERABLE || st == ConnectStatus.DISCONNECTED){
                        if(userId.equals(room.getHost().getUserId())){
                            room.removeUserByUserId(userId);
                            checkHostInRoom();
                        }
                        else{
                            if(isHost()) removeUserFromRoom(userId);
                        }
                    }
                    else if(st == ConnectStatus.CONNECTED_FROM_RECOVER){
                        if(gameStarted && isHost()){
                            Player player = room.getPlayerByUserId(userId);
                            int slotIndex = -1;
                            if(player != null){
                                slotIndex = player.getSlotIndex();
                            }
                            addUserToRoom(userId, null, slotIndex);
                        }
                    }
                    else if(st == ConnectStatus.CONNECTED){
                        if(gameStarted && isHost()){
                            Player player = room.getPlayerByUserId(userId);
                            int slotIndex = -1;
                            if(player != null){
                                slotIndex = player.getSlotIndex();
                            }
                            addUserToRoom(userId, null, slotIndex);
                        }
                    }
                }
                else{
                    if(st == ConnectStatus.CONNECTED_FROM_RECOVER){
                        refreshRoomPlayersIfIsHost();
                    }
                }
            }
        });

        _services.getGamingKit().addListener(this.getClassTag(), new RoomInfoListener(room.getWarpRoomId()) {
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
                }
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
                        if(roomUser != null){
                            if(!roomUser.getReady()){
                                sendIsReadyUpdate(true);
                            }
                        }
                        else{
                            if(!isHost()){
                                sendUpdateRoomMates(UpdateRoomMatesCode.JOIN_ROOM, "");
                            }
                        }
                    }
                    Threadings.sleep(3000);
                }
            }
        });
    }

    public void initRoomDbMonitor(final OneTimeRunnable onSuccess){

        _services.getDatabase().monitorRoomById(room.getId(), getClassTag(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room roomObj, Status st) {
                if(st == Status.SUCCESS){
                    if(countDownThread != null) countDownThread.kill();

                    ArrayList<RoomUser> justJoinedUsers = room.getJustJoinedUsers(roomObj);
                    ArrayList<RoomUser> justLeftUsers = room.getJustLeftUsers(roomObj);
                    ArrayList<RoomUser> changedSlotUsers = room.getSlotIndexChangedUsers(roomObj);
                    //ArrayList<RoomUser> changedReadyUsers = room.getIsReadyChangedUsers(roomObj);

                    for(RoomUser u : justJoinedUsers){
                        addUserToRoom(u.getProfile().getUserId(), u.getProfile(), u.getSlotIndex());
                    }
                    for(RoomUser u : justLeftUsers){
                        removeUserFromRoom(u.getProfile().getUserId());
                    }

                    for(RoomUser u : changedSlotUsers){
                        moveSlot(u.getProfile().getUserId(), u.getSlotIndex());
                    }
//
//                    for(RoomUser u : changedReadyUsers){
//                        userIsReadyChanged(u.getProfile().getUserId(), u.getReady());
//                    }

                    if(justJoinedUsers.size() > 0 || justLeftUsers.size() > 0 || changedSlotUsers.size() > 0){
                        refreshRoomDesign();
                    }

                    if(justJoinedUsers.size() > 0 || justLeftUsers.size() > 0){
                        selfUpdateRoomStatePush();
                    }

                    room.setOpen(roomObj.isOpen());

                    onSuccess.run();
                }
                else{
                    errorOccurred(RoomError.UnknownError);
                }
            }
        });
    }

    @Override
    public void setListeners() {
        super.setListeners();

        scene.getStartButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!starting){
                    starting = true;
                    hostSendStartGame();
                }

            }
        });

        scene.getInviteButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!starting){
                    _screen.toScene(SceneEnum.INVITE, room);
                }
            }
        });

        scene.getLeaderboardImage().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!starting){
                    _screen.toScene(SceneEnum.SINGLE_GAME_LEADER_BOARD, room.getGame());
                }
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();

        if(isHost()){
            removeUserFromRoom(_services.getProfile().getUserId());
            _services.getDatabase().updateRoomPlayingAndOpenState(room, null, false, null);
        }
        else{
            sendUpdateRoomMates(UpdateRoomMatesCode.LEFT_ROOM, "");
        }

        _services.getGamingKit().leaveRoom();
        publishBroadcast(BroadcastEvent.DESTROY_ROOM);
        userBadgeHelper.dispose();
        Gdx.files.local("records").deleteDirectory();
        if(roomLogicSafeThread != null) roomLogicSafeThread.kill();
        checkReadyThread.kill();
        _services.getChat().setMode(1);
        _services.getChat().resetChat();
        if(gameFileChecker != null) gameFileChecker.dispose();
        _services.getRecorder().reset();
    }

    public void userJoinLeftAddChat(Profile profile, boolean joined){
        _services.getChat().newMessage(new ChatMessage(String.format(
                joined ? _services.getTexts().userHasJoinedRoom() : _services.getTexts().userHasLeftRoom(),
                profile.getDisplayName(0)), ChatMessage.FromType.SYSTEM, null, ""));
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
                    Threadings.sleep(2000);
                    if(!room.isOpen() && isSceneFullyVisible() && isHost() && !starting && !gameStarted && !isContinue){
                        _services.getDatabase().updateRoomPlayingAndOpenState(room, false, true, null);
                    }

                }
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return scene;
    }

    private boolean isHost(){
        return room.getHost().equals(_services.getProfile());
    }

}
