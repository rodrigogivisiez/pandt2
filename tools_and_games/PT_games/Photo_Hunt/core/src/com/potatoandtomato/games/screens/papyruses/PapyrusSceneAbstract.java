package com.potatoandtomato.games.screens.papyruses;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 25/5/2016.
 */
public abstract class PapyrusSceneAbstract extends Table implements Disposable {

    Runnable onClosingRunnable;
    boolean disposed;

    public PapyrusSceneAbstract() {
        this.setName("papyrusScene");
    }

    public void setOnClosingRunnable(Runnable onClosingRunnable) {
        this.onClosingRunnable = onClosingRunnable;
    }

    public void shouldCloseInMiliSecs(long milisecs){
        Threadings.delay(milisecs, new Runnable() {
            @Override
            public void run() {
                if(disposed) return;
                if(onClosingRunnable != null) onClosingRunnable.run();
            }
        });
    }

    @Override
    public void dispose() {
        disposed = true;
    }
}
