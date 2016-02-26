package com.potatoandtomato.games.enums;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public enum  ChessType {

    UNKNOWN,
    RED_MOUSE,
    RED_CAT,
    RED_DOG,
    RED_WOLF,
    RED_TIGER,
    RED_LION,
    RED_ELEPHANT,
    YELLOW_MOUSE,
    YELLOW_CAT,
    YELLOW_DOG,
    YELLOW_WOLF,
    YELLOW_TIGER,
    YELLOW_LION,
    YELLOW_ELEPHANT,
    NONE;



    public ChessAnimal toChessAnimal(){
        String name = this.name();
        String[] splitted = name.split("_");
        if(splitted.length > 1){
            return ChessAnimal.valueOf(splitted[1]);
        }
        else{
            return ChessAnimal.NONE;
        }
    }
}
