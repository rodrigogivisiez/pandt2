package com.potatoandtomato.games.actors.chesses;

import com.potatoandtomato.games.helpers.Assets;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class ChessLogic {

    ChessActor _chessActor;

    public ChessLogic(Assets assets) {
        _chessActor = new ChessActor(assets);
    }

    public ChessActor getChessActor() {
        return _chessActor;
    }
}
