package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.InGameUpdateListener;
import com.potatoandtomato.games.actors.ChessActor;
import com.potatoandtomato.games.actors.DummyImage;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.Services;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class MainScreenLogic {

    private GameCoordinator _coordinator;
    private Services _services;
    private MainScreen _mainScreen;
    private ChessType[][] _chessTypes;
    private boolean _meIsRed;
    private boolean _isMyTurn;
    private Table[][] _plates;
    private ArrayList<ChessType> _originalChessTypes;
    private int _randomFirstInfo;
    private boolean _initialized;
    private boolean _isContinue;

    public MainScreenLogic(Services _services, GameCoordinator _coordinator, boolean isContinue) {
        this._services = _services;
        this._coordinator = _coordinator;
        this._isContinue = isContinue;
        _randomFirstInfo = MathUtils.random(0, 3);

        _originalChessTypes = new ArrayList<ChessType>();
        _chessTypes = new ChessType[9][10];
        _mainScreen = new MainScreen(_coordinator, _services);
        _mainScreen.setRootCanTouch(false);

        _coordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String msg, String senderId) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    receiveUpdate(jsonObject.getInt("code"), jsonObject.getString("msg"), senderId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(!isContinue){
            if(_coordinator.isHost()){
                sendFirstTimeInfoIfHost();
            }
            else{
                sendRequestFirstTimeInfoIfNeeded();
            }
        }
        else{
            sendContinueRequest();
        }

    }

    public void sendContinueRequest(){
        _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.CONTINUE_REQUEST, ""));
    }

    public void sendContinueReply(){
        _mainScreen.getBlockingOverlay().setVisible(true);
        JSONObject jsonObject = new JSONObject();
        String result = "";
        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                result += _chessTypes[reflectCol(col)][reflectRow(row)].ordinal() + ",";
            }
        }
        result = result.substring(0, result.length() - 1);
        try {
            jsonObject.put("chesses", result);
            jsonObject.put("isRed", !_meIsRed);
            jsonObject.put("isYourTurn", !_isMyTurn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.CONTINUE_REPLY, jsonObject.toString()));
    }

    public void receiveContinueReply(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.getString("chesses");
            _meIsRed = jsonObject.getBoolean("isRed");
            _isMyTurn = jsonObject.getBoolean("isYourTurn");

            String[] tmp = result.split(",");

            ChessType[] values = ChessType.values();

            int i = 0;
            for(int row = 0; row < 10; row++){
                for(int col = 0; col < 9 ; col++) {
                    _chessTypes[col][row] = values[Integer.parseInt(tmp[i])];
                    i++;
                }
            }

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    onContinue();
                    _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.CONTINUE_REPLY_RECEIVED, ""));
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendFirstTimeInfoIfHost(){
        _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.FIRST_TIME_INFO, String.valueOf(_randomFirstInfo)));
    }

    public void sendRequestFirstTimeInfoIfNeeded(){
        if(!_initialized) _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.REQUEST_FIRST_TIME_INFO, ""));
    }



    public MainScreen getMainScreen() {
        return _mainScreen;
    }

    public void receiveUpdate(int code, String msg, String senderId){
        if(code == UpdateCode.SEND_MOVE){
            if(!senderId.equals(_coordinator.getUserId())){
                receiveMove(msg);
            }
        }
        else if(code == UpdateCode.SELECT_CHESS){
            if(!senderId.equals(_coordinator.getUserId())){
                receiveSelectChess(msg);
            }
        }
        else if(code == UpdateCode.CONTINUE_REQUEST){
            if(!senderId.equals(_coordinator.getUserId()) && !_isContinue){
                sendContinueReply();
            }
        }
        else if(code == UpdateCode.CONTINUE_REPLY){
            if(_isContinue){
                _isContinue = false;
                receiveContinueReply(msg);
            }
        }
        else if(code == UpdateCode.CONTINUE_REPLY_RECEIVED){
            _mainScreen.getBlockingOverlay().setVisible(false);
        }
        else if(code == UpdateCode.REQUEST_FIRST_TIME_INFO){
            if(_coordinator.isHost()){
                sendFirstTimeInfoIfHost();
            }
        }
        else if(code == UpdateCode.FIRST_TIME_INFO){
            if(_initialized) return;
            _initialized = true;
            if(msg.equals("0")){
                if(_coordinator.getHostUserId().equals(_coordinator.getUserId())){
                    _meIsRed = true;
                    _isMyTurn = true;
                }
                else{
                    _meIsRed = false;
                    _isMyTurn = false;
                }
            }
            else if(msg.equals("1")){
                if(_coordinator.getHostUserId().equals(_coordinator.getUserId())){
                    _meIsRed = true;
                    _isMyTurn = false;
                }
                else{
                    _meIsRed = false;
                    _isMyTurn = true;
                }
            }
            else if(msg.equals("2")){
                if(_coordinator.getHostUserId().equals(_coordinator.getUserId())){
                    _meIsRed = false;
                    _isMyTurn = true;
                }
                else{
                    _meIsRed = true;
                    _isMyTurn = false;
                }
            }
            else if(msg.equals("3")){
                if(_coordinator.getHostUserId().equals(_coordinator.getUserId())){
                    _meIsRed = false;
                    _isMyTurn = false;
                }
                else{
                    _meIsRed = true;
                    _isMyTurn = true;
                }
            }
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
        }
    }

    public void sendMove(int fromCol, int fromRow, int toCol, int toRow){
        _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.SEND_MOVE, fromCol + "," + fromRow + "-" + toCol + "," + toRow));
    }

    public void sendSelectChess(int col, int row){
        _coordinator.sendRoomUpdate(UpdateHelper.toJson(UpdateCode.SELECT_CHESS, col + "," + row));
    }

    public void receiveMove(String move){
        String[] temp = move.split("-");
        String[] from = temp[0].split(",");
        String[] to = temp[1].split(",");

        int fromCol = reflectCol(Integer.valueOf(from[0]));
        int fromRow = reflectRow(Integer.valueOf(from[1]));
        int toCol = reflectCol(Integer.valueOf(to[0]));
        int toRow = reflectRow(Integer.valueOf(to[1]));

        moveChess(fromCol, fromRow, toCol, toRow, true);
    }

    public void receiveSelectChess(String coord){
        clearAllChessSelection();
        String[] temp = coord.split(",");
        int col = reflectCol(Integer.valueOf(temp[0]));
        int row = reflectRow(Integer.valueOf(temp[1]));
        if(_plates[col][row].findActor("chessActor") != null){
            ((ChessActor) _plates[col][row].findActor("chessActor")).setSelected(true);
        }

    }

    public void init(){
        initiateChessTypes();
        _plates =_mainScreen.populateChesses(_chessTypes);
        setChessesDragListeners();
        switchTurn(_isMyTurn);
    }

    public void onContinue(){
        ChessType[][] playedChessTypes = getChessTypesClone();
        initiateChessTypes();
        _chessTypes = playedChessTypes;
        _plates =_mainScreen.populateChesses(playedChessTypes);
        setChessesDragListeners();
        switchTurn(_isMyTurn);
        refreshGraveYard();
    }

    private void initiateChessTypes(){
        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                _chessTypes[col][row] = ChessType.EMPTY;
            }
        }

        int rowRed0 = 0;
        int rowRed1 = 2;
        int rowRed2 = 3;
        int rowBlack0 = 9;
        int rowBlack1 = 7;
        int rowBlack2 = 6;

        if(!_meIsRed){
            rowRed0 = reflectRow(rowRed0);
            rowRed1 = reflectRow(rowRed1);
            rowRed2 = reflectRow(rowRed2);

            rowBlack0 = reflectRow(rowBlack0);
            rowBlack1 = reflectRow(rowBlack1);
            rowBlack2 = reflectRow(rowBlack2);
        }

        _chessTypes[0][rowRed0] = ChessType.RED_CHE;
        _chessTypes[1][rowRed0] = ChessType.RED_MA;
        _chessTypes[2][rowRed0] = ChessType.RED_XIANG;
        _chessTypes[3][rowRed0] = ChessType.RED_SHI;
        _chessTypes[4][rowRed0] = ChessType.RED_SHUAI;
        _chessTypes[5][rowRed0] = ChessType.RED_SHI;
        _chessTypes[6][rowRed0] = ChessType.RED_XIANG;
        _chessTypes[7][rowRed0] = ChessType.RED_MA;
        _chessTypes[8][rowRed0] = ChessType.RED_CHE;
        _chessTypes[1][rowRed1] = ChessType.RED_PAO;
        _chessTypes[7][rowRed1] = ChessType.RED_PAO;
        _chessTypes[0][rowRed2] = ChessType.RED_BING;
        _chessTypes[2][rowRed2] = ChessType.RED_BING;
        _chessTypes[4][rowRed2] = ChessType.RED_BING;
        _chessTypes[6][rowRed2] = ChessType.RED_BING;
        _chessTypes[8][rowRed2] = ChessType.RED_BING;

        _chessTypes[0][rowBlack0] = ChessType.BLACK_CHE;
        _chessTypes[1][rowBlack0] = ChessType.BLACK_MA;
        _chessTypes[2][rowBlack0] = ChessType.BLACK_XIANG;
        _chessTypes[3][rowBlack0] = ChessType.BLACK_SHI;
        _chessTypes[4][rowBlack0] = ChessType.BLACK_SHUAI;
        _chessTypes[5][rowBlack0] = ChessType.BLACK_SHI;
        _chessTypes[6][rowBlack0] = ChessType.BLACK_XIANG;
        _chessTypes[7][rowBlack0] = ChessType.BLACK_MA;
        _chessTypes[8][rowBlack0] = ChessType.BLACK_CHE;
        _chessTypes[1][rowBlack1] = ChessType.BLACK_PAO;
        _chessTypes[7][rowBlack1] = ChessType.BLACK_PAO;
        _chessTypes[0][rowBlack2] = ChessType.BLACK_BING;
        _chessTypes[2][rowBlack2] = ChessType.BLACK_BING;
        _chessTypes[4][rowBlack2] = ChessType.BLACK_BING;
        _chessTypes[6][rowBlack2] = ChessType.BLACK_BING;
        _chessTypes[8][rowBlack2] = ChessType.BLACK_BING;

        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                if(_chessTypes[col][row] != ChessType.EMPTY){
                    _originalChessTypes.add(_chessTypes[col][row]);
                }
            }
        }

        Collections.sort(_originalChessTypes);

    }

    private void refreshGraveYard(){
        ArrayList<ChessType> result = (ArrayList) _originalChessTypes.clone();

        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                if(_chessTypes[col][row] != ChessType.EMPTY){
                    for(int i = 0; i < result.size(); i++){
                        if(result.get(i) == _chessTypes[col][row]){
                            result.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        _mainScreen.clearGraveTable();

        for(ChessType leftChessType : result){
            addToGrave(leftChessType);
        }

    }

    private void setChessesDragListeners(){
        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                if(_chessTypes[col][row] != ChessType.EMPTY && isMyChess(_chessTypes[col][row])){
                    final DragAndDrop dragAndDrop = new DragAndDrop();
                    dragAndDrop.setDragTime(0);
                    final ChessActor chessActor = _plates[col][row].findActor("chessActor");
                    final int finalCol = col;
                    final int finalRow = row;
                    dragAndDrop.addSource(new DragAndDrop.Source(_plates[finalCol][finalRow].findActor("chessActor")) {
                        public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                            DragAndDrop.Payload payload = new DragAndDrop.Payload();
                            Table clone = chessActor.clone();
                            payload.setDragActor(clone);
                            chessActor.getColor().a = 0;
                            dragAndDrop.setDragActorPosition(-x, -y + clone.getHeight());
                            return payload;
                        }

                        @Override
                        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                            super.dragStop(event, x, y, pointer, payload, target);
                            chessActor.getColor().a = 1;

                        }
                    });

                    for(int row1 = 0; row1 < 10; row1++){
                        for(int col1 = 0; col1 < 9 ; col1++) {
                            final Table boxTable = _plates[col1][row1];
                            final int finalCol1 = col1;
                            final int finalRow1 = row1;
                            dragAndDrop.addTarget(new DragAndDrop.Target(boxTable) {
                                public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                                    return true;
                                }

                                public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                                }

                                public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                                    ChessType[][] testChessTypes = getChessTypesClone();
                                    testChessTypes[finalCol1][finalRow1] = testChessTypes[chessActor.getCol()][chessActor.getRow()];
                                    testChessTypes[chessActor.getCol()][chessActor.getRow()] = ChessType.EMPTY;

                                    if(canMoveTo(chessActor.getCol(), chessActor.getRow(), finalCol1, finalRow1, _chessTypes)){
                                        sendMove(chessActor.getCol(), chessActor.getRow(), finalCol1, finalRow1);
                                        moveChess(chessActor.getCol(), chessActor.getRow(), finalCol1, finalRow1, false);
                                    }
                                }
                            });
                        }
                    }

                    chessActor.addListener(new InputListener(){
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            if(!chessActor.isSelected()){
                                clearAllChessSelection();
                                chessActor.setSelected(true);
                                sendSelectChess(chessActor.getCol(), chessActor.getRow());
                            }
                            return super.touchDown(event, x, y, pointer, button);
                        }
                    });
                }
            }
        }
    }

    private void clearAllChessSelection(){
        for(int row1 = 0; row1 < 10; row1++){
            for(int col1 = 0; col1 < 9 ; col1++) {
                if(_chessTypes[col1][row1] != ChessType.EMPTY){
                    ((ChessActor) _plates[col1][row1].findActor("chessActor")).setSelected(false);
                }
            }
        }
    }

    private void moveChess(int fromCol, int fromRow, int toCol, int toRow, boolean animate){
        Table toBoxTable = _plates[toCol][toRow];
        Table fromBoxTable =  _plates[fromCol][fromRow];
        final ChessActor chessActor = fromBoxTable.findActor("chessActor");
        final ChessActor toChessActor = toBoxTable.findActor("chessActor");

        Vector2 fromCoord = Positions.actorLocalToStageCoord(chessActor);

        fromBoxTable.clear();
        new DummyImage(fromBoxTable, _services.getAssets());

        _chessTypes[toCol][toRow] = _chessTypes[fromCol][fromRow];
        _chessTypes[fromCol][fromRow] = ChessType.EMPTY;
        chessActor.setColRow(toCol, toRow);
        toBoxTable.clear();
        toBoxTable.add(chessActor).size(40, 40);
        chessActor.setVisible(!animate);

        Vector2 toCoord = Positions.actorLocalToStageCoord(chessActor);

        final boolean finalIsMyTurn = _isMyTurn;
        final Runnable imitatedKingAnimation = new Runnable() {
            @Override
            public void run() {
                boolean redKingImitated = isKingIntimitated(_chessTypes, true);
                boolean blackKingImitated = isKingIntimitated(_chessTypes, false);

                if(_meIsRed && redKingImitated && !finalIsMyTurn){
                    _mainScreen.showJiangJun(false);
                }
                else if(!_meIsRed && blackKingImitated && !finalIsMyTurn){
                    _mainScreen.showJiangJun(true);
                }
                else if(_meIsRed && blackKingImitated && finalIsMyTurn){
                    _mainScreen.showJiangJun(true);
                }
                else if(!_meIsRed && redKingImitated && finalIsMyTurn){
                    _mainScreen.showJiangJun(false);
                }
            }
        };

        if(animate){
            final Table clone = chessActor.clone();
            clone.setPosition(fromCoord.x, fromCoord.y);
            clone.addAction(sequence(moveTo(toCoord.x, toCoord.y, 0.3f), new Action() {
                @Override
                public boolean act(float delta) {
                    chessActor.setVisible(true);
                    clone.remove();
                    if(toChessActor != null){
                        refreshGraveYard();
                    }
                    imitatedKingAnimation.run();
                    return true;
                }
            }));
            toBoxTable.getStage().addActor(clone);
        }
        else{
            if(toChessActor != null){
                refreshGraveYard();
            }
            imitatedKingAnimation.run();
        }

        if(toChessActor != null){
            boolean endGame = false;
            if(toChessActor.getType() == ChessType.BLACK_SHUAI){
                endGame = true;
                if(_meIsRed) _mainScreen.showWinLose(true);
                else _mainScreen.showWinLose(false);
            }
            else if(toChessActor.getType() == ChessType.RED_SHUAI){
                endGame = true;
                if(_meIsRed) _mainScreen.showWinLose(false);
                else _mainScreen.showWinLose(true);
            }

            if(endGame){
                _mainScreen.setRootCanTouch(false);
                Threadings.delay(2000, new Runnable() {
                    @Override
                    public void run() {
                        _mainScreen.getOverlayMessageTable().addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                _coordinator.endGame();
                            }
                        });
                    }
                });
                return;
            }
        }

        switchTurn(!_isMyTurn);



    }

    public boolean isMyChess(ChessType chessType){
        boolean yours = true;
        if(chessType.name().startsWith("RED_") && !_meIsRed) yours = false;
        if(chessType.name().startsWith("BLACK_") && _meIsRed) yours = false;
        return yours;
    }

    public void switchTurn(boolean myTurn){
        _isMyTurn = myTurn;
        _mainScreen.switchTurn(_isMyTurn);
        _mainScreen.setRootCanTouch(_isMyTurn);
    }

    public void addToGrave(ChessType chessType){
        if(chessType == ChessType.EMPTY) return;

        boolean yours = isMyChess(chessType);
        _mainScreen.addToGraveTable(chessType, yours);
    }

    private int reflectRow(int row){
        return 9-row;
    }

    private int reflectCol(int col){
        return 8-col;
    }


    private boolean isKingIntimitated(ChessType[][] testChessTypes, boolean testRedKing){

        int toCol = 0, toRow = 0;
        if(testRedKing){
            for(int row = 0; row < 10; row++){
                for(int col = 0; col < 9 ; col++) {
                    if(testChessTypes[col][row] == ChessType.RED_SHUAI){
                        toCol = col;
                        toRow = row;
                        break;
                    }
                }
            }
        }
        else{
            for(int row = 0; row < 10; row++){
                for(int col = 0; col < 9 ; col++) {
                    if(testChessTypes[col][row] == ChessType.BLACK_SHUAI){
                        toCol = col;
                        toRow = row;
                        break;
                    }
                }
            }
        }

        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                if(testChessTypes[col][row] != ChessType.EMPTY){
                    if(canMoveTo(col, row, toCol, toRow, testChessTypes)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean canMoveTo(int fromCol, int fromRow, int toCol, int toRow, ChessType[][] testChessTypes){

        if(isSameChessColor(fromCol, fromRow, toCol, toRow, testChessTypes)) return false;

        ChessType fromChessType = testChessTypes[fromCol][fromRow];
        ChessType toChessType = testChessTypes[toCol][toRow];
        if(fromChessType.name().endsWith("_BING")){
            if(toRow == fromRow + 1 && fromCol == toCol){
                return true;
            }
            if(!checkInOwnTerritory(fromRow) && (toRow == fromRow && (Math.abs(toCol - fromCol) == 1))){
                return true;
            }
        }
        else if(fromChessType.name().endsWith("_CHE")){
            int count = checkChessCountWithinStraightLine(fromCol, fromRow, toCol, toRow, testChessTypes);
            return count == 0;
        }
        else if(fromChessType.name().endsWith("_PAO")){
            int count = checkChessCountWithinStraightLine(fromCol, fromRow, toCol, toRow, testChessTypes);
            return (count == 1 && toChessType != ChessType.EMPTY) || (count == 0 && toChessType == ChessType.EMPTY);
        }
        else if(fromChessType.name().endsWith("_SHUAI")){
            if(checkIsWithinBoxNineArea(toCol, toRow) && checkStraightDistanceIsOne(fromCol, fromRow, toCol, toRow)){
                return true;
            }
            else if(checkChessCountWithinStraightLine(fromCol, fromRow, toCol, toRow, testChessTypes) == 0 && toChessType.name().endsWith("_SHUAI")){
                return true;
            }
        }
        else if(fromChessType.name().endsWith("_XIANG")){
            return (getDiagonalDistance(fromCol, fromRow, toCol, toRow) == 2 &&
                    checkChessCountWithinDiagonal(fromCol, fromRow, toCol, toRow, testChessTypes) == 0 &&
                    checkInOwnTerritory(toRow));
        }
        else if(fromChessType.name().endsWith("_SHI")){
            return (getDiagonalDistance(fromCol, fromRow, toCol, toRow) == 1 &&
                    checkIsWithinBoxNineArea(toCol, toRow));
        }
        else if(fromChessType.name().endsWith("_MA")){
            if(toRow == fromRow + 2 && Math.abs(toCol - fromCol) == 1){
                return testChessTypes[fromCol][fromRow + 1] == ChessType.EMPTY;
            }
            else if(toRow == fromRow - 2 && Math.abs(toCol - fromCol) == 1){
                return testChessTypes[fromCol][fromRow - 1] == ChessType.EMPTY;
            }
            else if(toCol == fromCol + 2 && Math.abs(toRow - fromRow) == 1){
                return testChessTypes[fromCol + 1][fromRow] == ChessType.EMPTY;
            }
            else if(toCol == fromCol - 2 && Math.abs(toRow - fromRow) == 1){
                return testChessTypes[fromCol - 1][fromRow] == ChessType.EMPTY;
            }
        }
        return false;
    }

    private boolean isSameChessColor(int fromCol, int fromRow, int toCol, int toRow, ChessType[][] testChessTypes){
        ChessType fromChessType = testChessTypes[fromCol][fromRow];
        ChessType toChessType = testChessTypes[toCol][toRow];
        if(fromChessType.name().startsWith("RED_") && toChessType.name().startsWith("RED_")) return true;
        if(fromChessType.name().startsWith("BLACK_") && toChessType.name().startsWith("BLACK_")) return true;

        return false;
    }

    private boolean checkIsWithinBoxNineArea(int toCol, int toRow){
        return (toCol >= 3 && toCol <= 5 && toRow >=0 && toRow <=2);
    }

    private boolean checkStraightDistanceIsOne(int fromCol, int fromRow, int toCol, int toRow){
        if(fromCol == toCol){
            return (Math.abs(fromRow - toRow) == 1);
        }
        else if(fromRow == toRow){
            return (Math.abs(fromCol - toCol) == 1);
        }
        return false;
    }

    private int checkChessCountWithinStraightLine(int fromCol, int fromRow, int toCol, int toRow, ChessType[][] testChessTypes){
        if(!(fromCol == toCol || fromRow == toRow)){
            return -1;
        }

        int total = 0;
        if(fromCol == toCol){
            if(toRow > fromRow){
                while (toRow > fromRow + 1){
                    toRow--;
                    if(testChessTypes[toCol][toRow] != ChessType.EMPTY) total++;
                }
            }
            else{
                while (fromRow > toRow + 1){
                    toRow++;
                    if(testChessTypes[toCol][toRow] != ChessType.EMPTY) total++;
                }
            }
        }
        else{
            if(toCol > fromCol){
                while (toCol > fromCol + 1){
                    toCol--;
                    if(testChessTypes[toCol][toRow] != ChessType.EMPTY) total++;
                }
            }
            else{
                while (fromCol > toCol + 1){
                    toCol++;
                    if(testChessTypes[toCol][toRow] != ChessType.EMPTY) total++;
                }
            }
        }
        return total;
    }

    private int getDiagonalDistance(int fromCol, int fromRow, int toCol, int toRow){
        if(!(Math.abs(toCol - fromCol) == Math.abs(toRow - fromRow))){
            return -1;
        }
        else{
            return Math.abs(toCol - fromCol);
        }
    }

    private int checkChessCountWithinDiagonal(int fromCol, int fromRow, int toCol, int toRow, ChessType[][] testChessTypes){
        if(!(Math.abs(toCol - fromCol) == Math.abs(toRow - fromRow))){
            return -1;
        }

        int total = 0;

        if(fromCol == toCol || fromRow == toRow) return 0;

        if(toCol > fromCol && toRow > fromRow){
            for(int i = 1; i < toCol - fromCol; i++){
                if(testChessTypes[fromCol + i][fromRow + i] != ChessType.EMPTY) total++;
            }
        }
        else if(toCol > fromCol && toRow < fromRow){
            for(int i = 1; i < toCol - fromCol; i++){
                if(testChessTypes[fromCol + i][fromRow - i] != ChessType.EMPTY) total++;
            }
        }
        else if(toCol < fromCol && toRow > fromRow){
            for(int i = 1; i < toCol - fromCol; i++){
                if(testChessTypes[fromCol - i][fromRow + i] != ChessType.EMPTY) total++;
            }
        }
        else if(toCol < fromCol && toRow < fromRow){
            for(int i = 1; i < toCol - fromCol; i++){
                if(testChessTypes[fromCol - i][fromRow - i] != ChessType.EMPTY) total++;
            }
        }

        return total;

    }

    private boolean checkInOwnTerritory(int row){
        return row <= 4;
    }

    private ChessType[][] getChessTypesClone(){
        ChessType[][] result = new ChessType[9][10];
        for(int row = 0; row < 10; row++){
            for(int col = 0; col < 9 ; col++) {
                result[col][row] = _chessTypes[col][row];
            }
        }
        return result;
    }

}
