package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Game;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class CreateGameLogic extends LogicAbstract {

    CreateGameScene _scene;
    ArrayList<Game> _games;
    Game _selectedGame;

    public CreateGameLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _scene = new CreateGameScene(_services, _screen);

        getAllGames();

        _scene.getCreateButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_selectedGame != null){
                    _screen.toScene(SceneEnum.PREREQUISITE, _selectedGame, true);
                }
            }
        });

    }

    public void getAllGames(){
        _services.getDatabase().getAllGames(new DatabaseListener<ArrayList<Game>>(Game.class) {
            @Override
            public void onCallback(ArrayList<Game> obj, Status st) {
                if(st == Status.SUCCESS) {
                    _games = obj;
                    for(final Game game : _games){
                        Actor actor = _scene.populateGame(game);
                        actor.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                onGameClicked(game);
                            }
                        });
                    }
                    _scene.populateGame(null);
                }
            }
        });
    }

    public void onGameClicked(Game game){
        _selectedGame = game;
        _scene.showGameDetails(game);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }


    public ArrayList<Game> getGames() {
        return _games;
    }
}
