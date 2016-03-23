package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.models.ChessModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class GameDataController {

    private GameCoordinator _coordinator;
    private int firstTurnIndex = -1;

    public GameDataController(GameCoordinator _coordinator) {
        this._coordinator = _coordinator;
    }

    public int getFirstTurnIndex(){
        if(firstTurnIndex == -1){
            Random random = new Random();
            firstTurnIndex = random.nextBoolean() ? 0 : 1;
        }
        return firstTurnIndex;
    }

    public ArrayList<ChessModel> getGameData(){
        ArrayList<ChessType> animals = new ArrayList<ChessType>();
        for(int i = 0; i < 5; i++) animals.add(ChessType.RED_MOUSE);
        for(int i = 0; i < 5; i++) animals.add(ChessType.YELLOW_MOUSE);
        for(int i = 0; i < 2; i++) animals.add(ChessType.RED_CAT);
        for(int i = 0; i < 2; i++) animals.add(ChessType.YELLOW_CAT);
        for(int i = 0; i < 2; i++) animals.add(ChessType.RED_DOG);
        for(int i = 0; i < 2; i++) animals.add(ChessType.YELLOW_DOG);
        for(int i = 0; i < 2; i++) animals.add(ChessType.RED_LION);
        for(int i = 0; i < 2; i++) animals.add(ChessType.YELLOW_LION);
        for(int i = 0; i < 2; i++) animals.add(ChessType.RED_TIGER);
        for(int i = 0; i < 2; i++) animals.add(ChessType.YELLOW_TIGER);
        for(int i = 0; i < 2; i++) animals.add(ChessType.RED_WOLF);
        for(int i = 0; i < 2; i++) animals.add(ChessType.YELLOW_WOLF);
        for(int i = 0; i < 1; i++) animals.add(ChessType.RED_ELEPHANT);
        for(int i = 0; i < 1; i++) animals.add(ChessType.YELLOW_ELEPHANT);

        Collections.shuffle(animals);

        ArrayList<ChessModel> chessModels = new ArrayList<ChessModel>();
        for(ChessType chessType : animals){
            chessModels.add(new ChessModel(chessType));
        }

        return chessModels;
    }

    public ChessColor getMyChessColor(){
        if(_coordinator.getMyUniqueIndex() == 0) return ChessColor.YELLOW;
        else return ChessColor.RED;
    }

    public ChessColor getEnemyChessColor(){
        if(getMyChessColor() == ChessColor.YELLOW) return ChessColor.RED;
        else return ChessColor.YELLOW;
    }
}
