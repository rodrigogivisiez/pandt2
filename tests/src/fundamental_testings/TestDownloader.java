package fundamental_testings;

import abstracts.TestAbstract;
import com.potatoandtomato.common.absints.DownloaderListener;
import com.potatoandtomato.common.utils.Downloader;
import com.potatoandtomato.common.enums.Status;
import helpers.T_Services;
import helpers.T_Threadings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipFile;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class TestDownloader extends TestAbstract {

    @Before
    public void setUp() throws Exception {
        File f = new File("testdownload.zip");
        if(f.exists()) f.delete();
    }

    @After
    public void tearDown() throws Exception {
        File f = new File("testdownload.zip");
        if(f.exists()) f.delete();
    }

    @Test
    public void testDownloadDataSuccess(){
        final boolean[] waiting = {true};
        T_Services.mockServices(new Downloader()).getDownloader().downloadData("http://www.potato-and-tomato.com/covered_chess/icon.png", new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, bytes.length > 0);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            T_Threadings.sleep(100);
        }
    }

    @Test
    public void testDownloadDataFailed(){
        final boolean[] waiting = {true};
        T_Services.mockServices(new Downloader()).getDownloader().downloadData("http://www.potato-and-tomato.com/nothinghere.png", new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                Assert.assertEquals(Status.FAILED, st);
                Assert.assertEquals(true, bytes == null);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            T_Threadings.sleep(100);
        }
    }


    @Test
    public void testDownloadFile(){
        final double[] finalPercentage = new double[1];
        final boolean[] waiting = {true};
        final ArrayList<Double> percents = new ArrayList<Double>();

        T_Services.mockServices(new Downloader()).getDownloader().downloadFileToPath("http://www.potato-and-tomato.com/covered_chess/assets.zip", new File("testdownload.zip"), new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, isZipFile(new File("testdownload.zip")));
                new File("testdownload.zip").delete();
                waiting[0] = false;
            }

            @Override
            public void onStep(double percentage) {
                super.onStep(percentage);
                finalPercentage[0] = percentage;
                percents.add(percentage);
            }
        });

        while (waiting[0]){
            T_Threadings.sleep(100);
        }

        Assert.assertEquals(100, finalPercentage[0], 0);
        for(int i =0; i< percents.size();i++){
            if(!(i == 0 || i == percents.size()-1)){
                Assert.assertEquals(true, percents.get(i) <= percents.get(i+1));
            }
        }
    }

    @Test
    public void testDownloadFileFailed(){
        final double[] finalPercentage = new double[1];
        final boolean[] waiting = {true};
        T_Services.mockServices(new Downloader()).getDownloader().downloadFileToPath("http://www.potato-and-tomato.com/covered_chess/nothinghere.zip", new File("testdownload.zip"), new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                Assert.assertEquals(Status.FAILED, st);
                Assert.assertEquals(false, isZipFile(new File("testdownload.zip")));
                waiting[0] = false;
            }

            @Override
            public void onStep(double percentage) {
                super.onStep(percentage);
                finalPercentage[0] = percentage;
            }
        });

        while (waiting[0]){
            T_Threadings.sleep(100);
        }

        Assert.assertEquals(0, finalPercentage[0], 0);
    }





    private boolean isZipFile(File f){
        ZipFile zipfile = null;
        if(!f.exists()) return false;
        try {
            zipfile = new ZipFile(f);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                    zipfile = null;
                }
            } catch (IOException e) {
            }
        }
    }


}
