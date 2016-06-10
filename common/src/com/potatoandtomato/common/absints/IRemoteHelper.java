package com.potatoandtomato.common.absints;

/**
 * Created by SiongLeng on 29/5/2016.
 */
public interface IRemoteHelper {

    void getRemoteImage(final String url, final WebImageListener listener);

    void dispose();

}
