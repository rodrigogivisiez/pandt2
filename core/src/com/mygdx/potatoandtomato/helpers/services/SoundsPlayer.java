package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
    private HashMap<Music, Boolean> _musicMap;
    private Broadcaster _broadcaster;
    private HashMap<Sounds.Name, Long> _soundIdsMap;
    private HashMap<Sound, Long> _externalSoundIdsMap;

    public SoundsPlayer(Assets assets, Broadcaster broadcaster) {
        this._assets = assets;
        this._broadcaster = broadcaster;
        _musicMap = new HashMap();
        _soundIdsMap = new HashMap<Sounds.Name, Long>();
        _externalSoundIdsMap = new HashMap();
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
        }
        playMusic(_themeMusic, false);
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
    public void playSound(final Sound sound) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                sound.play(_volume);
            }
        });
    }

    @Override
    public void playSoundLoop(final Sound sound) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                long id = sound.play(_volume);
                sound.setLooping(id, true);
                _externalSoundIdsMap.put(sound, id);
            }
        });
    }

    @Override
    public void stopSoundLoop(final Sound sound) {
        if(_externalSoundIdsMap.containsKey(sound)){
            sound.setLooping(_externalSoundIdsMap.get(sound), false);
            _externalSoundIdsMap.remove(sound);
        }
    }

    @Override
    public void playMusic(final Music music) {
        playMusic(music, true);
    }

    public void playMusic(final Music music, boolean external) {
        if(!_musicMap.containsKey(music)){
            _musicMap.put(music, external);
        }
        music.setVolume(_volume);
        music.setLooping(true);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                music.play();
            }
        });
    }

    @Override
    public void stopMusic(final Music music) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                music.stop();
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

        for(Music music : _musicMap.keySet()){
            music.setVolume(_volume);
        }
    }

    @Override
    public void disposeAllExternalSounds() {
        for(Sound sound : _externalSoundIdsMap.keySet()){
            sound.dispose();
        }
        _externalSoundIdsMap.clear();

        for(Music music : _musicMap.keySet()){
            if(_musicMap.get(music)){
                music.dispose();
            }
            _musicMap.remove(music);
        }
    }
}
