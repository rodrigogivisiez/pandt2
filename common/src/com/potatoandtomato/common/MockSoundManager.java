package com.potatoandtomato.common;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 20/1/2016.
 */
public class MockSoundManager implements ISounds {

    private float _volume;

    public Array<Music> _musicList;

    public MockSoundManager() {
        _musicList = new Array<Music>();
        setVolume(1f);
    }

    @Override
    public void addMusic(Music music) {
        if(!_musicList.contains(music, true)){
            _musicList.add(music);
        }
    }

    @Override
    public void disposeMusic(Music music) {
        if(_musicList.contains(music, true)){
            _musicList.removeValue(music, true);
            music.dispose();
        }
    }

    @Override
    public void playMusic(Music music) {
        if(!_musicList.contains(music, true)) {
            System.out.println("Please add the music using addMusic() method first before playing.");
            return;
        }
        music.setVolume(_volume);
        music.play();
    }

    @Override
    public void playSound(Sound sound) {
        sound.play(_volume);
    }

    @Override
    public void setVolume(float _volume) {
        this._volume = _volume;

        if(_volume > 0.1f) _volume = 0.1f;

        for(Music music : _musicList){
            music.setVolume(_volume);
        }
    }
}
