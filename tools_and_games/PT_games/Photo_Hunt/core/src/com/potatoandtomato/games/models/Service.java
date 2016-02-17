package com.potatoandtomato.games.models;

import com.potatoandtomato.games.abs.database.IDatabase;
import com.potatoandtomato.games.assets.Assets;
import com.potatoandtomato.games.helpers.ImageGetter;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class Service {

    private IDatabase database;
    private Assets assets;
    private ImageGetter imageGetter;

    public Service(IDatabase database, Assets assets, ImageGetter imageGetter) {
        this.database = database;
        this.assets = assets;
        this.imageGetter = imageGetter;
    }

    public ImageGetter getImageGetter() {
        return imageGetter;
    }

    public void setImageGetter(ImageGetter imageGetter) {
        this.imageGetter = imageGetter;
    }

    public IDatabase getDatabase() {
        return database;
    }

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public Assets getAssets() {
        return assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }
}
