package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.InGameUpdateListener;
import com.potatoandtomato.games.absint.MainScreenListener;
import com.potatoandtomato.games.actors.chesses.enums.ChessType;
import com.potatoandtomato.games.actors.plates.PlateLogic;
import com.potatoandtomato.games.helpers.Threadings;
import com.potatoandtomato.games.helpers.UpdateCode;
import com.potatoandtomato.games.helpers.UpdateRoomHelper;
import com.potatoandtomato.games.models.GameInfo;
import com.potatoandtomato.games.models.Services;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class MainScreenLogic {

    Services _services;
    GameCoordinator _coordinator;
    MainScreen _screen;
    Gson _gson;
    GameInfo _gameInfo;
    PlateLogic[][] _plateLogics;
    private boolean _isMyTurn;
    private int _redChessTotal, _yellowChessTotal;
    MainScreenListener _mainScreenListener;

    public MainScreenLogic(Services services, GameCoordinator coordinator) {
        this._services = services;
        this._coordinator = coordinator;
        _gson = new Gson();
        _plateLogics = new PlateLogic[4][8];
        _redChessTotal = _yellowChessTotal = 16;
        _mainScreenListener = new MainScreenListener() {
            @Override
            public void onChessKilled(Drawable animalDrawable, boolean isYellow) {
                chessIsKilled(animalDrawable, isYellow);
            }

            @Override
            public void onFinishAction(float delay) {
                preSwitchTurn(delay);
            }
        };

        _coordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                receiveInGameUpdate(s, s1);
            }
        });

        if(_coordinator.getMeIsHost()){
            computeAndSendGameInfo();
        }

        _screen = new MainScreen(coordinator, services);
    }

    public void init(){

        final String firstPlayerUsername = _coordinator.getTeams().get(0).getPlayers().get(0).getName();
        final String secondPlayerUsername = _coordinator.getTeams().get(1).getPlayers().get(0).getName();

        _screen.fadeInScreen(0.5f, new Runnable() {
            @Override
            public void run() {
                _screen.populatePreStartTable(5f, firstPlayerUsername, secondPlayerUsername, new Runnable() {
                    @Override
                    public void run() {
                        Threadings.runInBackground(new Runnable() {
                            @Override
                            public void run() {
                                while(_gameInfo == null){
                                    Threadings.sleep(1000);
                                }
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        _screen.populateChessTable(_plateLogics, new Runnable() {
                                            @Override
                                            public void run() {
                                                _screen.populateEndGameTable();
                                                _screen.populateTopInfoTable();
                                                setTopInfoListener();
                                                _screen.populateTransitionTable();
                                                _screen.setChessTotalCount(ChessType.RED, String.valueOf(_redChessTotal));
                                                _screen.setChessTotalCount(ChessType.YELLOW, String.valueOf(_yellowChessTotal));
                                                switchTurn(_gameInfo.isYellowTurn());
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void setTopInfoListener(){
        _screen.getTopInfoTable().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toggleTopInfo();
            }
        });
    }

    public void preSwitchTurn(float delay){
        if(_redChessTotal == 0 || _yellowChessTotal == 0) return;

        final boolean isYellowTurnNext;
        if(meIsYellow() && !_isMyTurn) isYellowTurnNext = true;
        else if(!meIsYellow() && _isMyTurn) isYellowTurnNext = true;
        else isYellowTurnNext = false;

        _screen.setCanTouchChessTable(false);

        Threadings.delay((long) delay, new Runnable() {
            @Override
            public void run() {
                switchTurn(isYellowTurnNext);
            }
        });
    }

    private void switchTurn(boolean isYellowTurn){
        _isMyTurn = ((isYellowTurn && meIsYellow()) || (!isYellowTurn && !meIsYellow())) ;
        _plateLogics[0][0].clearAllSelected();
        _screen.switchTurn(isYellowTurn, meIsYellow(), new Runnable() {
            @Override
            public void run() {
                _screen.setCanTouchChessTable(_isMyTurn);
            }
        });
    }

    private void computeAndSendGameInfo(){

        GameInfo gameInfo = new GameInfo();
        Random random = new Random();
        gameInfo.setYellowTurn(random.nextBoolean());

        Array<Integer> unAssignedAnimal = new Array<Integer>();
        for(int i = 0; i < 5; i++) unAssignedAnimal.add(ChessType.RED_MOUSE.ordinal());
        for(int i = 0; i < 5; i++) unAssignedAnimal.add(ChessType.YELLOW_MOUSE.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.RED_CAT.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.YELLOW_CAT.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.RED_DOG.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.YELLOW_DOG.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.RED_LION.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.YELLOW_LION.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.RED_TIGER.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.YELLOW_TIGER.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.RED_WOLF.ordinal());
        for(int i = 0; i < 2; i++) unAssignedAnimal.add(ChessType.YELLOW_WOLF.ordinal());
        for(int i = 0; i < 1; i++) unAssignedAnimal.add(ChessType.RED_ELEPHANT.ordinal());
        for(int i = 0; i < 1; i++) unAssignedAnimal.add(ChessType.YELLOW_ELEPHANT.ordinal());

        String chessesInfo = "";
        for(int q = 0; q < 32; q++){
            int index = MathUtils.random(0, unAssignedAnimal.size - 1);
            int ordinal = unAssignedAnimal.get(index);
            unAssignedAnimal.removeIndex(index);
            chessesInfo += ordinal + ",";
        }

        gameInfo.setChessInfo(chessesInfo);

        _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.GAME_INFO, _gson.toJson(gameInfo)));
    }

    private void receiveInGameUpdate(String msg, String senderId){
        try {
            JSONObject jsonObject = new JSONObject(msg);
            int code = jsonObject.getInt("code");
            String receivedMsg = jsonObject.getString("msg");
            String[] arr;
            switch (code){
                case UpdateCode.GAME_INFO:
                    gameInfoInitialized(receivedMsg);
                    break;
                case UpdateCode.CHESS_SELECTED:
                    if(!senderId.equals(_coordinator.getUserId())){
                        arr = receivedMsg.split(",");
                        _plateLogics[Integer.valueOf(arr[0])][Integer.valueOf(arr[1])].clearAllSelectedExceptSelf();
                        _plateLogics[Integer.valueOf(arr[0])][Integer.valueOf(arr[1])].setSelected(true);
                    }
                    break;
                case UpdateCode.CHESS_OPEN_FULL:
                    if(!senderId.equals(_coordinator.getUserId())) {
                        arr = receivedMsg.split(",");
                        _plateLogics[Integer.valueOf(arr[0])][Integer.valueOf(arr[1])].openChess();
                    }
                    break;
                case UpdateCode.CHESS_MOVE:
                    if(!senderId.equals(_coordinator.getUserId())) {
                        arr = receivedMsg.split("\\|");
                        String[] from = arr[0].split(",");
                        String[] to = arr[1].split(",");
                        String winner = arr[2];

                        _plateLogics[Integer.valueOf(to[0])][Integer.valueOf(to[1])].moveChessToThis(
                                _plateLogics[Integer.valueOf(from[0])][Integer.valueOf(from[1])], true, Integer.valueOf(winner), false
                        );
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gameInfoInitialized(String gameInfoString){
        GameInfo gameInfo = _gson.fromJson(gameInfoString, GameInfo.class);
        ArrayList<ChessType> chessTypes  = gameInfo.getChessTypes();
        int i = 0;
        for(int row = 0; row < 8 ; row++){
            for(int col = 0; col < 4; col++){
                ChessType chessType = chessTypes.get(i);
                PlateLogic plateLogic = new PlateLogic(_plateLogics, col, row,
                        _services.getAssets(), _services.getBattleReference(), _coordinator, chessType, meIsYellow(),
                        _mainScreenListener);
                _plateLogics[col][row] = plateLogic;
                i++;
            }
        }
        _gameInfo = gameInfo;
    }

    private void chessIsKilled(Drawable animalDrawable, boolean isYellow){
        if(isYellow){
            _yellowChessTotal -= 1;
            _screen.setChessTotalCount(ChessType.YELLOW, String.valueOf(_yellowChessTotal));
        }
        else{
            _redChessTotal -=1;
            _screen.setChessTotalCount(ChessType.RED, String.valueOf(_redChessTotal));
        }
        _screen.addToGraveyard(animalDrawable, isYellow);

        if(_redChessTotal == 0 || _yellowChessTotal == 0){
            endGame();
        }

    }

    public void endGame(){
        boolean won = false;
        if(_redChessTotal == 0 && meIsYellow()){
            won = true;
        }
        else if(_yellowChessTotal == 0 && !meIsYellow()){
            won = true;
        }
        final boolean finalWon = won;
        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                _screen.showEndGameTable(finalWon);
                Threadings.delay(5000, new Runnable() {
                    @Override
                    public void run() {
                        _coordinator.endGame();
                    }
                });

            }
        });

    }


    private boolean meIsYellow(){
        return _coordinator.getTeams().get(0).getPlayers().get(0).isMe;
    }

    public MainScreen getScreen() {
        return _screen;
    }
}
