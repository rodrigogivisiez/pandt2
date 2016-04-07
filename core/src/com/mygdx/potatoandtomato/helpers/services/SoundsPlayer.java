package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.ISoundsPlayer;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.util.HashMap;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class SoundsPlayer implements ISoundsPlayer {

    private Assets _assets;
    private Music _themeMusic;
    private float _volume;
    private Array<Music> _musicList;
    private Broadcaster _broadcaster;
    private HashMap<Sounds.Name, Long> _soundIdsMap;

    public SoundsPlayer(Assets assets, Broadcaster broadcaster) {
        this._assets = assets;
        this._broadcaster = broadcaster;
        _musicList = new Array<Music>();
        _soundIdsMap = new HashMap<Sounds.Name, Long>();
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
            _themeMusic = _assets.getSounds().getMusic(Sounds.Name.THEME);
            _themeMusic.setLooping(true);
            addMusic(_themeMusic);
        }
        playMusic(_themeMusic);
    }

    public void stopThemeMusic() {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _themeMusic.stop();
            }
        });
    }

    public void playSoundEffect(Sounds.Name soundName){
        Sound sound = _assets.getSounds().getSound(soundName);
        playSound(sound);
    }

    public void playSoundEffectLoop(final Sounds.Name soundName){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Sound sound = _assets.getSounds().getSound(soundName);
                long id = sound.play(_volume);
                sound.setLooping(id, true);
                _soundIdsMap.put(soundName, id);
            }
        });
    }

    public void stopSoundEffectLoop(final Sounds.Name soundName){
        if(_soundIdsMap.containsKey(soundName)){
            Sound sound = _assets.getSounds().getSound(soundName);
            sound.setLooping(_soundIdsMap.get(soundName), false);
            _soundIdsMap.remove(soundName);
        }

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
