package fundamental_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.DownloaderListener;
import com.mygdx.potatoandtomato.helpers.utils.Downloader;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import helpers.T_Threadings;
import org.junit.Assert;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class TestDownloader extends TestAbstract {

    @Test
    public void testDownloadDataSuccess(){
        final boolean[] waiting = {true};
        Downloader.getInstance().downloadData("http://www.potato-and-tomato.com/covered_chess/icon.png", new DownloaderListener() {
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
        Downloader.getInstance().downloadData("http://www.potato-and-tomato.com/nothinghere.png", new DownloaderListener() {
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

}
