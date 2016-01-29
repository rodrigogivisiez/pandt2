import com.firebase.client.*;

import java.util.HashMap;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class FireDB {

    Firebase _ref;
    boolean finished = false;
    boolean success = false;

    public FireDB() {
        _ref = new Firebase("https://glaring-inferno-8572.firebaseIO.com").child("gameBelongData").child("photo_hunt");

    }

    public String getKey(){
        final Firebase r = _ref.child("images");
        return r.push().getKey();
    }

    public String saveNew(String key, String meta){
        final Firebase r = _ref.child("images").child(key);
        r.setValue(meta);
        return key;
    }

    public void getImagesCount(){
        final Firebase r = _ref.child("images");
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                MainForm.updateImageCount(snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
