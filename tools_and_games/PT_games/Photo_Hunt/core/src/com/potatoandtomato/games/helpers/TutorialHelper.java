package com.potatoandtomato.games.helpers;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.potatoandtomato.games.services.Texts;
import com.potatoandtomato.games.statics.Terms;

/**
 * Created by SiongLeng on 11/8/2016.
 */
public class TutorialHelper implements TutorialPartListener {

    private GameCoordinator gameCoordinator;
    private Texts texts;
    private int tutorialStep;

    public TutorialHelper(GameCoordinator gameCoordinator, Texts texts) {
        this.gameCoordinator = gameCoordinator;
        this.texts = texts;
    }

    public void start(){
        gameCoordinator.getTutorialsWrapper().startTutorialIfNotCompleteBefore(Terms.TUTORIAL_BASIC,
                false, this);
    }

    @Override
    public void nextTutorial() {
        tutorialStep++;
        if(tutorialStep == 1){
            gameCoordinator.getTutorialsWrapper().showMessage(null, texts.tutorialAboutGameMsg());
        }
        else if(tutorialStep == 2){
            gameCoordinator.getTutorialsWrapper().completeTutorial();
        }
    }
}
