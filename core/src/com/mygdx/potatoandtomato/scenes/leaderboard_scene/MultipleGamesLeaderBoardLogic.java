package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.Status;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.common.models.LeaderboardRecord;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 14/3/2016.
 */
public class MultipleGamesLeaderBoardLogic extends LogicAbstract {

    private LeaderBoardScene _scene;
    private ArrayList<Game> _games;
    private int _current;
    private HashMap<String, Boolean> _loadedGameAbbrs;

    public MultipleGamesLeaderBoardLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new LeaderBoardScene(_services, _screen);
        _games = new ArrayList<Game>();
        _loadedGameAbbrs = new HashMap<String, Boolean>();
        setListeners();
        init();
    }

    @Override
    public void onShow() {
        super.onShow();
        Threadings.setContinuousRenderLock(true);
    }

    @Override
    public void onHide() {
        super.onHide();
        Threadings.setContinuousRenderLock(false);
    }

    private void init(){
        _services.getDatabase().getAllGames(new DatabaseListener<ArrayList<Game>>(Game.class) {
            @Override
            public void onCallback(ArrayList<Game> games, Status st) {
                if(st == Status.SUCCESS){
                    for(Game game : games){
                        if(game.hasLeaderboard()){
                            _games.add(game);
                        }
                    }
                    showGame(0);
                }
            }
        });
    }

    private void nextGame(){
        if(_current + 1 < _games.size()){
            _current++;
        }
        else{
            _current = 0;
        }
        showGame(_current);
    }

    private void showGame(int index){
        final Game game = _games.get(index);
        _scene.showGameLeaderboard(game);

        if(_loadedGameAbbrs.containsKey(game.getAbbr())){
            boolean found = _loadedGameAbbrs.get(game.getAbbr());
            _scene.setMascots(found ? LeaderBoardScene.MascotType.HAPPY : LeaderBoardScene.MascotType.BORING);
        }
        else{
            _services.getDatabase().getLeaderBoardAndStreak(game, 200, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
                @Override
                public void onCallback(final ArrayList<LeaderboardRecord> records, Status st) {
                    if(st == Status.SUCCESS){
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                LeaderboardFiller.fillEmptyRecords(records);
                                _scene.leaderboardDataLoaded(game, records);

                                boolean found = false;
                                for(LeaderboardRecord record : records){
                                    if(record.containUser(_services.getProfile().getUserId())){
                                        found = true;
                                        break;
                                    }
                                }

                                if(_games.get(_current).getAbbr().equals(game.getAbbr())){
                                    _scene.setMascots(found ? LeaderBoardScene.MascotType.HAPPY : LeaderBoardScene.MascotType.BORING);
                                }

                                _scene.hideLoading(game);
                                _loadedGameAbbrs.put(game.getAbbr(), found);
                            }
                        });
                    }
                }
            });
        }
    }


    private void setListeners(){
        _scene.getTitleLabel().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                nextGame();
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
