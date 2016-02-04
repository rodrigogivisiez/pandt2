package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.files.FileHandle;
import com.potatoandtomato.common.DownloaderListener;
import com.potatoandtomato.common.IDownloader;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.absintflis.uploader.UploadListener;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.common.Status;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

/**
 * Created by SiongLeng on 13/1/2016.
 */
public class App42Uploader implements IUploader {

    private String _appKey = "08e25748189dccf0d82070e17c87225350614c754e8e0d511128d65da9d27956";
    private String _secretKey = "ed573d5aa22d343d8b187e610007f299c9811bd3594c94d8ffe3f789a69de960";
    private UploadService _uploadService;
    private IDownloader _downloader;

    public App42Uploader(IDownloader downloader) {
        this._downloader = downloader;
        ServiceAPI api = new ServiceAPI(_appKey, _secretKey);
        _uploadService = api.buildUploadService();
    }

    @Override
    public void uploadFile(final FileHandle file, final UploadListener<String> listener) {
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                try{
                    Upload upload = _uploadService.uploadFile(file.name(), file.file().getAbsolutePath(), UploadFileType.BINARY, "audio file");
                    String jsonResponse = upload.toString();
                    if(jsonResponse.contains("\"success\":true")){
                        listener.onCallBack("", Status.SUCCESS);
                    }
                    else{
                        listener.onCallBack("", Status.FAILED);
                    }
                }
                catch (App42Exception ex){
                    listener.onCallBack("", Status.FAILED);
                }
            }
        });
    }

    @Override
    public void getUploadedFile(final String fileName, final FileHandle saveToFile, final UploadListener<FileHandle> listener) {
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                Upload upload = _uploadService.getFileByName(fileName);
                String jsonResponse = upload.toString();
                if(jsonResponse.contains("\"success\":true")){
                    _downloader.downloadFileToPath(upload.getFileList().get(0).getUrl(), saveToFile.file(), new DownloaderListener() {
                        @Override
                        public void onCallback(byte[] bytes, Status st) {
                            if(st == Status.SUCCESS){
                                listener.onCallBack(saveToFile, Status.SUCCESS);
                            }
                            else{
                                listener.onCallBack(null, Status.FAILED);
                            }
                        }
                    });
                }
                else{
                    listener.onCallBack(null, Status.FAILED);
                }
            }
        });
    }
}
