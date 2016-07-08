package com.ptuploader.process;

import com.ptuploader.models.FileData;
import com.ptuploader.utils.Logs;
import com.ptuploader.utils.ProgressInputStream;
import com.shephertz.app42.paas.sdk.android.*;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;
import com.shephertz.app42.paas.sdk.android.util.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

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

    public void uploadIcon(final Details details, final int tryNumber, final Runnable onFinish){
        logs.write("*"+ tryNumber + "Uploading Icon...");
        final String fileName = details.getDetailsMap().get(details.ABBR) + "_icon.png";

        final Runnable uploadProcess = new Runnable() {
            @Override
            public void run() {
                Upload upload = _uploadService.uploadFile(fileName, paths.getIconFile().getAbsolutePath(),
                        UploadFileType.IMAGE, details.getAbbr() + " icon file");
                String jsonResponse = upload.toString();
                if(jsonResponse.contains("\"success\":true")){
                    String iconUrl = upload.getFileList().get(0).getUrl();


                    URL url = null;
                    try {
                        url = new URL(iconUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            uploadIcon(details, tryNumber+1, onFinish);
                            return;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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

    public void uploadFile(final Details details, final String fileName, final FileData fileData, final int tryNumber, final Runnable onFinish){
        final String encodedFileName = encodeFileName(details, fileName);

        logs.write("*"+ tryNumber + " Uploading " + encodedFileName);

        final Runnable uploadProcess = new Runnable() {
            @Override
            public void run() {
                Upload upload = _uploadService.uploadFile(encodedFileName, fileData.getAbsolutePath(),
                        UploadFileType.OTHER, encodedFileName + "_" + fileData.getModifiedAt());
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
                            uploadFile(details, fileName, fileData, tryNumber+1, onFinish);
                            return;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    fileData.setUrl(fileUrl);
                    logs.write(encodedFileName + " upload successfully.");
                    onFinish.run();
                }
            }
        };

        try{
            _uploadService.removeFileByName(encodedFileName, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    uploadProcess.run();
                }

                @Override
                public void onException(Exception e) {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
                    uploadProcess.run();
                }
            });
        }
        catch (App42Exception ex){
            ex.printStackTrace();
            logs.write(encodedFileName + " upload failed!");
        }
    }


    public void deleteFile(final Details details, final String fileName, final Runnable onFinish){
        final String encodedFileName = encodeFileName(details, fileName);

        logs.write("Deleting " + encodedFileName);

        try{
            _uploadService.removeFileByName(encodedFileName, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    onFinish.run();
                }

                @Override
                public void onException(Exception e) {
                    onFinish.run();
                }
            });
        }
        catch (App42Exception ex){
            ex.printStackTrace();
            logs.write(encodedFileName + " delete failed!");
        }
    }

    private String encodeFileName(final Details details, String filename){
        String encodedFileName =  filename.replaceAll("[^\\p{L}\\p{Nd}]+", "__");
        return  details.getAbbr() + ".." + encodedFileName;
    }

}
