package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
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
    int _currentPercentage, _previousSentPercentage;
    SafeThread _downloadThread, _countDownThread;
    boolean _readyToStart;
    boolean _forceQuit;

    public Room getRoom() {
        return _room;
    }

    public RoomLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _scene = new RoomScene(services, screen);
        _room = (Room) objs[0];
        _noGameClientUsers = new HashMap<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _scene.populateGameDetails(_room.getGame());

        _room.addRoomUser(_services.getProfile());
        openRoom();

        if(!isHost()) flushRoom(true, null);

        refreshRoomDesign();

        _services.getDatabase().monitorRoomById(_room.getId(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                if(st == Status.SUCCESS){
                    if(_countDownThread != null) _countDownThread.kill();
                    _room = obj;
                    refreshRoomDesign();
                    checkHostInRoom();
                }
                else{
                    errorOccured();
                }
            }
        });

        _services.getDatabase().removeUserFromRoomOnDisconnect(_room, _services.getProfile(), new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if(st == Status.FAILED){
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
                _currentPercentage = (int) percentage;
                downloadingGameNotify();
            }
        });

        _scene.getHostLeftConfirm().setListener(new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                _forceQuit = true;
            }
        });

        _scene.getErrorConfirm().setListener(new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                _forceQuit = true;
            }
        });

        _scene.getStartButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                startGame();
            }
        });

    }


    @Override
    public void onQuit(final OnQuitListener listener) {
        if(!_forceQuit){
            _scene.getLeaveRoomConfirm().setListener(new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if(result == Result.YES){
                        _forceQuit = true;
                        leaveRoom();
                        listener.onResult(OnQuitListener.Result.YES);
                    }
                }
            });
            _scene.showLeaveRoomConfirm(isHost());
        }
        else{
            leaveRoom();
            super.onQuit(listener);
        }
    }

    public void refreshRoomDesign(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.updateRoom(_room);
                int i = 0;
                for(Table t : _scene.getTeamTables()){
                    final int finalI = i;
                    t.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            if(_room.changeTeam(finalI, _services.getProfile())){
                                refreshRoomDesign();
                                flushRoom(true, null);
                            }
                        }
                    });
                    i++;
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
                            _gameClientChecker.killDownloads();
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
        if(code == UpdateRoomMatesCode.UPDATE_DOWNLOAD){
            if(Integer.valueOf(msg) < 100){
                _noGameClientUsers.put(senderId, msg);
            }
            else{
                _noGameClientUsers.remove(senderId);
            }
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _scene.updateDownloadPercentage(senderId, Integer.valueOf(msg));
                }
            });

        }
    }

    public void openRoom(){
        _room.setOpen(true);
        _room.setPlaying(false);
        flushRoom(false, null);
    }

    public void leaveRoom(){
        if(isHost()){
            _room.setOpen(false);
        }
        _room.getRoomUsers().remove(_services.getProfile().getUserId());
        flushRoom(true, null);

        _services.getGamingKit().leaveRoom();
    }

    public void flushRoom(boolean force, DatabaseListener listener){
        if(isHost() || force){
            _services.getDatabase().saveRoom(_room, listener);      //only host can save room
        }
    }

    public boolean checkHostInRoom(){
        if(_forceQuit) return false;

        boolean found = false;
        for(RoomUser roomUser : _room.getRoomUsers().values()){
            if(roomUser.getProfile().equals(_room.getHost())){
                found = true;
                break;
            }
        }
        if(!found) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _scene.getHostLeftConfirm().setListener(new ConfirmResultListener() {
                        @Override
                        public void onResult(Result result) {
                            _forceQuit = true;
                            _screen.back();
                        }
                    });
                    _scene.hostLeft();
                }
            });
        }
        return found;
    }

    public void errorOccured(){
        if(_forceQuit) return;
        else{
            _scene.getErrorConfirm().setListener(new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    _forceQuit = true;
                    _screen.back();
                }
            });
            _scene.showError();
        }
    }

    public int startGameCheck(){
        if(!_room.checkAllTeamHasMinPlayers()){
            _scene.showMessage(String.format(_services.getTexts().notEnoughPlayers(), _room.getGame().getTeamMinPlayers()));
            return 1;
        }
        else if(_noGameClientUsers.size() > 0){
            _scene.showMessage(_services.getTexts().stillDownloadingClient());
            return 2;
        }
        else{
            return 0;
        }
    }

    public void startGame(){
        if(startGameCheck() == 0){
            _countDownThread = new SafeThread();
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    int i = 3;
                    while(i > 0){
                        Threadings.sleep(1 * 1000);
                        i--;
                        if(_countDownThread.isKilled()) return;
                    }
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            gameStarted();
                        }
                    });
                }
            });
        }
    }

    public void gameStarted(){
        _room.setOpen(false);
        _room.setPlaying(true);
        _room.setRoundCounter(_room.getRoundCounter()+1);
        flushRoom(false, null);
    }

    public void gameFinished(){
        openRoom();
    }

    private boolean isHost(){
        return _room.getHost().equals(_services.getProfile());
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
