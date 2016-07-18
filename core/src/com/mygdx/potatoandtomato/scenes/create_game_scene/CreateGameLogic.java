package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.RunnableArgs;

import java.util.ArrayList;

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
    }

    public void getAllGames(){
        _services.getDataCaches().getGamesListCache().getData(getNewCacheListener(new RunnableArgs<ArrayList<Game>>() {
            @Override
            public void run() {
                _games = this.getFirstArg();
                for (final Game game : _games) {
                    _scene.populateGame(game, new RunnableArgs<Actor>() {
                        @Override
                        public void run() {
                            this.getFirstArg().addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    onGameClicked(game);
                                }
                            });
                        }
                    });
                }
            }
        }));
    }

    public void onGameClicked(Game game){
        if(_selectedGame == null || !_selectedGame.getAbbr().equals(game.getAbbr())){
            _selectedGame = game;
            _scene.showGameDetails(game);
        }
    }

    @Override
    public void setListeners() {
        super.setListeners();
        _scene.getCreateButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_selectedGame != null){
                    _screen.toScene(SceneEnum.PREREQUISITE, _selectedGame, PrerequisiteLogic.JoinType.CREATING);
                }
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }


    public ArrayList<Game> getGames() {
        return _games;
    }
}
