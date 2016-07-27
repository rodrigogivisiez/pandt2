package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.statics.Terms;

/**
 * Created by SiongLeng on 2/3/2016.
 */
public class DebugLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.DEBUG = true;
        Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
        startActivity(i);
        Terms.LOCAL_HOST = "192.168.0.5";
    }
}
