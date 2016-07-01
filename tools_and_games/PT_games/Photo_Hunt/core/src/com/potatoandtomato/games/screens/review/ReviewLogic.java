package com.potatoandtomato.games.screens.review;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.DatabaseListener;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.absintf.ReviewLogicListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 9/4/2016.
 */
public class ReviewLogic {

    private ReviewActor reviewActor;
    private GameModel gameModel;
    private Services services;
    private GameCoordinator gameCoordinator;
    private ReviewLogicListener reviewLogicListener;
    private boolean confirmDeleted;

    public ReviewLogic(GameModel gameModel, Services services, GameCoordinator gameCoordinator) {
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.gameModel = gameModel;

        this.reviewActor = new ReviewActor(services);
        setListeners();
    }

    public void invalidate(){
        if(gameModel.getImageDetails() != null){
            confirmDeleted = false;
            reviewActor.refreshDesign(gameModel);
        }
    }

    private void setListeners(){

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onGameStateChanged(GameState oldState, GameState newState) {
                if(newState == GameState.Playing){
                    invalidate();
                }
                getReviewActor().updateBlockingReview(gameModel);
            }
        });


        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                reviewActor.getSkipLabel().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        gameModel.setGameState(GameState.BlockingReview);
                        reviewLogicListener.onGoToIndex(gameModel.getImageDetails().getIndex() + 1);
                    }
                });

                reviewActor.getGoToLabel().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        String goToIndex = reviewActor.getGoToTextField().getText();
                        if(Integer.valueOf(goToIndex) != gameModel.getImageDetails().getIndex()){
                            reviewActor.getStage().setKeyboardFocus(reviewActor.getGoToLabel());
                            gameModel.setGameState(GameState.BlockingReview);
                            reviewLogicListener.onGoToIndex(Integer.valueOf(goToIndex));
                        }
                    }
                });

                reviewActor.getDeleteLabel().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        if(!confirmDeleted){
                            confirmDeleted = true;
                            reviewActor.getDeleteLabel().setText("Delete?");
                        }
                        else{
                            gameModel.setGameState(GameState.BlockingReview);
                            services.getDatabase().removeImageById(String.valueOf(gameModel.getImageDetails().getId()), new DatabaseListener() {
                                @Override
                                public void onCallback(Object obj, Status st) {
                                    Threadings.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            reviewLogicListener.onGoToIndex(gameModel.getImageDetails().getIndex());
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });


    }







    public ReviewActor getReviewActor() {
        return reviewActor;
    }

    public void setReviewLogicListener(ReviewLogicListener reviewLogicListener) {
        this.reviewLogicListener = reviewLogicListener;
    }
}
