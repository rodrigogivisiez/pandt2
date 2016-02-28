package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.Gdx;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.InGameUpdateListener;
import com.potatoandtomato.games.screens.BoardLogic;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class RoomMsgHandler {

    private BoardLogic _boardLogic;
    private GameCoordinator _coordinator;
    private ArrayList<Runnable> _messagesQueue;
    private boolean _gameScreenReady;

    public RoomMsgHandler(BoardLogic boardLogic, GameCoordinator coordinator) {
        this._boardLogic = boardLogic;
        this._coordinator = coordinator;
        _messagesQueue = new ArrayList<Runnable>();

        _coordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                receivedInGameUpdate(s, s1);
            }
        });

    }

    public void receivedInGameUpdate(final String msg, final String senderId){
        try {

            if(senderId.equals(_coordinator.getUserId())) return;

            JSONObject jsonObject = new JSONObject(msg);
            int code = jsonObject.getInt("code");
            final String receivedMsg = jsonObject.getString("msg");
            String[] tmp = receivedMsg.split("@");
            String enemyLeftTime = tmp[0];
            String realMsg = tmp[1];

            if(!_gameScreenReady){
                _messagesQueue.add(new Runnable() {
                    @Override
                    public void run() {
                        receivedInGameUpdate(msg, senderId);
                    }
                });
                return;
            }

            _boardLogic.updateEnemyLeftTime(enemyLeftTime);

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTerrainSelected(int col, int row, int myLeftTime){
        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.TERRAIN_SELECTED, myLeftTime + "@" + col + "," + row));
    }

    public void sendChessOpenFull(int col, int row, String random, int myLeftTime){
        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_OPEN_FULL, myLeftTime + "@" + col + "," + row + "|" + random));
    }

    public void sendMoveChess(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon, String random, int myLeftTime){
        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_MOVE, myLeftTime + "@" +
                fromCol +"," + fromRow + "|" + toCol + "," + toRow + "|" + (isFromWon ? "1" : "0") + "|" + random));
    }

    public void onGameReady(){
        _gameScreenReady = true;
        for(int i = 0; i< _messagesQueue.size(); i++){
            _messagesQueue.get(i).run();
        }
        _messagesQueue.clear();

    }


}
