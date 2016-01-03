package com.mygdx.potatoandtomato.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.helpers.services.Downloader;
import com.mygdx.potatoandtomato.helpers.utils.Pair;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class ImageLoader {

    private Downloader _downloader;

    public ImageLoader() {

        _downloader = new Downloader();

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_IMAGE_REQUEST, new BroadcastListener<String>() {
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
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pair<String, Texture> pair = new Pair(url, image);
                Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, pair, BroadcastListener.Status.SUCCESS);
            }
        });
    }

    private void imageFailed(final String url){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pair<String, Texture> pair = new Pair(url, null);
                Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, pair, BroadcastListener.Status.FAILED);
            }
        });
    }


}
