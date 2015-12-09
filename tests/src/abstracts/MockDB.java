package abstracts;

import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.helpers.assets.Profile;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class MockDB implements IDatabase {
    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        listener.onCallback(1, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void loginAnonymous(DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId("12345");
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void createUserByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }
}
