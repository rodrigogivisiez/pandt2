package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absint.ScoresListener;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;

import java.util.ArrayList;
import java.util.HashMap;

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
    private GameCoordinator _coordinator;
    private Services _services;

    public GraveyardLogic(GraveModel graveModel, GameCoordinator gameCoordinator, Texts texts, MyAssets assets, Services services, SoundsWrapper soundsWrapper) {
        this._coordinator = gameCoordinator;
        this._graveModel = graveModel;
        this._soundsWrapper = soundsWrapper;
        this._services = services;
        this._graveyardActor = new GraveyardActor(gameCoordinator, texts, assets);
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

        //for debug purpose only
        if(Gdx.app.getType() == Application.ApplicationType.Desktop){
            _graveyardActor.getGraveLabel().addListener(new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    _services.getScoresHandler().setIsMeWin(false);
                    _services.getScoresHandler().process(new ScoresListener() {
                        @Override
                        public void onCallBack(HashMap<Team, ArrayList<ScoreDetails>> winnerResult, ArrayList<Team> losers) {
                            _coordinator.abandon(winnerResult, new Runnable() {
                                @Override
                                public void run() {
                                    _services.getScoresHandler().updateMatchHistory();
                                }
                            });
                        }
                    });
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
        }

    }

    @Override
    public void dispose() {
        if(_countDownThread != null) _countDownThread.kill();
    }
}
