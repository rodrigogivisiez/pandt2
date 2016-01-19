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
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
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
    ArrayList<String> _readyUserIds;        //for host usage only
    boolean _gameCanStart;
    GameCoordinator _coordinator;
    Notification _notification;
    boolean _isContinue;
    boolean _isReady;
    boolean _gameStarted;

    public GameSandboxLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _me = this;
        _notification = services.getNotification();
        _scene = new GameSandboxScene(_services, _screen);
        _readyUserIds = new ArrayList<String>();
        _room = (Room) objs[0];
        _isContinue = (Boolean) objs[1];
    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        listener.onResult(_gameStarted ? OnQuitListener.Result.YES : OnQuitListener.Result.NO);
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
                        failLoad(leftUsers);
                    }
                }
            }
        });


        subscribeBroadcast(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                if (st == Status.SUCCESS) {
                    _coordinator = obj;
                    if(!_isContinue){
                        _isReady = true;
                        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");
                    }
                    else{
                        gameStart();
                    }

                } else {
                    failLoad(_services.getProfile());
                }
            }
        });

        subscribeBroadcast(BroadcastEvent.INGAME_UPDATE_REQUEST, new BroadcastListener<String>() {
            @Override
            public void onCallback(String msg, Status st) {
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.IN_GAME_UPDATE, msg);
            }
        });

        subscribeBroadcast(BroadcastEvent.GAME_END, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                exitSandbox();
            }
        });

        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_REQUEST, new GameCoordinator(_room.getGame().getFullLocalJarPath(),
                        _room.getGame().getLocalAssetsPath(), _room.getGame().getBasePath(), _room.convertRoomUsersToTeams(_services.getProfile()),
                        Positions.getWidth(), Positions.getHeight(), _screen.getGame(), _screen.getGame().getSpriteBatch(),
                        _services.getProfile().getUserId(), _me, _services.getDatabase().getGameBelongDatabase(_room.getGame().getAbbr()), _room.getId()));
            }
        });




        if(!_isContinue){
            if(_room.getHost().equals(_services.getProfile())){

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            Threadings.sleep(3000);
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

            //timeout
            Threadings.delay(60 * 1000, new Runnable() {
                @Override
                public void run() {
                    if(_readyUserIds.size() < _room.getRoomUsersCount() && !_gameCanStart){
                        failLoad(getNotReadyUsers(), false);
                    }
                }
            });
        }



    }

    public void userIsReady(String userId){
        //run by host only
        if(_room.getHost().equals(_services.getProfile())){
            if(!_readyUserIds.contains(userId)){
                _readyUserIds.add(userId);
            }
            if(_readyUserIds.size() >= _room.getRoomUsersCount()){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS, "");
            }
        }
    }

    public void gameStart(){

        if(_gameCanStart) return;

        _gameCanStart = true;

        _services.getProfile().setUserPlayingState(new UserPlayingState(_room.getId(), true, _room.getRoundCounter()));
        _services.getDatabase().updateProfile(_services.getProfile(), new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                for(String userId : _room.getOriginalRoomUserIds()){
                    _services.getDatabase().monitorProfileByUserId(userId, getClassTag(), new DatabaseListener<Profile>(Profile.class) {
                        @Override
                        public void onCallback(final Profile obj, Status st) {
                            if (st == Status.SUCCESS && obj != null) {
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

                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _services.getSounds().stopThemeMusic();
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
                        _gameStarted = true;
                    }
                });


            }
        });
    }

    public void failLoad(ArrayList<RoomUser> roomUsers){
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        for(RoomUser user : roomUsers){
            profiles.add(user.getProfile());
        }
        failLoad(profiles, false);
    }

    public void failLoad(Profile profile){
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        profiles.add(profile);
        failLoad(profiles, false);
    }

    public void failLoad(ArrayList<Profile> profiles, boolean dummy){
        ArrayList<String> names = new ArrayList<String>();
        for(Profile profile : profiles){
            names.add(profile.getDisplayName(0));
        }
        if(names.size() > 0){
            _services.getChat().add(new ChatMessage(_texts.loadGameFailed(),
                                        ChatMessage.FromType.IMPORTANT, null), false);
        }
       exitSandbox();
    }

    public void updateReceived(int code, String msg, String senderId){
        if(code == UpdateRoomMatesCode.USER_IS_READY){
            userIsReady(senderId);
        }
        else if(code == UpdateRoomMatesCode.ASK_FOR_USER_READY){
            if(msg.equals(_services.getProfile().getUserId()) && _isReady){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");    //resend again
            }
        }
        else if(code == UpdateRoomMatesCode.IN_GAME_UPDATE){
            Broadcaster.getInstance().broadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE, new InGameUpdateMessage(senderId, msg));
        }
        else if(code == UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS){
            gameStart();
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
        _services.getSounds().playThemeMusic();
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }


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
    public void dispose() {
        super.dispose();
        _screen.switchToPTScreen();
        if(_coordinator.getGameEntrance() != null) _coordinator.getGameEntrance().dispose();
        _coordinator.dispose();
    }
}
