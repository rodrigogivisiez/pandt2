package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Sounds implements IAssetFragment {

    private AssetManager _manager;

    public Sounds(AssetManager _manager) {
        this._manager = _manager;
    }

    @Override
    public void load() {
        _manager.load("sounds/theme.mp3", Music.class);
        _manager.load("sounds/click_water.ogg", Sound.class);
        _manager.load("sounds/together_cheer.ogg", Sound.class);
        _manager.load("sounds/open_slide.ogg", Sound.class);
        _manager.load("sounds/game_created.ogg", Sound.class);
        _manager.load("sounds/count_down.ogg", Sound.class);
        _manager.load("sounds/send_message.ogg", Sound.class);
        _manager.load("sounds/mic.ogg", Sound.class);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onLoaded() {
        themeMusic = _manager.get("sounds/theme.mp3", Music.class);
        clickWaterSound = _manager.get("sounds/click_water.ogg", Sound.class);
        togetherCheersSound = _manager.get("sounds/together_cheer.ogg", Sound.class);
        openSlideSound = _manager.get("sounds/open_slide.ogg", Sound.class);
        gameCreatedSound = _manager.get("sounds/game_created.ogg", Sound.class);
        countDownSound = _manager.get("sounds/count_down.ogg", Sound.class);
        messagingSound = _manager.get("sounds/send_message.ogg", Sound.class);
        micSound = _manager.get("sounds/mic.ogg", Sound.class);
    }

    private Music themeMusic;
    private Sound clickWaterSound,
            togetherCheersSound, openSlideSound,
            gameCreatedSound, countDownSound, messagingSound, micSound ;

    public Sound getMicSound() {
        return micSound;
    }

    public Sound getMessagingSound() {
        return messagingSound;
    }

    public Sound getCountDownSound() {
        return countDownSound;
    }

    public Sound getGameCreatedSound() {
        return gameCreatedSound;
    }

    public Sound getOpenSlideSound() {
        return openSlideSound;
    }

    public Sound getTogetherCheersSound() {
        return togetherCheersSound;
    }

    public Sound getClickWaterSound() {
        return clickWaterSound;
    }

    public Music getThemeMusic() {
        return themeMusic;
    }

}
