package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class WebImage extends Table implements Disposable {

    private String url;
    private Texture tempTexture;
    private Assets assets;
    private Image image;
    private Table root;
    private Broadcaster broadcaster;
    private IPTGame iptGame;
    private String broadcastId;
    private Runnable onResumeRunnable;
    private boolean loadedBefore;

    public WebImage(String url, final Assets assets, Broadcaster broadcaster, IPTGame iptGame) {
        this.broadcaster = broadcaster;
        this.assets = assets;
        this.iptGame = iptGame;
        this.url = url;

        root = new Table();
        root.pad(5);
        this.add(root).expand().fill();

        new DummyButton(this, this.assets);

        if(Strings.isEmpty(url)){
            requestFailed();
            return;
        }

        broadcastId = this.broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                if (obj.getFirst().equals(WebImage.this.url)) {
                    if (st == Status.SUCCESS) {
                        tempTexture = obj.getSecond();
                        requestReceived();
                    } else {
                        requestFailed();
                    }
                }
            }
        });

        onResumeRunnable = new Runnable() {
            @Override
            public void run() {
                onResume();
            }
        };

        onResumeRunnable.run();

        iptGame.addOnResumeRunnable(onResumeRunnable);
    }

    private void loadingImage(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                root.clear();
                Image loadingImage = new Image(assets.getTextures().get(Textures.Name.LOADING_IMAGE));
                root.add(loadingImage).expand().fill().pad(10);
            }
        });
    }

    private void requestReceived(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(!loadedBefore){
                    assets.getTextures().addRef(url, tempTexture);
                    loadedBefore = true;
                }
                image = new Image(tempTexture);
                root.clear();
                root.add(image).expand().fill();
            }
        });
    }

    private void requestFailed(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                image = new Image(assets.getTextures().get(Textures.Name.NO_IMAGE));
                root.clear();
                root.add(image).expand().fill();
            }
        });
    }

    public void onResume(){
        if(tempTexture != null) tempTexture.dispose();
        loadingImage();
        this.broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, url);
    }


    @Override
    public void dispose() {
        assets.getTextures().removeRef(url);
        if(onResumeRunnable != null) iptGame.removeOnResumeRunnable(onResumeRunnable);
        if(broadcastId != null) broadcaster.unsubscribe(broadcastId);
    }
}
