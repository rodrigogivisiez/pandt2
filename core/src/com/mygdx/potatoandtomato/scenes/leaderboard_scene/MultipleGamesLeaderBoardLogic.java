package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
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
    private HashMap<String, Boolean> _loadedGameAbbrToFoundInLeaderboardMap;

    public MultipleGamesLeaderBoardLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new LeaderBoardScene(_services, _screen);
        _scene.showNextPrevContainer();
        _games = new ArrayList<Game>();
        _loadedGameAbbrToFoundInLeaderboardMap = new HashMap<String, Boolean>();
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
        _services.getDataCaches().getGamesListCache().getData(new CacheListener<ArrayList<Game>>() {
            @Override
            public void onResult(ArrayList<Game> games) {
                for (Game game : games) {
                    if (game.hasLeaderboard()) {
                        _games.add(game);
                    }
                }
                showGame(0);
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

    private void prevGame(){
        if(_current - 1 >= 0){
            _current--;
        }
        else{
            _current = _games.size() - 1;
        }
        showGame(_current);
    }


    private void showGame(int index){
        if(_games.size() == 0) return;

        final Game game = _games.get(index);
        _scene.showGameLeaderboard(game);

        if(_loadedGameAbbrToFoundInLeaderboardMap.containsKey(game.getAbbr())){
            Boolean found = _loadedGameAbbrToFoundInLeaderboardMap.get(game.getAbbr());
            if(found != null) _scene.setMascots(found ? LeaderBoardScene.MascotType.HAPPY : LeaderBoardScene.MascotType.BORING);
        }
        else{
            _loadedGameAbbrToFoundInLeaderboardMap.put(game.getAbbr(), null);
            _services.getDatabase().getLeaderBoardAndStreak(game, Global.LEADERBOARD_COUNT, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
                @Override
                public void onCallback(final ArrayList<LeaderboardRecord> records, Status st) {
                    if(st == Status.SUCCESS){
                        if(isDisposing()) return;

                        final boolean found = LeaderboardHelper.isMyRecordInLeaderboard(records, _services.getProfile().getUserId());

                        final Runnable populateRunnable = new Runnable() {
                            @Override
                            public void run() {
                                LeaderboardHelper.fillEmptyRecords(records);
                                _scene.leaderboardDataLoaded(game, records);

                                if(_games.get(_current).getAbbr().equals(game.getAbbr())){
                                    _scene.setMascots(found ? LeaderBoardScene.MascotType.HAPPY : LeaderBoardScene.MascotType.BORING);
                                }

                                _scene.hideLoading(game);
                                _loadedGameAbbrToFoundInLeaderboardMap.put(game.getAbbr(), found);
                            }
                        };

                        if(!found){
                            _services.getDatabase().getUserHighestLeaderBoardRecordAndStreak(game,
                                    _services.getProfile().getUserId(), new DatabaseListener<LeaderboardRecord>() {
                                        @Override
                                        public void onCallback(LeaderboardRecord record, Status st) {
                                            if (st == Status.SUCCESS && record != null && record.getScore() != 0) {
                                                records.add(record);
                                            }
                                            populateRunnable.run();
                                        }
                                    }
                            );
                        }
                        else{
                            populateRunnable.run();
                        }


                    }
                }
            });
        }
    }


    @Override
    public void setListeners(){
        super.setListeners();
        _scene.getNextButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextGame();
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.CLICK_BUTTON);
            }
        });

        _scene.getPrevButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prevGame();
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.CLICK_BUTTON);
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
