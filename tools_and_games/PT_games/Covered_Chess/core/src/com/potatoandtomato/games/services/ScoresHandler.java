package com.potatoandtomato.games.services;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Streak;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.games.absint.DatabaseListener;
import com.potatoandtomato.games.absint.ScoresListener;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.models.MatchHistory;
import com.potatoandtomato.games.screens.BoardLogic;
import com.potatoandtomato.games.services.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by SiongLeng on 21/3/2016.
 */
public class ScoresHandler implements Disposable{

    private GameDataController gameDataController;
    private GameCoordinator coordinator;
    private BoardLogic boardLogic;
    private Database database;
    private Texts texts;
    private Team winnerTeam, loserTeam;
    private ChessColor winnerColor, loserColor;
    private HashMap<String, ArrayList<MatchHistory>> headToHeadMatchHistories;
    private HashMap<String, ArrayList<MatchHistory>> lastMatchHistories;
    private boolean dataReady;
    private boolean disposed;
    private final int CATCH_UP_TRIGGERING_LOSE_STREAK_COUNT = 3;
    private final int EZ_WIN_TRIGGERING_TIME_LEFT = 720;
    private final int EZ_WIN_TRIGGERING_TURN_COUNT = 30;
    private final int PAWN_LEADERBOARD_TRIGGERING_RANK = 200;
    private final double KILL_STREAK_MULTIPLIER = 0.05;
    private final double CATCH_UP_MULTIPLIER = 2.5;
    private final double EASY_WIN = 50;
    private final double NORMAL_WIN = 300;
    private final double HARD_WIN = 500;
    private final double PAWN_LEADERBOARD_OPPONENT = 200;
    private final double FIRST_TIME_WIN_THIS_OPPONENT = 1000;


    public ScoresHandler(GameCoordinator coordinator, Database database, Texts texts, GameDataController gameDataController) {
        this.headToHeadMatchHistories = new HashMap<String, ArrayList<MatchHistory>>();
        this.lastMatchHistories = new HashMap<String, ArrayList<MatchHistory>>();
        this.coordinator = coordinator;
        this.gameDataController = gameDataController;
        this.database = database;
        this.texts = texts;
        populateData();
    }

    public void setBoardLogic(BoardLogic boardLogic) {
        this.boardLogic = boardLogic;
    }

    private void populateData(){
        final String userAId = coordinator.getMyTeam().getPlayersUserIds().get(0);
        final String userBId = coordinator.getEnemyTeams().get(0).getPlayersUserIds().get(0);     //only two team in this game

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ThreadsPool threadsPool = new ThreadsPool();

                final Threadings.ThreadFragment fragment1 = new Threadings.ThreadFragment();
                database.getLastMatchHistories(userAId, 5, new DatabaseListener<ArrayList<MatchHistory>>(MatchHistory.class) {
                    @Override
                    public void onCallback(ArrayList<MatchHistory> obj, Status st) {
                        if(st == Status.SUCCESS){
                            lastMatchHistories.put(userAId, obj);
                        }
                        fragment1.setFinished(true);
                    }
                });
                threadsPool.addFragment(fragment1);


                final Threadings.ThreadFragment fragment2 = new Threadings.ThreadFragment();
                database.getLastMatchHistories(userBId, 5, new DatabaseListener<ArrayList<MatchHistory>>(MatchHistory.class) {
                    @Override
                    public void onCallback(ArrayList<MatchHistory> obj, Status st) {
                        if(st == Status.SUCCESS){
                            lastMatchHistories.put(userBId, obj);
                        }
                        fragment2.setFinished(true);
                    }
                });
                threadsPool.addFragment(fragment2);

                final Threadings.ThreadFragment fragment3 = new Threadings.ThreadFragment();
                database.getHeadToHeadMatchHistories(userAId, userBId, new DatabaseListener<ArrayList<MatchHistory>>(MatchHistory.class) {
                    @Override
                    public void onCallback(ArrayList<MatchHistory> obj, Status st) {
                        if (st == Status.SUCCESS) {
                            headToHeadMatchHistories.put(userAId, obj);
                        }
                        fragment3.setFinished(true);
                    }
                });
                threadsPool.addFragment(fragment3);

                final Threadings.ThreadFragment fragment4 = new Threadings.ThreadFragment();
                database.getHeadToHeadMatchHistories(userBId, userAId, new DatabaseListener<ArrayList<MatchHistory>>(MatchHistory.class) {
                    @Override
                    public void onCallback(ArrayList<MatchHistory> obj, Status st) {
                        if (st == Status.SUCCESS) {
                            headToHeadMatchHistories.put(userBId, obj);
                        }
                        fragment4.setFinished(true);
                    }
                });
                threadsPool.addFragment(fragment4);

                while (!threadsPool.allFinished()){
                    if(disposed) return;
                    Threadings.sleep(300);
                }

                dataReady = true;
            }
        });
    }

    public void setIsMeWin(boolean won) {
        if(won){
            this.winnerTeam = coordinator.getMyTeam();
            this.loserTeam = coordinator.getEnemyTeams().get(0);        //only two team in this game
            winnerColor = gameDataController.getMyChessColor();
            loserColor = gameDataController.getEnemyChessColor();
        }
        else{
            this.loserTeam = coordinator.getMyTeam();
            this.winnerTeam = coordinator.getEnemyTeams().get(0);        //only two team in this game
            winnerColor = gameDataController.getEnemyChessColor();
            loserColor = gameDataController.getMyChessColor();
        }

        if(this.winnerTeam.getPlayersUserIds().size() > 0 && this.loserTeam.getPlayersUserIds().size() > 0){
            database.saveMatchHistory(this.winnerTeam.getPlayersUserIds().get(0), this.loserTeam.getPlayersUserIds().get(0), null);
        }
    }

    public void process(final ScoresListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (!dataReady){
                    if(disposed) return;
                    Threadings.sleep(300);
                }

                ArrayList<ScoreDetails> scoreDetails = new ArrayList<ScoreDetails>();

                boolean canAddStreak = checkWinSituation(scoreDetails);
                checkOtherSpecialCases(scoreDetails);
                double multiply = getMultiply(scoreDetails, canAddStreak);

                for(ScoreDetails scoreDetail : scoreDetails){
                    if(scoreDetail.isAddOrMultiply()){
                        scoreDetail.setValue(Math.round(scoreDetail.getValue() * multiply));
                    }
                }

                listener.onCallBack(getWinnerResult(scoreDetails), getLoser());

            }
        });
    }

    private double getMultiply(ArrayList<ScoreDetails> scoreDetails, boolean canAddStreak){
        double result = 1f;

        ///////////////////////////////
        ///check in losing streak start
        ////////////////////////////////
        boolean isInLosingStreak = false;

        ArrayList<MatchHistory> winnerLastMatchHistories = lastMatchHistories.get(getWinnerUserId());

        if(winnerLastMatchHistories != null && winnerLastMatchHistories.size() >= CATCH_UP_TRIGGERING_LOSE_STREAK_COUNT){
            Collections.reverse(winnerLastMatchHistories);
            isInLosingStreak = true;
            for(int i = 0; i < CATCH_UP_TRIGGERING_LOSE_STREAK_COUNT; i++){
                if(winnerLastMatchHistories.get(i).isWon()){
                    isInLosingStreak = false;
                    break;
                }
            }
        }

        if(isInLosingStreak){
            result = CATCH_UP_MULTIPLIER;
            scoreDetails.add(0, new ScoreDetails(result, texts.timeToCatchUp() , false, true));
            return result;
        }

       //----------------------------------------------------------------------------------------------------------------------//

        /////////////////////////////////////
        ///check enemy streak and my streak
        ////////////////////////////////////
        Streak winnerStreak = this.winnerTeam.getLeaderboardRecord().getStreak().clone();
        Streak loserStreak = this.loserTeam.getLeaderboardRecord().getStreak().clone();
        if(canAddStreak ){
            winnerStreak.addStreakCount();
        }
        loserStreak.addStreakCount();
        int winnerStreakCount = 0, loserStreakCount = 0;

        if(winnerStreak.hasValidStreak()){
            winnerStreakCount = winnerStreak.getStreakCount();
        }

        if(loserStreak.hasValidStreak()){
            loserStreakCount = loserStreak.getStreakCount();
        }

        if(winnerStreakCount != 0 || loserStreakCount != 0){
            if(loserStreakCount > winnerStreakCount){
                result = 1 + (loserStreakCount * KILL_STREAK_MULTIPLIER);
                scoreDetails.add(0, new ScoreDetails(result, String.format(texts.breakEnemyXWinStreak(), loserStreakCount), false, true));
            }
            else{
                if(canAddStreak){
                    result = 1 + (winnerStreakCount * KILL_STREAK_MULTIPLIER);
                    scoreDetails.add(0, new ScoreDetails(result, String.format(texts.xWinStreak(), winnerStreakCount) , false, true));
                }
            }
        }

        return result;
    }

    private boolean checkWinSituation(ArrayList<ScoreDetails> scoreDetails){
        double baseScore;
        boolean canAddStreak = false;
        if(boardLogic.getBoardModel().isCrackHappened()){       //hard fought win
            baseScore = HARD_WIN;
            scoreDetails.add(new ScoreDetails(baseScore, texts.hardFoughtWin(), true, false));
            canAddStreak = true;
        }
        else if(boardLogic.getGraveyardLogic().getGraveModel().getLeftTimeInt(winnerColor) >= EZ_WIN_TRIGGERING_TIME_LEFT    //easy win
                || boardLogic.getBoardModel().getAccTurnCount() <= EZ_WIN_TRIGGERING_TURN_COUNT){
            baseScore = EASY_WIN;
            scoreDetails.add(new ScoreDetails(baseScore, texts.easyWin(), true, false));
        }
        else{
            baseScore = NORMAL_WIN;
            canAddStreak = true;
            scoreDetails.add(new ScoreDetails(baseScore, texts.normalWin(), true, false));
        }

        return canAddStreak;
    }

    private void checkOtherSpecialCases(ArrayList<ScoreDetails> scoreDetails){
        /////////////////////////////
        //first time win this player
        /////////////////////////////
        ArrayList<MatchHistory> headToHeadHistories = headToHeadMatchHistories.get(getWinnerUserId());

        boolean foundWin = false;
        if(headToHeadHistories != null && headToHeadHistories.size() > 0){
            for(MatchHistory history : headToHeadHistories){
                if(history.isWon()){
                    foundWin = true;
                    break;
                }
            }
        }

        if(!foundWin){
            scoreDetails.add(new ScoreDetails(FIRST_TIME_WIN_THIS_OPPONENT, texts.firstTimeWinPlayer(), true, false));
        }

        //------------------------------------------------------------------------------------------------------//

        /////////////////////////////
        //loser is in leaderboard
        /////////////////////////////
        boolean loserInLeaderboard = false;

        if(loserTeam.getRank() <= PAWN_LEADERBOARD_TRIGGERING_RANK){
            loserInLeaderboard = true;
        }

        if(loserInLeaderboard){
            scoreDetails.add(new ScoreDetails(PAWN_LEADERBOARD_OPPONENT, texts.pawnLeaderboardPlayer(), true, false));
        }

    }


    private HashMap<Team, ArrayList<ScoreDetails>> getWinnerResult(ArrayList<ScoreDetails> scoreDetails){
        HashMap<Team, ArrayList<ScoreDetails>> result = new HashMap<Team, ArrayList<ScoreDetails>>();
        result.put(winnerTeam, scoreDetails);
        return result;
    }

    private ArrayList<Team> getLoser(){
        ArrayList<Team> loser = new ArrayList<Team>();
        loser.add(this.loserTeam);
        return loser;
    }

    private String getWinnerUserId(){
        return this.winnerTeam.getPlayersUserIds().get(0);
    }

    private String getLoserUserId(){
        return this.loserTeam.getPlayersUserIds().get(0);
    }


    @Override
    public void dispose() {
        disposed = true;
    }

}
