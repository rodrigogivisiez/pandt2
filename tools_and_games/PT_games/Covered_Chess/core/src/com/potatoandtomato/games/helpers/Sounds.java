package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class Sounds implements Disposable {

    public enum Name{
        START_GAME, OPEN_SLIDE,
        FLIP_CHESS, MOVE_CHESS,
        FIGHT_CHESS, WIN, LOSE
    }

    private Assets _assets;
    private GameCoordinator _coordinator;
    private Music _themeMusic;

    public Sounds(Assets _assets, GameCoordinator coordinator) {
        this._assets = _assets;
        this._coordinator = coordinator;

        _themeMusic = _assets.getThemeMusic();
        _themeMusic.setLooping(true);
    }

    public void playTheme(){
        _coordinator.getSoundManager().addMusic(_themeMusic);
        _coordinator.getSoundManager().playMusic(_themeMusic);
    }

    public void stopTheme(){
        _assets.getThemeMusic().stop();
    }

    public void playSounds(Name name){
        Sound sound = null;
        switch (name){
            case START_GAME:
                sound = _assets.getStartGameSound();
                break;
            case OPEN_SLIDE:
                sound = _assets.getOpenSlideSound();
                break;
            case FLIP_CHESS:
                sound = _assets.getFlipChessSound();
                break;
            case MOVE_CHESS:
                sound = _assets.getMoveSound();
                break;
            case FIGHT_CHESS:
                sound = _assets.getFightChessSound();
                break;
            case WIN:
                sound = _assets.getWinSound();
                break;
            case LOSE:
                sound = _assets.getLossSound();
                break;
        }

        _coordinator.getSoundManager().playSound(sound);
    }

    @Override
    public void dispose() {
        _coordinator.getSoundManager().disposeMusic(_themeMusic);
    }
}
