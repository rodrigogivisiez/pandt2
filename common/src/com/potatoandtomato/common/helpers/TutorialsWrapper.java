package com.potatoandtomato.common.helpers;

import com.potatoandtomato.common.absints.GamePreferencesAbstract;
import com.potatoandtomato.common.absints.ITutorials;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.potatoandtomato.common.controls.DisposableActor;
import com.potatoandtomato.common.enums.GestureType;

/**
 * Created by SiongLeng on 27/7/2016.
 */
public class TutorialsWrapper implements ITutorials {

    private ITutorials tutorials;
    private GamePreferencesAbstract gamePreferencesAbstract;

    public TutorialsWrapper(ITutorials tutorials, GamePreferencesAbstract gamePreferencesAbstract) {
        this.tutorials = tutorials;
        this.gamePreferencesAbstract = gamePreferencesAbstract;
    }

    @Override
    public void startTutorialIfNotCompleteBefore(String id, boolean canSkip, TutorialPartListener listener) {
        String wrappedId = gamePreferencesAbstract.appendAbbrToKey(id);
        tutorials.startTutorialIfNotCompleteBefore(wrappedId, canSkip, listener);
    }

    @Override
    public void showMessage(DisposableActor actor, String text) {
        tutorials.showMessage(actor, text);
    }

    @Override
    public void expectGestureOnPosition(GestureType gestureType, String text, int gestureAndTextDistanceX,
                                        int gestureAndTextDistanceY, float x, float y, int gestureActionDistanceX,
                                        int gestureActionDistanceY) {
        tutorials.expectGestureOnPosition(gestureType, text, gestureAndTextDistanceX, gestureAndTextDistanceY, x, y,
                                                gestureActionDistanceX, gestureActionDistanceY);
    }

    @Override
    public void completeTutorial() {
        tutorials.completeTutorial();
    }

    @Override
    public boolean completedTutorialBefore(String id) {
        String wrappedId = gamePreferencesAbstract.appendAbbrToKey(id);
        return tutorials.completedTutorialBefore(wrappedId);
    }
}
