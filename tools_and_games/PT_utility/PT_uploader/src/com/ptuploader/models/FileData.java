package com.ptuploader.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 7/7/2016.
 */
public class FileData {

    private String modifiedAt;
    private String url;
    private String absolutePath;
    private long size;

    public FileData() {
    }

    public FileData(String modifiedAt, String url, String absolutePath, long size) {
        this.modifiedAt = modifiedAt;
        this.url = url;
        this.absolutePath = absolutePath;
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

    @JsonIgnore
    public String getAbsolutePath() {
        return absolutePath;
    }

    @JsonIgnore
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
