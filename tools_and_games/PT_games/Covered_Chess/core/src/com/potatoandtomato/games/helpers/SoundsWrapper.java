package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ChessAnimal;
import com.potatoandtomato.games.statics.Global;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class SoundsWrapper implements Disposable {

    private Assets _assets;
    private GameCoordinator _coordinator;
    private Music _themeMusic;

    public SoundsWrapper(Assets _assets, GameCoordinator coordinator) {
        this._assets = _assets;
        this._coordinator = coordinator;

        _themeMusic = _assets.getSounds().getMusic(Sounds.Name.THEME);
        _themeMusic.setLooping(true);
    }

    public void playTheme(){
        _coordinator.getSoundManager().addMusic(_themeMusic);
        _coordinator.getSoundManager().playMusic(_themeMusic);
    }

    public void stopTheme(){
        _themeMusic.stop();
    }

    public void playSounds(Sounds.Name name){
        Sound sound =  _assets.getSounds().getSound(name);
        _coordinator.getSoundManager().playSound(sound);
    }

    public void playAnimalSound(ChessAnimal animal){
        Sound sound = _assets.getSounds().getSound(animal.name());
        _coordinator.getSoundManager().playSound(sound);
    }

    @Override
    public void dispose() {
        _coordinator.getSoundManager().disposeMusic(_themeMusic);
    }
}
