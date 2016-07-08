package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 7/7/2016.
 */
public class FileData {

    private String modifiedAt;
    private String url;
    private long size;

    public FileData() {
    }

    public FileData(String modifiedAt, String url, long size) {
        this.modifiedAt = modifiedAt;
        this.url = url;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
