package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.controls.DisposableActor;
import com.potatoandtomato.common.enums.GestureType;

/**
 * Created by SiongLeng on 28/3/2016.
 */
public interface ITutorials {


    void startTutorialIfNotCompleteBefore(String id, boolean canSkip, TutorialPartListener listener);

    void showMessage(DisposableActor actor, String text);

    void expectGestureOnPosition(GestureType gestureType, String text, int gestureAndTextDistanceX, int gestureAndTextDistanceY,
                                 float x, float y, final int gestureActionDistanceX, final int gestureActionDistanceY);

    void completeTutorial();

    boolean completedTutorialBefore(String id);

}
