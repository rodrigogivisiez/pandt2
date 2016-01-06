package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListLogic extends LogicAbstract {

    GameListScene _scene;
    ArrayList<Room> _rooms;
    Room _selectedRoom;

    public GameListLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _scene = new GameListScene(services, screen);
        _rooms = new ArrayList();

        _scene.getNewGameButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.CREATE_GAME);
            }
        });

        _scene.getJoinGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_selectedRoom != null){
                    _screen.toScene(SceneEnum.PREREQUISITE, _selectedRoom.getGame(), false, _selectedRoom);
                }
            }
        });

        _scene.getSettingsButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.SETTINGS);
            }
        });


        _services.getDatabase().monitorAllRooms(_rooms, new SpecialDatabaseListener<ArrayList<Room>, Room>(Room.class) {
            @Override
            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
                if(st == Status.SUCCESS){
                    for(Room r : obj){
                        roomDataChanged(r);
                    }
                }
            }

            @Override
            public void onCallbackTypeTwo(Room obj, Status st) {
                if(st == Status.SUCCESS){
                    roomDataChanged(obj);
                }
            }
        });

    }

    @Override
    public void onShow() {
        super.onShow();
        _scene.setUsername(_services.getProfile().getDisplayName(15));
    }

    public void roomDataChanged(final Room room){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(room.isOpen()){
                    final Actor clicked = _scene.updatedRoom(room);
                    if(clicked != null){
                        clicked.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                _selectedRoom = room;
                                _scene.gameRowHighlight(clicked.getName());
                            }
                        });
                    }
                }
                else{
                    if(_selectedRoom != null && _selectedRoom.getId().equals(room.getId())) {
                        _selectedRoom = null;
                        _scene.gameRowHighlight("-1");
                    }
                    _scene.removeRoom(room);
                }
            }
        });


    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
