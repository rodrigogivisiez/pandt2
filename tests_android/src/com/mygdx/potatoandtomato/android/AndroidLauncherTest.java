package com.mygdx.potatoandtomato.android;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.mygdx.potatoandtomato.android.AndroidLauncherTest \
 * com.mygdx.potatoandtomato.android.tests/android.test.InstrumentationTestRunner
 */
public class AndroidLauncherTest extends ActivityInstrumentationTestCase2<AndroidLauncher> {

    public AndroidLauncherTest() {
        super("com.mygdx.potatoandtomato.android", AndroidLauncher.class);
    }

}
