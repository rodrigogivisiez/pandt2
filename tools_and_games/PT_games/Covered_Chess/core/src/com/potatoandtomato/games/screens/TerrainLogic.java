package com.potatoandtomato.games.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absint.ActionListener;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ActionType;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Direction;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.ChessModel;
import com.potatoandtomato.games.models.TerrainModel;
import com.potatoandtomato.games.references.BattleRef;
import com.potatoandtomato.games.services.GameDataController;
import com.potatoandtomato.games.services.SoundsWrapper;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class TerrainLogic {

    private TerrainModel _terrainModel;
    private TerrainLogic _me;
    private MyAssets _assets;
    private TerrainActor terrainActor;
    private ChessLogic chessLogic;
    private ActionListener actionListener;

    private GameCoordinator _coordinator;
    private BattleRef _battleRefs;
    private GameDataController _gameDataController;
    private SoundsWrapper _soundsWrapper;

    public TerrainLogic(Table root, TerrainModel _terrainModel, MyAssets _assets,
                        GameCoordinator _coordinator, ChessModel chessModel,
                        SoundsWrapper soundsWrapper, GameDataController gameDataController,
                        BattleRef battleRef) {
        this._me = this;
        this._terrainModel = _terrainModel;
        this._assets = _assets;
        this._coordinator = _coordinator;
        this._soundsWrapper = soundsWrapper;
        this._gameDataController = gameDataController;
        this._battleRefs = battleRef;

        chessLogic = new ChessLogic(chessModel, _assets, soundsWrapper, gameDataController);
        terrainActor = new TerrainActor(root, _assets, chessLogic.getChessActor(), _soundsWrapper);

        setListeners();
    }

    public void moveChessToThis(final TerrainLogic fromLogic, boolean showMoveAnimation, final boolean isFromWon, final String random){

        final ChessModel originalFromChessModel = fromLogic.getChessLogic().getChessModel().clone();
        final Actor originalFromChessClone = fromLogic.getChessLogic().cloneActor();
        final ChessModel originalToChessModel = getChessLogic().getChessModel().clone();

        final Runnable afterMove = new Runnable() {
            @Override
            public void run() {

                if(isEmpty()){
                    _soundsWrapper.playSounds(Sounds.Name.MOVE_CHESS);
                    originalFromChessModel.resetSurface();
                    chessLogic.setChessModel(originalFromChessModel);
                    actionListener.changeTurnReady(ActionType.MOVE, originalFromChessModel.getChessType(), originalToChessModel.getChessType(), random);
                }
                else {
                    getTerrainActor().showBattle();
                    _soundsWrapper.playSounds(Sounds.Name.FIGHT_CHESS);

                    ChessModel winnerChessModel;
                    final ChessType winnerChessType, loserChessType;
                    final boolean loserIsYellow;
                    final Actor clone;

                    if(isFromWon){
                        winnerChessModel = originalFromChessModel;
                        winnerChessType = winnerChessModel.getChessType();
                        loserChessType = _me.getChessLogic().getChessModel().getChessType();
                        loserIsYellow = _me.getChessLogic().getChessModel().getChessColor() == ChessColor.YELLOW;
                        clone = _me.getChessLogic().cloneActor();
                    }
                    else{
                        winnerChessModel = _me.getChessLogic().getChessModel();
                        winnerChessType = winnerChessModel.getChessType();
                        loserChessType = originalFromChessModel.getChessType();
                        loserIsYellow = originalFromChessModel.getChessColor() == ChessColor.YELLOW;
                        clone = originalFromChessClone;
                    }

                    final Stage _stage = _me.getTerrainActor().getStage();

                    winnerChessModel.resetSurface();
                    winnerChessModel.addKillCount();
                    chessLogic.setChessModel(winnerChessModel);

                    Threadings.delay(1500, new Runnable() {
                        @Override
                        public void run() {
                            clone.setOrigin(Align.center);
                            clone.setPosition(Positions.actorLocalToStageCoord(_me.getChessLogic().getChessActor()).x,
                                    Positions.actorLocalToStageCoord(_me.getChessLogic().getChessActor()).y);
                            _stage.addActor(clone);

                            clone.addAction(parallel(
                                    moveTo(loserIsYellow ? 30 : _coordinator.getGameWidth() - 50, _coordinator.getGameHeight(), 0.5f),
                                    scaleTo(0.3f, 0.3f, 0.5f),
                                    forever(rotateBy(360, 1f))));
                            Threadings.delay(500, new Runnable() {
                                @Override
                                public void run() {
                                    getTerrainActor().hideBattle();
                                    if(!isFromWon) getChessLogic().getChessActor().defendSuccess();
                                    actionListener.changeTurnReady(ActionType.MOVE, winnerChessType, loserChessType, random);
                                    actionListener.onChessKilled(loserChessType);
                                }
                            });

                        }
                    });
                }


            }
        };

        if(showMoveAnimation){
            final Actor clone = fromLogic.getChessLogic().cloneActor();
            Stage stage = this.getTerrainActor().getStage();
            Vector2 initialPositionOnStage = Positions.actorLocalToStageCoord(fromLogic.getChessLogic().getChessActor());
            Vector2 finalPositionOnStage = Positions.actorLocalToStageCoord(this.getTerrainActor());
            clone.setPosition(initialPositionOnStage.x, initialPositionOnStage.y);
            stage.addActor(clone);
            fromLogic.getChessLogic().setChessModel(null);

            clone.addAction(sequence(moveTo(finalPositionOnStage.x + clone.getWidth() / 4, finalPositionOnStage.y + 5, 0.25f),
                    new Action() {
                            @Override
                            public boolean act(float delta) {
                                clone.remove();
                                afterMove.run();
                                return true;
                            }
                }));
        }
        else{
            afterMove.run();
            fromLogic.getChessLogic().setChessModel(null);
        }


    }

    public void openTerrainChess(final String random){
        this.chessLogic.openChess(new Runnable() {
            @Override
            public void run() {
                actionListener.changeTurnReady(ActionType.OPEN, null, null, random);
            }
        });
    }

    //col and row start from top left of board
    public void showPercentTile(TerrainLogic fromLogic){

        Direction direction = Direction.NONE;

        if(Math.abs(fromLogic.getTerrainModel().getCol() - this.getTerrainModel().getCol()) == 2 ||
                Math.abs(fromLogic.getTerrainModel().getRow() - this.getTerrainModel().getRow()) == 2){
            direction = Direction.NONE;
        }
        else{
            if(fromLogic.getTerrainModel().getCol() == this.getTerrainModel().getCol()
                    && fromLogic.getTerrainModel().getRow() != this.getTerrainModel().getRow()){
                if(fromLogic.getTerrainModel().getRow() > this.getTerrainModel().getRow()) direction = Direction.BOTTOM;
                else if(fromLogic.getTerrainModel().getRow() < this.getTerrainModel().getRow()) direction = Direction.TOP;
            }
            else if(fromLogic.getTerrainModel().getRow() == this.getTerrainModel().getRow()
                    && fromLogic.getTerrainModel().getCol() != this.getTerrainModel().getCol()){
                if(fromLogic.getTerrainModel().getCol() > this.getTerrainModel().getCol()) direction = Direction.RIGHT;
                else if(fromLogic.getTerrainModel().getCol() < this.getTerrainModel().getCol()) direction = Direction.LEFT;
            }
            else if(fromLogic.getTerrainModel().getRow() != this.getTerrainModel().getRow()
                    && fromLogic.getTerrainModel().getCol() != this.getTerrainModel().getCol()){
                if(fromLogic.getTerrainModel().getCol() > this.getTerrainModel().getCol() &&
                        fromLogic.getTerrainModel().getRow() > this.getTerrainModel().getRow()) direction = Direction.BOTTOM_RIGHT;
                else if(fromLogic.getTerrainModel().getCol() > this.getTerrainModel().getCol() &&
                        fromLogic.getTerrainModel().getRow() < this.getTerrainModel().getRow()) direction = Direction.TOP_RIGHT;
                else if(fromLogic.getTerrainModel().getCol() < this.getTerrainModel().getCol() &&
                        fromLogic.getTerrainModel().getRow() > this.getTerrainModel().getRow()) direction = Direction.BOTTOM_LEFT;
                else if(fromLogic.getTerrainModel().getCol() < this.getTerrainModel().getCol() &&
                        fromLogic.getTerrainModel().getRow() < this.getTerrainModel().getRow()) direction = Direction.TOP_LEFT;
            }
        }

        int percent = 0;
        if(this.isEmpty()) percent = -1;
        else{
             percent = _battleRefs.getWinPercent(fromLogic.getChessLogic().getChessModel(),
                                                    this.getChessLogic().getChessModel());
        }

        _terrainModel.setPercentShown(true);
        terrainActor.showPercent(percent, direction);

    }

    public void hidePercentTile(){
        if(_terrainModel.isPercentShown()){
            _terrainModel.setPercentShown(false);
            terrainActor.hidePercent();
        }
    }

    public void showCanMoveTo(){
        terrainActor.showCanMoveTo();
    }

    public void hideCanMoveTo(){
        terrainActor.hideCanMoveTo();
    }

    public void setSelected(boolean selected){
        if(_terrainModel.isSelected() != selected){
            _terrainModel.setSelected(selected);
            if(chessLogic != null) chessLogic.setSelected(selected);
            terrainActor.setSelected(selected);
        }

    }

    public void setDragAndDrop(ArrayList<TerrainLogic> possibleMoveTerrainLogics){
        chessLogic.clearDragDropTargets();
        for(final TerrainLogic terrainLogic : possibleMoveTerrainLogics){
            DragAndDrop.Target target = new DragAndDrop.Target(terrainLogic.getTerrainActor()) {
                public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    terrainLogic.showCanMoveTo();
                    return true;
                }

                public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    terrainLogic.hideCanMoveTo();
                }

                public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    terrainLogic.hideCanMoveTo();
                    actionListener.onMoved(_me.getTerrainModel().getCol(), _me.getTerrainModel().getRow(),
                            terrainLogic.getTerrainModel().getCol(), terrainLogic.getTerrainModel().getRow(),
                            _battleRefs.getFromIsWinner(_me.getChessLogic().getChessModel(),
                                    terrainLogic.getChessLogic().getChessModel()));
                }
            };
            chessLogic.addDragDropTarget(target);
        }
    }


    public boolean isEmpty() {
        return chessLogic.getChessModel().chessType == ChessType.NONE;
    }

    public boolean isOpened() {
        return chessLogic.getChessModel().getOpened();
    }

    public TerrainActor getTerrainActor() {
        return terrainActor;
    }

    public boolean isSelected() {
        return this.getChessLogic().getChessModel().getSelected();
    }

    public void setListeners(){
        terrainActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if((!getChessLogic().getChessModel().getOpened() ||
                        _gameDataController.getMyChessColor() == getChessLogic().getChessModel().getChessColor()) &&
                        (!isEmpty()) && (!_terrainModel.isBroken())){
                    actionListener.onSelected();
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });

    }

    public TerrainModel getTerrainModel() {
        return _terrainModel;
    }

    public ChessLogic getChessLogic() {
        return chessLogic;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
        this.actionListener.setTerrainLogic(this);
        chessLogic.setActionListener(this.actionListener);
    }

    public void invalidate(){
        this.getTerrainActor().invalidate(_terrainModel);
    }

}
