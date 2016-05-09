package com.potatoandtomato.common.absints;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.OneTimeRunnable;

/**
 * Created by SiongLeng on 4/5/2016.
 */
public class PTAssetsManager extends AssetManager {

    private IPTGame iptGame;
    private boolean finishLoading;
    private OneTimeRunnable onFinish;
    private Broadcaster broadcaster;

    public PTAssetsManager(IPTGame iptGame, Broadcaster broadcaster) {
        this.iptGame = iptGame;
        this.broadcaster = broadcaster;
    }

    public PTAssetsManager(FileHandleResolver resolver, IPTGame iptGame, Broadcaster broadcaster) {
        super(resolver);
        this.iptGame = iptGame;
        this.broadcaster = broadcaster;
    }

    public PTAssetsManager(FileHandleResolver resolver, boolean defaultLoaders, IPTGame iptGame, Broadcaster broadcaster) {
        super(resolver, defaultLoaders);
        this.iptGame = iptGame;
        this.broadcaster = broadcaster;
    }

    public synchronized boolean isFinishLoading() {
        return finishLoading;
    }

    public synchronized void setFinishLoading(boolean finishLoading) {
        onFinish.run();
        this.finishLoading = finishLoading;
    }

    public void startMonitor(OneTimeRunnable onFinish){
        finishLoading = false;
        this.onFinish = onFinish;
        iptGame.monitorPTAssetManager(this);
    }

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }
}
