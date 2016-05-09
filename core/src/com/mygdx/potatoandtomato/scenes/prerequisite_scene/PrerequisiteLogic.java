package com.mygdx.potatoandtomato.scenes.prerequisite_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.JoinRoomListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class PrerequisiteLogic extends LogicAbstract {

    PrerequisiteScene _scene;
    Game _game;
    Texts _texts;
    JoinType _joinType;
    String _roomId;
    Room _joiningRoom;

    public PrerequisiteLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        setSaveToStack(false);
        _texts = _services.getTexts();
        _scene = new PrerequisiteScene(services, screen);
        _game = (Game) objs[0];
        _joinType = (JoinType) objs[1];
        if(objs.length > 2) _roomId = (String) objs[2];

        _scene.getRetryButton().addListener((new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                restart();     //retry whole process
            }
        }));
    }

    @Override
    public void onQuit(OnQuitListener listener) {
        _services.getGamingKit().leaveRoom();
        super.onQuit(listener);
    }

    @Override
    public void onInit() {
        super.onInit();
        restart();
    }

    public void restart(){
        if(_joinType == JoinType.CREATING){
            createRoom();
        }
        else{
            joinRoom();
        }
    }

    public void createRoom(){
        _scene.changeMessage(_texts.lookingForServer());

        _services.getDatabase().getGameByAbbr(_game.getAbbr(), new DatabaseListener<Game>(Game.class) {
            @Override
            public void onCallback(Game obj, Status st) {
                if(st == Status.SUCCESS){
                    _game = obj;

                    _services.getGamingKit().addListener(getClassTag(), new JoinRoomListener() {
                        @Override
                        public void onRoomJoined(String roomId) {
                            createRoomSuccess(roomId);
                        }

                        @Override
                        public void onJoinRoomFailed() {
                            joinRoomFailed(0);
                        }
                    });
                    _services.getGamingKit().createAndJoinRoom();
                }
                else{
                    joinRoomFailed(0);
                }
            }
        });


    }

    public void joinRoom(){
        _scene.changeMessage(_texts.locatingRoom());

        _services.getDatabase().getRoomById(_roomId, new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                if(st == Status.SUCCESS){

                    if(obj.getRoomUsersCount() >= Integer.valueOf(obj.getGame().getMaxPlayers())){
                        joinRoomFailed(1);
                        return;
                    }

                    if(!obj.isOpen() && _joinType == JoinType.JOINING){
                        joinRoomFailed(2);
                        return;
                    }

                    if(_joinType == JoinType.CONTINUING && !obj.canContinue(_services.getProfile())){
                        joinRoomFailed(3);
                        return;
                    }

                    _joiningRoom = obj;

                    _services.getGamingKit().addListener(getClassTag(), new JoinRoomListener() {
                        @Override
                        public void onRoomJoined(String roomId) {
                            joinRoomSuccess();
                        }

                        @Override
                        public void onJoinRoomFailed() {
                            joinRoomFailed(0);
                        }
                    });
                    _services.getGamingKit().joinRoom(_joiningRoom.getWarpRoomId());
                }
                else{
                    joinRoomFailed(0);
                }
            }
        });
    }

    public void joinRoomFailed(final int reason){
        if(reason == 0){    //general msg
            _scene.failedMessage(_texts.joinRoomFailed());
        }
        else if(reason == 1){    //full room
            _scene.failedMessage(_texts.roomIsFull());
        }
        else if(reason == 2){    //room is not open
            _scene.failedMessage(_texts.roomStarted());
        }
        else if(reason == 3){   //cannot continue game
            _scene.failedMessage(_texts.cannotContinue());
        }

    }

    public void createRoomSuccess(final String roomId){
        _scene.changeMessage(_texts.joiningRoom());
        _joiningRoom = new Room();
        _joiningRoom.setWarpRoomId(roomId);
        _joiningRoom.setGame(_game);
        _joiningRoom.setOpen(true);
        _joiningRoom.setHost(_services.getProfile());
        _joiningRoom.setPlaying(false);
        _joiningRoom.setRoundCounter(0);
        _joiningRoom.addRoomUser(_services.getProfile(), true);
        _services.getDatabase().saveRoom(_joiningRoom, true, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if (st == Status.SUCCESS) {
                    _screen.toScene(SceneEnum.ROOM, _joiningRoom, false);
                    _services.getDatabase().removeUserFromRoomOnDisconnect(_joiningRoom.getId(), _services.getProfile(), new DatabaseListener<String>() {
                        @Override
                        public void onCallback(String obj, Status st) {

                        }
                    });
                } else {
                    joinRoomFailed(0);
                }
            }
        });
    }

    public void joinRoomSuccess(){
        _scene.changeMessage(_texts.joiningRoom());
        _services.getDatabase().addUserToRoom(_joiningRoom, _services.getProfile(), new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if (st == Status.SUCCESS) {
                    if (_joinType != JoinType.CONTINUING) {           //no need wait until removeUser attached to join the room
                        _screen.toScene(SceneEnum.ROOM, _joiningRoom, false);
                    }

                    _services.getDatabase().removeUserFromRoomOnDisconnect(_joiningRoom.getId(), _services.getProfile(), new DatabaseListener<String>() {
                        @Override
                        public void onCallback(String obj, Status st) {
                            if (_joinType == JoinType.CONTINUING) {        //continue game need to wait until removeUser attached to join the room
                                _screen.toScene(SceneEnum.ROOM, _joiningRoom, true);
                            }
                        }
                    });
                } else {
                    joinRoomFailed(0);
                }
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();

    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    public Room getJoiningRoom() {
        return _joiningRoom;
    }

    public enum JoinType{
        CREATING, JOINING, CONTINUING
    }



}
