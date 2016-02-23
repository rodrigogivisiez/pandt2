package com.potatoandtomato.games.absint;

import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.screens.TerrainLogic;

/**
 * Created by SiongLeng on 20/2/2016.
 */
public abstract class ActionListener {

    TerrainLogic terrainLogic;

    public void setTerrainLogic(TerrainLogic terrainLogic) {
        this.terrainLogic = terrainLogic;
    }

    public TerrainLogic getTerrainLogic() {
        return terrainLogic;
    }

    public abstract void onSelected();

    public abstract void onOpened();

    public abstract void onMoved(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon);

    public abstract void changeTurnReady();

    public abstract void onChessKilled(ChessType chessType);

}
