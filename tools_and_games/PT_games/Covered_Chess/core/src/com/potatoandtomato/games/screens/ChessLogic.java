package com.potatoandtomato.games.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.absint.ActionListener;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.ChessModel;
import com.potatoandtomato.games.services.GameDataController;
import com.potatoandtomato.games.services.SoundsWrapper;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class ChessLogic {

    ChessModel _chessModel;
    ChessActor _chessActor;
    ActionListener _actionListener;
    SoundsWrapper _soundsWrapper;
    private DragAndDrop _dragAndDrop;
    ArrayList<DragAndDrop.Target> _dragDropTargets;
    GameDataController _gameDataController;

    public ChessLogic(ChessModel chessModel, MyAssets assets, SoundsWrapper soundsWrapper, GameDataController gameDataController) {
        this._chessModel = chessModel;
        this._soundsWrapper = soundsWrapper;
        this._gameDataController = gameDataController;
        this._dragDropTargets = new ArrayList<DragAndDrop.Target>();

        _chessActor = new ChessActor(assets, soundsWrapper);

        setListeners();
        invalidate();
    }



    public void openChess(final Runnable onFinish){
        _chessModel.setOpened(true);
        _chessActor.openChess(new Runnable() {
            @Override
            public void run() {
                onFinish.run();
            }
        });

        if(_chessModel.getChessColor() == _gameDataController.getMyChessColor()){
            setDragDrop();
        }



    }

    public void addDragDropTarget(DragAndDrop.Target target){
        if(_chessModel.getChessColor() == _gameDataController.getMyChessColor()) {
            if(_dragAndDrop == null){
                setDragDrop();
            }
            _dragAndDrop.addTarget(target);
            _dragDropTargets.add(target);
        }
    }

    public void clearDragDropTargets(){
        for(DragAndDrop.Target target : _dragDropTargets){
            _dragAndDrop.removeTarget(target);
        }
    }

    public void setSelected(boolean isSelected){
        _chessModel.setSelected(isSelected);
        invalidate();
    }

    public void setFocusing(boolean focusing){
        if(_chessModel.getFocusing() != focusing){
            _chessModel.setFocusing(focusing);
            invalidate();
        }
    }

    public void setChessModel(ChessModel chessModel){
        if(chessModel == null){
            _chessModel.setChessType(ChessType.NONE);
            _chessModel.setStatus(Status.NONE);
        }
        else{
            _chessModel = chessModel;
        }
        invalidate();
    }

    public Actor cloneActor(){
        return getChessActor().clone();
    }

    public ChessActor getChessActor() {
        return _chessActor;
    }

    public void invalidate(){
        _chessActor.invalidate(_chessModel);
    }

    public ChessModel getChessModel() {
        return _chessModel;
    }

    public void setActionListener(ActionListener _actionListener) {
        this._actionListener = _actionListener;
    }

    public void setListeners(){
        _chessActor.getCoverChess().addListener(new DragListener() {
            private float _startDragX;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(_chessActor.openChess(_startDragX, x) && !_chessModel.getOpened()){
                    _actionListener.onOpened();
                }
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                _startDragX = x;
                return super.touchDown(event, x, y, pointer, button);

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(!_chessModel.getOpened()){
                    _chessActor.openChess(0, 0);
                    _chessActor.resetOpenChess();
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });



    }

    public void setDragDrop(){
        if(_dragAndDrop != null) _dragAndDrop.clear();
        _dragAndDrop = new DragAndDrop();
        _dragAndDrop.setDragTime(0);
        _dragAndDrop.addSource(new DragAndDrop.Source(getChessActor()) {
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                if(getChessModel().getOpened()){
                    Actor clone = getChessActor().clone();
                    payload.setDragActor(clone);
                    _dragAndDrop.setDragActorPosition(-x, -y + clone.getHeight());
                    getChessModel().setDragging(true);
                    invalidate();
                }
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);
                if(getChessModel().getOpened() && getChessModel().getChessType() != ChessType.NONE){
                    final Actor clone = getChessActor().clone();
                    clone.setPosition(payload.getDragActor().getX(), payload.getDragActor().getY());
                    getChessActor().getStage().addActor(clone);
                    Vector2 coords = Positions.actorLocalToStageCoord(getChessActor());
                    clone.addAction(sequence(moveTo(coords.x, coords.y, 0.15f), new Action() {
                        @Override
                        public boolean act(float delta) {
                            clone.remove();
                            getChessModel().setDragging(false);
                            invalidate();
                            return true;
                        }
                    }));
                }

            }
        });
    }

}
