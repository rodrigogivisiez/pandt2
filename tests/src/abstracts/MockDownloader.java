package abstracts;

import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;

import java.io.File;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class MockDownloader implements IDownloader {
    @Override
    public SafeThread downloadFileToPath(String urlString, File targetFile, DownloaderListener listener) {
        listener.onStep(100);
        listener.onCallback(null, DownloaderListener.Status.SUCCESS);
        return new SafeThread();
    }

    @Override
    public void downloadData(String url, DownloaderListener listener) {

    }
}
