package com.potatoandtomato.games.actors.plates;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.absint.MainScreenListener;
import com.potatoandtomato.games.actors.chesses.ChessActor;
import com.potatoandtomato.games.actors.chesses.enums.ChessType;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.PlateSimple;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class PlateLogic {

    private PlateLogic _me;
    private Assets _assets;
    private PlateActor _plateActor;
    private ChessActor _chessActor;
    private boolean _myChess;

    private boolean _selected;
    private PlateLogic[][] _plateLogics;
    private int _col, _row;
    private GameCoordinator _coordinator;
    private float _startDragX, _startDragY;
    private boolean _opened;
    private DragAndDrop _dragAndDrop;
    private boolean _empty;
    private BattleReference _battleRefs;
    private MainScreenListener _mainScreenListener;
    private Sounds _sounds;

    public PlateLogic(PlateLogic[][] plateLogics, int col, int row, Assets _assets, BattleReference _battleRefs,
                      GameCoordinator _coordinator, ChessType chessType,
                      boolean meIsYellow, Sounds sounds, MainScreenListener mainScreenListener) {
        this._me = this;
        this._battleRefs = _battleRefs;
        this._plateLogics = plateLogics;
        this._col = col;
        this._row = row;
        this._assets = _assets;
        this._coordinator = _coordinator;
        this._mainScreenListener = mainScreenListener;
        this._sounds = sounds;

        if((meIsYellow && chessType.toString().startsWith("YELLOW")) || (!meIsYellow && chessType.toString().startsWith("RED"))){
            _myChess = true;
        }

        _chessActor = new ChessActor(_assets);
        _chessActor.setChessType(chessType);
        _plateActor = new PlateActor(_assets, _chessActor);

        setListeners();
    }

    public int getCol() {
        return _col;
    }

    public int getRow() {
        return _row;
    }

    public boolean isMyChess() {
        return _myChess;
    }

    public void setListeners(){
        _plateActor.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(!isEmpty()){
                    if(!_selected){
                        if(_opened && !_myChess){

                        }
                        else{
                            clearAllSelectedExceptSelf();
                            setSelected(true);
                            _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_SELECTED, _col + "," + _row));
                        }
                    }
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });

        _chessActor.getFrontChess().addListener(new DragListener() {

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(!_chessActor.isOpened()){
                    if(_chessActor.openChess(_startDragX, x)){
                        openChess(true);
                    }
                }

                super.touchDragged(event, x, y, pointer);
                // System.out.println(x);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                _startDragX = x;
                _startDragY = y;
                // System.out.println("ST "+ x);
                return super.touchDown(event, x, y, pointer, button);

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                if(!_chessActor.isOpened()){
//                    _chessActor.openChess(0, 0);
//                }
                _chessActor.resetOpenChess();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        setDragAndDropIfIsMyChess();

    }

    public void setDragAndDropIfIsMyChess(){
        if(isMyChess()){
            if(_dragAndDrop != null) _dragAndDrop.clear();
            _dragAndDrop = new DragAndDrop();
            _dragAndDrop.setDragTime(0);
            _dragAndDrop.addSource(new DragAndDrop.Source(this.getChessActor().getBackChess()) {
                public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    Table clone = getChessActor().clone();
                    payload.setDragActor(clone);
                    getChessActor().getColor().a = 0;
                    _dragAndDrop.setDragActorPosition(-x, -y + clone.getHeight());
                    return payload;
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    super.dragStop(event, x, y, pointer, payload, target);
                    if(getChessActor() != null){
                        final Table clone = getChessActor().clone();
                        clone.setPosition(payload.getDragActor().getX(), payload.getDragActor().getY());
                        getChessActor().getStage().addActor(clone);
                        Vector2 coords = Positions.actorLocalToStageCoord(getChessActor());
                        clone.addAction(sequence(moveTo(coords.x, coords.y, 0.15f), new Action() {
                            @Override
                            public boolean act(float delta) {
                                clone.remove();
                                getChessActor().getColor().a = 1;
                                return true;
                            }
                        }));
                    }

                }
            });
        }
    }

    public void openChess(boolean notify){
        _opened = true;
        _chessActor.openChess(true);
        _sounds.playSounds(Sounds.Name.FLIP_CHESS);
        _mainScreenListener.onFinishAction(500);
        if(notify){
            _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_OPEN_FULL,
                    getCol() + "," + getRow()));
        }
    }

    public void moveChessToThis(final PlateLogic fromLogic, boolean showMoveAnimation, final int knownWinner, final boolean sendUpdate){

        _mainScreenListener.onFinishAction((isEmpty() ? 0 : 2000) + 1000 + (showMoveAnimation ? 250 : 0));

        final Runnable toRun = new Runnable() {
            @Override
            public void run() {
                PlateLogic winnerLogic;
                final PlateLogic loserLogic;
                winnerLogic = fromLogic;
                int winner = -1;

                if(!isEmpty()){

                    getPlateActor().showBattle();
                    _sounds.playSounds(Sounds.Name.FIGHT_CHESS);
                    _coordinator.requestVibrate(1500);

                    if(knownWinner != -1){
                        winner = knownWinner;
                    }
                    else{
                        winner = _battleRefs.getWinner(fromLogic.getChessActor().getChessType(), getChessActor().getChessType());
                    }
                    if(winner == 1){
                        winnerLogic = fromLogic;
                        loserLogic = _me;
                    }
                    else{
                        winnerLogic = _me;
                        loserLogic = fromLogic;
                    }


                    final Stage _stage = _chessActor.getStage();
                    final Actor clone = loserLogic.getChessActor().clone();
                    final boolean loserIsYellow = loserLogic.getChessActor().isYellow();
                    final Drawable loserAnimal = loserLogic.getChessActor().getAnimalDrawable();
                    final ChessType loserChessType = loserLogic.getChessActor().getChessType();
                    loserLogic.setChessActor(null);

                    Threadings.delay(1500, new Runnable() {
                        @Override
                        public void run() {
                            clone.setOrigin(Align.center);
                            clone.setPosition(Positions.actorLocalToStageCoord(_chessActor).x, Positions.actorLocalToStageCoord(_chessActor).y);
                            _stage.addActor(clone);

                            clone.addAction(parallel(
                                    moveTo(loserIsYellow ? 30 : _coordinator.getGameWidth() - 50, _coordinator.getGameHeight(), 0.5f),
                                    scaleTo(0.3f, 0.3f, 0.5f),
                                    forever(rotateBy(360, 1f))));
                            Threadings.delay(500, new Runnable() {
                                @Override
                                public void run() {
                                    getPlateActor().hideBattle();
                                    _mainScreenListener.onChessKilled(loserChessType, loserAnimal, loserIsYellow);
                                }
                            });

                        }
                    });
                }

                setChessActor(winnerLogic.getChessActor());
                setMyChess(winnerLogic.isMyChess());
                setEmpty(false);
                setDragAndDropIfIsMyChess();

                if(sendUpdate){
                    _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_MOVE,
                            fromLogic.getCol() +"," + fromLogic.getRow() + "|" + getCol() + "," + getRow() + "|" + winner));
                }

            }
        };

        if(isEmpty()) _sounds.playSounds(Sounds.Name.MOVE_CHESS);

        if(showMoveAnimation){
            final Table clone = fromLogic.getChessActor().clone();
            Stage stage = fromLogic.getChessActor().getStage();
            Vector2 initialPositionOnStage = Positions.actorLocalToStageCoord(fromLogic.getChessActor());
            Vector2 finalPositionOnStage = Positions.actorLocalToStageCoord(this.getPlateActor());
            clone.setPosition(initialPositionOnStage.x, initialPositionOnStage.y);
            fromLogic.getChessActor().remove();
            stage.addActor(clone);
            clone.addAction(sequence(moveTo(finalPositionOnStage.x + clone.getWidth() / 4, finalPositionOnStage.y + 5, 0.25f),
                    new Action() {
                            @Override
                            public boolean act(float delta) {
                                clone.remove();
                                toRun.run();
                                return true;
                            }
                }));
        }
        else{
            toRun.run();
        }

        fromLogic.chessMovedClearance();
    }

    public void showMovementTileIfValid(PlateLogic fromLogic){
        int leftTopRightBottom = 0;
        if(isValidMove(fromLogic, this)){
            if(fromLogic.getCol() > this.getCol()) leftTopRightBottom = 2;
            if(fromLogic.getRow() > this.getRow()) leftTopRightBottom = 3;
            if(fromLogic.getCol() < this.getCol()) leftTopRightBottom = 0;
            if(fromLogic.getRow() < this.getRow()) leftTopRightBottom = 1;

            int percent;
            if(this.isEmpty()) percent = -1;
            else{
                percent = _battleRefs.getWinPercent(fromLogic.getChessActor().getChessType(), this.getChessActor().getChessType());
            }

            _plateActor.showPercent(percent, leftTopRightBottom);
        }

    }

    public void hideMovementTile(){
        _plateActor.hidePercent();
    }

    public void showCanMoveTo(){
        _plateActor.showCanMoveTo();
    }

    public void hideCanMoveTo(){
        _plateActor.hideCanMoveTo();
    }

    public void clearAllSelectedExceptSelf(){
        for (int row = 0; row<_plateLogics[0].length; row++){
            for (int col = 0; col<_plateLogics.length; col++){
                if(row == this.getRow() && col == this.getCol()) continue;
                _plateLogics[col][row].setSelected(false);
            }
        }
    }

    public void clearAllSelected(){
        for (int row = 0; row<_plateLogics[0].length; row++){
            for (int col = 0; col<_plateLogics.length; col++){
                _plateLogics[col][row].setSelected(false);
            }
        }
    }

    public void showPossibleMoves(){
        for(final PlateLogic plateLogic : getPossibleMoves()){
            if(isValidMove(this, plateLogic)){
                plateLogic.showMovementTileIfValid(this);

                if(_dragAndDrop != null){
                    _dragAndDrop.addTarget(new DragAndDrop.Target(plateLogic.getPlateActor()) {
                        public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            plateLogic.showCanMoveTo();
                            return true;
                        }

                        public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            plateLogic.hideCanMoveTo();
                        }

                        public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            plateLogic.hideCanMoveTo();
                            plateLogic.moveChessToThis(_me, false, -1, true);
                        }
                    });
                }

            }
        }
    }

    public void chessMovedClearance(){
        clearPossibleMoves();
        clearAllSelected();
        setEmpty(true);
        if(_dragAndDrop != null) _dragAndDrop.clear();
    }

    public void clearPossibleMoves(){
        for(PlateLogic plateLogic : getPossibleMoves()){
            plateLogic.hideMovementTile();
        }
    }

    public ArrayList<PlateLogic> getPossibleMoves(){
        ArrayList<PlateLogic> possibleMoveLogics = new ArrayList<PlateLogic>();
        if(getRow() -1 >= 0){
            possibleMoveLogics.add(_plateLogics[getCol()][getRow()-1]);
        }
        if(getRow() + 1 <= 7){
            possibleMoveLogics.add(_plateLogics[getCol()][getRow()+1]);
        }
        if(getCol() - 1 >= 0){
            possibleMoveLogics.add(_plateLogics[getCol()-1][getRow()]);
        }
        if(getCol() + 1 <= 3){
            possibleMoveLogics.add(_plateLogics[getCol()+1][getRow()]);
        }
        return possibleMoveLogics;
    }

    public boolean isValidMove(PlateLogic from, PlateLogic to){

        if(to.isEmpty()) return true;

        if(from.isMyChess() == to.isMyChess()) return false;

        if(!from.isOpened() || !to.isOpened()) return false;


        return true;
    }


    public void setSelected(boolean selected){
        _selected = selected;
        if(_chessActor != null) _chessActor.setSelected(selected);
        _plateActor.setSelected(selected);
        if(isOpened()){
            if(selected){
                showPossibleMoves();
            }
            else{
                clearPossibleMoves();
            }
        }

    }

    public void setMyChess(boolean _myChess) {
        this._myChess = _myChess;
    }

    public void setChessActor(ChessActor _chessActor) {
        this._chessActor = _chessActor;
        this.getPlateActor().setChessActor(_chessActor);
    }

    public boolean isEmpty() {
        return _empty;
    }

    public void setEmpty(boolean _empty) {
        this._empty = _empty;
    }

    public boolean isOpened() {
        return _opened;
    }

    public void setOpened(boolean _opened) {
        this._opened = _opened;
    }

    public PlateActor getPlateActor() {
        return _plateActor;
    }

    public ChessActor getChessActor() {
        return _chessActor;
    }

    public PlateSimple getPlateSimple(){
        ChessActor chessActor = _chessActor;
        if(chessActor == null){
            chessActor = new ChessActor(_assets);
            chessActor.setChessType(ChessType.RED_CAT);
        }
        return new PlateSimple(chessActor.getChessType(), isOpened(), isEmpty());
    }


}
