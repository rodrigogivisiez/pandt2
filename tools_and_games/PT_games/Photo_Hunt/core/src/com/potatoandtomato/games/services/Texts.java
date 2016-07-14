package com.potatoandtomato.games.services;

import com.badlogic.gdx.math.MathUtils;
import com.potatoandtomato.common.enums.SpeechActionType;
import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.utils.Pair;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class Texts {

    public String nextHighScore(){ return "Next HighScore"; }
    public String gameStarting(){ return "Game starting"; }
    public String gameContinue(){ return "Game continue"; }

    public String verySoon(){ return "very very soon....."; }
    public String gameOver(){ return "Game Over."; }
    public String memoryStart(){ return "You may start now";}
    public String finalScore() { return "Final Scores"; }
    public String gameContinueFailed(){ return "Game continue failed as all your partners have disconnected."; }

    public String evilKnightPreparing() { return "The evil knight is preparing..";}
    public String waitingForContinue() { return "Waiting for next stage..";}
    public String gameOverText() { return "CONTRIBUTIONS";}

    public String noPlayer() { return "No Player"; }
    public String totalScores() { return "Scores"; }
    public String waitForNextStage() { return "You teammates are playing, you will rejoin in next stage.";}
    public String slowMessage() { return "Slow connection detected, please wait for next stage.";}
    public String replenishHints(){ return "Replenish"; }

    public String notContinue() {return "Give up;";}
    public String xDecidedNotToContinue() { return "%s has decided not to continue game.";}

    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getMascotsSpeechAboutContinue(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("You have one chance to continue the game", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Continue???", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }

}
