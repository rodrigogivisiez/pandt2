package com.mygdx.potatoandtomato.android;

import android.graphics.Rect;
import android.view.View;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.SafeThread;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 5/1/2016.
 */
public class LayoutChangedFix {

    private View _rootView;
    private int _screenHeightPotrait, _screenHeightLandscape;
    private int width, _height;
    private SafeThread _safeThread;
    private int count;
    private Broadcaster _broadcaster;

    public LayoutChangedFix(View rootView, Broadcaster broadcaster) {
        this._rootView = rootView;
        this._broadcaster = broadcaster;

        Rect rect = new Rect();
        _rootView.getWindowVisibleDisplayFrame(rect);
        _screenHeightPotrait = rect.height();
        addLayoutChangedListener();

    }

    private void addLayoutChangedListener(){
        _rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boolean isPotrait = Global.IS_POTRAIT;
                Rect rect = new Rect();
                _rootView.getWindowVisibleDisplayFrame(rect);

                if(!isPotrait && _screenHeightLandscape == 0){
                    _screenHeightLandscape = rect.height();
                }

                int usingScreenHeight = isPotrait ? _screenHeightPotrait :_screenHeightLandscape;

                if (!(width == rect.width() && _height == rect.height())) {
                    width = rect.width();
                    _height = rect.height();
                    broadcastLayoutChanged(usingScreenHeight - _height, usingScreenHeight);

                }
            }
        });
    }


    private void broadcastLayoutChanged(final float gdxHeight, final float screenHeight){
        _broadcaster.broadcast(BroadcastEvent.SCREEN_LAYOUT_CHANGED,
                Positions.screenYToGdxY(gdxHeight, screenHeight));
    }





}
