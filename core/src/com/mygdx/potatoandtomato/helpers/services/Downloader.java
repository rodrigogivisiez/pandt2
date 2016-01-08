package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.potatoandtomato.common.Status;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class Downloader implements IDownloader{

    public Downloader() {
    }

    @Override
    public SafeThread downloadFileToPath(final String urlString, final File targetFile, final DownloaderListener listener){
        final SafeThread safeThread = new SafeThread();

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection con = null;
                long completeFileSize;
                long downloadedFileSize = 0;

                try {
                    url = new URL(urlString);
                    con = (HttpURLConnection) url.openConnection();
                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        listener.onCallback(null, Status.FAILED);
                        return;
                    }
                    completeFileSize = con.getContentLength();
                    File file = targetFile;
                    if (file.exists()) file.delete();
                    file.createNewFile();
                    BufferedInputStream bis = new BufferedInputStream(
                            con.getInputStream());

                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(file), 1024);
                    byte[] data = new byte[1024];
                    int x = 0;

                    while ((x = bis.read(data, 0, 1024)) >= 0) {

                        if (safeThread.isKilled()) return;
                        downloadedFileSize += x;

                        // calculate progress
                        final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);
                        listener.onStep(currentProgress);

                        bos.write(data, 0, x);
                    }

                    bos.flush();
                    bis.close();
                    bos.close();

                    listener.onCallback(null, Status.SUCCESS);

                } catch (MalformedInputException malformedInputException) {
                    malformedInputException.printStackTrace();
                    listener.onCallback(null, Status.FAILED);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });

        return safeThread;
    }


    @Override
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
                                    listener.onCallback(result, Status.SUCCESS);
                                } else {    //failed
                                    listener.onCallback(null, Status.FAILED);
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
                                    listener.onCallback(null, Status.FAILED);
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
                                listener.onCallback(null, Status.FAILED);
                            }
                        });
                    }
                });

            }
        });
    }


}
