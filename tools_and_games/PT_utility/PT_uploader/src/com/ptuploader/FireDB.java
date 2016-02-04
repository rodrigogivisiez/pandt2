package com.ptuploader;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class FireDB {

    Firebase _ref;
    boolean finished = false;
    boolean success = false;
    Firebase r;

    public FireDB() {
        _ref = new Firebase("https://glaring-inferno-8572.firebaseIO.com");
        r = _ref.child("games");
    }

    public void save(final UploadedGame uploadedGame){
        if(!uploadedGame.version.equals("1.00")){
            r.child(uploadedGame.abbr).child("createTimestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String timestamp = snapshot.getValue(String.class);
                        uploadedGame.createTimestamp = timestamp;
                        saveUploadedGame(uploadedGame);
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        else{
            uploadedGame.createTimestamp = String.valueOf(System.currentTimeMillis());
            saveUploadedGame(uploadedGame);
        }



    }

    private void saveUploadedGame(UploadedGame uploadedGame){
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
                    finished = true;
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
