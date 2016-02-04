package connection_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.potatoandtomato.absintflis.uploader.UploadListener;
import com.mygdx.potatoandtomato.helpers.services.App42Uploader;
import com.potatoandtomato.common.Downloader;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.common.Status;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by SiongLeng on 12/1/2016.
 */
public class TestUploader extends TestAbstract {

    @Test
    public void testUploadDownload(){

        final boolean[] waiting = {true};

        final byte[] bytes = new byte[] { 20, 3, -2, 10 };
        final FileHandle file = Gdx.files.local("test.bin");
        file.writeBytes(bytes, false);

        App42Uploader ftpUploader = new App42Uploader(new Downloader());
        ftpUploader.uploadFile(file, new UploadListener<String>() {
            @Override
            public void onCallBack(String result, Status status) {
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;
        file.delete();

        ftpUploader.getUploadedFile("test.bin", file, new UploadListener<FileHandle>() {
            @Override
            public void onCallBack(FileHandle result, Status status) {
                Assert.assertEquals(true, result.exists());
                Assert.assertEquals("test.bin", result.file().getName());
                byte[] resultBytes = result.readBytes();
                for(int i = 0; i < resultBytes.length; i++){
                    Assert.assertEquals(bytes[i], resultBytes[i]);
                }
                Assert.assertEquals(Status.SUCCESS, status);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        file.delete();

    }



}
