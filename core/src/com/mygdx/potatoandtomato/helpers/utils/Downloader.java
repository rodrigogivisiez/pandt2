package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.mygdx.potatoandtomato.absintflis.DownloaderListener;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.MalformedInputException;
import java.util.Map;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class Downloader {

    private static Downloader _instance;
    public static Downloader getInstance(){
        if(_instance == null) _instance = new Downloader();
        return _instance;
    }

    public Downloader() {
    }

    public void downloadFileToPath(String urlString, File targetFile){
        URL url = null;
        URLConnection con = null;
        int i;
        try {
            url = new URL(urlString);
            con = url.openConnection();
            File file = targetFile;
            if(file.exists()) file.delete();
            file.createNewFile();
            BufferedInputStream bis = new BufferedInputStream(
                    con.getInputStream());

            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));

            while ((i = bis.read()) != -1) {
                bos.write(i);
            }
            bos.flush();
            bis.close();
            bos.close();
        } catch (MalformedInputException malformedInputException) {
            malformedInputException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }



    public void downloadData(final String url, final DownloaderListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                final Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
                httpRequest.setUrl(url);
                Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                        final HttpStatus status = httpResponse.getStatus();
                        final byte[] result = httpResponse.getResult();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (status.getStatusCode() >= 200 && status.getStatusCode() < 300) {
                                    listener.onCallback(result, DownloaderListener.Status.SUCCESS);
                                } else {    //failed
                                    listener.onCallback(null, DownloaderListener.Status.FAILED);
                                }
                            }
                        });

                    }

                    @Override
                    public void failed(Throwable t) {
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onCallback(null, DownloaderListener.Status.FAILED);
                                }
                            });

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void cancelled() {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCallback(null, DownloaderListener.Status.FAILED);
                            }
                        });
                    }
                });

            }
        });
    }


}
