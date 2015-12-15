package com.mygdx.potatoandtomato.absintflis.downloader;

import com.mygdx.potatoandtomato.helpers.utils.SafeThread;

import java.io.File;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public interface IDownloader {

    SafeThread downloadFileToPath(String urlString, File targetFile, final DownloaderListener listener);

    void downloadData(final String url, final DownloaderListener listener);

}
