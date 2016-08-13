package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.Gdx;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.InGameUpdateListener;
import com.potatoandtomato.games.screens.BoardLogic;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class RoomMsgHandler {

    private UpdateRoomHelper updateRoomHelper;
    private BoardLogic _boardLogic;
    private GameCoordinator _coordinator;
    private ArrayList<Runnable> _messagesQueue;
    private boolean _gameScreenReady;

    public RoomMsgHandler(BoardLogic boardLogic, GameCoordinator coordinator) {
        this._boardLogic = boardLogic;
        this._coordinator = coordinator;
        updateRoomHelper = new UpdateRoomHelper();
        _messagesQueue = new ArrayList<Runnable>();

        _coordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                receivedInGameUpdate(s, s1);
            }
        });

    }

    //cannot use senderId anymore to accommodate bot match
    public void receivedInGameUpdate(final String msg, final String userId){
        HashMap<String, String> map = updateRoomHelper.jsonToMap(msg);
        int code = Integer.valueOf(map.get("code"));
        final String receivedMsg = map.get("msg");
        final String senderId = map.get("userId");
        if(senderId.equals(_coordinator.getMyUserId())) return;

        String[] tmp;
        String enemyLeftTime = null;
        String realMsg = null;
        if(!receivedMsg.equals("")){
            tmp = receivedMsg.split("@!!");
            if(tmp.length > 1){
                enemyLeftTime = tmp[0];
                realMsg = tmp[1];
            }
            else{
                realMsg = tmp[0];
            }
        }

        if(!_gameScreenReady){
            _messagesQueue.add(new Runnable() {
                @Override
                public void run() {
                    receivedInGameUpdate(msg, senderId);
                }
            });
            return;
        }

        if(enemyLeftTime != null) _boardLogic.updateEnemyLeftTime(enemyLeftTime);

        final String[] arr;
        switch (code){
            case UpdateCode.TERRAIN_SELECTED:
                arr = realMsg.split(",");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _boardLogic.terrainSelected(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]));
                    }
                });
                break;
            case UpdateCode.CHESS_OPEN_FULL:
                String[] tmp2 = realMsg.split("\\|");
                arr = tmp2[0].split(",");
                final String randomString = tmp2[1];
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _boardLogic.openChess(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]), randomString);
                    }
                });
                break;
            case UpdateCode.CHESS_MOVE:
                arr = realMsg.split("\\|");
                final String[] from = arr[0].split(",");
                final String[] to = arr[1].split(",");
                final boolean isFromWon = arr[2].equals("1");
                final String random = arr[3];
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _boardLogic.chessMoved(Integer.valueOf(from[0]), Integer.valueOf(from[1]),
                                Integer.valueOf(to[0]), Integer.valueOf(to[1]), isFromWon, true, random);
                    }
                });
                break;
            case UpdateCode.SKIP_TURN:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _boardLogic.skipTurn();
                    }
                });
                break;
            case UpdateCode.SURRENDER:
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _boardLogic.endGame(true);
                    }
                });
                break;
        }
    }

    public void skipTurn(String fromUserId, int myLeftTime){
        _coordinator.sendRoomUpdate(
                updateRoomHelper.convertToJson(fromUserId, UpdateCode.SKIP_TURN, myLeftTime + "@!!0"));
    }

    public void sendTerrainSelected(String fromUserId, int col, int row, int myLeftTime){
        _coordinator.sendRoomUpdate(
                updateRoomHelper.convertToJson(fromUserId, UpdateCode.TERRAIN_SELECTED, myLeftTime + "@!!" + col + "," + row));
    }

    public void sendChessOpenFull(String fromUserId, int col, int row, String random, int myLeftTime){
        _coordinator.sendRoomUpdate(
                updateRoomHelper.convertToJson(fromUserId, UpdateCode.CHESS_OPEN_FULL, myLeftTime + "@!!" + col + "," + row + "|" + random));
    }

    public void sendMoveChess(String fromUserId, int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon, String random, int myLeftTime){
        _coordinator.sendRoomUpdate(
                updateRoomHelper.convertToJson(fromUserId, UpdateCode.CHESS_MOVE, myLeftTime + "@!!" +
                fromCol +"," + fromRow + "|" + toCol + "," + toRow + "|" + (isFromWon ? "1" : "0") + "|" + random));
    }

    public void sendSurrender(String fromUserId){
        _coordinator.sendRoomUpdate(
                updateRoomHelper.convertToJson(fromUserId, UpdateCode.SURRENDER, ""));
    }


    public void onGameReady(){
        _gameScreenReady = true;
        for(int i = 0; i< _messagesQueue.size(); i++){
            _messagesQueue.get(i).run();
        }
        _messagesQueue.clear();

    }


}
