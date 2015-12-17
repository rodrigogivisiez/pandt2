package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.mygdx.potatoandtomato.models.Services;

import java.util.HashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomLogic extends LogicAbstract {

    RoomScene _scene;
    Room _room;
    GameClientChecker _gameClientChecker;
    HashMap<String, String> _noGameClientUsers;


    public RoomLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new RoomScene(_services);
        _room = (Room) objs[0];

        _noGameClientUsers = new HashMap<>();
    }

    @Override
    public void init() {
        super.init();

        _scene.populateGameDetails(_room.getGame());

        _room.addUser(_services.getProfile());

        openRoom();

        refreshRoom();

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

        _services.getGamingKit().addListener(new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                receivedUpdateRoomMates(code, msg, senderId);
            }
        });

        _gameClientChecker = new GameClientChecker(_room.getGame(), _services.getPreferences(), _services.getDownloader(), new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {

            }
            @Override
            public void onStep(double percentage) {
                super.onStep(percentage);
                int toSend = (int) percentage;
                sendUpdateRoomMates(UpdateRoomMatesCode.UPDATE_DOWNLOAD, String.valueOf(toSend));
            }
        });

        _scene.getHostLeftConfirm().setListener(new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                leaveRoom();
            }
        });

        _scene.getErrorConfirm().setListener(new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                leaveRoom();
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

    public void refreshRoom(){
        _scene.updateRoom(_room);
        int i = 0;
        for(Table t : _scene.getTeamTables()){
            final int finalI = i;
            t.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if(_room.changeTeam(finalI, _services.getProfile())){
                        refreshRoom();
                    }
                }
            });
            i++;
        }
    }

    public void sendUpdateRoomMates(int code, String msg){
        _services.getGamingKit().updateRoomMates(code, msg);
    }

    public void receivedUpdateRoomMates(int code, String msg, String senderId){
        if(code == UpdateRoomMatesCode.UPDATE_DOWNLOAD){
            if(Integer.valueOf(msg) < 100){
                _noGameClientUsers.put(senderId, msg);
            }
            else{
                _noGameClientUsers.remove(senderId);
            }
            _scene.updateDownloadPercentage(senderId, Integer.valueOf(msg));
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

        _services.getGamingKit().leaveRoom();
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
        if(!found) _scene.hostLeft();
    }

    public void errorOccured(){
        _scene.showError();
    }

    public void startGameCheck(){
        if(!_room.checkAllTeamHasMinPlayers()){
            _scene.showMessage(String.format(_services.getTexts().notEnoughPlayers(), _room.getGame().getTeamMinPlayers()));
        }
        else if(_noGameClientUsers.size() > 0){
            _scene.showMessage(_services.getTexts().stillDownloadingClient());
        }
        else{
            startGame();
        }
    }

    public void startGame(){

    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
