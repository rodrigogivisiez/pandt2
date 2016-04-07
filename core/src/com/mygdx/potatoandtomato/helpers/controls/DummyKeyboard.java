package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class DummyKeyboard implements TextField.OnscreenKeyboard {

    private Broadcaster _broadcaster;

    public DummyKeyboard(Broadcaster broadcaster) {
        _broadcaster = broadcaster;
    }

    @Override
    public void show(boolean visible) {
        _broadcaster.broadcast(BroadcastEvent.SHOW_NATIVE_KEYBOARD);
    }
}
