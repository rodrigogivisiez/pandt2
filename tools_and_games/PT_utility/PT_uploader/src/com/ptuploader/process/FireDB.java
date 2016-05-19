package com.ptuploader.process;

import com.firebase.client.*;
import com.ptuploader.process.Details;
import com.ptuploader.utils.Logs;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class FireDB {

    private Firebase _ref;
    private final String URL = "https://glaring-inferno-8572.firebaseIO.com";
    private final String TEST_URL = "https://ptapptest.firebaseio.com/";
    private final String SECRET = "UogxKt0DL9RgHnadZ3nmcrPwJQBT3b699vjMOpPO";
    private final String TEST_SECRET = "xU62Y6naxtpRUZZad429zIPu7f3rSVcVrjG4MOMp";
    private Logs _logs;

    public FireDB(boolean isTesting, Logs logs) {
        _logs = logs;
        _ref = new Firebase(isTesting ? TEST_URL : URL).child("games");

        _ref.authWithCustomToken(isTesting ? TEST_SECRET : SECRET, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                _logs.write("Successfully login to firebase.");
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                _logs.write("Failed to login to firebase.");
            }
        });

    }

    public void save(Details details, final Runnable onFinish) {
        _logs.write("Saving details to FireDB...");
        _ref.child(details.getAbbr()).setValue(details.getDetailsMap(), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    _logs.write("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    _logs.write("Data saved successfully.");
                    onFinish.run();
                }

            }
        });
    }
}
