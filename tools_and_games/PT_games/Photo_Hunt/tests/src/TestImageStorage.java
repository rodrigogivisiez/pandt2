import com.badlogic.gdx.graphics.Texture;
import com.potatoandtomato.common.absints.DownloaderListener;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.DatabaseListener;
import com.potatoandtomato.games.absintf.ImageStorageListener;
import com.potatoandtomato.games.absintf.mockings.MockModel;
import com.potatoandtomato.games.models.ImageDetails;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.main.ImageStorage;
import com.potatoandtomato.games.services.Database;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 7/4/2016.
 */
public class TestImageStorage extends TestAbstract {

    @Test
    public void testInitiateDownloads_Randomize(){

        Services services = Mockings.mockServices(_game.getCoordinator());
        services.setDatabase(Mockito.spy(new Database(null){
            @Override
            public void getLastImageIndex(DatabaseListener<Integer> listener) {
                listener.onCallback(100, Status.SUCCESS);
            }

            @Override
            public void getImageDetailsByIndex(int index, DatabaseListener<ImageDetails> listener) {
                ImageDetails imageDetails = MockModel.mockImageDetails();
                imageDetails.setId(String.valueOf(index));
                listener.onCallback(imageDetails, Status.SUCCESS);
            }

            @Override
            public void getImageDetailsById(String id, DatabaseListener<ImageDetails> listener) {
                ImageDetails imageDetails = MockModel.mockImageDetails();
                imageDetails.setId(id);
                listener.onCallback(imageDetails, Status.SUCCESS);
            }

        }));


        _game.getCoordinator().setDownloader(new IDownloader() {
            @Override
            public SafeThread downloadFileToPath(String s, File file, DownloaderListener downloaderListener) {
                return null;
            }

            @Override
            public void downloadData(String s, DownloaderListener downloaderListener) {
                downloaderListener.onCallback(new byte[]{1}, Status.SUCCESS);
            }
        });

        final ImageStorage imageStorage = Mockito.spy(new ImageStorage(services, _game.getCoordinator()){
            @Override
            public Texture processTextureBytes(byte[] textureBytes) {
                return Mockito.mock(Texture.class);
            }
        });
        imageStorage.setDownloadPeriod(3000);

        doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                imageStorage.receivedDownloadRequest((ArrayList) invocation.getArguments()[0]);
                return 0;
            }
        }).when(services.getRoomMsgHandler()).sendDownloadImageRequest(any(ArrayList.class));

        imageStorage.startMonitor();

        Threadings.sleep(1000);

        verify(imageStorage, times(5)).downloadImages(any(ImageDetails.class), anyInt());
        verify(services.getRoomMsgHandler(), times(1)).sendDownloadImageRequest(any(ArrayList.class));

        Threadings.sleep(5000);

        Assert.assertEquals(true, imageStorage.getImagePairs().size() >= 5);
        verify(imageStorage, atLeast(5)).downloadImages(any(ImageDetails.class), anyInt());

        imageStorage.getImagePairs().get(0).getImageDetails().setId("test");

        imageStorage.pop(new ImageStorageListener() {
            @Override
            public void onPopped(ImagePair imagePair) {
                Assert.assertEquals("test", imagePair.getImageDetails().getId());
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        Assert.assertEquals(true, imageStorage.getImagePairs().size() >= 4);

        Threadings.sleep(5000);

        Assert.assertEquals(true, imageStorage.getImagePairs().size() >= 5 && imageStorage.getImagePairs().size() <= 10);

        imageStorage.dispose();

    }

    @Test
    public void testInitiateDownloads_NoRandomize(){
        Services services = Mockings.mockServices(_game.getCoordinator());
        services.setDatabase(Mockito.spy(new Database(null){
            @Override
            public void getLastImageIndex(DatabaseListener<Integer> listener) {
                listener.onCallback(100, Status.SUCCESS);
            }

            @Override
            public void getImageDetailsByIndex(int index, DatabaseListener<ImageDetails> listener) {
                ImageDetails imageDetails = MockModel.mockImageDetails();
                imageDetails.setId(String.valueOf(index));
                listener.onCallback(imageDetails, Status.SUCCESS);
            }

            @Override
            public void getImageDetailsById(String id, DatabaseListener<ImageDetails> listener) {
                ImageDetails imageDetails = MockModel.mockImageDetails();
                imageDetails.setId(id);
                listener.onCallback(imageDetails, Status.SUCCESS);
            }

        }));

        _game.getCoordinator().setDownloader(new IDownloader() {
            @Override
            public SafeThread downloadFileToPath(String s, File file, DownloaderListener downloaderListener) {
                return null;
            }

            @Override
            public void downloadData(String s, DownloaderListener downloaderListener) {
                downloaderListener.onCallback(new byte[]{1}, Status.SUCCESS);
            }
        });

        final ImageStorage imageStorage = Mockito.spy(new ImageStorage(services, _game.getCoordinator()){
            @Override
            public Texture processTextureBytes(byte[] textureBytes) {
                return Mockito.mock(Texture.class);
            }
        });

        imageStorage.setDownloadPeriod(3000);

        doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                imageStorage.receivedDownloadRequest((ArrayList) invocation.getArguments()[0]);
                ArrayList items = (ArrayList) invocation.getArguments()[0];
                for(int i = 0; i < 5; i++){
                    Assert.assertEquals(String.valueOf(i), items.get(i).toString());
                }
                return 0;
            }
        }).when(services.getRoomMsgHandler()).sendDownloadImageRequest(any(ArrayList.class));

        imageStorage.setRandomize(false);
        imageStorage.startMonitor();

        Threadings.sleep(1000);

        ArrayList<ImagePair> imagePairs = imageStorage.getImagePairs();
        for(int i = 0; i < 5; i++){
            Assert.assertEquals(String.valueOf(i), imagePairs.get(i).getImageDetails().getId());
        }

        imageStorage.dispose();

    }

    @Test
    public void testReceivedDownloadRequests(){
        Services services = Mockings.mockServices(_game.getCoordinator());
        services.setDatabase(Mockito.spy(new Database(null){
            @Override
            public void getLastImageIndex(DatabaseListener<Integer> listener) {
                listener.onCallback(100, Status.SUCCESS);
            }

            @Override
            public void getImageDetailsById(String id, DatabaseListener<ImageDetails> listener) {
                ImageDetails imageDetails = MockModel.mockImageDetails();
                imageDetails.setId(id);
                listener.onCallback(imageDetails, Status.SUCCESS);
            }
        }));

        _game.getCoordinator().setDownloader(new IDownloader() {
            @Override
            public SafeThread downloadFileToPath(String s, File file, DownloaderListener downloaderListener) {
                return null;
            }

            @Override
            public void downloadData(String s, DownloaderListener downloaderListener) {
                downloaderListener.onCallback(new byte[]{1}, Status.SUCCESS);
            }
        });

        ImageStorage imageStorage = Mockito.spy(new ImageStorage(services, _game.getCoordinator()){
            @Override
            public Texture processTextureBytes(byte[] textureBytes) {
                return Mockito.mock(Texture.class);
            }
        });

        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            ids.add(String.valueOf(i));
        }

        imageStorage.receivedDownloadRequest(ids);

        Threadings.sleep(1000);


        ArrayList<ImagePair> imagePairs = imageStorage.getImagePairs();
        Assert.assertEquals(true, imagePairs.size() >=5 );
        for(int i = 0; i < 5; i++){
            Assert.assertEquals(String.valueOf(i), imagePairs.get(i).getImageDetails().getId());
        }

        imageStorage.dispose();

    }


}
