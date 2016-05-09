package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.files.FileHandle;
import com.mygdx.potatoandtomato.utils.Terms;
import com.potatoandtomato.common.absints.DownloaderListener;
import com.potatoandtomato.common.absints.IDownloader;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.absintflis.uploader.UploadListener;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.enums.Status;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

/**
 * Created by SiongLeng on 13/1/2016.
 */
public class App42Uploader implements IUploader {

    private String _appKey = Terms.WARP_API_KEY;
    private String _secretKey = Terms.WARP_SECRET_KEY;
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
