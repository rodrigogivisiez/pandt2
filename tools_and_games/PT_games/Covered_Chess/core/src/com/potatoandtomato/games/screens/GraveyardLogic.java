package com.potatoandtomato.games.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.SafeThread;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.SoundsWrapper;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class GraveyardLogic implements Disposable {

    private GraveModel _graveModel;
    private GraveyardActor _graveyardActor;
    private SoundsWrapper _soundsWrapper;
    private boolean _showed;
    private boolean _handledSuddenDeath;
    private SafeThread _countDownThread;
    private boolean _pauseTimer;
    private ChessColor _currentTurnChessColor;

    public GraveyardLogic(GraveModel graveModel, GameCoordinator gameCoordinator, Texts texts, Assets assets, SoundsWrapper soundsWrapper) {
        this._graveModel = graveModel;
        this._soundsWrapper = soundsWrapper;
        this._graveyardActor = new GraveyardActor(gameCoordinator, texts, assets);
        setListener();
        setCountDownThread();
    }

    public void invalidate(){
        _graveyardActor.modelChanged(getGraveModel());
    }

    public void addChessToGrave(ChessType chessType){
        _graveModel.addToGrave(chessType);
        invalidate();
    }

    public void onBoardModelChanged(BoardModel boardModel){
        if(_handledSuddenDeath != boardModel.isSuddenDeath()) {
            _handledSuddenDeath = boardModel.isSuddenDeath();
        }

        _currentTurnChessColor = boardModel.getCurrentTurnChessColor();
        _graveyardActor.onBoardModelChanged(boardModel);
    }

    private void setCountDownThread(){
        _countDownThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(_countDownThread.isKilled()) break;
                    else{
                        Threadings.sleep(1000);
                        if(_pauseTimer){
                            continue;
                        }
                        _graveModel.minusTimeLeft(_currentTurnChessColor);
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                _graveyardActor.setCountDownTime(_currentTurnChessColor, _graveModel.getLeftTime(_currentTurnChessColor));
                            }
                        });
                    }
                }
            }
        });
    }

    public void setPauseTimer(boolean _pauseTimer) {
        this._pauseTimer = _pauseTimer;
    }

    public GraveModel getGraveModel() {
        return _graveModel;
    }

    public void setGraveModel(GraveModel _graveModel) {
        this._graveModel = _graveModel;
        invalidate();
    }

    public GraveyardActor getGraveyardActor() {
        return _graveyardActor;
    }

    public boolean isShowed() {
        return _showed;
    }

    public void setListener(){
        _graveyardActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(isShowed()){
                    _graveyardActor.hide();
                }
                else{
                    _graveyardActor.expand();
                }
                _showed = !_showed;
                _soundsWrapper.playSounds(Sounds.Name.OPEN_SLIDE);
            }
        });
    }

    @Override
    public void dispose() {
        _countDownThread.kill();
    }
}
