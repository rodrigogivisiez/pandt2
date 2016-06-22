package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.ISoundsPlayer;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class SoundsPlayer implements ISoundsPlayer {

    private Assets _assets;
    private float _volume;
    private Music _currentMusic;
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

    public void playMusic(Sounds.Name name){
        Music music = _assets.getSounds().getMusic(name);
        if(music != _currentMusic){
            stopMusic(_currentMusic);
            playMusic(music, false, true);
        }
    }

    public void stopMusic(Sounds.Name name){
        Music music = _assets.getSounds().getMusic(name);
        stopMusic(music);
    }


    public Music playMusicFromFile(FileHandle fileHandle){
        final Music music = Gdx.audio.newMusic(fileHandle);
        music.setVolume(1);
        music.setLooping(false);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                music.play();
            }
        });
        return music;
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
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_soundIdsMap.containsKey(soundName)){
                    Sound sound = _assets.getSounds().getSound(soundName);
                    sound.setLooping(_soundIdsMap.get(soundName), false);
                    _soundIdsMap.remove(soundName);
                }

            }
        });
    }

    @Override
    public void playSound(final Sound sound) {
        if(sound != null){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    sound.play(_volume);
                }
            });
        }
    }

    public void playSound(final Sound sound, final float volume) {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                sound.play(volume);
            }
        });
    }

    @Override
    public void playSoundLoop(final Sound sound) {
        if(sound != null){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    long id = sound.loop(_volume);
                    //sound.setLooping(id, true);
                    _externalSoundIdsMap.put(sound, id);
                }
            });
        }
    }

    @Override
    public void stopSoundLoop(final Sound sound) {
        if(sound != null){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if(_externalSoundIdsMap.containsKey(sound)){
                        sound.stop();
                        _externalSoundIdsMap.remove(sound);
                    }
                }
            });
        }
    }

    @Override
    public void playMusic(final Music music) {
        if(music != null){
            playMusic(music, true, true);
        }
    }

    @Override
    public void playMusicNoLoop(Music music) {
        if(music != null){
            playMusic(music, true, false);
        }
    }

    public void playMusic(final Music music, boolean external, boolean looping) {
        if(!_musicMap.containsKey(music)){
            _musicMap.put(music, external);
        }
        music.setVolume(_volume);
        music.setLooping(looping);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _currentMusic = music;
                music.play();
            }
        });
    }

    @Override
    public void stopMusic(final Music music) {
        if(music != null){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    music.stop();
                }
            });
        }
    }

    @Override
    public void stopAllMusics() {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(Music music : _musicMap.keySet()){
                    music.stop();
                }
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
            sound.stop();
            sound.dispose();
        }
        _externalSoundIdsMap.clear();

        ArrayList<Music> disposedMusic = new ArrayList();
        for(Music music : _musicMap.keySet()){
            if(_musicMap.get(music)){
                disposedMusic.add(music);

            }
        }

        for(Music music : disposedMusic){
            music.stop();
            music.dispose();
            _musicMap.remove(music);
        }
    }
}
