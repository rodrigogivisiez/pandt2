package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.potatoandtomato.games.absint.IAssetFragment;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Sounds implements IAssetFragment {

    private AssetManager _manager;

    private Music themeMusic;
    private Sound fightChessSound, flipChessSound, lossSound, moveSound, openSlideSound, startGameSound, winSound;

    public Sounds(AssetManager _manager) {
        this._manager = _manager;
    }

    @Override
    public void load() {
        _manager.load("sounds/theme.mp3", Music.class);
        _manager.load("sounds/fight_chess.ogg", Sound.class);
        _manager.load("sounds/flip_chess.ogg", Sound.class);
        _manager.load("sounds/lose.ogg", Sound.class);
        _manager.load("sounds/move.ogg", Sound.class);
        _manager.load("sounds/open_slide.ogg", Sound.class);
        _manager.load("sounds/start_game.ogg", Sound.class);
        _manager.load("sounds/win.ogg", Sound.class);
    }

    @Override
    public void onLoaded() {
        themeMusic = _manager.get("sounds/theme.mp3", Music.class);
        fightChessSound  = _manager.get("sounds/fight_chess.ogg", Sound.class);
        flipChessSound = _manager.get("sounds/flip_chess.ogg", Sound.class);
        lossSound = _manager.get("sounds/lose.ogg", Sound.class);
        moveSound = _manager.get("sounds/move.ogg", Sound.class);
        openSlideSound = _manager.get("sounds/open_slide.ogg", Sound.class);
        startGameSound = _manager.get("sounds/start_game.ogg", Sound.class);
        winSound = _manager.get("sounds/win.ogg", Sound.class);
    }

    public Sound getWinSound() {
        return winSound;
    }

    public Sound getStartGameSound() {
        return startGameSound;
    }

    public Sound getOpenSlideSound() {
        return openSlideSound;
    }

    public Sound getMoveSound() {
        return moveSound;
    }

    public Sound getLossSound() {
        return lossSound;
    }

    public Sound getFlipChessSound() {
        return flipChessSound;
    }

    public Sound getFightChessSound() {
        return fightChessSound;
    }

    public Music getThemeMusic() {
        return themeMusic;
    }

    @Override
    public void dispose() {

    }
}
