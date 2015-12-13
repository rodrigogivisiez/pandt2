package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.DownloaderListener;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Caches;
import com.mygdx.potatoandtomato.helpers.utils.Downloader;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class WebImage extends Table implements Disposable {

    private String _url;
    private Texture _tempTexture;
    private Caches _caches;
    private Downloader _downloader;
    private Textures _textures;

    public WebImage(String url, Textures textures) {

        _caches = Caches.getInstance();
        _textures = textures;
        _downloader = Downloader.getInstance();
        this._url = url;

        new DummyButton(this, _textures);

        if(_caches.exist(_url)){
            getFromCache();
        }
        else{
            downloadTextureAsync();
        }

    }

    private void downloadTextureAsync() {

        _downloader.downloadData(_url, new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                if(st == Status.SUCCESS){
                    processTextureBytes(bytes);
                }
                else{
                    downloadImageFailed();
                }
            }
        });
    }

    private void processTextureBytes(byte[] textureBytes) {
        try {
            Pixmap pixmap = new Pixmap(textureBytes, 0, textureBytes.length);
            _tempTexture = new Texture(pixmap);
            _tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Drawable draw = new SpriteDrawable(new Sprite(_tempTexture));
            this.setBackground(draw);
            _caches.add(_url, _tempTexture, textureBytes.length);
            pixmap.dispose();

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            textureBytes = null;
        }
    }

    private void getFromCache(){
        Drawable draw = new SpriteDrawable(new Sprite((Texture) _caches.get(_url)));
        this.setBackground(draw);
    }

    private void downloadImageFailed(){
        this.setBackground(new TextureRegionDrawable(_textures.getNoImage()));
    }


    @Override
    public void dispose() {
        if(_tempTexture != null) _tempTexture.dispose();
    }
}
