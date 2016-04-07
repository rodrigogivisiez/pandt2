package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.models.LeaderboardRecord;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 22/3/2016.
 */
public class SingleGameLeaderBoardLogic extends LogicAbstract {

    private LeaderBoardScene _scene;
    private Game _game;
    private ArrayList<LeaderboardRecord> _records;

    public SingleGameLeaderBoardLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _services.getChat().hide();
        _game = (Game) objs[0];
        _scene = new LeaderBoardScene(services, screen);
        _scene.showGameLeaderboard(_game);
        init();
    }

    @Override
    public void onShow() {
        super.onShow();
        Threadings.setContinuousRenderLock(true);
    }

    private void init(){
        _services.getDatabase().getLeaderBoardAndStreak(_game, 200, new DatabaseListener<ArrayList<LeaderboardRecord>>() {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                if(st == Status.SUCCESS){
                    _records = records;
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            dataReady();
                        }
                    });
                }
            }
        });
    }

    private void dataReady(){
        LeaderboardFiller.fillEmptyRecords(_records);
        _scene.leaderboardDataLoaded(_game, _records);

        boolean found = false;
        int i = 0;
        for(LeaderboardRecord record : _records){
            if(record.containUser(_services.getProfile().getUserId())){
                found = true;
                //_scene.scrollToRecord(_game, i);
                break;
            }
            i++;
        }

        _scene.setMascots(found ? LeaderBoardScene.MascotType.HAPPY : LeaderBoardScene.MascotType.BORING);

        _scene.hideLoading(_game);
    }

    @Override
    public void onHide() {
        super.onHide();
        _services.getChat().show();
        Threadings.setContinuousRenderLock(false);
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
