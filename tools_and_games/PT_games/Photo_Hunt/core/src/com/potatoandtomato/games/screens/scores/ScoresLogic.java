package com.potatoandtomato.games.screens.scores;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.CastleState;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.SimpleRectangle;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.time_bar.CastleLogic;
import com.potatoandtomato.games.screens.time_bar.KnightLogic;

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

        this.scoresActor = new ScoresActor(services, gameCoordinator);
        scoresActor.populate(gameModel.getScore());
        setListeners();
    }

    public void addScoreWithoutAnimation(int addingScore){
        gameModel.setScore(gameModel.getScore() + addingScore);
        scoresActor.setMainScore(gameModel.getScore());
    }


    public void calculate(){
        Actor hintsActor = hintsLogic.getHintsActor();
        addScoreAndPopScoreOnActor(hintsActor, gameModel.getHintsLeft() * PER_HINT_LEFT_SCORE, new Runnable() {
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

                        final int remainingDistance = (int) knightLogic.getRemainingDistanceByRemainingTime(gameModel.getRemainingMiliSecs());
                        final int[] originalScore = {gameModel.getScore()};
                        gameModel.setScore(originalScore[0] + ((int) (remainingDistance * PER_METER_DISTANCE_SCORE)));
                        final Vector2 knightActorPosition = new Vector2(knightLogic.getKnightActor().getPositionOnStage().x,
                                                                    knightLogic.getKnightActor().getPositionOnStage().y) ;
                        knightActorPosition.y = knightActorPosition.y + knightLogic.getKnightActor().getHeight() / 2;

                        Logs.show("Final score:" + gameModel.getScore());

                        Threadings.runInBackground(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < remainingDistance; i++) {
                                    final int finalI = i;
                                    Threadings.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            scoresActor.popRulerScoreOnPosition(knightActorPosition.x - 10, knightActorPosition.y, finalI + 1);
                                            scoresActor.setMainScore(originalScore[0]);
                                        }
                                    });
                                    Threadings.sleep(8);

                                    originalScore[0] += PER_METER_DISTANCE_SCORE;
                                }

                                gameModel.setGameState(GameState.Ended);
                            }
                        });
                    }
                });
            }
        });

    }

    public void addScoreAndPopScoreOnActor(Actor actor, final int score, final Runnable onFinish){
        Vector2 actorPosition = Positions.actorLocalToStageCoord(actor);
        scoresActor.popScoreOnPosition(actorPosition.x + actor.getWidth() / 2, actorPosition.y + actor.getHeight() / 2, score, new Runnable() {
            @Override
            public void run() {
                addScoreWithoutAnimation(score);
                if(onFinish != null) onFinish.run();
            }
        });
    }

    public void setListeners(){
        gameModel.addGameModelListener(new GameModelListener() {

            @Override
            public void onStageNumberChanged(int newStageNumber) {
                scoresActor.clearAllPopScores();
            }

            @Override
            public void onCorrectClicked(SimpleRectangle rectangle, String userId, int remainingMiliSecsWhenClicked) {
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

    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
    }
}
