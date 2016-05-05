package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.ArrayUtils;
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
        _services.getDatabase().getLeaderBoardAndStreak(_game, Global.LEADERBOARD_COUNT, new DatabaseListener<ArrayList<LeaderboardRecord>>() {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                if(st == Status.SUCCESS){
                    _records = records;
                    if(!LeaderboardHelper.isMyRecordInLeaderboard(_records, _services.getProfile().getUserId())){
                        _services.getDatabase().getUserHighestLeaderBoardRecordAndStreak(_game,
                                _services.getProfile().getUserId(), new DatabaseListener<LeaderboardRecord>() {
                                    @Override
                                    public void onCallback(LeaderboardRecord record, Status st) {
                                        if (st == Status.SUCCESS && record != null && record.getScore() != 0) {
                                            _records.add(record);
                                        }
                                        dataReady();
                                    }
                                });

                    } else{
                        dataReady();
                    }

                }
            }
        });
    }

    private void dataReady(){
        LeaderboardHelper.fillEmptyRecords(_records);
        _scene.leaderboardDataLoaded(_game, _records);

        for(int i = Global.LEADERBOARD_COUNT; i < _records.size(); i++){
            _scene.changeRecordTableToUnknownRank(_game, i);
        }

        _scene.setMascots(LeaderboardHelper.isMyRecordInLeaderboard(_records, _services.getProfile().getUserId()) ?
                                    LeaderBoardScene.MascotType.HAPPY : LeaderBoardScene.MascotType.BORING);

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
