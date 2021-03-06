package com.potatoandtomato.games.screens.scores;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.CastleState;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.time_bar.CastleLogic;
import com.potatoandtomato.games.screens.time_bar.KnightLogic;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 14/4/2016.
 */
public class ScoresLogic implements Disposable {

    private GameCoordinator gameCoordinator;
    private Services services;
    private GameModel gameModel;
    private KnightLogic knightLogic;
    private CastleLogic castleLogic;
    private HintsLogic hintsLogic;
    private ScoresActor scoresActor;
    private SafeThread safeThread;
    private ArrayList<LeaderboardRecord> leaderboardRecords;
    private int originalScores = 0;
    private final int PER_CLICK_SCORE = 1000;
    private final int PER_HINT_LEFT_SCORE = 1000;
    private final int PER_CASTLE_STATE_SCORE = 250;
    private final int PER_METER_DISTANCE_SCORE = 10;

    public ScoresLogic(GameCoordinator gameCoordinator, Services services, GameModel gameModel, KnightLogic knightLogic,
                       CastleLogic castleLogic, HintsLogic hintsLogic) {
        this.gameCoordinator = gameCoordinator;
        this.services = services;
        this.knightLogic = knightLogic;
        this.castleLogic = castleLogic;
        this.hintsLogic = hintsLogic;
        this.gameModel = gameModel;
        this.leaderboardRecords = new ArrayList();

        this.scoresActor = new ScoresActor(services, gameCoordinator);
        scoresActor.populate(gameModel.getScore().intValue());
        setListeners();
    }

    public void refreshAllScores(){
        this.leaderboardRecords = gameCoordinator.getGameLeaderboardRecords();
        refreshScoresDesign();
    }

    public void refreshScoresDesign(){
        scoresActor.setMainScore(gameModel.getScore().intValue());
        scoresActor.setNextHighScore(getNextLeaderboardScore());
    }

    public void fakeAddScores(int toAdd){
        originalScores += toAdd;
        scoresActor.setMainScore(originalScores);
    }

    public void calculateWithoutAnimation(boolean sound){
        if(sound) services.getSoundsWrapper().playSounds(Sounds.Name.WIN);
        gameModel.setScore(gameModel.getScore() + getCastleScores() + getHintsScores() + getRemainingDistanceScores(), true);
        gameModel.setGameState(GameState.WaitingForNextStage);

        Logs.show("Final score:" + gameModel.getScore());
    }

    public void calculate(){
        if(Global.REVIEW_MODE){
            gameModel.setGameState(GameState.WaitingForNextStage);
            return;
        }

        originalScores = gameModel.getScore().intValue();
        Actor hintsActor = hintsLogic.getHintsActor();
        services.getSoundsWrapper().playSounds(Sounds.Name.WIN);

        final Runnable afterCalculateHintRunnable = new Runnable() {
            @Override
            public void run() {
                Threadings.delay(600, new Runnable() {
                    @Override
                    public void run() {
                        addScoreAndPopScoreOnActor(castleLogic.getCastleActor(), getCastleScores(), new Runnable() {
                            @Override
                            public void run() {
                                Threadings.delay(900, new Runnable() {
                                    @Override
                                    public void run() {
                                        final int remainingDistance = (int) knightLogic.getRemainingDistanceByRemainingTime(gameModel.getRemainingMiliSecs());
                                        final int[] originalScore = {originalScores};
                                        final Vector2 knightActorPosition = new Vector2(knightLogic.getKnightActor().getPositionOnStage().x,
                                                knightLogic.getKnightActor().getPositionOnStage().y);
                                        knightActorPosition.y = knightActorPosition.y + knightLogic.getKnightActor().getHeight() / 2;

                                        Threadings.runInBackground(new Runnable() {
                                            @Override
                                            public void run() {

                                                if(remainingDistance > 0){
                                                    services.getSoundsWrapper().playSoundLoop(Sounds.Name.ADDING_SCORE);
                                                }

                                                int totalSleepTime = 0;
                                                int sleepTime = 8;

                                                for (int i = 0; i < remainingDistance; i++) {
                                                    final int finalI = i;
                                                    Threadings.postRunnable(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            scoresActor.popRulerScoreOnPosition(knightActorPosition.x - 10, knightActorPosition.y, finalI + 1);
                                                            scoresActor.setMainScore(originalScore[0]);
                                                        }
                                                    });
                                                    Threadings.sleep(sleepTime);
                                                    totalSleepTime += sleepTime;

                                                    originalScore[0] += PER_METER_DISTANCE_SCORE;
                                                }

                                                services.getSoundsWrapper().stopSoundLoop(Sounds.Name.ADDING_SCORE);

                                                Threadings.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        scoresActor.setNextHighScore(getNextLeaderboardScore());
                                                    }
                                                });

                                                if(totalSleepTime < 2000){
                                                    Threadings.sleep(2000 - totalSleepTime);
                                                }

                                                calculateWithoutAnimation(false);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };


       if(gameModel.getStageType() == StageType.Bonus){
            services.getSoundsWrapper().stopMusic(Sounds.Name.BONUS_MUSIC);
            popStringOnActor(hintsActor, services.getTexts().replenishHints(), new Runnable() {
                @Override
                public void run() {
                    afterCalculateHintRunnable.run();
                }
            });
        }
        else{
            Logs.show(hintsLogic.getCurrentHintsLeft() + " hint left");
            addScoreAndPopScoreOnActor(hintsActor, getHintsScores(), new Runnable() {
                @Override
                public void run() {
                    afterCalculateHintRunnable.run();
                }
            });
        }
    }

    private int getCastleScores(){
        CastleState castleState = castleLogic.getCastleState(gameModel.getRemainingMiliSecs());
        int i = 0;
        if (castleState == CastleState.Normal) {
            i = 2;
        } else if (castleState == CastleState.Semi_Destroyed) {
            i = 1;
        }

        return i * PER_CASTLE_STATE_SCORE;
    }

    private int getHintsScores(){
        return hintsLogic.getCurrentHintsLeft() * PER_HINT_LEFT_SCORE;
    }

    private int getRemainingDistanceScores(){
        final int remainingDistance = (int) knightLogic.getRemainingDistanceByRemainingTime(gameModel.getRemainingMiliSecs());
        return (remainingDistance * PER_METER_DISTANCE_SCORE);
    }


    public void addScoreAndPopScoreOnActor(Actor actor, final int score, final Runnable onFinish){
        Vector2 actorPosition = Positions.actorLocalToStageCoord(actor);
        scoresActor.popScoreOnPosition(actorPosition.x + actor.getWidth() / 2, actorPosition.y + actor.getHeight() / 2,
                String.valueOf(score), false, new Runnable() {
            @Override
            public void run() {
                fakeAddScores(score);
                if (onFinish != null) onFinish.run();
            }
        });
    }

    public void popStringOnActor(Actor actor, String msg, final Runnable onFinish){
        Vector2 actorPosition = Positions.actorLocalToStageCoord(actor);
        scoresActor.popScoreOnPosition(actorPosition.x + actor.getWidth() / 2, actorPosition.y + actor.getHeight() / 2, msg, true, new Runnable() {
            @Override
            public void run() {
                if(onFinish != null) onFinish.run();
            }
        });
    }

    public HashMap<Team, ArrayList<ScoreDetails>> getFinalScoreDetails(){
        HashMap<Team, ArrayList<ScoreDetails>> result = new HashMap();
        ArrayList<ScoreDetails> scores = new ArrayList();
        scores.add(new ScoreDetails(gameModel.getScore(), services.getTexts().finalScore(), true, false));
        if(gameModel.getScore() > 0){

            Team team = gameCoordinator.getMyTeam();
            Map.Entry<String,Integer> maxEntry = null;
            for(Map.Entry<String,Integer> entry : gameModel.getUserRecords().entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }

            if(maxEntry != null){
                team.setLeaderId(maxEntry.getKey());
            }

            result.put(team, scores);
        }
        return result;
    }

    public int getNextLeaderboardScore(){
        if(leaderboardRecords.size() <= 0){
            return -1;
        }
        else{
            for(int i = leaderboardRecords.size() -1; i >= 0; i--){
                if(leaderboardRecords.get(i).getScore() > gameModel.getScore()){
                    return (int) leaderboardRecords.get(i).getScore();
                }
            }
            return -1;
        }
    }

    public void setListeners(){
        gameModel.addGameModelListener(new GameModelListener() {

            @Override
            public void onStageNumberChanged(int newStageNumber) {
                scoresActor.clearAllPopScores();
            }

            @Override
            public void onAddedClickCount(String userId, int newCount) {
                gameModel.setScore(gameModel.getScore() + PER_CLICK_SCORE, true);
            }

            @Override
            public void onGameStateChanged(final GameState oldState, GameState newState) {
                if(newState == GameState.Won){
                    Threadings.delay(100, new Runnable() {  //delay to prevent ontouch but not circle yet bug
                        @Override
                        public void run() {
                            calculate();
                        }
                    });
                }
//                else if(newState == GameState.WonWithoutContributions){
//                    calculateWithoutAnimation(true);
//                }
            }

            @Override
            public void onScoresChanged(double newScores) {
                super.onScoresChanged(newScores);
                if(gameModel.getGameState() != GameState.BeforeContinue){
                    refreshScoresDesign();
                }
            }
        });
    }

    public ScoresActor getScoresActor() {
        return scoresActor;
    }

    public void setScoresActor(ScoresActor scoresActor) {
        this.scoresActor = scoresActor;
    }

    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
    }
}
