package cases;

import abstracts.MockGameCoordinator;
import abstracts.TestAbstract;
import com.potatoandtomato.common.*;
import com.potatoandtomato.games.abs.database.DatabaseListener;
import com.potatoandtomato.games.abs.database.IDatabase;
import com.potatoandtomato.games.abs.image_getter.PeekImageListener;
import com.potatoandtomato.games.helpers.Database;
import com.potatoandtomato.games.helpers.ImageGetter;
import com.potatoandtomato.games.helpers.UpdateCode;
import com.potatoandtomato.games.models.ImageData;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.UpdateMsg;
import com.potatoandtomato.games.utils.Strings;
import helpers.WaitingTask;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class TestImageGetter extends TestAbstract{

    @Test
    public void testPopImage(){

        final WaitingTask waitingTask = new WaitingTask();

        final ImageGetter imageGetter = new ImageGetter(new MockGameCoordinator(), mock(Database.class));
        imageGetter.addImagePairs(new ImagePair(null, null, "0", "0"));
        imageGetter.addImagePairs(new ImagePair(null, null, "1", "1"));
        imageGetter.addImagePairs(new ImagePair(null, null, "2", "2"));
        imageGetter.addImagePairs(new ImagePair(null, null, "3", "3"));


        imageGetter.peekImage(new PeekImageListener() {
            @Override
            public void onImagePeeked(ImagePair imagePair) {
                Assert.assertEquals("0", imagePair.getMetaJson());
                imageGetter.peekImage(new PeekImageListener() {
                    @Override
                    public void onImagePeeked(ImagePair imagePair) {
                        Assert.assertEquals("0", imagePair.getMetaJson());
                        waitingTask.kill();
                    }
                });
            }
        });

        waitingTask.start(200);

    }

    @Test
    public void testRandomGetMsgThread(){
        final String imageId = "imageId";
        ArrayList<String> images = new ArrayList<String>();
        images.add(imageId);
        images.add(imageId);
        images.add(imageId);

        MockGameCoordinator mockGameCoordinator = Mockito.spy(new MockGameCoordinator());
        mockGameCoordinator.setDownloader(new IDownloader() {
            @Override
            public SafeThread downloadFileToPath(String s, File file, DownloaderListener downloaderListener) {
                return null;
            }

            @Override
            public void downloadData(String s, DownloaderListener downloaderListener) {
                downloaderListener.onCallback(null, Status.SUCCESS);
            }
        });

        ImageGetter imageGetter = Mockito.spy(new ImageGetter(mockGameCoordinator, new IDatabase() {
            @Override
            public void getTotalImagesCount(DatabaseListener<Long> listener) {
                listener.onCallback((long) 5, Status.SUCCESS);
            }

            @Override
            public void getImageIdByIndex(int index, DatabaseListener<String> listener) {
                listener.onCallback(imageId, Status.SUCCESS);
            }

            @Override
            public void getImageDataById(String id, DatabaseListener<ImageData> listener) {
                listener.onCallback(new ImageData(), Status.SUCCESS);
            }
        }));
        imageGetter.init();
        Threadings.sleep(1000);
        verify(imageGetter, times(1)).randomGetImages();
        UpdateMsg updateMsg = new UpdateMsg(UpdateCode.DOWNLOAD_IMAGES, Strings.joinArr(images));
        String updateMsgJson = updateMsg.toJson();
        verify(mockGameCoordinator, times(1)).sendRoomUpdate(eq(updateMsgJson));

        Assert.assertEquals(1, mockGameCoordinator.getInGameUpdateListeners().size());

        imageGetter.downloadImages(updateMsg.getMsg().split(","));
        Threadings.sleep(1000);
        verify(imageGetter, times(3)).addImagePairs(any(ImagePair.class));

    }





}
