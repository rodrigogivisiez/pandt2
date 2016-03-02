package com.ptuploader.process;

import com.ptuploader.utils.Logs;
import com.ptuploader.utils.ProgressInputStream;
import com.shephertz.app42.paas.sdk.android.*;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;
import com.shephertz.app42.paas.sdk.android.util.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by SiongLeng on 2/3/2016.
 */
public class Uploads {

    private final String TEST_APIKEY = "c7236c0f55a51bcdde0415e639f2e87f73178a02cdd5d41485e19ad15334c56f";
    private final String TEST_SECRETKEY = "eab7966ea03b163de7bcba6cfae3156ed0ead16bf357233746507d32796fee3a";
    private final String API_KEY = "08e25748189dccf0d82070e17c87225350614c754e8e0d511128d65da9d27956";
    private final String SECRET_KEY = "ed573d5aa22d343d8b187e610007f299c9811bd3594c94d8ffe3f789a69de960";

    private Paths paths;
    private UploadService _uploadService;
    private Logs logs;

    public Uploads(Paths paths, Logs logs, boolean isTesting) {
        this.paths = paths;
        this.logs = logs;
        ServiceAPI api = new ServiceAPI(isTesting ? TEST_APIKEY : API_KEY, isTesting ? TEST_SECRETKEY : SECRET_KEY);
        _uploadService = api.buildUploadService();
    }

    public void uploadIcon(final Details details, final Runnable onFinish){
        logs.write("Uploading Icon...");
        final String fileName = details.getDetailsMap().get(details.ABBR) + "_icon.png";

        final Runnable uploadProcess = new Runnable() {
            @Override
            public void run() {
                Upload upload = _uploadService.uploadFile(fileName, paths.getIconFile().getAbsolutePath(),
                        UploadFileType.IMAGE, details.getAbbr() + " icon file");
                String jsonResponse = upload.toString();
                if(jsonResponse.contains("\"success\":true")){
                    String iconUrl = upload.getFileList().get(0).getUrl();
                    details.setIconUrl(iconUrl);
                    logs.write("Icon upload successfully at " + details.getIconUrl());
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
            logs.write("Icon upload failed!");
        }
    }

    public void uploadGame(final Details details, final Runnable onFinish){
        logs.write("Uploading Game...");
        final String fileName = details.getDetailsMap().get(details.ABBR) + "_game.zip";

        final Runnable uploadProcess = new Runnable() {
            @Override
            public void run() {
                Upload upload = _uploadService.uploadFile(fileName, paths.getAssetsZip().getAbsolutePath(),
                        UploadFileType.OTHER, details.getAbbr() + " zip file");
                String jsonResponse = upload.toString();
                if(jsonResponse.contains("\"success\":true")){
                    String gameUrl = upload.getFileList().get(0).getUrl();
                    details.setGameUrl(gameUrl);
                    logs.write("Game upload successfully at " + details.getGameUrl());
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
            logs.write("Game upload failed!");
        }
    }


}
