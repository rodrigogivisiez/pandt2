package com.ptuploader;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class FireDB {

    Firebase _ref;
    boolean finished = false;
    boolean success = false;

    public FireDB() {
        _ref = new Firebase("https://glaring-inferno-8572.firebaseIO.com");
    }

    public void save(final UploadedGame uploadedGame){
        final Firebase r = _ref.child("games");
        r.child(uploadedGame.abbr).setValue(uploadedGame, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    success = false;
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                    finished = true;
                } else {
                    success = true;
                    System.out.println("Data saved successfully.");
                    if(uploadedGame.version.equals("1.00")){
                        r.child(uploadedGame.abbr).child("createTimestamp").setValue(System.currentTimeMillis());
                        finished = true;
                    }
                }

            }
        });



    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isSuccess() {
        return success;
    }
}
