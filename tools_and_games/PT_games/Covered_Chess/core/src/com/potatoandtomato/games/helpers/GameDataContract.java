package com.potatoandtomato.games.helpers;

import com.potatoandtomato.common.absints.GameDataContractAbstract;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.ChessModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.screens.BoardLogic;
import com.potatoandtomato.games.screens.TerrainLogic;
import com.potatoandtomato.games.services.GameDataController;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 29/5/2016.
 */
public class GameDataContract extends GameDataContractAbstract {

    private GameDataController gameDataController;
    private BoardLogic boardLogic;

    public GameDataContract(GameDataController gameDataController, BoardLogic boardLogic) {
        this.gameDataController = gameDataController;
        this.boardLogic = boardLogic;
    }

    @Override
    public String generateGameData() {
        boardLogic.getBoardModel().setCurrentTurnIndex(gameDataController.getFirstTurnIndex());

        JsonObj jsonObject = new JsonObj();
        int i = 0;
        for(ChessModel chessModel : gameDataController.getGameData()){
            jsonObject.put(String.valueOf(i), chessModel.toJson());
            i++;
        }
        jsonObject.put("graveModel", boardLogic.getGraveyardLogic().getGraveModel().toJson());
        jsonObject.put("boardModel", boardLogic.getBoardModel().toJson());

        return jsonObject.toString();
    }

    @Override
    public String getCurrentGameData() {
        JsonObj jsonObject = new JsonObj();
        int i = 0;
        for(TerrainLogic logic : boardLogic.getTerrains()){
            jsonObject.put(String.valueOf(i), logic.getChessLogic().getChessModel().toJson());
            i++;
        }
        jsonObject.put("graveModel", boardLogic.getGraveyardLogic().getGraveModel().toJson());
        jsonObject.put("boardModel", boardLogic.getBoardModel().toJson());

        return jsonObject.toString();
    }

    @Override
    public void onGameDataOutdated() {
        boardLogic.gamePause();
    }

    @Override
    public void onGameDataReceived(String jsonData) {
        try {

            ArrayList<ChessModel> chessModels = new ArrayList<ChessModel>();
            JsonObj jsonObject = new JsonObj(jsonData);
            ObjectMapper mapper1 = new ObjectMapper();
            for (int i = 0; i < 32; i++) {
                chessModels.add(mapper1.readValue(jsonObject.getString(String.valueOf(i)), ChessModel.class));
            }
            GraveModel graveModel = mapper1.readValue(jsonObject.getString("graveModel"), GraveModel.class);
            BoardModel boardModel = mapper1.readValue(jsonObject.getString("boardModel"), BoardModel.class);

            boardLogic.gameDataReceived(boardModel, chessModels, graveModel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
