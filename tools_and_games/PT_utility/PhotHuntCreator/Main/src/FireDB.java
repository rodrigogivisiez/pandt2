import com.firebase.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class FireDB {

    private static long totalImageCount = 0;
    Firebase _ref;
    boolean finished = false;
    boolean success = false;
    private String _tableStorage = "storage";
    private final String SECRET = "UogxKt0DL9RgHnadZ3nmcrPwJQBT3b699vjMOpPO";

    public FireDB() {
        _ref = new Firebase("https://glaring-inferno-8572.firebaseIO.com").child("gameBelongData").child("photo_hunt");

        _ref.authWithCustomToken(SECRET, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                System.exit(1);
            }
        });

    }

    public String getKey(){
        final Firebase r = _ref.child(_tableStorage);
        return r.push().getKey();
    }

    public void getImagesCount(final Runnable onFinish){
        final Query r = _ref.child(_tableStorage).orderByChild("index").limitToLast(1);
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long count = 0;
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        ImageDetails imageDetails = snapshot1.getValue(ImageDetails.class);
                        count = imageDetails.getIndex() + 1;
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


    public void save(final String key, final ImageDetails details, final Runnable onFinish){
        getImagesCount(new Runnable() {
            @Override
            public void run() {
                final Firebase r = _ref.child(_tableStorage).child(key);
                details.setIndex((int) totalImageCount);
                System.out.println(totalImageCount);
                r.setValue(details, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if(onFinish!= null) onFinish.run();
                    }
                });
            }
        });
    }

    public void getAllVersionOneImages(DatabaseListener listener){
        getData(_ref.child("images"), listener);
    }

    private void getData(final Query ref, final DatabaseListener listener){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Object> results = new ArrayList<Object>();
                for(DataSnapshot postSnapShot : snapshot.getChildren()){
                    Object newPost = postSnapShot.getValue(listener.getType());
                    results.add(newPost);
                }
                listener.onCallback(results);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                listener.onCallback(0);
            }
        });
    }

}
