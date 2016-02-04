import com.firebase.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class FireDB {

    private static long totalImageCount = 0;
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


    public String saveNew(final String key, final String meta, final Runnable onFinish){
        getImagesCount(new Runnable() {
            @Override
            public void run() {
                final Firebase r = _ref.child("images").child(key);
                ImageData imageData = new ImageData(meta, totalImageCount, key);
                r.setValue(imageData, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if(onFinish!= null) onFinish.run();
                    }
                });
            }
        });
        return key;
    }

    public void getImagesCount(final Runnable onFinish){
        final Query r = _ref.child("images").orderByChild("index").limitToLast(1);
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long count = 0;
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        ImageData imageData = snapshot1.getValue(ImageData.class);
                        count = imageData.getIndex() + 1;
                        break;
                    }
                }
                totalImageCount = count;
                MainForm.updateImageCount(count);
                if(onFinish != null) onFinish.run();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
