package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.helpers.services.BackBlazeStorage;
import com.potatoandtomato.common.utils.Downloader;
import org.junit.Test;

/**
 * Created by SiongLeng on 16/4/2016.
 */
public class TestBackBlazeStorage extends TestAbstract {

    @Test
    public void testDownload(){
        BackBlazeStorage backBlazeStorage = new BackBlazeStorage(new Downloader());
        backBlazeStorage.getUploadedFile("", null, null);


    }

}
