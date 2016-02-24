package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.helpers.controls.Notification;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.*;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxLogic extends LogicAbstract implements IGameSandBox {

    IGameSandBox _me;
    GameSandboxScene _scene;
    Room _room;
    ArrayList<String> _readyUserIds;
    boolean _gameCanStart;
    GameCoordinator _coordinator;
    Notification _notification;
    boolean _isContinue;
    boolean _isReady;
    boolean _gameStarted;
    boolean _failed;
    ArrayList<String> _monitorRetrievedUserId;

    public GameSandboxLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _me = this;
        _notification = services.getNotification();
        _scene = new GameSandboxScene(_services, _screen);
        _readyUserIds = new ArrayList<String>();
        _monitorRetrievedUserId = new ArrayList<String>();
        _room = (Room) objs[0];
        _isContinue = (Boolean) objs[1];
        initiateUserReady();
    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        listener.onResult(_gameStarted || _failed ? OnQuitListener.Result.YES : OnQuitListener.Result.NO);
    }

    @Override
    public void onInit() {
        super.onInit();

        _services.getChat().hide();

        _services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                updateReceived(code, msg, senderId);
            }
        });

        _services.getDatabase().monitorRoomById(_room.getId(), getClassTag(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                ArrayList<RoomUser> leftUsers = _room.getJustLeftUsers(obj);
                if(leftUsers.size() > 0){
                    if(!_gameCanStart){
                        userLeftRoom(leftUsers.get(0).getProfile().getUserId());
                    }
                }
            }
        });

        subscribeBroadcast(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                if (st == Status.SUCCESS) {
                    _coordinator = obj; //coordinator will call gameLoaded of sandbox
                } else {
                    _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.LOAD_FAILED, "");
                }
            }
        });

        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                publishBroadcast(BroadcastEvent.LOAD_GAME_REQUEST, new GameCoordinator(_room.getGame().getFullLocalJarPath(),
                        _room.getGame().getLocalAssetsPath(), _room.getGame().getBasePath(), _room.getTeams(),
                        Positions.getWidth(), Positions.getHeight(), _screen.getGame(), _screen.getGame().getSpriteBatch(),
                        _services.getProfile().getUserId(), _me, _services.getDatabase().getGameBelongDatabase(_room.getGame().getAbbr()),
                        _room.getId(), _services.getSoundsWrapper(), getBroadcaster(), _services.getDownloader()));
            }
        });

        startHostUserReadyMonitor();
        startTimeoutThread();
    }

    private void initiateUserReady(){
        if(_isContinue){
            Profile myProfile = _services.getProfile();
            setUserTable(myProfile.getUserId(), false, false);
        }
        else{
            for(RoomUser roomUser : _room.getRoomUsers().values()){
                ;Profile profile = roomUser.getProfile();
                setUserTable(profile.getUserId(), false, false);
            }
        }
    }

    private void startHostUserReadyMonitor(){
        if(!_isContinue){
            if(_room.getHost().equals(_services.getProfile())){

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            Threadings.sleep(3000);
                            if(_failed) return;

                            if(_readyUserIds.size() < _room.getRoomUsersCount() && !_gameCanStart){
                                askForUserIsReady();
                            }
                            else{
                                break;
                            }
                        }
                    }
                });
            }
        }
    }

    private void startTimeoutThread(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 60;
                while (true){
                    if(_failed) return;

                    i--;
                    setRemainingTime(i);

                    if(i == 0 && !_gameCanStart){
                        failLoad(null);
                    }
                    Threadings.sleep(1000);

                }
            }
        });
    }

    public void userIsReady(String userId){
        if(!_readyUserIds.contains(userId)){
            _readyUserIds.add(userId);
            setUserTable(userId, true, false);
        }
        if(_readyUserIds.size() >= _room.getRoomUsersCount() && _services.getProfile().equals(_room.getHost())){
            _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS, "");  //only host can send
        }
    }

    public void gameStart(){

        if(_gameCanStart) return;

        _gameCanStart = true;

        final Runnable dbMonitor = new Runnable() {
            @Override
            public void run() {
                _services.getProfile().setUserPlayingState(new UserPlayingState(_room.getId(), true, _room.getRoundCounter()));
                _services.getDatabase().updateProfile(_services.getProfile(), new DatabaseListener() {
                    @Override
                    public void onCallback(Object obj, Status st) {
                        for(String userId : _room.getOriginalRoomUserIds()){
                            _services.getDatabase().monitorProfileByUserId(userId, getClassTag(), new DatabaseListener<Profile>(Profile.class) {
                                @Override
                                public void onCallback(final Profile obj, Status st) {
                                    if (st == Status.SUCCESS && obj != null) {
                                        if(!_monitorRetrievedUserId.contains(obj.getUserId())){
                                            _monitorRetrievedUserId.add(obj.getUserId());
                                            return;
                                        }

                                        final UserPlayingState userPlayingState = obj.getUserPlayingState();
                                        if (userPlayingState != null) {
                                            if (userPlayingState.getRoomId().equals(_room.getId())) {
                                                Threadings.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (userPlayingState.getAbandon()) {
                                                            //user abandoned
                                                            _services.getChat().add(new ChatMessage(String.format(_texts.notificationAbandon(),
                                                                    obj.getDisplayName(0)), ChatMessage.FromType.IMPORTANT, null), false);
                                                            _notification.important(String.format(_texts.notificationAbandon(), obj.getDisplayName(15)));
                                                            _coordinator.userAbandon(obj.getUserId());
                                                        } else if (userPlayingState.getConnected()) {
                                                            //user connected back
                                                            _notification.info(String.format(_texts.notificationConnected(), obj.getDisplayName(15)));
                                                            _coordinator.userConnectionChanged(obj.getUserId(), true);
                                                        } else if (!userPlayingState.getConnected()) {
                                                            //user disconnected
                                                            _notification.important(String.format(_texts.notificationDisconnected(), obj.getDisplayName(15)));
                                                            _coordinator.userConnectionChanged(obj.getUserId(), false);
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                    }
                                }
                            });
                        }
                    }
                });
            }
        };

        for(RoomUser roomUser : _room.getRoomUsers().values()){
            setUserTable(roomUser.getProfile().getUserId(), true, false);
        }

        Threadings.delay(500, new Runnable() {
            @Override
            public void run() {

                _services.getSoundsWrapper().stopThemeMusic();
                Threadings.setContinuousRenderLock(true);
                _screen.switchToGameScreen();
                if(!_isContinue){
                    _coordinator.getGameEntrance().init();
                }
                else{
                    _coordinator.getGameEntrance().onContinue();
                }
                _scene.clearRoot();
                _services.getChat().setMode(2);
                _services.getChat().resetChat();
                _services.getChat().show();
                if(_coordinator.isLandscape()){
                    _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 1);
                    Threadings.delay(500, new Runnable() {
                        @Override
                        public void run() {
                            _services.getChat().animateHideForMode2();
                        }
                    });
                }
                _gameStarted = true;
                dbMonitor.run();
            }
        });


    }

    public void userLeftRoom(String userId){
        _failed = true;
        _services.getChat().add(new ChatMessage(_texts.playerLeftCauseGameCancel(),
                ChatMessage.FromType.IMPORTANT, null), false);

        setUserTable(userId, false, true);

        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                exitSandbox();
            }
        });

    }

    public void failLoad(String userId){
        _failed = true;

        if(userId == null && getNotReadyUsers().size() > 0){
            userId = getNotReadyUsers().get(0).getUserId();
        }

        if(userId != null){
            _services.getChat().add(new ChatMessage(_texts.loadGameFailed(),
                    ChatMessage.FromType.IMPORTANT, null), false);

            setUserTable(userId, false, true);

        }

        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                exitSandbox();
            }
        });
    }


    @Override
    public void onGameLoaded() {
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (_coordinator.getGameEntrance() == null && !_failed){
                    Threadings.sleep(500);
                }
                if(!_failed){
                    _isReady = true;
                    if(!_isContinue){
                        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");
                    }
                    else{
                        gameStart();
                    }
                }
            }
        });
    }

    public void updateReceived(int code, String msg, String senderId){
        if(code == UpdateRoomMatesCode.USER_IS_READY){
            userIsReady(senderId);
        }
        else if(code == UpdateRoomMatesCode.ASK_FOR_USER_READY){
            if(msg.equals(_services.getProfile().getUserId()) && _isReady){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");    //resend again
            }
            if(msg.equals(_services.getProfile().getUserId()) && _failed){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.LOAD_FAILED, "");    //resend again
            }
        }
        else if(code == UpdateRoomMatesCode.IN_GAME_UPDATE){
            publishBroadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE, new InGameUpdateMessage(senderId, msg));
        }
        else if(code == UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS){
            gameStart();
        }
        else if(code == UpdateRoomMatesCode.LOAD_FAILED){
            failLoad(senderId);
        }
    }

    public void askForUserIsReady(){
        ArrayList<Profile> profiles = getNotReadyUsers();
        for(Profile p : profiles){
            _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.ASK_FOR_USER_READY, p.getUserId());
        }
    }

    public ArrayList<Profile> getNotReadyUsers(){
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        for(RoomUser roomUser : _room.getRoomUsers().values()){
            if(!_readyUserIds.contains(roomUser.getProfile().getUserId())){
                profiles.add(roomUser.getProfile());
            }
        }
        return profiles;
    }

    public void exitSandbox(){
        _screen.switchToPTScreen();
        _screen.back();
        _services.getChat().setMode(1);
        _services.getChat().add(new ChatMessage(_texts.gameEnded(),
                ChatMessage.FromType.SYSTEM, null), false);
        Threadings.setContinuousRenderLock(false);
        _services.getSoundsWrapper().playThemeMusic();
    }

    @Override
    public void dispose() {
        super.dispose();
        _screen.switchToPTScreen();
        if(_coordinator != null){
            if(_coordinator.getGameEntrance() != null) _coordinator.getGameEntrance().dispose();
            _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
            _coordinator.dispose();
        }
    }

    public void setUserTable(final String userId, final boolean isReady, final boolean isFailed){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.setUser(userId, _room.getRoomUserByUserId(userId).getProfile().getDisplayName(15),
                        isReady, isFailed, _services.getChat().getUserColor(userId));
            }
        });
    }

    public void setRemainingTime(final int sec){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.setRemainingTime(sec);
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    ///////////////////////////////////////////////////////////
    // called from in game
    ////////////////////////////////////////////////
    @Override
    public void useConfirm(String msg, final Runnable yesRunnable, final Runnable noRunnable) {
        Confirm.Type type = Confirm.Type.YESNO;
        if(noRunnable == null){
            type = Confirm.Type.YES;
        }
        String text = _texts.getSpecialText(msg);
        if(text == null) text = msg;

        _confirm.show(text, type, new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                if(result == Result.YES){
                    if(yesRunnable != null) yesRunnable.run();
                }
                else {
                    if(noRunnable != null) noRunnable.run();
                }
            }
        });
    }

    @Override
    public void userAbandoned() {
        UserPlayingState userPlayingState = _services.getProfile().getUserPlayingState();
        userPlayingState.setAbandon(true);
        userPlayingState.setConnected(false);
        _services.getProfile().setUserPlayingState(userPlayingState);
        _services.getDatabase().updateProfile(_services.getProfile(), null);
    }

    @Override
    public void endGame() {
        exitSandbox();
    }

    @Override
    public void inGameUpdateRequest(String msg) {
        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.IN_GAME_UPDATE, msg);
    }


}
