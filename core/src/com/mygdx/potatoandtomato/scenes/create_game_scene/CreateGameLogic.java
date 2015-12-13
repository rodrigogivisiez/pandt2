package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Assets;
import com.mygdx.potatoandtomato.models.Game;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class CreateGameLogic extends LogicAbstract {

    CreateGameScene _scene;
    ArrayList<Game> _games;

    public CreateGameLogic(PTScreen screen, Assets assets) {
        super(screen, assets);
        _scene = new CreateGameScene(_assets);

        getAllGames();
    }

    public void getAllGames(){
        _assets.getDatabase().getAllGames(new DatabaseListener<ArrayList<Game>>(Game.class) {
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
                    _scene.populateGame(null);
                }
            }
        });
    }

    public void onGameClicked(Game game){
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
