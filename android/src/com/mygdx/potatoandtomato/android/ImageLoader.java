package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class ImageLoader {

    private HashMap<String, Target> _requestMap;
    private ArrayList<String> _retriedUrl;
    private Activity _activity;
    private Broadcaster _broadcaster;

    public ImageLoader(Activity activity, Broadcaster broadcaster) {
        this._broadcaster = broadcaster;
        this._activity = activity;
        _requestMap = new HashMap();
        _retriedUrl = new ArrayList();

        _broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_REQUEST, new BroadcastListener<String>() {
            @Override
            public void onCallback(String url, Status st) {
                load(url);
            }
        });

    }

    private void load(final String url){
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Target loadtarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        _requestMap.remove(url);
                        _retriedUrl.remove(url);
                        imageLoaded(bitmap, url);
                    }

                    @Override
                    public void onBitmapFailed(Drawable drawable) {
                        if(_retriedUrl.contains(url)){
                            _requestMap.remove(url);
                            imageFailed(url);
                            _retriedUrl.remove(url);
                        }
                        else{
                            _retriedUrl.add(url);
                            _activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.with(_activity)
                                            .load(url)
                                            .into(_requestMap.get(url));
                                }
                            });

                        }
                    }

                    @Override
                    public void onPrepareLoad(Drawable drawable) {

                    }
                };

                _requestMap.put(url, loadtarget);


                Picasso.with(_activity)
                        .load(url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(_requestMap.get(url));
            }
        });

    }

    private void imageLoaded(final Bitmap bitmap, final String url){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                //bitmap.recycle();
                Pair<String, Texture> pair = new Pair(url, tex);
                _broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, pair, Status.SUCCESS);
            }
        });

    }

    private void imageFailed(final String url){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pair<String, Texture> pair = new Pair(url, null);
                _broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, pair, Status.FAILED);
            }
        });
    }



}
