import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

import java.io.File;

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

    public void uploadImage(final File file, final String id, int imageIndex, ImageDetails imageDetails){
        final String fileName = id + "_" + imageIndex;

        Upload upload = _uploadService.uploadFile(fileName, file.getAbsolutePath(),
                UploadFileType.IMAGE, id + " photohunt file");
        String jsonResponse = upload.toString();
        if(jsonResponse.contains("\"success\":true")){
            String url = upload.getFileList().get(0).getUrl();
            if(imageIndex == 1){
                imageDetails.setImageOneUrl(url);
            }
            else{
                imageDetails.setImageTwoUrl(url);
            }
            System.out.println("Image uploaded successfully at " + url);
            //onFinish.run();
        }

    }


}
