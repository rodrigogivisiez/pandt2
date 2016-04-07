import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.DatabaseListener;
import com.potatoandtomato.games.absintf.mockings.MockModel;
import com.potatoandtomato.games.models.ImageDetails;
import com.potatoandtomato.games.services.Database;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by SiongLeng on 31/3/2016.
 */
public class TestDatabase extends TestAbstract {

    private Database database;



    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        database = new Database(_game.getCoordinator().getTestingFirebase());
        database.removeAllImages();
    }

    @Test
    public void testGetLastImageIndex(){

        ImageDetails details1 = MockModel.mockImageDetails();
        ImageDetails details2 = MockModel.mockImageDetails();
        details1.setIndex(2);

        database.addNewImageDetails(details1, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        database.addNewImageDetails(details2, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(2);

        database.getLastImageIndex(new DatabaseListener<Integer>() {
            @Override
            public void onCallback(Integer result, Status st) {
                Assert.assertEquals(2, result, 0);
                Assert.assertEquals(Status.SUCCESS, st);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
    }


    @Test
    public void testGetImageDetailsByIndex(){

        ImageDetails details1 = MockModel.mockImageDetails();
        details1.setIndex(0);
        final ImageDetails details2 = MockModel.mockImageDetails();
        details2.setIndex(3);

        database.addNewImageDetails(details1, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        database.addNewImageDetails(details2, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                details2.setId(obj);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(2);

        database.getImageDetailsByIndex(3, new DatabaseListener<ImageDetails>(ImageDetails.class) {
            @Override
            public void onCallback(ImageDetails result, Status st) {
                Assert.assertEquals(details2.getId(), result.getId());
                Assert.assertEquals(Status.SUCCESS, st);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

    }


    @Test
    public void testGetImageDetailsById(){

        ImageDetails details1 = MockModel.mockImageDetails();
        details1.setIndex(0);
        final ImageDetails details2 = MockModel.mockImageDetails();
        details2.setImageOneUrl("details2");
        details2.setIndex(3);

        database.addNewImageDetails(details1, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        database.addNewImageDetails(details2, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                details2.setId(obj);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(2);

        database.getImageDetailsById(details2.getId(), new DatabaseListener<ImageDetails>(ImageDetails.class) {
            @Override
            public void onCallback(ImageDetails result, Status st) {
                Assert.assertEquals(details2.getImageOneUrl(), result.getImageOneUrl());
                Assert.assertEquals(Status.SUCCESS, st);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
    }

    @Test
    public void testRemoveImageById(){

        final ImageDetails details0 = MockModel.mockImageDetails();
        details0.setIndex(0);
        final ImageDetails details1 = MockModel.mockImageDetails();
        details1.setIndex(1);
        final ImageDetails details2 = MockModel.mockImageDetails();
        details2.setIndex(2);

        database.addNewImageDetails(details0, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                details0.setId(obj);
                Threadings.oneTaskFinish();
            }
        });

        database.addNewImageDetails(details1, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                details1.setId(obj);
                Threadings.oneTaskFinish();
            }
        });

        database.addNewImageDetails(details2, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                details2.setId(obj);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(3);



        database.removeImageById(details0.getId(), new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);


        database.getImageDetailsByIndex(0, new DatabaseListener<ImageDetails>(ImageDetails.class) {
            @Override
            public void onCallback(ImageDetails result, Status st) {
                Assert.assertEquals(details1.getId(), result.getId());
                Assert.assertEquals(Status.SUCCESS, st);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

    }
}

