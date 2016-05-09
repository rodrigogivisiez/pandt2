package com.potatoandtomato.common.models;

import com.potatoandtomato.common.absints.WebImageListener;

/**
 * Created by SiongLeng on 9/5/2016.
 */
public class RemoteModel {

    public String url;
    public WebImageListener listener;

    public RemoteModel(String url, WebImageListener listener) {
        this.url = url;
        this.listener = listener;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebImageListener getListener() {
        return listener;
    }

    public void setListener(WebImageListener listener) {
        this.listener = listener;
    }
}
