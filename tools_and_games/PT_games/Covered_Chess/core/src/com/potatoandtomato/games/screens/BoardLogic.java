package com.potatoandtomato.games.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.absint.ActionListener;
import com.potatoandtomato.games.absint.DatabaseListener;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.ChessModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.TerrainModel;
import com.shaded.fasterxml.jackson.core.JsonParseException;
import com.shaded.fasterxml.jackson.databind.JsonMappingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class BoardLogic {

    Services _services;
    GameCoordinator _coordinator;
    ArrayList<TerrainLogic> _terrains;
    GraveyardLogic _graveyard;

    BoardScreen _screen;
    GameDataController _gameDataController;
    TerrainLogic _lastActiveTerrainLogic;
    RoomMsgHandler _roomMsgHandler;
    SafeThread _getGameDataSafeThread;

    public BoardLogic(Services services, GameCoordinator coordinator, boolean isContinue) {
        this._services = services;
        this._coordinator = coordinator;
        _terrains = new ArrayList<TerrainLogic>();
        _gameDataController = new GameDataController(coordinator);
        _graveyard = new GraveyardLogic(new GraveModel(_gameDataController.getFirstTurnIndex()),
                coordinator, services.getTexts(), services.getAssets(), services.getSounds());
        _roomMsgHandler = new RoomMsgHandler(this, _coordinator);

        _screen = new BoardScreen(coordinator, services);

        Threadings.delay(200, new Runnable() {
            @Override
            public void run() {
                _screen.setGraveActor(_graveyard.getGraveyardActor());
                _graveyard.invalidate();
            }
        });

        setListeners();
    }

    //call when new game
    public void init(){

        if(_coordinator.meIsDecisionMaker()){
            gameDataReady(_gameDataController.getGameData(), _graveyard.getGraveModel());
            saveGameDataToDB();
        }
        else{
           getGameDataFromDB();
        }

        _services.getSounds().playTheme();


//        final String firstPlayerUsername = _coordinator.getTeams().get(0).getPlayers().get(0).getName();
//        final String secondPlayerUsername = _coordinator.getTeams().get(1).getPlayers().get(0).getName();
//
//        _services.getSounds().playSounds(Sounds.Name.START_GAME);
//
//        _screen.fadeInScreen(0.5f, new Runnable() {
//            @Override
//            public void run() {
//                _screen.populatePreStartTable(5f, firstPlayerUsername, secondPlayerUsername, new Runnable() {
//                    @Override
//                    public void run() {
//                        Threadings.runInBackground(new Runnable() {
//                            @Override
//                            public void run() {
//                                while(_gameInfo == null){
//                                    Threadings.sleep(1000);
//                                }
//
//                                Gdx.app.postRunnable(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        _screen.populateChessTable(_terrains, new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                _screen.populateTopInfoTable();
//                                                setTopInfoListener();
//                                                _screen.populateTransitionTable();
//                                                _screen.setChessTotalCount(ChessType.RED, String.valueOf(_redChessTotal));
//                                                _screen.setChessTotalCount(ChessType.YELLOW, String.valueOf(_yellowChessTotal));
//                                                switchTurn(_gameInfo.isYellowTurn());
//
//                                                if(_isContinue){
//                                                    for(ChessType type : _graveyard){
//                                                        chessIsKilled(getDrawableFromChessType(type), type.name().startsWith("YELLOW"));
//                                                    }
//                                                    _isContinue = false;
//                                                    _initialized = true;
//                                                    _coordinator.sendRoomUpdate(UpdateRoomHelper.convertToJson(UpdateCode.SUCCESS_CONTINUE, ""));
//                                                }
//                                                //only for continue game
//
//                                                _services.getSounds().playTheme();
//                                                onGameScreenReady();
//                                            }
//                                        });
//                                    }
//                                });
//                            }
//                        });
//                    }
//                });
//            }
//        });
    }

    public void continueGame(){
        getGameDataFromDB();
        _services.getSounds().playTheme();
    }

    private void saveGameDataToDB(){
        JSONObject jsonObject = new JSONObject();
        try {
            int i = 0;
            for(TerrainLogic logic : _terrains){
                jsonObject.put(String.valueOf(i), logic.getChessLogic().getChessModel().toJson());
                i++;
            }
            jsonObject.put("graveModel", _graveyard.getGraveModel().toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _services.getDatabase().saveGameData(jsonObject.toString());

    }

    private void getGameDataFromDB(){
        _getGameDataSafeThread = new SafeThread();
        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        final String[] json = new String[1];
                        while (!_getGameDataSafeThread.isKilled()){
                            _services.getDatabase().getGameData(new DatabaseListener<String>(String.class) {
                                @Override
                                public void onCallback(String result, Status st) {
                                    if(st == Status.SUCCESS && result != null && !result.equals("")){
                                        json[0] = result;
                                        _getGameDataSafeThread.kill();
                                    }
                                }
                            });
                            int i = 0;
                            while (!_getGameDataSafeThread.isKilled() && i < 5){
                                Threadings.sleep(500);
                                i++;
                            }
                        }
                        try {
                            ArrayList<ChessModel> chessModels = new ArrayList<ChessModel>();
                            JSONObject jsonObject = new JSONObject(json[0]);
                            ObjectMapper mapper1 = new ObjectMapper();
                            for(int i = 0; i < 32; i++){
                                chessModels.add(mapper1.readValue(jsonObject.getString(String.valueOf(i)), ChessModel.class));
                            }
                            GraveModel graveModel = mapper1.readValue(jsonObject.getString("graveModel"), GraveModel.class);

                            gameDataReady(chessModels, graveModel);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }


    private void gameDataReady(ArrayList<ChessModel> chessModels, GraveModel graveModel){

        _graveyard.setGraveModel(graveModel);

        int i = 0;
        for(int row = 0; row < 8 ; row++){
            for(int col = 0; col < 4; col++){
                final ChessModel chessModel = chessModels.get(i);
                TerrainLogic terrainLogic = new TerrainLogic(new TerrainModel(col, row),
                        _services.getAssets(), _coordinator, chessModel,
                        _services.getSounds(), _gameDataController, _services.getBattleReference());
                terrainLogic.setActionListener(new ActionListener() {
                    @Override
                    public void onSelected() {
                        if(!this.getTerrainLogic().isSelected()){
                            terrainSelected(this.getTerrainLogic().getTerrainModel().getCol(),
                                    this.getTerrainLogic().getTerrainModel().getRow());
                            _roomMsgHandler.sendTerrainSelected(this.getTerrainLogic().getTerrainModel().getCol(),
                                    this.getTerrainLogic().getTerrainModel().getRow());
                        }
                    }

                    @Override
                    public void onOpened() {
                        openChess(this.getTerrainLogic().getTerrainModel().getCol(),
                                this.getTerrainLogic().getTerrainModel().getRow());
                        _roomMsgHandler.sendChessOpenFull(this.getTerrainLogic().getTerrainModel().getCol(),
                                this.getTerrainLogic().getTerrainModel().getRow());
                    }

                    @Override
                    public void onMoved(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon) {
                        chessMoved(fromCol, fromRow, toCol, toRow, isFromWon, false);
                        _roomMsgHandler.sendMoveChess(fromCol, fromRow, toCol, toRow, isFromWon);
                    }

                    @Override
                    public void changeTurnReady() {
                        switchTurn();
                    }

                    @Override
                    public void onChessKilled(ChessType chessType) {
                        chessKilled(chessType);
                    }
                });
                _terrains.add(terrainLogic);
                i++;
            }
        }

        _screen.populateTerrains(_terrains);
        _roomMsgHandler.onGameReady();

    }

    private void chessKilled(ChessType chessType){
        _graveyard.addChessToGrave(chessType);
        if(_graveyard.getGraveModel().getLeftChessCountByColor(ChessColor.RED) == 0){
            endGame(ChessColor.YELLOW);
        }
        else if(_graveyard.getGraveModel().getLeftChessCountByColor(ChessColor.YELLOW) == 0){
            endGame(ChessColor.RED);
        }
    }

    public void openChess(int col, int row){
        TerrainLogic openLogic =  getTerrainByPosition(col, row);
        _lastActiveTerrainLogic = openLogic;
        openLogic.getChessLogic().openChess();
    }

    public void chessMoved(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon, boolean showMovement){
        TerrainLogic fromLogic = getTerrainByPosition(fromCol, fromRow);
        TerrainLogic toLogic = getTerrainByPosition(toCol, toRow);
        _lastActiveTerrainLogic = toLogic;
        toLogic.moveChessToThis(fromLogic, showMovement, isFromWon);
    }

    private void switchTurn(){
        if(_lastActiveTerrainLogic != null){
            clearAllTerrainsHighlights();
            _lastActiveTerrainLogic.getChessLogic().getChessModel().setFocusing(true);
            _lastActiveTerrainLogic.getChessLogic().invalidate();
        }
        _graveyard.switchTurn();

        _screen.setCanTouchChessTable(_graveyard.getGraveModel().getCurrentTurnIndex() == _coordinator.getMyUniqueIndex());
    }

    public void terrainSelected(int col, int row){
        TerrainLogic clickedLogic = getTerrainByPosition(col, row);
        if(!clickedLogic.isSelected()){
            clearAllTerrainsHighlights();

            clickedLogic.setSelected(true);
            if(clickedLogic.isOpened()){
                showPossibleMoves(clickedLogic);
            }
        }
    }

    private void clearAllTerrainsHighlights(){
        for(TerrainLogic terrainLogic : _terrains){
            terrainLogic.getChessLogic().getChessModel().setFocusing(false);
            terrainLogic.setSelected(false);
            terrainLogic.hidePercentTile();
        }
    }

    public void showPossibleMoves(TerrainLogic logic){
        ArrayList<TerrainLogic> possibleMoveLogics = getPossibleValidMoves(logic);
        for(final TerrainLogic terrainLogic : possibleMoveLogics){
            terrainLogic.showPercentTile(logic);
        }
        logic.setDragAndDrop(possibleMoveLogics);
    }

    public ArrayList<TerrainLogic> getPossibleValidMoves(TerrainLogic logic){
        ArrayList<TerrainLogic> possibleMoveLogics = new ArrayList<TerrainLogic>();
        TerrainModel model = logic.getTerrainModel();
        if(model.getRow() -1 >= 0){
            possibleMoveLogics.add(getTerrainByPosition(model.getCol(), model.getRow()-1));
        }
        if(model.getRow() + 1 <= 7){
            possibleMoveLogics.add(getTerrainByPosition(model.getCol(), model.getRow()+1));
        }
        if(model.getCol() - 1 >= 0){
            possibleMoveLogics.add(getTerrainByPosition(model.getCol() - 1, model.getRow()));
        }
        if(model.getCol() + 1 <= 3){
            possibleMoveLogics.add(getTerrainByPosition(model.getCol() + 1, model.getRow()));
        }
        ArrayList<TerrainLogic> validMoveLogics = new ArrayList<TerrainLogic>();
        for(TerrainLogic terrainLogic : possibleMoveLogics){
            if(isValidMove(logic, terrainLogic)){
                validMoveLogics.add(terrainLogic);
            }
        }
        return validMoveLogics;
    }

    private TerrainLogic getTerrainByPosition(int col, int row){
        for(TerrainLogic terrainLogic : _terrains){
            if(terrainLogic.getTerrainModel().getCol() == col && terrainLogic.getTerrainModel().getRow() == row){
                return terrainLogic;
            }
        }
        return null;
    }

    private boolean isValidMove(TerrainLogic from, TerrainLogic to){

        if(to.isEmpty()) return true;

        if(from.getChessLogic().getChessModel().isRed() ==  to.getChessLogic().getChessModel().isRed()) return false;

        if(from.getChessLogic().getChessModel().isYellow() == to.getChessLogic().getChessModel().isYellow()) return false;

        if(!from.isOpened() || !to.isOpened()) return false;

        return true;
    }

    private void endGame(final boolean won){
        _screen.populateEndGameTable();
        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                _screen.showEndGameTable(won);
                _services.getSounds().stopTheme();
                _services.getSounds().playSounds(won ? Sounds.Name.WIN : Sounds.Name.LOSE);
                Threadings.delay(2000, new Runnable() {
                    @Override
                    public void run() {
                        _screen.getEndGameRootTable().addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                _coordinator.endGame();
                            }
                        });
                    }
                });

            }
        });
    }

    private void endGame(ChessColor wonChessColor) {
        endGame(_gameDataController.getMyChessColor() == wonChessColor);
    }

    public BoardScreen getScreen() {
        return _screen;
    }

    private void setListeners(){
        _coordinator.setUserStateListener(new UserStateListener() {
            @Override
            public void userAbandoned(String s) {
                if(!s.equals(_coordinator.getUserId())){
                    endGame(true);
                }
            }

            @Override
            public void userConnected(String s) {
                _screen.setPaused(false, _graveyard.getGraveModel().getCurrentTurnIndex() == _coordinator.getMyUniqueIndex());
            }

            @Override
            public void userDisconnected(String s) {
                clearAllTerrainsHighlights();
                _screen.setPaused(true, _graveyard.getGraveModel().getCurrentTurnIndex() == _coordinator.getMyUniqueIndex());
                saveGameDataToDB();
            }
        });

    }

}
