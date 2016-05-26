package com.potatoandtomato.common.absints;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public interface ISoundsPlayer {

    void disposeAllExternalSounds();

    void playMusic(Music music);
    void playMusicNoLoop(Music music);
    void stopMusic(Music music);
    void playSound(Sound sound);
    void playSoundLoop(final Sound sound);
    void stopSoundLoop(final Sound sound);

    void setVolume(float volume);

}
