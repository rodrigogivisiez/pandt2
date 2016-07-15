package com.mygdx.potatoandtomato.android;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

public class GCMClientManager {
    // Constants
    public static final String TAG = "GCMClientManager";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // Member variables
    private GoogleCloudMessaging gcm;
    private String regid;
    private final String projectNumber = "171699917132";
    private Activity activity;
    private Broadcaster broadcaster;

    public static abstract class RegistrationCompletedHandler {
        public abstract void onSuccess(String registrationId, boolean isNewRegistration);
        public void onFailure(String ex) {
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
            Log.e(TAG, ex);
        }
    }

    public GCMClientManager(Activity activity, Broadcaster broadcaster) {
        this.activity = activity;
        this.broadcaster = broadcaster;
        this.gcm = GoogleCloudMessaging.getInstance(activity);
        registerBroadcastListener();
    }

    private void registerBroadcastListener(){
        broadcaster.subscribe(BroadcastEvent.LOGIN_GCM_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                registerIfNeeded(new RegistrationCompletedHandler() {
                    @Override
                    public void onSuccess(String registrationId, boolean isNewRegistration) {
                        broadcaster.broadcast(BroadcastEvent.LOGIN_GCM_CALLBACK, registrationId, Status.SUCCESS);
                    }

                    @Override
                    public void onFailure(String ex) {
                        super.onFailure(ex);
                        broadcaster.broadcast(BroadcastEvent.LOGIN_GCM_CALLBACK, null, Status.FAILED);
                    }
                });
            }
        });
    }


    // Register if needed or fetch from local store
    public void registerIfNeeded(final RegistrationCompletedHandler handler) {
        if (checkPlayServices()) {
            regid = getRegistrationId(getContext());

            if (regid.isEmpty()) {
                registerInBackground(handler);
            } else { // got id from cache
                //Log.i(TAG, regid);
                handler.onSuccess(regid, false);
            }
        } else { // no play services
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final RegistrationCompletedHandler handler) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getContext());
                    }
                    InstanceID instanceID = InstanceID.getInstance(getContext());
                    regid = instanceID.getToken(projectNumber, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    //Log.i(TAG, regid);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getContext(), regid);

                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    handler.onFailure("Error :" + ex.getMessage());
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String regId) {
                if (regId != null) {
                    handler.onSuccess(regId, true);
                }
            }
        }.execute(null, null, null);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        //Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getContext().getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    private Context getContext() {
        return activity;
    }

    private Activity getActivity() {
        return activity;
    }
}