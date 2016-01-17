package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class DummyKeyboard implements TextField.OnscreenKeyboard {
    @Override
    public void show(boolean visible) {
        Broadcaster.getInstance().broadcast(BroadcastEvent.SHOW_NATIVE_KEYBOARD);
    }
}
