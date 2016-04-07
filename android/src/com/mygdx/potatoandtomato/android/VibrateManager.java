package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.os.Vibrator;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 21/1/2016.
 */
public class VibrateManager {

    private Context _context;
    private Broadcaster _broadcaster;

    public VibrateManager(Context context, Broadcaster broadcaster) {
        this._broadcaster = broadcaster;
        this._context = context;
        _broadcaster.subscribe(BroadcastEvent.VIBRATE_DEVICE, new BroadcastListener<Object>() {
            @Override
            public void onCallback(Object obj, Status st) {
                if(obj instanceof Integer){
                    vibrate(Double.valueOf(obj.toString()));
                }
                else if(obj instanceof Double){
                    vibrate(Double.valueOf(obj.toString()));
                }

            }
        });
    }

    private void vibrate(double periodInMili){
        long period = (long) (periodInMili);
        Vibrator v = (Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(period);
    }




}
