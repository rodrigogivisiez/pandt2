package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class ImageData {

    private String id;
    private long index;
    private String json;

    public ImageData() {
    }

    public ImageData(String id, long index, String json) {
        this.id = id;
        this.index = index;
        this.json = json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
