package com.mygdx.potatoandtomato.scenes.room_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomLogic extends LogicAbstract {

    RoomScene _scene;
    Room _room;

    public RoomLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new RoomScene(_services);
        _room = (Room) objs[0];
    }

    @Override
    public void init() {
        super.init();
        _room.addUser(_services.getProfile());

        //todo subscribe appwarp room

        openRoom();

        _scene.updateRoom(_room);

        _services.getDatabase().monitorRoomById(_room.getId(), new DatabaseListener<Room>() {
            @Override
            public void onCallback(Room obj, Status st) {
                if(st == Status.SUCCESS){
                    _room = obj;
                    _scene.updateRoom(_room);
                    checkHostInRoom();
                }
                else{
                    errorOccured();
                }
            }
        });
    }

    @Override
    public void onQuit(Runnable toRun) {
        if(true){       //check confirm quit
            leaveRoom();
        }
        else{

        }
    }

    public void openRoom(){
        _room.setOpen(true);
        _room.setPlaying(false);
        flushRoom(false);
    }

    public void roomGameStart(){
        _room.setOpen(false);
        _room.setPlaying(true);
        _room.setRoundCounter(_room.getRoundCounter()+1);
        flushRoom(false);
    }

    public void leaveRoom(){
        if(_room.getHost().equals(_services.getProfile())){
            _room.setOpen(false);
        }
        _room.getRoomUsers().remove(_services.getProfile().getUserId());
        flushRoom(true);
        //todo unsubscribe appwarp room
    }

    public void flushRoom(boolean force){
        if(_room.getHost().equals(_services.getProfile()) || force){
            _services.getDatabase().saveRoom(_room, null);      //only host can save room
        }
    }

    public void checkHostInRoom(){
        boolean found = false;
        for(RoomUser roomUser : _room.getRoomUsers().values()){
            if(roomUser.getProfile().equals(_room.getHost())){
                found = true;
                break;
            }
        }
        _scene.toggleHostLeft(!found);
    }

    public void errorOccured(){
        _scene.showError();
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
