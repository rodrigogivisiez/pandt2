package com.potatoandtomato.games.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.BackKeyListener;
import com.potatoandtomato.common.absints.UserStateListener;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.utils.ThreadsPool;
import com.potatoandtomato.games.absint.ActionListener;
import com.potatoandtomato.games.absint.DatabaseListener;
import com.potatoandtomato.games.absint.ScoresListener;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ActionType;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.ArrayLists;
import com.potatoandtomato.games.helpers.RoomMsgHandler;
import com.potatoandtomato.games.helpers.Strings;
import com.potatoandtomato.games.helpers.Terrains;
import com.potatoandtomato.games.models.*;
import com.potatoandtomato.games.references.BattleRef;
import com.potatoandtomato.games.references.MovementRef;
import com.potatoandtomato.games.references.StatusRef;
import com.potatoandtomato.games.services.GameDataController;
import com.potatoandtomato.common.enums.Status;
import com.shaded.fasterxml.jackson.core.JsonParseException;
import com.shaded.fasterxml.jackson.databind.JsonMappingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class BoardLogic implements Disposable{

    Services _services;
    GameCoordinator _coordinator;
    ArrayList<TerrainLogic> _terrains;
    GraveyardLogic _graveyard;
    BoardModel _boardModel;
    BattleRef _battleRef;
    MovementRef _movementRef;
    StatusRef _statusRef;

    BoardScreen _screen;
    GameDataController _gameDataController;
    TerrainLogic _lastActiveTerrainLogic;
    SplashLogic _splashLogic;
    RoomMsgHandler _roomMsgHandler;
    SafeThread _getGameDataSafeThread, _checkCountTimeExpiredThread;
    boolean _crackStarting, _crackHappened, _suddenDeathHappened, _gameEnded;

    public BoardModel getBoardModel() {
        return _boardModel;
    }

    public GraveyardLogic getGraveyardLogic() {
        return _graveyard;
    }

    public BoardLogic(Services services, GameCoordinator coordinator) {
        this._services = services;
        this._coordinator = coordinator;
        _battleRef = new BattleRef();
        _movementRef = new MovementRef();
        _statusRef = new StatusRef(services.getSoundsWrapper());
        _services.getScoresHandler().setBoardLogic(this);

        _boardModel = new BoardModel(-1);
        _terrains = new ArrayList<TerrainLogic>();
        _gameDataController = _services.getGameDataController();
        _graveyard = new GraveyardLogic(new GraveModel(),
                coordinator, services.getTexts(), services.getAssets(), services, services.getSoundsWrapper());
        _roomMsgHandler = new RoomMsgHandler(this, _coordinator);
        _splashLogic = new SplashLogic(coordinator, new Runnable() {
            @Override
            public void run() {
                setCountDownThread();
            }
        }, _services);

        _screen = new BoardScreen(coordinator, services, _splashLogic.getSplashActor(), _graveyard.getGraveyardActor());

        setListeners();
    }

    //call when new game
    public void init(){
        _splashLogic.start();
        if(_coordinator.meIsDecisionMaker()){
            _boardModel.setCurrentTurnIndex(_gameDataController.getFirstTurnIndex());
            gameDataReady(_boardModel, _gameDataController.getGameData(), _graveyard.getGraveModel());
            saveGameDataToDB();
        }
        else{
           getGameDataFromDB();
        }
    }

    public void continueGame(){
        _splashLogic.start();
        getGameDataFromDB();
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
            jsonObject.put("boardModel", _boardModel.toJson());
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
                        while (!_getGameDataSafeThread.isKilled()) {
                            _services.getDatabase().getGameData(new DatabaseListener<String>(String.class) {
                                @Override
                                public void onCallback(String result, Status st) {
                                    if (st == Status.SUCCESS && result != null && !result.equals("")) {
                                        json[0] = result;
                                        _getGameDataSafeThread.kill();
                                    }
                                }
                            });
                            int i = 0;
                            while (!_getGameDataSafeThread.isKilled() && i < 5) {
                                Threadings.sleep(500);
                                i++;
                            }
                        }
                        try {
                            ArrayList<ChessModel> chessModels = new ArrayList<ChessModel>();
                            JSONObject jsonObject = new JSONObject(json[0]);
                            ObjectMapper mapper1 = new ObjectMapper();
                            for (int i = 0; i < 32; i++) {
                                chessModels.add(mapper1.readValue(jsonObject.getString(String.valueOf(i)), ChessModel.class));
                            }
                            GraveModel graveModel = mapper1.readValue(jsonObject.getString("graveModel"), GraveModel.class);
                            BoardModel boardModel = mapper1.readValue(jsonObject.getString("boardModel"), BoardModel.class);

                            gameDataReady(boardModel, chessModels, graveModel);

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


    private void gameDataReady(final BoardModel boardModel, ArrayList<ChessModel> chessModels, GraveModel graveModel){

        _boardModel = boardModel;
        _crackStarting = _boardModel.isCrackStarting();
        _crackHappened = _boardModel.isCrackHappened();
        _graveyard.setGraveModel(graveModel);

        int i = 0;
        for(int row = 0; row < 8 ; row++){
            for(int col = 0; col < 4; col++){
                final ChessModel chessModel = chessModels.get(i);
                TerrainLogic terrainLogic = new TerrainLogic(new TerrainModel(col, row),
                        _services.getAssets(), _coordinator, chessModel,
                        _services.getSoundsWrapper(), _gameDataController, _battleRef);
                if(isCrackable(terrainLogic)){
                    terrainLogic.getTerrainModel().setBreaking(_crackStarting);
                    terrainLogic.getTerrainModel().setBroken(_crackHappened);
                }

                terrainLogic.setActionListener(new ActionListener() {
                    @Override
                    public void onSelected() {
                        if(!this.getTerrainLogic().isSelected()){
                            terrainSelected(this.getTerrainLogic().getTerrainModel().getCol(),
                                    this.getTerrainLogic().getTerrainModel().getRow());
                            _roomMsgHandler.sendTerrainSelected(this.getTerrainLogic().getTerrainModel().getCol(),
                                    this.getTerrainLogic().getTerrainModel().getRow(),
                                    getMyTimeLeft());
                        }
                    }

                    @Override
                    public void onOpened() {
                        disableTouchable();
                        String random = Strings.join(ArrayLists.randomNumericArray(2, 0, 4));
                        openChess(this.getTerrainLogic().getTerrainModel().getCol(),
                                this.getTerrainLogic().getTerrainModel().getRow(), random);
                        _roomMsgHandler.sendChessOpenFull(this.getTerrainLogic().getTerrainModel().getCol(),
                                this.getTerrainLogic().getTerrainModel().getRow(), random,
                                getMyTimeLeft());
                    }

                    @Override
                    public void onMoved(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon) {
                        int r = MathUtils.random(0, 100);
                        int random = 0;
                        if(r < 15) random = 1;

                        disableTouchable();
                        chessMoved(fromCol, fromRow, toCol, toRow, isFromWon, false, String.valueOf(random));
                        _roomMsgHandler.sendMoveChess(fromCol, fromRow, toCol, toRow, isFromWon, String.valueOf(random),
                               getMyTimeLeft());
                    }

                    @Override
                    public void changeTurnReady(ActionType actionType, ChessType winnerChessType, ChessType loserChessType, String random) {
                        beforeTurnSwitched(actionType, this.getTerrainLogic(), winnerChessType, loserChessType, random);
                    }

                    @Override
                    public void onChessKilled(ChessType chessType) {
                        chessKilled(chessType, true);
                    }
                });
                _terrains.add(terrainLogic);
                i++;
            }
        }

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                invalidate();
                _screen.populateTerrains(_terrains);
                setTurnTouchable();
                _roomMsgHandler.onGameReady();
                for(TerrainLogic terrainLogic : _terrains) terrainLogic.invalidate();
            }
        });
    }

    private void chessKilled(ChessType chessType, boolean alsoCheckGameEnded){
        _graveyard.addChessToGrave(chessType);

        if(alsoCheckGameEnded) checkGameEnded();
    }

    private void checkGameEnded(){
        //draw, both parties lose
        if(_graveyard.getGraveModel().getLeftChessCountByColor(ChessColor.RED) == 0
                && _graveyard.getGraveModel().getLeftChessCountByColor(ChessColor.YELLOW) == 0){
            endGame(false);
        }
        //yellow win
        else if(_graveyard.getGraveModel().getLeftChessCountByColor(ChessColor.RED) == 0 ){
            endGame(ChessColor.YELLOW);
        }
        //red win
        else if(_graveyard.getGraveModel().getLeftChessCountByColor(ChessColor.YELLOW) == 0){
            endGame(ChessColor.RED);
        }
    }

    public void openChess(int col, int row, String randomString){
        TerrainLogic openLogic =  Terrains.getTerrainLogicByPosition(_terrains, col, row);
        _lastActiveTerrainLogic = openLogic;
        openLogic.openTerrainChess(randomString);
    }

    //random variable is for decision making(eg, injured/king)
    public void chessMoved(int fromCol, int fromRow, int toCol, int toRow, boolean isFromWon, boolean showMovement, String random){
        TerrainLogic fromLogic = Terrains.getTerrainLogicByPosition(_terrains, fromCol, fromRow);
        TerrainLogic toLogic = Terrains.getTerrainLogicByPosition(_terrains, toCol, toRow);
        _lastActiveTerrainLogic = toLogic;
        toLogic.moveChessToThis(fromLogic, showMovement, isFromWon, random);
        hideAllTerrainPercentTile();
    }

    private void beforeTurnSwitched(ActionType actionType, TerrainLogic terrainLogic,
                                    ChessType winnerChessType, ChessType loserChessType, String random){
        disableTouchable();

        if(_boardModel.nextTurnIsSuddenDeath()){
            suddenDeath();
        }

        if(actionType == ActionType.OPEN && !_suddenDeathHappened){
            _statusRef.chessOpened(_terrains, terrainLogic, _gameDataController.getMyChessColor(), random);
        }
        else if(actionType == ActionType.MOVE && !_suddenDeathHappened){
            _statusRef.chessMoved(_terrains, terrainLogic, winnerChessType, loserChessType, random);
        }
        else if(actionType == ActionType.SKIP){

        }

        switchTurn();
    }

    private void switchTurn(){
        clearAllTerrainsHighlights();
        if(_lastActiveTerrainLogic != null){
            _lastActiveTerrainLogic.getChessLogic().setFocusing(true);
        }


        checkBoardCrack();

        _boardModel.switchTurnIndex();
        _statusRef.turnOver(_terrains);

        invalidate();
    }

    public void skipTurn(){
        beforeTurnSwitched(ActionType.SKIP, null, null, null, null);
    }

    private void invalidate(){
        _graveyard.onBoardModelChanged(_boardModel);
        setTurnTouchable();

        if(_boardModel.getCurrentTurnChessColor() == _gameDataController.getMyChessColor()){        //is my turn, if no available move, jz skip
            if(!checkHaveMove()){
                Threadings.delay(1000, new Runnable() {
                    @Override
                    public void run() {
                        _roomMsgHandler.skipTurn(getMyTimeLeft());
                        skipTurn();
                    }
                });
            }
        }

    }

    private void disableTouchable(){
        _screen.setCanTouchChessTable(false);
    }

    private void setTurnTouchable(){
        _screen.setCanTouchChessTable(isMyTurn());
    }

    private boolean isMyTurn(){
        return _boardModel.getCurrentTurnIndex() == _coordinator.getMyUniqueIndex();
    }

    public void terrainSelected(int col, int row){
        TerrainLogic clickedLogic = Terrains.getTerrainLogicByPosition(_terrains, col, row);
        if(!clickedLogic.isSelected()){
            clearAllTerrainsHighlights();

            clickedLogic.setSelected(true);
            if(clickedLogic.isOpened()){
                showPossibleMoves(clickedLogic);
            }
        }
    }


    private void hideAllTerrainPercentTile(){
        for(TerrainLogic terrainLogic : _terrains){
            terrainLogic.hidePercentTile();
        }
    }

    private void clearAllTerrainsHighlights(){
        for(TerrainLogic terrainLogic : _terrains){
            terrainLogic.getChessLogic().setFocusing(false);
            terrainLogic.setSelected(false);
            terrainLogic.hidePercentTile();
        }
    }

    public void showPossibleMoves(TerrainLogic logic){
        ArrayList<TerrainLogic> possibleMoveLogics = _movementRef.getPossibleValidMoves(_terrains, logic);
        for(final TerrainLogic terrainLogic : possibleMoveLogics){
            terrainLogic.showPercentTile(logic);
        }
        logic.setDragAndDrop(possibleMoveLogics);
    }

    public void endGame(final boolean won){
        if(!_gameEnded){
            _gameEnded = true;

            _screen.populateEndGameTable();
            _screen.showEndGameTable(won, _gameDataController.getMyChessColor());
            _services.getSoundsWrapper().stopTheme();
            _services.getSoundsWrapper().playSounds(won ? Sounds.Name.WIN : Sounds.Name.LOSE);

            _services.getScoresHandler().setIsMeWin(won);
            _services.getScoresHandler().updateMatchHistory();
            _services.getScoresHandler().process(new ScoresListener(){
                @Override
                public void onCallBack(HashMap<Team, ArrayList<ScoreDetails>> winnerResult, ArrayList<Team> losers) {
                    _coordinator.beforeEndGame(winnerResult, losers, false);
                    Threadings.delay(1000, new Runnable() {
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
    }

    private void endGame(ChessColor wonChessColor) {
        endGame(_gameDataController.getMyChessColor() == wonChessColor);
    }

    private void suddenDeath(){
        if(!_suddenDeathHappened){
            _suddenDeathHappened = true;
            _screen.thunderAnimation();
            _statusRef.suddenDeathStatus(_terrains);
            Threadings.delay(1300, new Runnable() {
                @Override
                public void run() {
                    _screen.setSuddenDeathBg();
                }
            });
            Threadings.delay(1500, new Runnable() {
                @Override
                public void run() {
                    for(TerrainLogic terrainLogic : _terrains){
                        terrainLogic.getChessLogic().invalidate(true);
                    }
                }
            });

        }
    }

    private void checkBoardCrack(){
        if(_crackHappened != _boardModel.isCrackHappened() && _boardModel.isCrackHappened()){
            crackBoardHappened();
            _crackHappened = true;
        }
        else if(_crackStarting != _boardModel.isCrackStarting() && _boardModel.isCrackStarting()){
            crackBoardStarting();
            _crackStarting = true;
        }
    }

    private void crackBoardStarting(){
        for(TerrainLogic terrainLogic : _terrains){
            if(isCrackable(terrainLogic)){
                terrainLogic.getTerrainModel().setBreaking(true);
                terrainLogic.invalidate();
            }
        }
        _services.getSoundsWrapper().playSounds(Sounds.Name.GLASS_CRACKING);
    }

    private void crackBoardHappened(){
        final ThreadsPool threadsPool = new ThreadsPool();
        boolean hasDropping = false;
        for(final TerrainLogic terrainLogic : _terrains){
            if(isCrackable(terrainLogic)){
                if(!terrainLogic.isEmpty() && !hasDropping) hasDropping = true;
                terrainLogic.getTerrainActor().animateBroken();
                terrainLogic.getTerrainModel().setBroken(true);

                threadsPool.addFragment(Threadings.delay(4000, new Runnable() {
                    @Override
                    public void run() {
                        if (!terrainLogic.isEmpty()) {
                            chessKilled(terrainLogic.getChessLogic().getChessModel().getChessType(), false);
                        }
                        terrainLogic.invalidate();
                    }
                }));
            }
        }
        _services.getSoundsWrapper().playSounds(Sounds.Name.GLASS_BROKEN);
        if(hasDropping){
            Threadings.delay(500, new Runnable() {
                @Override
                public void run() {
                    _services.getSoundsWrapper().playSounds(Sounds.Name.DROPPING);
                }
            });
        }

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (!threadsPool.allFinished()){
                    Threadings.sleep(300);
                }
                checkGameEnded();
            }
        });


    }

    private boolean isCrackable(TerrainLogic terrainLogic){
        return (terrainLogic.getTerrainModel().getCol() == 0 || terrainLogic.getTerrainModel().getCol() == 3 ||
                terrainLogic.getTerrainModel().getRow() == 0 || terrainLogic.getTerrainModel().getRow() == 7);
    }

    public void updateEnemyLeftTime(String leftTime){
        if(_gameDataController.getMyChessColor() == ChessColor.YELLOW){
            _graveyard.getGraveModel().setRedLeftTime(Integer.valueOf(leftTime));
        }
        else{
            _graveyard.getGraveModel().setYellowLeftTime(Integer.valueOf(leftTime));
        }
    }

    public BoardScreen getScreen() {
        return _screen;
    }

    private void setListeners(){
        _coordinator.setUserStateListener(new UserStateListener() {
            @Override
            public void userAbandoned(String s) {
                if(!s.equals(_coordinator.getMyUserId())){
                    endGame(true);
                }
            }

            @Override
            public void userConnected(String s) {
                _screen.setPaused(false, _boardModel.getCurrentTurnIndex() == _coordinator.getMyUniqueIndex());
                _graveyard.setPauseTimer(false);
            }

            @Override
            public void userDisconnected(String s) {
                clearAllTerrainsHighlights();
                _graveyard.setPauseTimer(true);
                _screen.setPaused(true, _boardModel.getCurrentTurnIndex() == _coordinator.getMyUniqueIndex());
                saveGameDataToDB();
            }
        });


        _screen.setBackKeyListener(new BackKeyListener() {
            @Override
            public void backPressed() {
                if(!_gameEnded){
                    _coordinator.abandon();
                }
            }
        });

    }

    private int getMyTimeLeft(){
        return _graveyard.getGraveModel().getLeftTimeInt(_gameDataController.getMyChessColor());
    }

    private boolean checkHaveMove(){
        if(_terrains.size() == 0) return true;  //gamedata not ready yet

        for(TerrainLogic terrainLogic : _terrains){
            if(!terrainLogic.isOpened()){
                return true;
            }
            else{
                if(terrainLogic.getChessLogic().getChessModel().getChessColor() == _gameDataController.getMyChessColor() &&
                        terrainLogic.getChessLogic().getChessModel().getStatus() != com.potatoandtomato.games.enums.Status.PARALYZED &&
                        terrainLogic.getChessLogic().getChessModel().getChessType() != ChessType.NONE){
                    return true;
                }
            }
        }

        return false;
    }

    private void setCountDownThread(){
        _graveyard.setCountDownThread();
        _checkCountTimeExpiredThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ChessColor myColor = _gameDataController.getMyChessColor();
                while (true){
                    if(_checkCountTimeExpiredThread.isKilled()) break;
                    else{
                        boolean timeout = false;

                        if(myColor == ChessColor.RED && _graveyard.getGraveModel().getRedLeftTime() == 0){
                            timeout = true;
                        }
                        if(myColor == ChessColor.YELLOW && _graveyard.getGraveModel().getYellowLeftTime() == 0){
                            timeout = true;
                        }

                        if(timeout){
                            _roomMsgHandler.sendSurrender();
                            endGame(false);
                        }

                    }
                }
            }
        });
    }

    @Override
    public void dispose() {
        if(_graveyard != null) _graveyard.dispose();
        if(_checkCountTimeExpiredThread != null) _checkCountTimeExpiredThread.kill();
    }
}
