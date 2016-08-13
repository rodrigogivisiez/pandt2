package com.potatoandtomato.games.services;

import com.badlogic.gdx.math.MathUtils;
import com.potatoandtomato.common.enums.SpeechActionType;
import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.games.enums.BonusType;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class Texts {

    public String nextHighScore(){ return "Next High Score"; }

    public String memoryStart(){ return "Done studying? I hope so too.";}
    public String finalScore() { return "Final Score"; }
    public String gameContinueFailed(){ return "Game continue fails as all your partners have disconnected."; }

    public String evilKnightPreparing() { return "Mr. Knight is warming up..";}
    public String evilKnightComing() { return "Mr. Knight CHARGES! Find 5 differences to STOP HIM!"; };

    public String waitingForContinue() { return "Waiting for next stage..";}
    public String gameOverText() { return "NO. OF CLICKS";}

    public String noPlayer() { return "No Player"; }
    public String totalScores() { return "Scores"; }
    public String waitForNextStage() { return "Your comrades are playing, you will rejoin,\nif they survive...";}
    public String slowMessage() { return "Slow connection detected, you will rejoin next stage.";}
    public String replenishHints(){ return "Replenish"; }

    public String notContinue() {return "Give up;";}
    public String xDecidedNotToContinue() { return "%s has decided not to continue game.";}


    public String tutorialAboutGameMsg(){
        return "Welcome to Photo Mania Resistance. Protect your castle from the evil knight by finding 5 differences with your friends, before the knight can reach you! 3 clues on the top left to help you each time.";
    }

    public String getBonusString(BonusType bonusType){
        switch (bonusType){
            case INVERTED:
                return "Huh? Which way is up?";
            case MEMORY:
                return "Remember the days...";
            case LIGHTING:
                return "Who off the light?";
            case LOOPING:
                return "Like an old movie...";
            case WRINKLE:
                return "Wait! Don't throw that away!";
            case TORCH_LIGHT:
                return "Now we find a way out...";
            case COVERED:
                return "Time to smash fingers...";
            case EGG:
                return "Careful, careful...Oops";
            case ONE_PERSON:
                return "Now %s will bring the ring...";
            case DISTRACTION:
                return "It is a bird. No, it is a plane. No, it is a....";
        }
        return "";
    }

    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getMascotsSpeechAboutContinue(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 3);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("Is this your best?", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Come on, just one more time!", SpeechActionType.Add));
        }
        else if(style == 1){
            potatoSpeechActions.add(new SpeechAction("So close!", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("One more chance please!", SpeechActionType.Add));
        }
        else if(style == 2){
            potatoSpeechActions.add(new SpeechAction("All good things...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Have one more chance!", SpeechActionType.Add));
        }
        else if(style == 3){
            potatoSpeechActions.add(new SpeechAction("Never ever let you down...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Never ever give you up, one last time!", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }

}
