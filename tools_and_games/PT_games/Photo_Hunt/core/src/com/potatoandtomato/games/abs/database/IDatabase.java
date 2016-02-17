package com.potatoandtomato.games.abs.database;

import com.potatoandtomato.games.models.ImageData;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public interface IDatabase {

    void getTotalImagesCount(DatabaseListener<Long> listener);

    void getImageIdByIndex(int index, DatabaseListener<String> listener);

    void getImageDataById(String id, DatabaseListener<ImageData> listener);

    void removeImageById(String id, DatabaseListener listener);

}
