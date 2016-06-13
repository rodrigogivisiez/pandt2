package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.Game;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameDataContractAbstract;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.GameModel;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by SiongLeng on 31/5/2016.
 */
public class GameDataContract extends GameDataContractAbstract {

    private GameModel gameModel;
    private boolean hasData;
    private GameCoordinator gameCoordinator;

    public GameDataContract(GameModel gameModel, GameCoordinator gameCoordinator) {
        this.gameModel = gameModel;
        this.gameCoordinator = gameCoordinator;

   }

    @Override
    public String generateGameData() {
        return gameModelToJson();
    }

    @Override
    public String getCurrentGameData() {
        return gameModelToJson();
    }

    @Override
    public void onGameDataOutdated() {
        hasData = false;
        if(gameCoordinator.getTotalPlayersCount() > 1){
            gameModel.setGameState(GameState.BeforeContinue);
        }
    }

    @Override
    public void onGameDataReceived(String s) {
        if(!hasData){
            if(gameCoordinator.getTotalPlayersCount() > 1){
                reconstructGameModelIfNoData(s);
            }
            hasData = true;
        }
    }

    public boolean isHasData() {
        return hasData;
    }

    public String gameModelToJson(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(gameModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void reconstructGameModelIfNoData(String receivedGameModelJson){
        if(!isHasData()){
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                GameModel receivedGameModel = objectMapper.readValue(receivedGameModelJson, GameModel.class);

                if(receivedGameModel.getGameState() == GameState.Lose
                        || receivedGameModel.getGameState() == GameState.WonWithoutContributions
                        || receivedGameModel.getGameState() == GameState.Playing){

                }
                else if(receivedGameModel.getGameState() == GameState.Won){
                    receivedGameModel.setGameState(GameState.WonWithoutContributions);
                } else {
                    receivedGameModel.setGameState(gameModel.getGameState());   //set to before continue
                }

                this.gameModel.copyGameModelDataToThis(receivedGameModel);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       // _scoresLogic.refreshAllScores();
    }

}
