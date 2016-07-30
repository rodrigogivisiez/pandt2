package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.potatoandtomato.common.enums.GestureType;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.references.MovementRef;
import com.potatoandtomato.games.screens.ChessActor;
import com.potatoandtomato.games.screens.GraveyardLogic;
import com.potatoandtomato.games.screens.TerrainLogic;
import com.potatoandtomato.games.services.Texts;
import com.potatoandtomato.games.statics.Terms;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 27/7/2016.
 */
public class TutorialsHelper implements TutorialPartListener {

    private TutorialsHelper _this;
    private GameCoordinator coordinator;
    private Texts texts;
    private boolean completeMoveTutorial, completeOpenTutorial, completeConcludeTutorial;
    private TerrainLogic suggestOpenTerrainLogic, suggestMoveTerrainLogicFrom, suggestMoveTerrainLogicTo;
    private boolean closeOnNext;
    private boolean disposed;
    private GraveyardLogic graveyardLogic;

    public TutorialsHelper(GraveyardLogic graveyardLogic, GameCoordinator coordinator, Texts texts) {
        this.graveyardLogic = graveyardLogic;
        this.coordinator = coordinator;
        this.texts = texts;
        _this = this;

        check();
    }

    public void check(){
        if(coordinator.getTutorialsWrapper().completedTutorialBefore(Terms.TUTORIAL_MOVE)){
            completeMoveTutorial = false;
        }

        if(coordinator.getTutorialsWrapper().completedTutorialBefore(Terms.TUTORIAL_OPEN)){
            completeOpenTutorial = false;
        }

    }

    public void switchedToMyTurn(final ChessColor myChessColor, final ArrayList<TerrainLogic> terrains, final MovementRef movementRef){
        if(terrains.size() == 0) return;

        if(!completeOpenTutorial){
            for(int i = 13 ; i < terrains.size(); i++){
                if(!terrains.get(i).getChessLogic().getChessModel().getOpened()){
                    suggestOpenTerrainLogic = terrains.get(i);
                    completeOpenTutorial = true;
                    break;
                }
            }
            coordinator.getTutorialsWrapper().startTutorialIfNotCompleteBefore(Terms.TUTORIAL_OPEN,
                    false, _this);

        }
        else if(!completeMoveTutorial){
            for(int i = 0 ; i < terrains.size(); i++){
                TerrainLogic terrainLogic = terrains.get(i);
                if(terrainLogic.getChessLogic().getChessModel().getOpened() &&
                        terrainLogic.getChessLogic().getChessModel().getChessColor() == myChessColor){
                    ArrayList<TerrainLogic> possibleMovements = movementRef.getPossibleValidMoves(terrains, terrainLogic);
                    if(possibleMovements.size() > 0){
                        suggestMoveTerrainLogicFrom = terrainLogic;
                        suggestMoveTerrainLogicTo = possibleMovements.get(0);
                        completeMoveTutorial = true;
                        break;
                    }
                }
            }

            if(suggestMoveTerrainLogicFrom != null && suggestMoveTerrainLogicTo != null){
                coordinator.getTutorialsWrapper().startTutorialIfNotCompleteBefore(Terms.TUTORIAL_MOVE,
                        false, _this);
            }

        }


    }


    @Override
    public void nextTutorial() {
        if(closeOnNext){
            if(completeOpenTutorial && completeMoveTutorial && !completeConcludeTutorial){
                Vector2 position = Positions.actorLocalToStageCoord(graveyardLogic.getGraveyardActor().getTutorialButton());
                coordinator.getTutorialsWrapper().expectGestureOnPosition(GestureType.PointRight, "See here for more,", 0, 20,
                        position.x, position.y + 3, 0, 0);
                completeConcludeTutorial = true;
                closeOnNext = true;
            }
            else{
                coordinator.getTutorialsWrapper().completeTutorial();
                closeOnNext = false;
            }
        }

        if(suggestOpenTerrainLogic != null){
            Vector2 position = Positions.actorLocalToStageCoord(suggestOpenTerrainLogic.getChessLogic().getChessActor());
            coordinator.getTutorialsWrapper().expectGestureOnPosition(GestureType.Swipe, "Swipe to open", 0, 15,
                    position.x, position.y + 20, 60, 0);
            closeOnNext = true;
            suggestOpenTerrainLogic = null;
        }


        if(suggestMoveTerrainLogicFrom != null && suggestMoveTerrainLogicTo != null){
            Vector2 positionFrom = Positions.actorLocalToStageCoord(suggestMoveTerrainLogicFrom.getChessLogic().getChessActor());
            Vector2 positionTo = Positions.actorLocalToStageCoord(suggestMoveTerrainLogicTo.getChessLogic().getChessActor());
            coordinator.getTutorialsWrapper().expectGestureOnPosition(GestureType.Drag, "Drag to move", 15, 0,
                    positionFrom.x + 23, positionFrom.y + 20, (int) (positionTo.x - positionFrom.x), (int) (positionTo.y - positionFrom.y));
            closeOnNext = true;
            suggestMoveTerrainLogicTo = null;
            suggestMoveTerrainLogicFrom = null;
        }
    }


}
