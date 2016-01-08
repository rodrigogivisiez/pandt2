package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Caches;
import com.mygdx.potatoandtomato.helpers.utils.Pair;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class WebImage extends Table implements Disposable {

    private String _url;
    private Texture _tempTexture;
    private Assets _assets;
    private Image _image;
    private Table _root;

    public WebImage(String url, Assets assets) {

        _assets = assets;
        this._url = url;

        _root = new Table();
        _root.pad(5);
        _root.setBackground(new TextureRegionDrawable(_assets.getWebImageLoading()));
        this.add(_root).expand().fill();

        new DummyButton(this, _assets);

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                if(obj.getFirst().equals(_url)){
                    if(st == Status.SUCCESS){
                        _tempTexture = obj.getSecond();
                        requestReceived();
                    }
                    else{
                        requestFailed();
                    }
                    Broadcaster.getInstance().unsubscribe(this.getId());
                }
            }
        });

        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, url);

    }

    private void requestReceived(){
        _image = new Image(_tempTexture);
        _root.add(_image).expand().fill();
    }

    private void requestFailed(){
        _image = new Image(_assets.getNoImage());
        _root.add(_image).expand().fill();
    }


    @Override
    public void dispose() {
        if(_tempTexture != null) _tempTexture.dispose();
    }
}
