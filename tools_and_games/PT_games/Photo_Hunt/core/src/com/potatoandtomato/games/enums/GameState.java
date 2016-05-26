package com.potatoandtomato.games.enums;

/**
 * Created by SiongLeng on 7/4/2016.
 */
public enum  GameState {

    //normal flow, Close -> Playing -> Pause -> Won/Lose -> WaitingForNextStage -> Playing ............
    //BlockingReview is for review mode only

    BeforeNewGame, BeforeContinue,
    BeforeBouns,
    PrePlaying,
    Playing, Pause, Won, Lose, WaitingForNextStage,
    BlockingReview

}
