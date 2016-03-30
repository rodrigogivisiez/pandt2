package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.helpers.services.Confirm;
import com.mygdx.potatoandtomato.helpers.utils.OneTimeRunnable;
import com.mygdx.potatoandtomato.helpers.utils.Pair;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.ScoreDetails;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 8/3/2016.
 */
public class EndGameLeaderBoardLogic extends LogicAbstract {

    private LeaderBoardScene _scene;
    private int _leaderboardSize = 200;
    private double _score;
    private int _scoreToAdd;
    private SafeThread _addScoreThread;
    private boolean _addScoreFinished;
    private EndGameData _endGameData;
    private Game _game;
    private Room _room;
    private ArrayList<Player> _myTeam;
    private ArrayList<ScoreDetails> _scoreDetails;
    private ArrayList<LeaderboardRecord> _records;
    private Pair<Integer, LeaderboardRecord> _myRankRecordPair;
    private LeaderboardRecord _myLeaderboardRecord;
    private boolean _leaderboardReady, _streakResetted;
    private OneTimeRunnable _addScoreOnePartDoneRunnable;

    public EndGameLeaderBoardLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new LeaderBoardScene(services, screen);

        _endGameData = (EndGameData) objs[0];
        _myTeam = (ArrayList<Player>) objs[1];
        _game = _endGameData.getRoom().getGame();
        _room = _endGameData.getRoom();
        _scene.showGameLeaderboard(_game);
        getLeaderBoardAndMyCurrentRank();
    }

    @Override
    public void onShow() {
        super.onShow();

        Threadings.setContinuousRenderLock(true);

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (!_leaderboardReady) {
                    Threadings.sleep(1000);
                }

                if(_streakResetted){
                    _myLeaderboardRecord.resetStreak();
                }

                _scoreDetails = _endGameData.getEndGameResult().getScoreDetails();

                if (_endGameData.getEndGameResult().isWon()) {
                    winnerHandling();
                } else {
                    loserHandling();
                }
            }
        });
    }

    @Override
    public void onHide() {
        super.onHide();
        Threadings.setContinuousRenderLock(false);
    }

    public void loserHandling(){
        final int currentRank = _myRankRecordPair.getFirst();
        final LeaderboardRecord leaderboardRecord = _myRankRecordPair.getSecond();

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.leaderboardDataLoaded(_game, _records);
                _services.getSoundsWrapper().playSoundEffect(Sounds.Name.LOSE);
                Threadings.delay(8000, new Runnable() {
                    @Override
                    public void run() {
                        _services.getSoundsWrapper().playThemeMusic();
                    }
                });

                _scene.setMascots(LeaderBoardScene.MascotType.BORING);
                if(currentRank != _leaderboardSize || leaderboardRecord.getScore() != 0){        //lose streak
                    _scene.scrollToRecord(_game, currentRank);
                    Threadings.delay(50, new Runnable() {
                        @Override
                        public void run() {
                            _scene.hideLoading(_game);
                            if(!_endGameData.getEndGameResult().isEmpty()){
                                Threadings.delay(2000, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(_endGameData.getEndGameResult().isStreakEnabled() && leaderboardRecord.getStreak().hasValidStreak()){
                                            _scene.loseStreakAnimate(_game, currentRank);
                                            _scene.setMascots(LeaderBoardScene.MascotType.FAILED);

                                            if(leaderboardRecord.getStreak().canRevive()){
                                                Threadings.delay(2000, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        _services.getConfirm().show("Revive your streak?", Confirm.Type.YESNO, new ConfirmResultListener() {
                                                            @Override
                                                            public void onResult(Result result) {
                                                                if(result == Result.YES){
                                                                    _scene.reviveStreakAnimate(_game, currentRank);
                                                                    _scene.setMascots(LeaderBoardScene.MascotType.HAPPY);
                                                                    _services.getDatabase().streakRevive(getMyTeamUserIds(), _room, null);
                                                                }
                                                                else{
                                                                    _scene.setMascots(LeaderBoardScene.MascotType.CRY);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    _scene.hideLoading(_game);
                }
            }
        });
    }

    public void winnerHandling(){
        final int currentRank = _myRankRecordPair.getFirst();

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.leaderboardDataLoaded(_game, _records);
                if (_scoreDetails.size() > 0 && !_endGameData.getEndGameResult().isEmpty()) {
                    _scene.populateAnimateTable(_game, _myLeaderboardRecord, currentRank, currentRank == _leaderboardSize);
                    Threadings.delay(10, new Runnable() {
                        @Override
                        public void run() {
                            _services.getSoundsWrapper().playSoundEffect(Sounds.Name.WIN);
                            _scene.scrollToRecord(_game, currentRank);
                            Threadings.delay(50, new Runnable() {
                                @Override
                                public void run() {
                                    _scene.hideLoading(_game);
                                    _scene.setMascots(LeaderBoardScene.MascotType.ANTICIPATE);
                                    Threadings.delay(2000, new Runnable() {
                                        @Override
                                        public void run() {
                                            _score = _myLeaderboardRecord.getScore();
                                            addScoresRecur(0, new Runnable() {
                                                @Override
                                                public void run() {
                                                    Threadings.delay(1000, new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            _myLeaderboardRecord.addScoresToRecord(_scoreDetails);
                                                            final int finalRank = getAfterRanking();
                                                            moveUpRankAnimation(currentRank, finalRank, new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Threadings.delay(1000, new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                _scene.setMascots(LeaderBoardScene.MascotType.HAPPY);
                                                                                if(getStreakToAdd() > 0){
                                                                                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.STREAK);
                                                                                    _myLeaderboardRecord.getStreak().addStreakCount(getStreakToAdd());
                                                                                    _scene.invalidateNameStreakTable(_game, _myLeaderboardRecord,
                                                                                            finalRank, true);
                                                                                }
                                                                                _services.getSoundsWrapper().playThemeMusic();
                                                                            }
                                                                        });
                                                                    }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    _scene.hideLoading(_game);
                    _services.getSoundsWrapper().playThemeMusic();
                }
            }
        });
    }

    private void getLeaderBoardAndMyCurrentRank(){
        _services.getDatabase().getLeaderBoardAndStreak(_game, _leaderboardSize, new DatabaseListener<ArrayList<LeaderboardRecord>>() {
            @Override
            public void onCallback(final ArrayList<LeaderboardRecord> records, Status st) {
                if (st == Status.SUCCESS) {
                    _records = records;
                    LeaderboardFiller.fillEmptyRecords(_records);
                    _myRankRecordPair = generateRecordModel();
                    _myLeaderboardRecord = _myRankRecordPair.getSecond();
                    if (_game.getLeaderboardTypeEnum() == LeaderboardType.Accumulate && _myRankRecordPair.getFirst() == _leaderboardSize) {
                        _services.getDatabase().getAccLeaderBoardRecordAndStreak(_room, getMyTeamUserIds(), new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
                            @Override
                            public void onCallback(LeaderboardRecord obj, Status st) {
                                if (st == Status.SUCCESS && obj != null) {
                                    _records.add(obj);
                                    _myRankRecordPair = generateRecordModel();
                                    _myLeaderboardRecord = _myRankRecordPair.getSecond();
                                }
                                _leaderboardReady = true;
                            }
                        });
                    }
                    else{
                        _leaderboardReady = true;
                    }

                }
            }
        });
    }

    public void moveUpRankAnimation(int fromRank, int toRank, Runnable onFinish){
        _scene.moveUpRank(_game, toRank, fromRank, _myLeaderboardRecord, onFinish);
    }

    public void addScoresRecur(final int index, final Runnable onFinish){
        _services.getSoundsWrapper().playSoundEffect(Sounds.Name.SCORE_APPEAR);
        _scene.addScore(_scoreDetails.get(index), new Runnable() {
            @Override
            public void run() {
                if(_scoreDetails.get(index).isAddOrMultiply()){
                    _scoreToAdd += _scoreDetails.get(index).getValue();
                }

                _addScoreOnePartDoneRunnable = new OneTimeRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if(index + 1 <= _scoreDetails.size() - 1){
                            addScoresRecur(index + 1, onFinish);
                        }
                        else{
                            _addScoreFinished = true;
                        }
                    }
                });

                runAddScoreThread(onFinish);

            }
        });
    }

    private void runAddScoreThread(final Runnable onFinish){
        if(_addScoreThread == null || _addScoreThread.isKilled()){
            _addScoreThread = new SafeThread();
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    boolean stoppedSound = true;

                    while (_scoreToAdd > 0 || !_addScoreFinished) {

                        Threadings.sleep(30);
                        int adding = 0;
                        if (_scoreToAdd > 1000) {
                            adding = MathUtils.random(100, 200);
                        } else if (_scoreToAdd > 500) {
                            adding = MathUtils.random(100, 200);
                        } else if (_scoreToAdd > 100) {
                            adding = MathUtils.random(10, 50);
                        } else if (_scoreToAdd > 20) {
                            adding = MathUtils.random(1, 10);
                        } else{
                            if(_scoreToAdd > 0){
                                adding = 1;
                            }
                        }

                        if(stoppedSound && adding > 0){
                            _services.getSoundsWrapper().playSoundEffectLoop(Sounds.Name.ADDING_SCORE);
                            stoppedSound = false;
                        }

                        _scoreToAdd -= adding;
                        _score += adding;
                        _scene.setAnimatingScore(_score);

                        if(_scoreToAdd == 0) {
                            _services.getSoundsWrapper().stopSoundEffectLoop(Sounds.Name.ADDING_SCORE);
                            stoppedSound = true;
                            if(!_addScoreOnePartDoneRunnable.isRunFinish()){
                                _addScoreOnePartDoneRunnable.run();
                            }
                            Threadings.sleep(300);
                        }
                    }
                    _addScoreThread.kill();
                    onFinish.run();

                }
            });
        }
    }

    private int getStreakToAdd(){
        int toAdd = 0;
        for(ScoreDetails details : _scoreDetails){
            if(details.canAddStreak()) {
                toAdd++;
            }
        }
        return toAdd;
    }

    private Pair<Integer, LeaderboardRecord> generateRecordModel(){
        if(_game.getLeaderboardTypeEnum() == LeaderboardType.Accumulate){
            ArrayList<String> playerIds = getMyTeamUserIds();
            int i = 0;
            for(LeaderboardRecord record : _records){
                if(record.usersMatched(playerIds)){
                    return new Pair<Integer, LeaderboardRecord>(i, record);
                }
                i++;
            }
        }
        return new Pair<Integer, LeaderboardRecord>(_leaderboardSize, new LeaderboardRecord(_myTeam));
    }

    private ArrayList<String> getMyTeamUserIds(){
        ArrayList<String> playerIds = new ArrayList<String>();
        for(Player p : _myTeam){
            playerIds.add(p.getUserId());
        }
        return playerIds;
    }

    public int getAfterRanking(){
        int i = 0;
        if(_game.getLeaderboardTypeEnum() == LeaderboardType.Accumulate){
            boolean foundMine = false;
            for(LeaderboardRecord record : _records){
                if(record.usersMatched(_myLeaderboardRecord.getUserIds())) foundMine = true;

                if(record.getScore() < _myLeaderboardRecord.getScore()){
                    return foundMine ? i -1 : i;
                }
                i++;
            }
        }
        else if(_game.getLeaderboardTypeEnum() == LeaderboardType.Normal){
            for(LeaderboardRecord record : _records){
                if(record.getScore() < _myLeaderboardRecord.getScore()){
                    return i;
                }
                i++;
            }
        }
        return i;
    }

    public int getUpperRankingDifferencePercent(){
        int afterRank = getAfterRanking();
        afterRank = afterRank - 1;
        while (afterRank >= 0){
            LeaderboardRecord upperRank = _records.get(afterRank);
            if(upperRank.getScore() != _myLeaderboardRecord.getScore()){
                double score1 = upperRank.getScore();
                double score2 = _myLeaderboardRecord.getScore();
                double result = ((score1 - score2) / score1) * 100;
                return (int) result;
            }
            else{
                afterRank--;
            }
        }
        return 100;
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    public LeaderboardRecord getMyLeaderboardRecord() {
        return _myLeaderboardRecord;
    }

    public void setStreakResetted(boolean _streakResetted) {
        this._streakResetted = _streakResetted;
    }
}
