package com.potatoandtomato.common.assets;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.utils.OneTimeRunnable;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public class Assets implements Disposable {

    PTAssetsManager manager;
    TextureAssets textureAssets;
    PatchAssets patchAssets;
    SoundAssets soundAssets;
    AnimationAssets animationAssets;
    FontAssets fontAssets;
    boolean finishLoading;
    Runnable onFinish;
    boolean disposed;

    public Assets(PTAssetsManager manager) {
        this(manager, null, null, null, null, null);
    }

    public Assets(PTAssetsManager manager, FontAssets fontAssets,
                  AnimationAssets animationAssets, SoundAssets soundAssets,
                  PatchAssets patchAssets, TextureAssets textureAssets){
        this.manager = manager;
        this.fontAssets = fontAssets;
        this.animationAssets = animationAssets;
        this.soundAssets = soundAssets;
        this.patchAssets = patchAssets;
        this.textureAssets = textureAssets;

    }

    public void loadAsync(Runnable onFinish){
        this.onFinish = onFinish;

        if(soundAssets != null) soundAssets.load();
        if(textureAssets != null) textureAssets.load();
        if(fontAssets != null) fontAssets.load();
        if(animationAssets != null) animationAssets.load();

        manager.startMonitor(new OneTimeRunnable(new Runnable() {
            @Override
            public void run() {
                onLoadedAssets();
            }
        }));
       // manager.finishLoading();

//        if(soundAssets != null) soundAssets.onLoaded();
//        if(textureAssets != null){
//            textureAssets.onLoaded();
//            if(patchAssets != null) patchAssets.onLoaded(textureAssets.getUIPack());
//        }
//        if(fontAssets != null) fontAssets.onLoaded();
//        if(animationAssets != null) animationAssets.onLoaded();
//
//        if(onFinish != null) onFinish.run();
    }

    public void loadSync(Runnable onFinish){
        this.onFinish = onFinish;

        if(soundAssets != null) soundAssets.load();
        if(textureAssets != null) textureAssets.load();
        if(fontAssets != null) fontAssets.load();
        if(animationAssets != null) animationAssets.load();

         manager.finishLoading();

        if(soundAssets != null) soundAssets.onLoaded();
        if(textureAssets != null){
            textureAssets.onLoaded();
            if(patchAssets != null) patchAssets.onLoaded(textureAssets.getUIPack());
        }
        if(fontAssets != null) fontAssets.onLoaded();
        if(animationAssets != null) animationAssets.onLoaded();

        if(onFinish != null) onFinish.run();
    }


    public void onLoadedAssets(){
        if(disposed) return;
        if(soundAssets != null) soundAssets.onLoaded();
        if(textureAssets != null){
            textureAssets.onLoaded();
            if(patchAssets != null) patchAssets.onLoaded(textureAssets.getUIPack());
        }
        if(fontAssets != null) fontAssets.onLoaded();
        if(animationAssets != null) animationAssets.onLoaded();

        if(onFinish != null) onFinish.run();
    }


    public void setSoundAssets(SoundAssets soundAssets) {
        this.soundAssets = soundAssets;
    }

    public SoundAssets getSounds() {
        return soundAssets;
    }

    public AnimationAssets getAnimations() {
        return animationAssets;
    }

    public void setAnimationAssets(AnimationAssets animationAssets) {
        this.animationAssets = animationAssets;
    }

    public TextureAssets getTextures() {
        return textureAssets;
    }

    public void setTextureAssets(TextureAssets textureAssets) {
        this.textureAssets = textureAssets;
    }

    public PatchAssets getPatches() {
        return patchAssets;
    }

    public void setPatchAssets(PatchAssets patchAssets) {
        this.patchAssets = patchAssets;
    }

    public FontAssets getFonts() {
        return fontAssets;
    }

    public void setFontAssets(FontAssets fontAssets) {
        this.fontAssets = fontAssets;
    }

    public PTAssetsManager getPTAssetsManager() {
        return manager;
    }

    @Override
    public void dispose() {
        disposed = true;
        manager.dispose();
        if(soundAssets != null) soundAssets.dispose();
        if(textureAssets != null) textureAssets.dispose();
        if(patchAssets != null) patchAssets.dispose();
        if(fontAssets != null) fontAssets.dispose();
        if(animationAssets != null) animationAssets.dispose();
    }
}
