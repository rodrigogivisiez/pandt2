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

            if(!_gameScreenReady){
                _messagesQueue.add(new Runnable() {
                    @Override
                    public void run() {
                        receivedInGameUpdate(msg, senderId);
                    }
                });
                return;
            }

            final String[] arr;
            switch (code){
                case UpdateCode.TERRAIN_SELECTED:
                    arr = receivedMsg.split(",");
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            _boardLogic.terrainSelected(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]));
                        }
                    });
                    break;
                case UpdateCode.CHESS_OPEN_FULL:
                    arr = receivedMsg.split(",");
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            _boardLogic.openChess(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]));
                        }
                    });
                    break;
                case UpdateCode.CHESS_MOVE:
                    arr = receivedMsg.split("\\|");
                    final String[] from = arr[0].split(",");
                    final String[] to = arr[1].split(",");
                    final boolean isFromWon = arr[2].equals("1");
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            _boardLogic.chessMoved(Integer.valueOf(from[0]), Integer.valueOf(from[1]),
                                    Integer.valueOf(to[0]), Integer.valueOf(to[1]), isFromWon, true);
                        }
                    });

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTerrainSelected(int col, int row){
        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.TERRAIN_SELECTED, col + "," + row));
    }

    public void sendChessOpenFull(int col, int row){
        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_OPEN_FULL, col + "," + row));
    }

    public void sendMoveChess(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon){
        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.CHESS_MOVE,
                fromCol +"," + fromRow + "|" + toCol + "," + toRow + "|" + (isFromWon ? "1" : "0")));
    }

    public void onGameReady(){
        _gameScreenReady = true;
        for(int i = 0; i< _messagesQueue.size(); i++){
            _messagesQueue.get(i).run();
        }
        _messagesQueue.clear();

    }


}
