package cases;

import abstracts.MockGameCoordinator;
import abstracts.TestAbstract;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.MockGame;
import com.potatoandtomato.common.Status;
import com.potatoandtomato.games.PhotoHuntGame;
import com.potatoandtomato.games.abs.database.DatabaseListener;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Database;
import com.potatoandtomato.games.models.ImageData;
import helpers.WaitingTask;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class TestDatabase extends TestAbstract {

    Database database;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockGame mockGame = new PhotoHuntGame("photo_hunt");
        GameCoordinator gameCoordinator = mockGame.getCoordinator();
        database = new Database(gameCoordinator.getFirebase());
    }

    @Test
    public void testGetTotalImagesCount(){
        final WaitingTask waitingTask = new WaitingTask();
        database.getTotalImagesCount(new DatabaseListener<Long>() {
            @Override
            public void onCallback(Long obj, Status st) {
                Assert.assertEquals(true, obj > 1);
                Assert.assertEquals(Status.SUCCESS, st);
                waitingTask.kill();
            }
        });
        waitingTask.start(500);
    }

    @Test
    public void testGetImageIdByIndex(){
        final WaitingTask waitingTask = new WaitingTask();
        database.getImageIdByIndex(0, new DatabaseListener<String>(String.class) {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals("-K9aVT6Ar4r0KDUWErT-", obj);
                Assert.assertEquals(Status.SUCCESS, st);
                waitingTask.kill();
            }
        });
        waitingTask.start(500);
    }

    @Test
    public void testGetImageMetaById(){
        final WaitingTask waitingTask = new WaitingTask();
        database.getImageDataById("-K9aVT6Ar4r0KDUWErT-", new DatabaseListener<ImageData>(ImageData.class) {
            @Override
            public void onCallback(ImageData obj, Status st) {
                try {
                    JSONObject jsonObject = new JSONObject(obj.getJson());
                } catch (JSONException e) {
                    Assert.assertEquals(1, 0);
                    e.printStackTrace();
                }
                Assert.assertEquals(Status.SUCCESS, st);
                waitingTask.kill();
            }
        });
        waitingTask.start(500);
    }


}
