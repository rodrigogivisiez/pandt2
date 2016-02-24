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
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.helpers.services.VersionControl;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.potatoandtomato.common.SafeThread;
import com.potatoandtomato.common.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomLogic extends LogicAbstract {

    RoomScene _scene;
    Room _room;
    GameFileChecker _gameFileChecker;
    HashMap<String, String> _noGameClientUsers;
    int _currentPercentage, _previousSentPercentage;
    SafeThread _downloadThread, _countDownThread, _checkReadyThread, _checkMonitorSuccessThread;
    boolean _roomMonitorSuccess;
    boolean _starting;
    boolean _forceQuit;
    boolean _gameStarted;
    boolean _isContinue;
    boolean _quiting;
    boolean _onScreen;
    boolean _populatedPlayersListener;


    public Room getRoom() {
        return _room;
    }

    public RoomLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _room = (Room) objs[0];
        _isContinue = (Boolean) objs[1];
        _scene = new RoomScene(services, screen, _room);
        _noGameClientUsers = new HashMap();
    }

    @Override
    public void onInit() {
        super.onInit();

        _services.getChat().resetChat();
        _scene.populateGameDetails(_room.getGame());
        joinRoomInit();

        if(isHost()) _scene.setStartButtonText(_texts.startGame());

        refreshRoomDesign();

        _services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                receivedUpdateRoomMates(code, msg, senderId);
            }
        });

        if(!_isContinue){
            _gameFileChecker = new GameFileChecker(_room.getGame(), _services.getPreferences(),
                    _services.getDownloader(), _services.getDatabase(), new VersionControl(),
                    new GameFileCheckerListener() {
                @Override
                public void onCallback(GameFileChecker.GameFileResult result, Status st) {
                    if(st == Status.FAILED && !_quiting){
                        switch (result){
                            case FAILED_RETRIEVE:
                                errorOccured(_texts.failedRetriveGameData());
                                break;

                            case GAME_OUTDATED:
                                sendUpdateRoomMates(UpdateRoomMatesCode.GAME_OUTDATED, "");
                                break;

                            case CLIENT_OUTDATED:
                                errorOccured(_texts.gameClientOutdated());
                                break;
                        }
                    }
                }

                @Override
                public void onStep(double percentage) {
                    super.onStep(percentage);
                    _currentPercentage = (int) percentage;
                    downloadingGameNotify();
                }
            });
        }


        _scene.getStartButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_starting){
                    _starting = true;
                    hostSendStartGame();
                }

            }
        });

        _scene.getInviteButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!_starting){
                    _screen.toScene(SceneEnum.INVITE, _room);
                }
            }
        });

        _checkReadyThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(_checkReadyThread.isKilled()) return;
                    Threadings.sleep(3000);
                    if(_onScreen && !_confirm.isVisible()){
                        RoomUser roomUser = _room.getRoomUserByUserId(_services.getProfile().getUserId());
                        if(roomUser != null){
                            if(!roomUser.getReady()){
                                sendIsReadyUpdate(true);
                            }
                        }
                        else{
                            sendUpdateRoomMates(UpdateRoomMatesCode.JOIN_ROOM, "");
                        }

                    }
                }
            }
        });

    }

    public void joinRoomInit(){

        final Runnable monitorRunnable = new Runnable() {
            @Override
            public void run() {
                _services.getDatabase().monitorRoomById(_room.getId(), getClassTag(), new DatabaseListener<Room>(Room.class) {
                    @Override
                    public void onCallback(Room roomObj, Status st) {
                        if(st == Status.SUCCESS){
                            if(_countDownThread != null) _countDownThread.kill();
                            ArrayList<RoomUser> justJoinedUsers = _room.getJustJoinedUsers(roomObj);
                            ArrayList<RoomUser> justLeftUsers = _room.getJustLeftUsers(roomObj);

                            for(RoomUser u : justJoinedUsers){
                                chatAddUserJustJoinedRoom(u.getProfile());
                            }
                            for(RoomUser u : justLeftUsers){
                                chatAddUserJustLeftRoom(u.getProfile());
                            }

                            if(justJoinedUsers.size() > 0){
                                stopGameStartCountDown(justJoinedUsers.get(0).getProfile());
                            }
                            else if(justLeftUsers.size() > 0){
                                stopGameStartCountDown(justLeftUsers.get(0).getProfile());
                            }

                            _room = roomObj;
                            _services.getChat().setRoom(_room);
                            refreshRoomDesign();
                            checkHostInRoom();
                            if(justJoinedUsers.size() > 0 || justLeftUsers.size() > 0){
                                if(!_isContinue) selfUpdateRoomStatePush();
                            }
                            _roomMonitorSuccess = true;

                        }
                        else{
                            errorOccured(_texts.roomError());
                        }
                    }
                });

                if(!isHost()){
                    Threadings.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            _checkMonitorSuccessThread = new SafeThread();
                            while (true){
                                Threadings.sleep(100);
                                if(_roomMonitorSuccess){
                                    sendUpdateRoomMates(UpdateRoomMatesCode.JOIN_ROOM, "");
                                    break;
                                }
                                else if(_checkMonitorSuccessThread.isKilled()){
                                    break;
                                }
                            }
                        }
                    });
                }


            }
        };

        if(isHost()){
            _room.addRoomUser(_services.getProfile(), true);
            chatAddUserJustJoinedRoom(_services.getProfile());
            selfUpdateRoomStatePush();
            hostSaveRoom(true, new DatabaseListener() {
                @Override
                public void onCallback(Object obj, Status st) {
                    monitorRunnable.run();
                }
            });
        }
        else{
            monitorRunnable.run();
        }
    }


    @Override
    public void onShow() {
        super.onShow();

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

        _gameStarted = false;
        _onScreen = true;

        if(!_isContinue && _roomMonitorSuccess) {
            openRoom(false);
            checkHostInRoom();
            selfUpdateRoomStatePush();
        }

        _services.getChat().setRoom(_room);
        _services.getChat().setMode(1);
        _services.getChat().show();
        _services.getChat().scrollToBottom();
        _starting = false;

        if(_roomMonitorSuccess){
            sendIsReadyUpdate(true);
        }


        if(_isContinue) {
            continueGame();
        }

    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        if(!_forceQuit){
            _confirm.show(isHost() ? _texts.confirmHostLeaveRoom() : _texts.confirmLeaveRoom(), Confirm.Type.YESNO, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result == Result.YES) {
                        _forceQuit = true;
                        _quiting = true;
                        listener.onResult(OnQuitListener.Result.YES);
                    } else {
                        listener.onResult(OnQuitListener.Result.NO);
                    }
                }
            });
        }
        else{
            _quiting = true;
            super.onQuit(listener);
        }
    }

    public void refreshRoomDesign(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.updateRoom(_room);

                int i = 0;
                for(Table t : _scene.getPlayersTable()){
                    final int finalI = i;
                    if(!t.getName().contains("disableclick")){
                        t.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                if(_room.getRoomUserBySlotIndex(finalI) == null){
                                    sendUpdateRoomMates(UpdateRoomMatesCode.MOVE_SLOT, String.valueOf(finalI));
                                }
                            }
                        });
                    }

                    i++;
                }



//                for(Table t : _scene.getTeamTables()){
//                    final int finalI = i;
//                    t.addListener(new ClickListener() {
//                        @Override
//                        public void clicked(InputEvent event, float x, float y) {
//                            super.clicked(event, x, y);
//                            if(_room.changeTeam(finalI, _services.getProfile())){
//                                sendUpdateRoomMates(UpdateRoomMatesCode.MOVE_SLOT, String.valueOf(finalI));
//                            }
//                        }
//                    });
//                    i++;
//                }

                if(isHost()){
                    for (Map.Entry<String, Table> entry : _scene.getPlayersMaps().entrySet()) {
                        final String userId = entry.getKey();
                        Table table = entry.getValue();
                        Actor kickImage = table.findActor("kickImage");
                        if(table.getName().contains("nokickattached") && kickImage != null && !userId.equals(_services.getProfile().getUserId())){
                            kickImage.addListener(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    _confirm.show(String.format(_texts.confirmKick(), _room.getProfileByUserId(userId).getDisplayName(0)),
                                            Confirm.Type.YESNO, new ConfirmResultListener() {
                                                @Override
                                                public void onResult(Result result) {
                                                    if(result == Result.YES){
                                                        JsonObj jsonObj = new JsonObj();
                                                        jsonObj.put("userId", userId);
                                                        jsonObj.put("name", _room.getProfileByUserId(userId).getDisplayName(0));
                                                        sendUpdateRoomMates(UpdateRoomMatesCode.KICK_USER, jsonObj.toString());
                                                    }
                                            }
                                    });
                                }
                            });
                            table.setName(table.getName().replace("nokickattached", "kickattached"));
                        }
                    }
                }
            }
        });

    }

    public void downloadingGameNotify(){
        if(_downloadThread == null){
            _downloadThread = new SafeThread();

            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(_previousSentPercentage != _currentPercentage){
                            sendUpdateRoomMates(UpdateRoomMatesCode.UPDATE_DOWNLOAD, String.valueOf(_currentPercentage));
                            _previousSentPercentage = _currentPercentage;
                        }
                        Threadings.sleep(2000);
                        if(_downloadThread.isKilled()){
                            _gameFileChecker.killDownloads();
                            _previousSentPercentage = _currentPercentage = 0;
                            return;
                        }
                        if(_previousSentPercentage >= 100) return;
                    }
                }
            });

        }
    }

    public void sendUpdateRoomMates(int code, String msg){
        _services.getGamingKit().updateRoomMates(code, msg);
    }

    public void receivedUpdateRoomMates(int code, final String msg, final String senderId){

        if(code == UpdateRoomMatesCode.JOIN_ROOM){
            if(isHost()){

                _services.getDatabase().getProfileByUserId(senderId, new DatabaseListener<Profile>(Profile.class) {
                    @Override
                    public void onCallback(Profile obj, Status st) {
                        if (st == Status.SUCCESS && obj != null && _room != null) {
                            Room roomClone = _room.clone();
                            roomClone.addRoomUser(obj, true);
                            hostSaveRoom(roomClone, true, null);
                        }
                    }
                });

            }
        }
        else if(code == UpdateRoomMatesCode.LEFT_ROOM){
            if(isHost()){
                Room roomClone = _room.clone();
                roomClone.getRoomUsers().remove(senderId);
                hostSaveRoom(roomClone, true, null);
            }
        }
        else if(code == UpdateRoomMatesCode.MOVE_SLOT){
            if(isHost()){
                int toSlot = Integer.valueOf(msg);
                Profile profile = _room.getRoomUserByUserId(senderId).getProfile();
                _room.changeSlotIndex(toSlot, profile);
                hostSaveRoom(false, null);
            }
        }
        else if(code == UpdateRoomMatesCode.KICK_USER){
            JsonObj jsonObj = new JsonObj(msg);
            String userId = jsonObj.getString("userId");
            String name = jsonObj.getString("name");
            if(isHost()){
                _room.getRoomUsers().remove(userId);
                hostSaveRoom(true, null);
            }
            _services.getChat().add(new ChatMessage(String.format(_texts.userKicked(), name), ChatMessage.FromType.SYSTEM, null), false);
            if(userId.equals(_services.getProfile().getUserId())){
                errorOccured(_texts.youAreKicked());
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
                                _room.addInvitedUser(obj);
                            }
                            i[0]++;
                            if(i[0] == userIds.length){
                                hostSaveRoom(true, null);
                            }
                        }
                    });
                }
            }
        }
        else if(code == UpdateRoomMatesCode.UPDATE_DOWNLOAD){
            if(Integer.valueOf(msg) < 100){
                _noGameClientUsers.put(senderId, msg);
            }
            else{
                _noGameClientUsers.remove(senderId);
            }
            if(_noGameClientUsers.size() > 0){
                Map.Entry<String, String> entry = _noGameClientUsers.entrySet().iterator().next();  //first item
                stopGameStartCountDown(_room.getProfileByUserId(entry.getKey()));
            }
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _scene.updateDownloadPercentage(senderId, Integer.valueOf(msg));
                }
            });
        }
        else if(code == UpdateRoomMatesCode.START_GAME){
           Threadings.postRunnable(new Runnable() {
               @Override
               public void run() {
                   startGameCountDown();
               }
           });
        }
        else if(code == UpdateRoomMatesCode.UPDATE_USER_READY){
            if(isHost()){           //only host can update db table
                boolean isReady = false;
                if(msg.equals("1")) isReady = true;
                _room.setRoomUserReady(senderId, isReady);
                hostSaveRoom(false, null);
            }
        }
        else if(code == UpdateRoomMatesCode.GAME_OUTDATED){
            errorOccured(_texts.gameVersionOutdated());
        }
        else if(code == UpdateRoomMatesCode.GAME_STARTED){
            if(_countDownThread != null) _countDownThread.kill();
            if(!_gameStarted) gameStarted();
        }
        else if(code == UpdateRoomMatesCode.PLAYER_CANCEL_START_GAME){
            stopGameStartCountDown(_room.getProfileByUserId(msg));
        }
    }

    public void chatAddUserJustJoinedRoom(Profile user){
        _services.getChat().add(new ChatMessage(String.format(_services.getTexts().userHasJoinedRoom(), user.getDisplayName(0)),
                ChatMessage.FromType.SYSTEM, null), false);
    }

    public void chatAddUserJustLeftRoom(Profile user){
        _services.getChat().add(new ChatMessage(String.format(_services.getTexts().userHasLeftRoom(), user.getDisplayName(0)),
                ChatMessage.FromType.SYSTEM, null), false);
    }

    public void openRoom(boolean forceUpdate){
        if(_room.isOpen() && !_room.isPlaying()) return;

        _room.setOpen(true);
        _room.setPlaying(false);
        hostSaveRoom(true, null);
    }

    public void hostSaveRoom(boolean notify, DatabaseListener listener){
        hostSaveRoom(_room, notify, listener);
    }

    public void hostSaveRoom(Room room, boolean notify, DatabaseListener listener){
        if(isHost()){
            _services.getDatabase().saveRoom(room, notify, listener);      //only host can save room
        }
    }

    public boolean checkHostInRoom(){
        if(_forceQuit) return false;
        if(_gameStarted) return true;


        boolean found = false;
        for(RoomUser roomUser : _room.getRoomUsers().values()){
            if(roomUser.getProfile().equals(_room.getHost())){
                found = true;
                break;
            }
        }
        if(!found) {
            _room.setOpen(false);
            _forceQuit = true;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _confirm.show(_texts.hostLeft(), Confirm.Type.YES, new ConfirmResultListener() {
                        @Override
                        public void onResult(Result result) {
                            _screen.back();
                        }
                    });
                }
            });
        }
        return found;
    }

    private void sendIsReadyUpdate(boolean isReady){
        sendUpdateRoomMates(UpdateRoomMatesCode.UPDATE_USER_READY, isReady ? "1" : "0");
        if(_starting && !isReady){
            sendUpdateRoomMates(UpdateRoomMatesCode.PLAYER_CANCEL_START_GAME, _services.getProfile().getUserId());
        }
    }

    public void selfUpdateRoomStatePush(){

        boolean canStart = (startGameCheck(false) == 0);
        PushNotification push = new PushNotification();
        push.setId(PushCode.UPDATE_ROOM);
        push.setSticky(true);
        push.setTitle(_texts.PUSHRoomUpdateTitle());
        push.setMessage(String.format(_texts.PUSHRoomUpdateContent(), _room.getRoomUsersCount(), _room.getGame().getMaxPlayers()));
        push.setSilentNotification(true);
        push.setSilentIfInGame(false);

        if(canStart && !_gameStarted && isHost() && !_isContinue){
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
            push.setMessage(String.format(_texts.PUSHRoomUpdateContent(), _room.getRoomUsersCount(), _room.getGame().getMaxPlayers()));
            push.setSilentNotification(false);
            push.setSilentIfInGame(true);
            for(RoomUser roomUser : _room.getRoomUsers().values()){
                _services.getGcmSender().send(roomUser.getProfile(), push);
            }
        }
    }

    public void errorOccured(String message){
        if(_forceQuit) return;
        else{
            _forceQuit = true;
            _confirm.show(message, Confirm.Type.YES, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    _screen.back();
                }
            });
        }
    }

    public int startGameCheck(boolean showMessage){
        if(!_room.checkAllTeamHasMinPlayers()){
            if(showMessage){
                _confirm.show(String.format(_services.getTexts().notEnoughPlayers(), _room.getGame().getTeamMinPlayers()), Confirm.Type.YES, null);
            }
            return 1;
        }
        else if(_noGameClientUsers.size() > 0){
            if(showMessage){
                _confirm.show(_services.getTexts().stillDownloadingClient(), Confirm.Type.YES, null);
            }
            return 2;
        }
        else if(_room.getNotYetReadyCount() > 0){
            if(showMessage){
                _confirm.show(_services.getTexts().waitAllUsersReady(), Confirm.Type.YES, null);
            }
            return 3;
        }
        else if(_room.getGame().getMustFairTeam() && !_room.checkAllFairTeam()){
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
                _starting = false;
            }
        }
    }

    public void startGameCountDown(){
        _countDownThread = new SafeThread();
        _starting = true;
        _scene.getTeamsRoot().setTouchable(Touchable.disabled);

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 3;
                _services.getChat().show();
                _services.getChat().expanded();
                while(i > 0){
                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.COUNT_DOWN);
                    _services.getChat().add(new ChatMessage(String.format(_texts.gameStartingIn(), i), ChatMessage.FromType.IMPORTANT, null), false);
                    Threadings.sleep(1500);
                    i--;
                    if(_countDownThread == null || _countDownThread.isKilled()){
                        _countDownThread = null;
                        _starting = false;
                        _scene.getTeamsRoot().setTouchable(Touchable.enabled);
                        return;
                    }
                }
                _countDownThread = null;
                if(isHost()){
                    sendUpdateRoomMates(UpdateRoomMatesCode.GAME_STARTED, "");
                }
            }
        });
    }

    public void stopGameStartCountDown(Profile profile){
        if(_countDownThread != null){
            _countDownThread.kill();
            if(profile != null){
                _services.getChat().add(new ChatMessage(String.format(_texts.gameStartStop(),
                        profile.getDisplayName(15)), ChatMessage.FromType.SYSTEM, null), false);
            }
        }
    }


    public void gameStarted(){
        if(_gameStarted) return;

        _gameStarted = true;
        hostSendGameStartedPush();
        _room.setOpen(false);
        _room.setPlaying(true);
        _room.setRoundCounter(_room.getRoundCounter()+1);
        _room.storeRoomUsersToOriginalRoomUserIds();
        _room.convertRoomUsersToTeams();
        hostSaveRoom(true, null);
        _services.getDatabase().savePlayedHistory(_services.getProfile(), _room, null);
        _services.getChat().add(new ChatMessage(_texts.gameStarted(), ChatMessage.FromType.SYSTEM, null), false);


        Threadings.delayNoPost(1000, new Runnable() {
            @Override
            public void run() {
                _services.getChat().hide();
                _screen.toScene(SceneEnum.GAME_SANDBOX, _room, false);
                _scene.getTeamsRoot().setTouchable(Touchable.enabled);
            }
        });
    }

    public void continueGame() {
        if (_gameStarted) return;

        selfUpdateRoomStatePush();
        _services.getChat().hide();
        _screen.toScene(SceneEnum.GAME_SANDBOX, _room, true);
        _gameStarted = true;
        _isContinue = false;
    }


    private boolean isHost(){
        return _room.getHost().equals(_services.getProfile());
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    @Override
    public void onHide() {
        super.onHide();
        _confirm.setStateChangedListener(null);
        _onScreen = false;
        if(!_quiting)  sendIsReadyUpdate(false);

    }

    public void leaveRoom(){
        if(isHost()){
            _room.setOpen(false);
            _room.getRoomUsers().remove(_services.getProfile().getUserId());
        }

        sendUpdateRoomMates(UpdateRoomMatesCode.LEFT_ROOM, "");
        _services.getGamingKit().leaveRoom();
        publishBroadcast(BroadcastEvent.DESTROY_ROOM);
        hostSaveRoom(true, null);
    }

    @Override
    public void dispose() {
        leaveRoom();
        super.dispose();
        Gdx.files.local("records").deleteDirectory();
        if(_checkMonitorSuccessThread != null) _checkMonitorSuccessThread.kill();
        _checkReadyThread.kill();
        _services.getChat().hide();
        _services.getChat().resetChat();
        if(_gameFileChecker != null) _gameFileChecker.dispose();
    }

}
