package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Pair;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class WebImage extends Table implements Disposable {

    private String _url;
    private Texture _tempTexture;
    private Assets _assets;
    private Image _image;
    private Table _root;
    private Broadcaster _broadcaster;

    public WebImage(String url, Assets assets, Broadcaster broadcaster) {
        _broadcaster = broadcaster;
        _assets = assets;
        this._url = url;

        _root = new Table();
        _root.pad(5);
        Image loadingImage = new Image(_assets.getTextures().get(Textures.Name.LOADING_IMAGE));
        _root.add(loadingImage).expand().fill().pad(10);
        this.add(_root).expand().fill();

        new DummyButton(this, _assets);

        _broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                if (obj.getFirst().equals(_url)) {
                    if (st == Status.SUCCESS) {
                        _tempTexture = obj.getSecond();
                        requestReceived();
                    } else {
                        requestFailed();
                    }
                    _broadcaster.unsubscribe(this.getId());
                }
            }
        });

        _broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, url);

    }

    private void requestReceived(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _image = new Image(_tempTexture);
                _root.clear();
                _root.add(_image).expand().fill();
            }
        });
    }

    private void requestFailed(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _image = new Image(_assets.getTextures().get(Textures.Name.NO_IMAGE));
                _root.clear();
                _root.add(_image).expand().fill();
            }
        });
    }


    @Override
    public void dispose() {
        if(_tempTexture != null) _tempTexture.dispose();
    }
}
