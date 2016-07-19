package com.potatoandtomato.games.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class GraveyardLogic implements Disposable {

    private GraveModel _graveModel;
    private GraveyardActor _graveyardActor;
    private SoundsWrapper _soundsWrapper;
    private boolean _handledSuddenDeath;
    private SafeThread _countDownThread;
    private boolean _pauseTimer;
    private ChessColor _currentTurnChessColor;
    private GameCoordinator _coordinator;
    private Services _services;

    public GraveyardLogic(GraveModel graveModel, GameCoordinator gameCoordinator, Texts texts, MyAssets assets, Services services, SoundsWrapper soundsWrapper) {
        this._coordinator = gameCoordinator;
        this._graveModel = graveModel;
        this._soundsWrapper = soundsWrapper;
        this._services = services;
        this._graveyardActor = new GraveyardActor(gameCoordinator, texts, assets, _soundsWrapper);
        setListener();
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

    public void setCountDownThread(){
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

    public void setListener(){

        _graveyardActor.getGraveButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _graveyardActor.toggle(true);
            }
        });

        _graveyardActor.getTutorialButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _graveyardActor.toggle(false);
            }
        });

        _graveyardActor.getGraveCloseImage().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _graveyardActor.hide();
            }
        });

        _graveyardActor.getTutorialCloseImage().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _graveyardActor.hide();
            }
        });

    }

    @Override
    public void dispose() {
        if(_countDownThread != null) _countDownThread.kill();
    }
}
