package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.*;
import com.mygdx.potatoandtomato.statics.Global;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class Sounds implements ISounds {

    public enum Name{
        BUTTON_CLICKED, TOGETHER_CHEERS,
        SLIDING, GAME_CREATED, MIC, COUNT_DOWN, MESSAGING
    }

    private Assets _assets;
    private Music _themeMusic;
    private float _volume;
    private Array<Music> _musicList;
    private Broadcaster _broadcaster;

    public Sounds(Assets assets, Broadcaster broadcaster) {
        this._assets = assets;
        this._broadcaster = broadcaster;
        _musicList = new Array<Music>();
        setVolume(1);

        _broadcaster.subscribe(BroadcastEvent.SOUNDS_CHANGED, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                setVolume(1);
            }
        });

    }


    public void playThemeMusic() {
        if(_themeMusic == null){
            _themeMusic = _assets.getSounds().getThemeMusic();
            _themeMusic.setLooping(true);
            addMusic(_themeMusic);
        }
        playMusic(_themeMusic);
    }

    public void stopThemeMusic() {
        _themeMusic.stop();
    }

    public void playSoundEffect(Name soundName){
        Sound sound = null;
        switch (soundName){
            case BUTTON_CLICKED:
                sound =  _assets.getSounds().getClickWaterSound();
                break;
            case TOGETHER_CHEERS:
                sound = _assets.getSounds().getTogetherCheersSound();
                break;
            case SLIDING:
                sound = _assets.getSounds().getOpenSlideSound();
                break;
            case GAME_CREATED:
                sound = _assets.getSounds().getGameCreatedSound();
                break;
            case MIC:
                sound = _assets.getSounds().getMicSound();
                break;
            case COUNT_DOWN:
                sound = _assets.getSounds().getCountDownSound();
                break;
            case MESSAGING:
                sound = _assets.getSounds().getMessagingSound();
                break;
        }

        playSound(sound);
    }

    @Override
    public void addMusic(Music music) {
        if(!_musicList.contains(music, true)){
            _musicList.add(music);
        }
    }

    @Override
    public void playSound(final Sound sound) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                sound.play(_volume);
            }
        });

    }

    @Override
    public void playMusic(final Music music) {
        if(!_musicList.contains(music, true)) {
            System.out.println("Please add the music using addMusic() method first before playing.");
            return;
        }
        music.setVolume(_volume);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                music.play();
            }
        });

    }

    @Override
    public void setVolume(float volume){
        this._volume = volume;

        if(this._volume > 0.4) this._volume = 0.4f;

        if(!Global.ENABLE_SOUND){
            this._volume = 0f;
        }

        for(Music music : _musicList){
            music.setVolume(_volume);
        }
    }

    @Override
    public void disposeMusic(Music music) {
        if(_musicList.contains(music, true)){
            _musicList.removeValue(music, true);
            music.dispose();
        }
    }

}
