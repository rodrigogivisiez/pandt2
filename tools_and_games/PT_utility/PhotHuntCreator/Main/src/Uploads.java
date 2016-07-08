import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SiongLeng on 2/3/2016.
 */
public class Uploads {

    private final String API_KEY = "08e25748189dccf0d82070e17c87225350614c754e8e0d511128d65da9d27956";
    private final String SECRET_KEY = "ed573d5aa22d343d8b187e610007f299c9811bd3594c94d8ffe3f789a69de960";

    private UploadService _uploadService;

    public Uploads() {
        ServiceAPI api = new ServiceAPI(API_KEY, SECRET_KEY);
        _uploadService = api.buildUploadService();
    }

    public void uploadImage(final File file, final String id, final int imageIndex, final ImageDetails imageDetails, final Runnable onFinish){
        final String fileName = id + "_" + imageIndex;

        final Runnable uploadProcess = new Runnable() {
            @Override
            public void run() {
                Upload upload = _uploadService.uploadFile(fileName, file.getAbsolutePath(),
                        UploadFileType.IMAGE, id + " photohunt file");
                String jsonResponse = upload.toString();
                if(jsonResponse.contains("\"success\":true")){
                    String fileUrl = upload.getFileList().get(0).getUrl();

                    URL url = null;
                    try {
                        url = new URL(fileUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        if (con.getResponseCode() != HttpURLConnection.HTTP_OK ) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            uploadImage(file, id, imageIndex, imageDetails, onFinish);
                            return;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(imageIndex == 1){
                        imageDetails.setImageOneUrl(fileUrl);
                    }
                    else{
                        imageDetails.setImageTwoUrl(fileUrl);
                    }
                    onFinish.run();
                }
            }
        };

        try{
            _uploadService.removeFileByName(fileName, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    uploadProcess.run();
                }

                @Override
                public void onException(Exception e) {
                    uploadProcess.run();
                }
            });
        }
        catch (App42Exception ex){
            ex.printStackTrace();
        }
    }
}
