package com.potatoandtomato.common;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public interface ISounds {

    void addMusic(Music music);
    void disposeMusic(Music music);

    void playMusic(Music music);
    void playSound(Sound sound);
}
