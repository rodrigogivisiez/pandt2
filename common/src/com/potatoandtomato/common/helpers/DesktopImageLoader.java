package com.potatoandtomato.common.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.potatoandtomato.common.absints.DownloaderListener;
import com.potatoandtomato.common.utils.Downloader;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.util.HashMap;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class DesktopImageLoader {

    private Downloader _downloader;
    private Broadcaster _broadcaster;

    public DesktopImageLoader(Broadcaster broadcaster) {

        _broadcaster = broadcaster;
        _downloader = new Downloader();

        _broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_REQUEST, new BroadcastListener<String>() {
            @Override
            public void onCallback(String url, Status st) {
                downloadImage(url);
            }
        });

    }

    private void downloadImage(final String url){
        _downloader.downloadData(url, new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                if(st == Status.SUCCESS){
                    Texture texture = processTextureBytes(bytes);
                    imageLoaded(url, texture);
                }
                else{
                    imageFailed(url);
                }
            }
        });
    }

    private Texture processTextureBytes(byte[] textureBytes) {
        try {
            Pixmap pixmap = new Pixmap(textureBytes, 0, textureBytes.length);
            Texture texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            pixmap.dispose();
            return texture;

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    private void imageLoaded(final String url, final Texture image){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pair<String, Texture> pair = new Pair(url, image);
                _broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, pair, Status.SUCCESS);
            }
        });
    }

    private void imageFailed(final String url){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pair<String, Texture> pair = new Pair(url, null);
                _broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, pair, Status.FAILED);
            }
        });
    }


}
