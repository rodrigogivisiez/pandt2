package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.*;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxLogic extends LogicAbstract {

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

        _services.getDatabase().monitorRoomById(_room.getId(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                ArrayList<RoomUser> leftUsers = _room.getJustLeftUsers(obj);
                if(leftUsers.size() > 0){
                    if(!_gameStarted){
                        failLoad(leftUsers);
                    }
                    else{
                        //show msg waiting for user to connect back?
                        _coordinator.getGameEntrance().getCurrentScreen().setPause(true);
                    }
                }
                else{
                    if(_gameStarted){
                        _coordinator.getGameEntrance().getCurrentScreen().setPause(false);
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
                _room.getGame().getLocalAssetsPath(), _room.getGame().getBasePath(), _room.convertRoomUsersToTeams(),
                Positions.getWidth(), Positions.getHeight(), _screen.getGame(), _screen.getGame().getSpriteBatch()));


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
        _gameStarted = true;
        _services.getChat().show();
        _screen.switchToGameScreen(_coordinator.getGameEntrance().getCurrentScreen());
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
            names.add(profile.getDisplayName());
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
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }



}
