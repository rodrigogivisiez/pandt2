package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.helpers.assets.FirebaseDB;
import com.mygdx.potatoandtomato.helpers.assets.Profile;
import helpers.T_Threadings;
import org.junit.Assert;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class TestFireBase extends TestAbstract {

    @Test
    public void testConnectFirebase(){
        final boolean[] waiting = {true};
        IDatabase databases = new FirebaseDB();
        databases.getTestTableCount(new DatabaseListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                waiting[0] = false;
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, obj > 0);
            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }
    }

    @Test
    public void testCreateUser(){
        final boolean[] waiting = {true};
        final IDatabase databases = new FirebaseDB();
        databases.loginAnonymous(new DatabaseListener<Profile>() {
            @Override
            public void onCallback(Profile obj, Status st) {
                Assert.assertEquals(false, st == Status.FAILED);
                Assert.assertEquals(false, obj == null);
                databases.createUserByUserId(obj.getUserId(), new DatabaseListener<Profile>() {
                    @Override
                    public void onCallback(Profile obj, Status st) {
                        Assert.assertEquals(false, st == Status.FAILED);
                        Assert.assertEquals(false, obj == null);
                        databases.getProfileByUserId(obj.getUserId(), new DatabaseListener<Profile>(Profile.class) {
                            @Override
                            public void onCallback(Profile obj, Status st) {
                                Assert.assertEquals(false, st == Status.FAILED);
                                Assert.assertEquals(false, obj == null);
                                Assert.assertEquals(false, obj.getUserId() == null);
                                waiting[0] = false;
                            }
                        });
                    }
                });

            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }
    }


}
