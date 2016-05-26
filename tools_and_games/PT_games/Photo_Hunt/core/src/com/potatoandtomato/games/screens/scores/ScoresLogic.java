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
        scoresActor.setMainScore(gameModel.getScore().intValue());
        scoresActor.setNextHighScore(getNextLeaderboardScore());
    }

    public void addScoreWithoutAnimation(int addingScore){
        gameModel.setScore(gameModel.getScore() + addingScore);
        scoresActor.setMainScore(gameModel.getScore().intValue());
        scoresActor.setNextHighScore(getNextLeaderboardScore());
    }


    public void calculate(){
        if(Global.REVIEW_MODE){
            gameModel.setGameState(GameState.WaitingForNextStage);
            return;
        }

        Actor hintsActor = hintsLogic.getHintsActor();
        services.getSoundsWrapper().playSounds(Sounds.Name.WIN);

        final Runnable afterCalculateHintRunnable = new Runnable() {
            @Override
            public void run() {
                Threadings.delay(600, new Runnable() {
                    @Override
                    public void run() {

                        CastleState castleState = castleLogic.getCastleState(gameModel.getRemainingMiliSecs());
                        int i = 0;
                        if (castleState == CastleState.Normal) {
                            i = 2;
                        } else if (castleState == CastleState.Semi_Destroyed) {
                            i = 1;
                        }

                        addScoreAndPopScoreOnActor(castleLogic.getCastleActor(), i * PER_CASTLE_STATE_SCORE, new Runnable() {
                            @Override
                            public void run() {
                                Threadings.delay(900, new Runnable() {
                                    @Override
                                    public void run() {
                                        final int remainingDistance = (int) knightLogic.getRemainingDistanceByRemainingTime(gameModel.getRemainingMiliSecs());
                                        final int[] originalScore = {gameModel.getScore().intValue()};
                                        gameModel.setScore((double) originalScore[0] + ((int) (remainingDistance * PER_METER_DISTANCE_SCORE)));
                                        final Vector2 knightActorPosition = new Vector2(knightLogic.getKnightActor().getPositionOnStage().x,
                                                knightLogic.getKnightActor().getPositionOnStage().y);
                                        knightActorPosition.y = knightActorPosition.y + knightLogic.getKnightActor().getHeight() / 2;

                                        Logs.show("Final score:" + gameModel.getScore());

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

                                                scoresActor.setMainScore(gameModel.getScore().intValue());

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

                                                gameModel.setGameState(GameState.WaitingForNextStage);
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
            addScoreAndPopScoreOnActor(hintsActor, hintsLogic.getCurrentHintsLeft() * PER_HINT_LEFT_SCORE, new Runnable() {
                @Override
                public void run() {
                    afterCalculateHintRunnable.run();
                }
            });
        }
    }

    public void addScoreAndPopScoreOnActor(Actor actor, final int score, final Runnable onFinish){
        Vector2 actorPosition = Positions.actorLocalToStageCoord(actor);
        scoresActor.popScoreOnPosition(actorPosition.x + actor.getWidth() / 2, actorPosition.y + actor.getHeight() / 2,
                String.valueOf(score), false, new Runnable() {
            @Override
            public void run() {
                addScoreWithoutAnimation(score);
                if(onFinish != null) onFinish.run();
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
            result.put(gameCoordinator.getMyTeam(), scores);
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
                addScoreWithoutAnimation(PER_CLICK_SCORE);
            }

            @Override
            public void onGameStateChanged(GameState newState) {
                if(newState == GameState.Won){
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            calculate();
                        }
                    });
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
