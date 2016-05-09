package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.files.FileHandle;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.absintflis.uploader.UploadListener;
import com.potatoandtomato.common.absints.IDownloader;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by SiongLeng on 16/4/2016.
 */
public class BackBlazeStorage  implements IUploader {

    private String _accId = "32c6c633ddf5";
    private String _appKey = "001c1baf0b66b1e4339560118d40a0de572fbfd75d";
    private UploadService _uploadService;
    private IDownloader _downloader;
    private Map<String, Object> jsonMap;

    public BackBlazeStorage(IDownloader downloader) {
        String accountId = _accId; // Obtained from your B2 account page.
        String applicationKey = _appKey; // Obtained from your B2 account page.
        HttpURLConnection connection = null;
        String headerForAuthorizeAccount = "Basic " + DatatypeConverter.printBase64Binary((accountId + ":" + applicationKey).getBytes());
        try {
            URL url = new URL("https://api.backblaze.com/b2api/v1/b2_authorize_account");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", headerForAuthorizeAccount);
            InputStream in = new BufferedInputStream(connection.getInputStream());

            ObjectMapper mapper = new ObjectMapper();
            jsonMap = mapper.readValue(in, Map.class);

            System.out.println("success");
            // You will need to implement myInputStreamReader.
           // myInputStreamReader(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }



    @Override
    public void uploadFile(FileHandle file, UploadListener<String> listener) {

    }

    @Override
    public void getUploadedFile(String fileName, FileHandle saveToFile, UploadListener<FileHandle> listener) {
        String accountAuthorizationToken = (String) jsonMap.get("authorizationToken");  // Provided by b2_authorize_account
        String downloadUrl = (String) jsonMap.get("downloadUrl"); // Provided by b2_authorize_account
        String bucketName = "pandt-storage"; // The bucket ID where the file exists
        String fileName1 = "test.txt"; // The file name of the file you want to download.
        HttpURLConnection connection = null;
        byte downloadedData[] = null;
        try {
            URL url = new URL(downloadUrl + "/file/" + bucketName + "/" + fileName1);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Authorization", accountAuthorizationToken);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            FileOutputStream outputStream =
                    new FileOutputStream(new File("test.txt"));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = connection.getInputStream().read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            //downloadedData = myDataInputStreamHandler(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

    }
}
