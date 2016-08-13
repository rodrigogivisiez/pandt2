package com.potatoandtomato.games.references;

import com.badlogic.gdx.math.Vector2;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.helpers.Terrains;
import com.potatoandtomato.games.models.TerrainModel;
import com.potatoandtomato.games.screens.TerrainLogic;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/2/2016.
 */
public class MovementRef {

    private ArrayList<Move> previousMoves;

    public MovementRef() {
        this.previousMoves = new ArrayList();
    }

    public ArrayList<TerrainLogic> getPossibleValidMoves(ArrayList<TerrainLogic> _terrains, TerrainLogic logic){
        ArrayList<TerrainLogic> possibleMoveLogics = new ArrayList<TerrainLogic>();
        TerrainModel model = logic.getTerrainModel();

        ArrayList<Vector2> possibleMovePositions = new ArrayList<Vector2>();
        possibleMovePositions.add(new Vector2(model.getCol() - 1, model.getRow()));
        possibleMovePositions.add(new Vector2(model.getCol(), model.getRow() - 1));
        possibleMovePositions.add(new Vector2(model.getCol(), model.getRow() + 1));
        possibleMovePositions.add(new Vector2(model.getCol() + 1, model.getRow()));
        addToPossibleMovesIfNoNull(_terrains, possibleMovePositions, possibleMoveLogics);
        processStatus(_terrains, logic, possibleMoveLogics);

        ArrayList<TerrainLogic> validMoveLogics = new ArrayList<TerrainLogic>();
        for(TerrainLogic terrainLogic : possibleMoveLogics){
            if(isValidMove(_terrains, logic, terrainLogic)){
                if(!isRepeatedMove(logic, terrainLogic)){
                    validMoveLogics.add(terrainLogic);
                }
            }
        }
        return validMoveLogics;
    }

    private boolean isValidMove(ArrayList<TerrainLogic> _terrains, TerrainLogic from, TerrainLogic to){

        if(to.getTerrainModel().isBroken()) return false;

        if(from.getTerrainModel().getCol() - 2 == to.getTerrainModel().getCol()){
            if(checkHasChess(_terrains, from.getTerrainModel().getCol() -1, from.getTerrainModel().getRow())){
                return false;
            }
        }
        else if(to.getTerrainModel().getCol() - 2 == from.getTerrainModel().getCol()){
            if(checkHasChess(_terrains, to.getTerrainModel().getCol() -1, to.getTerrainModel().getRow())){
                return false;
            }
        }
        else if(from.getTerrainModel().getRow() - 2 == to.getTerrainModel().getRow()){
            if(checkHasChess(_terrains, from.getTerrainModel().getCol(), from.getTerrainModel().getRow() - 1)){
                return false;
            }
        }
        else if(to.getTerrainModel().getRow() - 2 == from.getTerrainModel().getRow()){
            if(checkHasChess(_terrains, to.getTerrainModel().getCol(), to.getTerrainModel().getRow() - 1)){
                return false;
            }
        }

        if(to.isEmpty()) return true;

        if(from.getChessLogic().getChessModel().isRed() ==  to.getChessLogic().getChessModel().isRed()) return false;

        if(from.getChessLogic().getChessModel().isYellow() == to.getChessLogic().getChessModel().isYellow()) return false;

        if(!from.isOpened() || !to.isOpened()) return false;

        return true;
    }

    private boolean checkHasChess(ArrayList<TerrainLogic> _terrains, int col, int row){
        TerrainLogic terrainLogic = Terrains.getTerrainLogicByPosition(_terrains, col, row);
        if(terrainLogic == null) return true;
        else{
            return !terrainLogic.isEmpty();
        }
    }

    private void addToPossibleMovesIfNoNull(ArrayList<TerrainLogic> _terrains, ArrayList<Vector2> positions,
                                            ArrayList<TerrainLogic> possibleMovelogics){
        for(Vector2 position : positions){
            TerrainLogic terrainLogic = Terrains.getTerrainLogicByPosition(_terrains, (int) position.x, (int) position.y);
            if(terrainLogic != null){
                possibleMovelogics.add(terrainLogic);
            }
        }
    }

    private void processStatus(ArrayList<TerrainLogic> _terrains,
                                                  TerrainLogic logic, ArrayList<TerrainLogic> possibleMovelogics){
        switch (logic.getChessLogic().getChessModel().getStatus()){
            case ANGRY: case KING: case VENGEFUL:
                TerrainModel model = logic.getTerrainModel();
                ArrayList<Vector2> possibleMovePositions = new ArrayList<Vector2>();
                possibleMovePositions.add(new Vector2(model.getCol() - 2, model.getRow()));
                possibleMovePositions.add(new Vector2(model.getCol() - 1, model.getRow() + 1));
                possibleMovePositions.add(new Vector2(model.getCol() - 1, model.getRow() - 1));
                possibleMovePositions.add(new Vector2(model.getCol(), model.getRow() + 2));
                possibleMovePositions.add(new Vector2(model.getCol(), model.getRow() - 2));
                possibleMovePositions.add(new Vector2(model.getCol() + 2, model.getRow()));
                possibleMovePositions.add(new Vector2(model.getCol() + 1, model.getRow() + 1));
                possibleMovePositions.add(new Vector2(model.getCol() + 1, model.getRow() - 1));
                addToPossibleMovesIfNoNull(_terrains, possibleMovePositions, possibleMovelogics);
                break;
            case PARALYZED:
                possibleMovelogics.clear();
                break;
        }
    }

    private boolean isRepeatedMove(TerrainLogic from, TerrainLogic to){
        if(previousMoves.size() >= 14){
            Move nextMove = new Move(from.getChessLogic().getChessModel().getChessType(),
                    to.getTerrainModel().getCol(), to.getTerrainModel().getRow());


            boolean repeated = false;
            if(previousMoves.get(3).equals(nextMove) &&
                    previousMoves.get(3).equals(previousMoves.get(7)) &&
                    previousMoves.get(3).equals(previousMoves.get(11))){
                repeated = true;
            }

            if(repeated){
                repeated = false;
                if(previousMoves.get(0).equals(previousMoves.get(4)) &&
                        previousMoves.get(0).equals(previousMoves.get(8)) &&
                        previousMoves.get(0).equals(previousMoves.get(12))) {
                    repeated = true;
                }
            }

            if(repeated){
                repeated = false;
                if(previousMoves.get(1).equals(previousMoves.get(5)) &&
                        previousMoves.get(1).equals(previousMoves.get(9)) &&
                        previousMoves.get(1).equals(previousMoves.get(13))) {
                    repeated = true;
                }
            }

            if(repeated){
                if(previousMoves.get(2).equals(previousMoves.get(6)) &&
                        previousMoves.get(2).equals(previousMoves.get(10))) {
                    return true;
                }
            }

        }
        return false;
    }

    public void addPreviousMove(TerrainLogic terrainLogic, int toCol, int toRow){
        previousMoves.add(0, new Move(terrainLogic.getChessLogic().getChessModel().getChessType(), toCol, toRow));
    }

    public void clearPreviousMoves(){
        previousMoves.clear();
    }


    private class Move {
        private ChessType chessType;
        private int col;
        private int row;

        public Move(ChessType chessType, int col, int row) {
            this.chessType = chessType;
            this.col = col;
            this.row = row;
        }

        public ChessType getChessType() {
            return chessType;
        }

        public int getCol() {
            return col;
        }

        public int getRow() {
            return row;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Move){
                Move compareMove = (Move) o;
                return this.getChessType() == compareMove.getChessType() && this.getCol() == compareMove.getCol() &&
                        this.getRow() == compareMove.getRow();
            }
            else{
                return super.equals(o);
            }
        }
    }

}



