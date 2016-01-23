package com.mygdx.potatoandtomato.android;

import android.graphics.Rect;
import android.view.View;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 5/1/2016.
 */
public class LayoutChangedFix {

    private View _rootView;
    private int _screenHeight;
    private int width, _height;
    private SafeThread _safeThread;
    private int count;
    private Broadcaster _broadcaster;

    public LayoutChangedFix(View rootView, Broadcaster broadcaster) {
        this._rootView = rootView;
        this._broadcaster = broadcaster;

        Rect rect = new Rect();
        _rootView.getWindowVisibleDisplayFrame(rect);
        _screenHeight = rect.height();
        addLayoutChangedListener();

    }

    private void addLayoutChangedListener(){
        _rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Rect rect = new Rect();
                _rootView.getWindowVisibleDisplayFrame(rect);

                if (!(width == rect.width() && _height == rect.height())) {
                    width = rect.width();
                    _height = rect.height();
                    broadcastLayoutChanged(_screenHeight - _height, _screenHeight);

                }
            }
        });
    }


    private void broadcastLayoutChanged(final float gdxHeight, final float screenHeight){
        _broadcaster.broadcast(BroadcastEvent.SCREEN_LAYOUT_CHANGED,
                Positions.screenYToGdxY(gdxHeight, screenHeight));
    }





}
