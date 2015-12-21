package com.mygdx.potatoandtomato.scenes.prerequisite_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.JoinRoomListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class PrerequisiteLogic extends LogicAbstract {

    PrerequisiteScene _scene;
    Game _game;
    Texts _texts;
    boolean _isCreating;
    Room _joiningRoom;

    public PrerequisiteLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        setSaveToStack(false);
        _texts = _services.getTexts();
        _scene = new PrerequisiteScene(services, screen);
        _game = (Game) objs[0];
        _isCreating = (boolean) objs[1];
        if(!_isCreating) _joiningRoom = (Room) objs[2];

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
        if(_isCreating){
            createRoom();
        }
        else{
            joinRoom();
        }
    }

    public void createRoom(){
        _scene.changeMessage(_texts.lookingForServer());
        _services.getGamingKit().addListener(new JoinRoomListener() {
            @Override
            public void onRoomJoined(String roomId) {
                createRoomSuccess(roomId);
            }

            @Override
            public void onJoinRoomFailed() {
                joinRoomFailed();
            }
        });
        _services.getGamingKit().createAndJoinRoom();
    }

    public void joinRoom(){
        _scene.changeMessage(_texts.locatingRoom());
        _services.getGamingKit().addListener(new JoinRoomListener() {
            @Override
            public void onRoomJoined(String roomId) {
                joinRoomSuccess();
            }

            @Override
            public void onJoinRoomFailed() {
                joinRoomFailed();
            }
        });
        _services.getGamingKit().joinRoom(_joiningRoom.getRoomId());
    }

    public void joinRoomFailed(){
        _scene.failedMessage(_texts.joinRoomFailed());
    }

    public void createRoomSuccess(String roomId){
        _scene.changeMessage(_texts.joiningRoom());
        final Room room = new Room();
        room.setRoomId(roomId);
        room.setGame(_game);
        room.setOpen(true);
        room.setHost(_services.getProfile());
        room.setPlaying(false);
        room.setRoundCounter(0);
        _screen.toScene(SceneEnum.ROOM, room);
    }

    public void joinRoomSuccess(){
        _scene.changeMessage(_texts.joiningRoom());
        _screen.toScene(SceneEnum.ROOM, _joiningRoom);
    }

    @Override
    public void dispose() {
        super.dispose();

    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }



}
