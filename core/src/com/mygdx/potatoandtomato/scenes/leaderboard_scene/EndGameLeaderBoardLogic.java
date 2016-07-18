package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.potatoandtomato.common.absints.CoinListener;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.utils.Scores;
import com.potatoandtomato.common.models.*;
import com.potatoandtomato.common.utils.*;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by SiongLeng on 8/3/2016.
 */
public class EndGameLeaderBoardLogic extends LogicAbstract {

    private LeaderBoardScene _scene;
    private int _leaderboardSize = Global.LEADERBOARD_COUNT;
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
    private boolean _leaderboardReady, _scoreUpdated;
    private SafeThread safeThread;
    private OneTimeRunnable _addScoreOnePartDoneRunnable;
    private boolean handled;
    private boolean hasChanceToReviveStreak;

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

        if(hasChanceToReviveStreak){
            reviveStreakProcess();
        }

        if(handled) return;
        handled = true;

        Threadings.setContinuousRenderLock(true);
        _scene.setLoadingToUpdatingScores();

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                safeThread = new SafeThread();
                while (!_leaderboardReady) {
                    Threadings.sleep(200);
                    if(safeThread.isKilled()) return;
                }

                if((_endGameData.getEndGameResult() == null) ||
                        (_endGameData.getEndGameResult().getWinnersScoreDetails().size() == 0
                        && _endGameData.getEndGameResult().getLoserTeams().size() == 0)){
                    noHandling();
                }
                else{
                    _scoreDetails = _endGameData.getEndGameResult().getMyTeamWinnerScoreDetails(_services.getProfile().getUserId());
                    processOtherTeamScoresAndStreaks();

                    if (_endGameData.getEndGameResult().isWon()) {
                        winnerHandling();
                    } else {
                        loserHandling();
                    }
                }


            }
        });
    }

    @Override
    public void onHide() {
        super.onHide();
        Threadings.setContinuousRenderLock(false);
        _services.getCoins().hideCoinMachine();
    }

    @Override
    public void onQuit(OnQuitListener listener) {
        if(hasChanceToReviveStreak){
            listener.onResult(OnQuitListener.Result.NO);
        }
        else{
            super.onQuit(listener);
            _services.getSoundsPlayer().playMusic(Sounds.Name.THEME_MUSIC);
        }
    }


    public void noHandling(){
        _scene.leaderboardDataLoaded(_game, _records);
        _scene.hideLoading(_game);
        _scene.setMascots(LeaderBoardScene.MascotType.BORING);

        if(_endGameData.getEndGameResult() != null && _endGameData.getEndGameResult().isAbandon()){
            _scene.setLeaderboardScrollPaneScrollable(_game, false);
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    final int myCurrentRank = _myRankRecordPair.getFirst();
                    if(_room.getGame().isStreakEnabled() && _endGameData.getEndGameResult().isWillLoseStreak()) {
                        _scene.loseStreakAnimatePhaseOne(_game, myCurrentRank);
                        Threadings.delay(1000, new Runnable() {
                            @Override
                            public void run() {
                                _scene.loseStreakAnimatePhaseTwo(_game, myCurrentRank);
                                _scene.setMascots(LeaderBoardScene.MascotType.CRY);
                            }
                        });
                        _scene.setLeaderboardScrollPaneScrollable(_game, true);
                        _services.getDatabase().resetUserStreak(_game, _services.getProfile().getUserId(), null);
                    }
                }
            });
        }
    }


    public void loserHandling(){
        hasChanceToReviveStreak = true;
        final int myCurrentRank = _myRankRecordPair.getFirst();
        final LeaderboardRecord myLeaderboardRecord = _myRankRecordPair.getSecond();

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _scene.setLeaderboardScrollPaneScrollable(_game, false);
                _scene.leaderboardDataLoaded(_game, _records);

                if(_records.size() > _leaderboardSize){     //appended not in list record in leaderboard also
                    _scene.changeRecordTableToUnknownRank(_game, _leaderboardSize);
                }

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.LOSE);

                _scene.setMascots(LeaderBoardScene.MascotType.BORING);
                if(myCurrentRank != _leaderboardSize || myLeaderboardRecord.getScore() != 0){        //lose streak
                    _scene.scrollToRecord(_game, myCurrentRank);
                    Threadings.delay(50, new Runnable() {
                        @Override
                        public void run() {
                            _scene.hideLoading(_game);
                            if(!_endGameData.getEndGameResult().isEmpty()){
                                Threadings.delay(2000, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(_room.getGame().isStreakEnabled() && myLeaderboardRecord.getStreak().hasValidStreak()){
                                            _scene.loseStreakAnimatePhaseOne(_game, myCurrentRank);
                                            _scene.setLeaderboardScrollPaneScrollable(_game, true);
                                            Threadings.delay(4000, new Runnable() {
                                                @Override
                                                public void run() {
                                                    reviveStreakProcess();
                                                }
                                            });
                                        }
                                        else{
                                            hasChanceToReviveStreak = false;
                                        }
                                    }
                                });
                            }
                            else{
                                hasChanceToReviveStreak = false;
                            }
                        }
                    });
                }
                else{
                    hasChanceToReviveStreak = false;
                    _scene.hideLoading(_game);
                    _scene.setLeaderboardScrollPaneScrollable(_game, true);
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
                    _scene.setLeaderboardScrollPaneScrollable(_game, false);
                    _scene.populateAnimateTable(_game, _myLeaderboardRecord, currentRank, currentRank == _leaderboardSize);
                    Threadings.delay(10, new Runnable() {
                        @Override
                        public void run() {
                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.WIN);
                            _scene.scrollToRecord(_game, currentRank);
                            Threadings.delay(50, new Runnable() {
                                @Override
                                public void run() {
                                    _scene.hideLoading(_game);
                                    _scene.setMascots(LeaderBoardScene.MascotType.ANTICIPATE);
                                    Threadings.delay(2000, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (_room.getGame().getLeaderboardTypeEnum() == LeaderboardType.Accumulate) {
                                                _score = _myLeaderboardRecord.getScore();
                                            }
                                            addScoresRecur(0, new Runnable() {
                                                @Override
                                                public void run() {
                                                    _scene.animateFakeLabelIfExist(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Threadings.delay(1000, new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    boolean achieveSomething = true;

                                                                    if (_room.getGame().getLeaderboardTypeEnum() == LeaderboardType.Accumulate) {
                                                                        _myLeaderboardRecord.addScoresToRecord(_scoreDetails);
                                                                    }
                                                                    else if(_room.getGame().getLeaderboardTypeEnum() == LeaderboardType.Normal){
                                                                        if(Scores.getTotalScoresInScoresArray(_scoreDetails) > _myLeaderboardRecord.getScore()){
                                                                            _myLeaderboardRecord.setScore(0);
                                                                            _myLeaderboardRecord.addScoresToRecord(_scoreDetails);
                                                                        }
                                                                        else{
                                                                            achieveSomething = false;
                                                                        }
                                                                    }

                                                                    int finalRank = getAfterRanking();
                                                                    if(finalRank > currentRank){        //can nvr be moving downward
                                                                        finalRank = currentRank;
                                                                    }

                                                                    final int finalRank1 = finalRank;
                                                                    final boolean finalAchieveSomething = achieveSomething;
                                                                    moveUpRankAnimation(currentRank, finalRank, achieveSomething, new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Threadings.delay(500, new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (getStreakToAdd() > 0) {
                                                                                        _services.getSoundsPlayer().playSoundEffect(Sounds.Name.STREAK);
                                                                                        _myLeaderboardRecord.getStreak().addStreak(getStreakToAdd());
                                                                                        _scene.invalidateNameStreakTable(_game, _myLeaderboardRecord,
                                                                                                finalRank1, true);
                                                                                    }

                                                                                    _scene.setLeaderboardScrollPaneScrollable(_game, true);

                                                                                    if (finalRank1 == _leaderboardSize) {  //no enough pt to move up any rank
                                                                                        _scene.setMascots(LeaderBoardScene.MascotType.BORING);
                                                                                        _scene.changeRecordTableToUnknownRank(_game, finalRank1);
                                                                                    }
                                                                                    else if(!finalAchieveSomething){
                                                                                        _scene.setMascots(LeaderBoardScene.MascotType.BORING);
                                                                                    }
                                                                                    else {
                                                                                        _scene.setMascots(LeaderBoardScene.MascotType.HAPPY);
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                    }
                                                }
                                            );
                                            }
                                        }
                                    );
                                    }
                                }
                            );
                            }
                        }
                    );
                    }else {
                        _scene.hideLoading(_game);
                    }
            }
        });
    }

    public void getLeaderBoardAndMyCurrentRank(){
        _services.getDatabase().getLeaderBoardAndStreak(_game, _leaderboardSize, new DatabaseListener<ArrayList<LeaderboardRecord>>() {
            @Override
            public void onCallback(final ArrayList<LeaderboardRecord> records, Status st) {
                if (st == Status.SUCCESS) {
                    _records = records;
                    LeaderboardHelper.fillEmptyRecords(_records);
                    onRecordsChanged();
                    if (_myRankRecordPair.getFirst() == _leaderboardSize) {
                        _services.getDatabase().getTeamHighestLeaderBoardRecordAndStreak(_room.getGame(), getMyTeamUserIds(),
                                new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
                                    @Override
                                    public void onCallback(LeaderboardRecord obj, Status st) {
                                        if (st == Status.SUCCESS && obj != null) {
                                            _records.add(obj);
                                            onRecordsChanged();
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

    public void moveUpRankAnimation(int fromRank, int toRank, boolean starAnimate, Runnable onFinish){
        _scene.moveUpRank(_game, toRank, fromRank, _myLeaderboardRecord, _leaderboardSize, starAnimate, onFinish);
    }

    public void addScoresRecur(final int index, final Runnable onFinish){
        if(!_scoreDetails.get(index).isAddOrMultiply() && _scoreDetails.get(index).getValue() == 1){
            if(index + 1 <= _scoreDetails.size() - 1){
                addScoresRecur(index + 1, onFinish);
            }
            return;
        }
        _services.getSoundsPlayer().playSoundEffect(Sounds.Name.SCORE_APPEAR);

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
                        if (_scoreToAdd > 10000) {
                            adding = _scoreToAdd / 10;
                        }
                        else if (_scoreToAdd > 1000) {
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
                            _services.getSoundsPlayer().playSoundEffectLoop(Sounds.Name.ADDING_SCORE);
                            stoppedSound = false;
                        }

                        _scoreToAdd -= adding;
                        _score += adding;
                        _scene.setAnimatingScore(_score);

                        if(_scoreToAdd == 0) {
                            _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.ADDING_SCORE);
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

    public void reviveStreakProcess(){
        _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
        if(!isSceneVisible()) return;

        _services.getCoins().reset();
        final int myCurrentRank = _myRankRecordPair.getFirst();
        final LeaderboardRecord myLeaderboardRecord = _myRankRecordPair.getSecond();
        Streak streak = myLeaderboardRecord.getStreak();

        int revivingStreakCount = streak.getStreakCount();
        if(revivingStreakCount == 0){
            revivingStreakCount = streak.getBeforeStreakCount();
        }

        if(revivingStreakCount > 0){
            final String teamUserIdsString = Strings.joinArr(getMyTeamUserIds(), ",");

            Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> speechPair =
                                    _texts.getRandomMascotsSpeechAboutLostStreaks(revivingStreakCount);

            ArrayList<Pair<String, String>> userIdToNamePairs = new ArrayList();
            for(Player player : _myTeam){
                userIdToNamePairs.add(new Pair<String, String>(player.getUserId(), player.getName()));
            }

            _services.getCoins().initCoinMachine("Revive streaks", MathUtils.floor(revivingStreakCount / 2),
                    _room.getId() + "_" + _room.getRoundCounter() + "_" + teamUserIdsString + "_streaks",
                    userIdToNamePairs, false, speechPair.getFirst(), speechPair.getSecond(), "nonono");

            _services.getCoins().setCoinListener(new CoinListener() {
                @Override
                public void onEnoughCoins() {
                    hasChanceToReviveStreak = false;
                    _services.getSoundsPlayer().playSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
                    Threadings.delay(1000, new Runnable() {
                        @Override
                        public void run() {
                            _scene.reviveStreakAnimate(_game, myCurrentRank);
                            _scene.setMascots(LeaderBoardScene.MascotType.HAPPY);
                        }
                    });

                    _services.getCoins().hideCoinMachine();
                    _services.getCoins().checkMeIsCoinsCollectedDecisionMaker(new Runnable() {
                        @Override
                        public void run() {
                            _services.getRestfulApi().reviveStreak(teamUserIdsString, _services.getCoins().getCurrentUsersPutCoinsMeta(),
                                    _room.getGame().getAbbr(), _room.getId(), String.valueOf(_room.getRoundCounter()),
                                    new RestfulApiListener<String>() {
                                        @Override
                                        public void onCallback(String obj, Status st) {
                                    }
                                });
                        }
                    });
                }

                @Override
                public void onDismiss(String dismissUserId) {
                    super.onDismiss(dismissUserId);
                    hasChanceToReviveStreak = false;
                    _services.getSoundsPlayer().playSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
                    Threadings.delay(1000, new Runnable() {
                        @Override
                        public void run() {
                            _scene.setMascots(LeaderBoardScene.MascotType.CRY);
                            _scene.loseStreakAnimatePhaseTwo(_game, myCurrentRank);
                        }
                    });

                }
            });

            _services.getCoins().showCoinMachine();
        }
        else{
            hasChanceToReviveStreak = false;
        }
    }

    private int getStreakToAdd(){
        int toAdd = 0;
        for(ScoreDetails details : _scoreDetails){
            if(details.isCanAddStreak()) {
                toAdd++;
            }
        }
        return toAdd;
    }

    private Pair<Integer, LeaderboardRecord> generateRecordModel(){
        ArrayList<String> playerIds = getMyTeamUserIds();
        int i = 0;
        for(LeaderboardRecord record : _records){
            if(record.usersMatched(playerIds)){
                return new Pair<Integer, LeaderboardRecord>(i, record);
            }
            i++;
        }
        return new Pair<Integer, LeaderboardRecord>(_leaderboardSize, new LeaderboardRecord(_myTeam));
    }

    private ArrayList<String> getMyTeamUserIds(){
        ArrayList<String> playerIds = new ArrayList<String>();
        for(Player p : _myTeam){
            playerIds.add(p.getUserId());
        }
        Collections.sort(playerIds);
        return playerIds;
    }

    public int getAfterRanking(){
        int i = 0;
        boolean foundMine = false;
        for(LeaderboardRecord record : _records){
            if(record.usersMatched(_myLeaderboardRecord.getUserIds())) foundMine = true;

            if(record.getScore() < _myLeaderboardRecord.getScore()){
                return foundMine ? i -1 : i;
            }
            i++;
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

    public void processOtherTeamScoresAndStreaks(){

        HashMap<Team, ArrayList<ScoreDetails>> winnersScoreDetails = _endGameData.getEndGameResult().getWinnersScoreDetails();
        ArrayList<Team> loserTeams = _endGameData.getEndGameResult().getLoserTeams();

        ArrayList<Integer> changedRecordIndexes = new ArrayList<Integer>();

        for(Team team : winnersScoreDetails.keySet()){
            if(!team.hasUser(_services.getProfile().getUserId())){
                ArrayList<ScoreDetails> scoreDetails = winnersScoreDetails.get(team);
                int i = 0;
                if(_records.size() == 0){
                    _records.add(scoreDetailsToNewLeaderboardRecord(team, scoreDetails));
                    changedRecordIndexes.add(i);
                }

                boolean foundMatch = false;

                for(LeaderboardRecord record : _records){
                    if(record.usersMatched(team.getPlayersUserIds())){
                        if(_room.getGame().getLeaderboardTypeEnum() == LeaderboardType.Normal){
                            if(record.getScore() < Scores.getTotalScoresInScoresArray(scoreDetails)){
                                record.setScore(Scores.getTotalScoresInScoresArray(scoreDetails));
                            }
                        }
                        else if(_room.getGame().getLeaderboardTypeEnum() == LeaderboardType.Accumulate){
                            record.addScoresToRecord(scoreDetails);
                        }
                        changedRecordIndexes.add(i);
                        if(_room.getGame().isStreakEnabled()){
                            record.addStreakToRecord(scoreDetails);
                        }
                        foundMatch = true;
                    }
                    i++;
                }

                if(!foundMatch){
                    boolean added = false;
                    for(LeaderboardRecord record : _records){
                        if(record.getScore() < Scores.getTotalScoresInScoresArray(scoreDetails)){
                            added = true;
                        }
                    }
                    if(added){
                        _records.add(_records.size(), scoreDetailsToNewLeaderboardRecord(team, scoreDetails));
                        changedRecordIndexes.add(_records.size() - 1);
                    }
                    else if(!added && _records.size() < Global.LEADERBOARD_COUNT){
                        _records.add(_records.size(), scoreDetailsToNewLeaderboardRecord(team, scoreDetails));
                        changedRecordIndexes.add(_records.size() - 1);
                    }

                }

            }
        }

        if(_room.getGame().isStreakEnabled()){
            for(Team team : loserTeams){
                if(!team.hasUser(_services.getProfile().getUserId())){
                    for(LeaderboardRecord record : _records){
                        if(record.usersMatched(team.getPlayersUserIds())){
                            record.getStreak().resetStreak();
                        }
                    }
                }
            }
        }

        for(Integer index : changedRecordIndexes){
            for(int i = 0; i < index; i++){
                if(_records.get(i).getScore() < _records.get(index).getScore()){
                    LeaderboardRecord record = _records.get(index);
                    _records.remove(record);
                    _records.add(i, record);
                    break;
                }
            }
        }

        for(int i = _records.size(); i > Global.LEADERBOARD_COUNT; i--){
            _records.remove(_records.get(Global.LEADERBOARD_COUNT));
        }

        onRecordsChanged();

    }

    public LeaderboardRecord scoreDetailsToNewLeaderboardRecord(Team team, ArrayList<ScoreDetails> scoreDetails){
        LeaderboardRecord leaderboardRecord = new LeaderboardRecord();
        for(Player player : team.getPlayers()){
            leaderboardRecord.addUserName(player.getUserId(), player.getName());
            leaderboardRecord.addUserId(player.getUserId());
        }
        if(_room.getGame().isStreakEnabled()){
            leaderboardRecord.addStreakToRecord(scoreDetails);
        }
        leaderboardRecord.addScoresToRecord(scoreDetails);
        return leaderboardRecord;
    }

    public void onRecordsChanged(){
        _myRankRecordPair = generateRecordModel();
        _myLeaderboardRecord = _myRankRecordPair.getSecond();
    }

    public void checkScoreUpdated(){
        _services.getDatabase().checkScoreUpdated(_room, new DatabaseListener<Boolean>() {
            @Override
            public void onCallback(Boolean obj, Status st) {
                if(st == Status.SUCCESS){
                    _scoreUpdated = obj;
                }
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    @Override
    public void dispose() {
        super.dispose();
        if(safeThread != null) safeThread.kill();
        _services.getCoins().reset();
        _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
    }

    public LeaderboardRecord getMyLeaderboardRecord() {
        return _myLeaderboardRecord;
    }

    public void setLeaderboardRecords(ArrayList<LeaderboardRecord> _records) {
        this._records = _records;
    }

    public ArrayList<LeaderboardRecord> getLeaderboardRecords() {
        return _records;
    }
}
