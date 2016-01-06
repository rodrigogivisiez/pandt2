package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.*;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxLogic extends LogicAbstract implements IGameSandBox {

    GameSandboxScene _scene;
    Room _room;
    ArrayList<String> _readyUserIds;
    boolean _gameStarted;
    GameCoordinator _coordinator;

    public GameSandboxLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new GameSandboxScene(_services, _screen);
        _readyUserIds = new ArrayList<String>();
        _room = (Room) objs[0];
    }

    @Override
    public void onInit() {
        super.onInit();
        _services.getChat().hide();

        _services.getGamingKit().addListener(new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                updateReceived(code, msg, senderId);
            }
        });

        _services.getProfile().setUserPlayingState(new UserPlayingState(_room.getId(), true));
        _services.getDatabase().updateProfile(_services.getProfile());

        _services.getDatabase().monitorRoomById(_room.getId(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                ArrayList<RoomUser> leftUsers = _room.getJustLeftUsers(obj);
                if(leftUsers.size() > 0){
                    if(!_gameStarted){
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
                    _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");
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

        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_REQUEST, new GameCoordinator(_room.getGame().getFullLocalJarPath(),
                _room.getGame().getLocalAssetsPath(), _room.getGame().getBasePath(), _room.convertRoomUsersToTeams(_services.getProfile()),
                Positions.getWidth(), Positions.getHeight(), _screen.getGame(), _screen.getGame().getSpriteBatch(),
                _room.getHost().equals(_services.getProfile()), _services.getProfile().getUserId(), this));


        Threadings.delay(10000, new Runnable() {
            @Override
            public void run() {
                if(_readyUserIds.size() < _room.getRoomUsersCount()){
                    askForUserIsReady();
                }
            }
        });

        //timeout
        Threadings.delay(60 * 1000, new Runnable() {
            @Override
            public void run() {
                if(_readyUserIds.size() < _room.getRoomUsersCount()){
                    failLoad(getNotReadyUsers(), false);
                }
            }
        });

    }

    public void userIsReady(String userId){
        if(!_readyUserIds.contains(userId)){
            _readyUserIds.add(userId);
        }
        if(_readyUserIds.size() >= _room.getRoomUsersCount()){
            gameStart();
        }

    }

    public void gameStart(){

        for(RoomUser user : _room.getRoomUsers().values()){
            _services.getDatabase().monitorProfileByUserId(user.getProfile().getUserId(), new DatabaseListener<Profile>(Profile.class) {
                @Override
                public void onCallback(Profile obj, Status st) {
                    if (st == Status.SUCCESS && obj != null) {
                        final UserPlayingState userPlayingState = obj.getUserPlayingState();
                        if (userPlayingState != null) {
                            if (userPlayingState.getRoomId().equals(_room.getId())) {
                                Threadings.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (userPlayingState.getAbandon()) {
                                            //user abandoned
                                        } else if (userPlayingState.getConnected()) {
                                            //user connected back
                                        } else if (!userPlayingState.getConnected()) {
                                            //user disconnected
                                        }
                                    }
                                });
                            }
                        }

                    }
                }
            });
        }

        _gameStarted = true;
        _screen.switchToGameScreen(_coordinator.getGameEntrance().getCurrentScreen());
        _coordinator.getGameEntrance().init();
        _scene.clearRoot();
        _services.getChat().show();

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
            _services.getChat().add(new ChatMessage(String.format(_texts.loadGameFailed(), StringUtils.join(names, ", ")),
                                        ChatMessage.FromType.IMPORTANT, null));
        }
       exitSandbox();
    }

    public void updateReceived(int code, String msg, String senderId){
        if(code == UpdateRoomMatesCode.USER_IS_READY){
            userIsReady(senderId);
        }
        else if(code == UpdateRoomMatesCode.ASK_FOR_USER_READY){
            if(msg.equals(_services.getProfile().getUserId())){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");    //resend again
            }
        }
        else if(code == UpdateRoomMatesCode.IN_GAME_UPDATE){
            Broadcaster.getInstance().broadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE, new InGameUpdateMessage(senderId, msg));
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
        _services.getChat().add(new ChatMessage(_texts.gameEnded(),
                ChatMessage.FromType.SYSTEM, null));
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
    public void dispose() {
        super.dispose();
        if(_coordinator.getGameEntrance() != null) _coordinator.getGameEntrance().dispose();
    }
}