package com.potatoandtomato.common.absints;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.potatoandtomato.common.utils.OneTimeRunnable;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 4/5/2016.
 */
public class PTAssetsManager extends AssetManager {

    private IPTGame iptGame;
    private boolean finishLoading;
    private OneTimeRunnable onFinish;

    public PTAssetsManager(IPTGame iptGame) {
        this.iptGame = iptGame;
    }

    public PTAssetsManager(FileHandleResolver resolver, IPTGame iptGame) {
        super(resolver);
        this.iptGame = iptGame;
    }

    public PTAssetsManager(FileHandleResolver resolver, boolean defaultLoaders, IPTGame iptGame) {
        super(resolver, defaultLoaders);
        this.iptGame = iptGame;
    }

    public synchronized boolean isFinishLoading() {
        return finishLoading;
    }

    public synchronized void setFinishLoading(boolean finishLoading) {
        onFinish.run();
        this.finishLoading = finishLoading;
    }

    public void startMonitor(OneTimeRunnable onFinish){
        this.onFinish = onFinish;
        iptGame.monitorPTAssetManager(this);
    }

}
