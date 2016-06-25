package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.*;
import com.potatoandtomato.common.enums.CoinRequestType;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.*;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.*;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.review.ReviewLogic;
import com.potatoandtomato.games.screens.scores.ScoresLogic;
import com.potatoandtomato.games.screens.stage_counter.StageCounterLogic;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;
import com.potatoandtomato.games.screens.user_counters.UserCountersLogic;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 5/4/2016.
 */
public class MainLogic extends GameLogic {

    private Services _services;
    private MainScreen _screen;
    private TimeLogic _timeLogic;
    private HintsLogic _hintsLogic;
    private ReviewLogic _reviewLogic;
    private UserCountersLogic _userCounterLogic;
    private StageCounterLogic _stageCounterLogic;
    private StageStateLogic _stageStateLogic;
    private ScoresLogic _scoresLogic;
    private GameModel _gameModel;
    private ImagePair _currentImagePair;
    private ImageStorage _imageStorage;
    private StageImagesLogic _stageImagesLogic;
    private GameDataContract _gameDataContract;
    private SafeThread _safeThread;
    private boolean disposed, waitingContinue;

    public MainLogic(GameCoordinator gameCoordinator, Services _services,
                     TimeLogic _timeLogic, HintsLogic _hintsLogic, ReviewLogic _reviewLogic, UserCountersLogic _userCounterLogic,
                     StageCounterLogic _stageCounterLogic, ScoresLogic _scoresLogic,
                     ImageStorage _imageStorage, GameModel _gameModel, StageImagesLogic _stageImagesLogic,
                     StageStateLogic _stageStateLogic, GameDataContract gameDataContract) {
        super(gameCoordinator);
        this._services = _services;
        this._timeLogic = _timeLogic;
        this._hintsLogic = _hintsLogic;
        this._reviewLogic = _reviewLogic;
        this._userCounterLogic = _userCounterLogic;
        this._stageCounterLogic = _stageCounterLogic;
        this._scoresLogic = _scoresLogic;
        this._imageStorage = _imageStorage;
        this._gameModel = _gameModel;
        this._stageImagesLogic = _stageImagesLogic;
        this._stageStateLogic = _stageStateLogic;
        this._gameDataContract = gameDataContract;

        _screen = new MainScreen(_services, gameCoordinator);
        _screen.populate(_timeLogic.getTimeActor(), _hintsLogic.getHintsActor(),
                                _userCounterLogic.getUserCountersActor(), _stageCounterLogic.getStageCounterActor(),
                                _scoresLogic.getScoresActor(), _stageStateLogic.getStageStateActor());

        _stageImagesLogic.init(_screen.getImageOneTable(), _screen.getImageTwoTable(),
                                    _screen.getImageOneInnerTable(), _screen.getImageTwoInnerTable());
        setListeners();
    }

    public void init(){
        _screen.readyToStart();
        _imageStorage.startMonitor();
        _gameModel.setGameState(GameState.BeforeNewGame);
        sendGoToNextStageIfIsDecisionMaker(-1);
    }

    public void onContinue(){
        _screen.readyToStart();
        _imageStorage.startMonitor();
        _gameModel.setGameState(GameState.BeforeContinue);
        waitingContinue = true;
    }

    public void waitingContinue(){
        _currentImagePair = null;
        _gameModel.setImageDetails(null);
        _screen.showMessages(_services.getTexts().waitForNextStage());
    }

    //index = -1 mean peek next item in imagestorage, else peek item by index
    public void sendGoToNextStageIfIsDecisionMaker(int index){
        if(_gameModel.getGameState() == GameState.Playing){
            return;
        }

        if(getCoordinator().getDecisionsMaker().meIsDecisionMaker()) {
            _imageStorage.peek(index, new ImageStorageListener() {
                @Override
                public void onPeeked(ImagePair imagePair) {

                    StageType stageType = StageType.Normal;
                    BonusType bonusType = BonusType.NONE;
                    if(_gameModel.isNextStageBonus()){
                        stageType = StageType.Bonus;
                        bonusType = BonusType.random();
                    }
                    String extra = StageImagesLogic.generateBonusTypeExtra(bonusType, getCoordinator().getAllConnectedPlayers());

                    _services.getRoomMsgHandler().sendGotoNextStage(imagePair.getImageDetails().getId(),
                            _gameModel.getStageNumber() + 1, stageType, bonusType, extra, _gameModel.getScore().intValue());

                }
            });
        }
    }

    public void goToNewStage(final String id, final int stageNumber, final StageType stageType,
                             final BonusType bonusType, final String extra, int currentScores){
        waitingContinue = false;

        if(Global.REVIEW_MODE){
            newStageReviewMode(id);
            return;
        }

        if(_gameModel.getGameState() == GameState.Playing || _gameModel.getGameState() == GameState.PrePlaying){
            return;
        }

        _gameModel.setScore((double) currentScores, true);
        _currentImagePair = null;

        if(stageType == StageType.Bonus){
            _gameModel.setGameState(GameState.BeforeBouns);
            _stageStateLogic.setBonusMeta(bonusType, extra);
        }

        _gameModel.setGameState(GameState.PrePlaying);

        long delayTime = 0;
        //if papyrus is blocking or wait for continue user papyrus blocking
        if(_stageStateLogic.isPapyrusOpened()){
            delayTime = 5000;
        }

        if(stageType == StageType.Bonus){
            delayTime = 16000;
        }

        Threadings.delay(delayTime, new Runnable() {
            @Override
            public void run() {
                if(_safeThread != null) _safeThread.kill();
                if(disposed) return;

                if(_gameModel.getGameState() == GameState.PrePlaying){
                    if(getCoordinator().getDecisionsMaker().meIsDecisionMaker()){
                        _services.getRoomMsgHandler().sendStartPlaying(id, stageNumber, stageType, bonusType, extra);
                    }
                }
            }
        });
    }

    public void onStartGameReceived(final String id, final int stageNumber, final StageType stageType, final BonusType bonusType, final String extra){
        if(_safeThread != null) _safeThread.kill();
        if(disposed) return;

        popImagePairIfCurrentIsNull(id);
        if(_currentImagePair != null){
            _gameModel.setImageDetails(_currentImagePair.getImageDetails());
        }
        else{
            _gameModel.setImageDetails(null);
        }

        if (_currentImagePair == null) {
            _screen.showMessages(_services.getTexts().slowMessage());
        }
        else{
            _screen.setImages(_currentImagePair.getImageOne(), _currentImagePair.getImageTwo());
            _stageImagesLogic.beforeStartStage(stageType, bonusType, extra);
        }

        _gameModel.clearHandledAreas();
        _gameModel.setStageType(stageType);
        _gameModel.setStageNumber(stageNumber);
        _gameModel.convertStageNumberToRemainingMiliSecs();
        _gameModel.setGameState(GameState.Playing);
    }

    private void newStageReviewMode(final String id){
        _imageStorage.popWait(id, new ImageStorageListener() {
            @Override
            public void onPopped(ImagePair imagePair) {
                super.onPopped(imagePair);
                _currentImagePair = imagePair;
                _currentImagePair.getImageDetails().setGameImageSize((int) _screen.getImageSize().x, (int) _screen.getImageSize().y);
                _gameModel.setImageDetails(_currentImagePair.getImageDetails());
                _gameModel.clearHandledAreas();
                _gameModel.setStageType(StageType.Normal);
                _gameModel.addStageNumber();
                _gameModel.setGameState(GameState.PrePlaying);
                _gameModel.setGameState(GameState.Playing);
                _screen.setImages(_currentImagePair.getImageOne(), _currentImagePair.getImageTwo());
                _stageImagesLogic.beforeStartStage(StageType.Normal, BonusType.NONE, "");
            }
        });
    }

    private void popImagePairIfCurrentIsNull(String id){
        if(_currentImagePair == null){
            _imageStorage.pop(id, new ImageStorageListener() {
                @Override
                public void onPopped(ImagePair imagePair) {
                    super.onPopped(imagePair);
                    _currentImagePair = imagePair;
                    if (_currentImagePair != null) {
                        _currentImagePair.getImageDetails().setGameImageSize((int) _screen.getImageSize().x, (int) _screen.getImageSize().y);
                    }
                }
            });
        }
    }

    //only call by self
    public void imageTouched(float x, float y, int remainingMiliSecs, int hintLeft){
        ArrayList<SimpleRectangle> correctRects = getCorrectRectsByTouchPosition(x, y);
        SimpleRectangle correctRect = null;

        for(SimpleRectangle simpleRectangle : correctRects){
            if(!_gameModel.isAreaAlreadyHandled(simpleRectangle)){
                correctRect = simpleRectangle;
                break;
            }
        }

        if(correctRect == null){
            if(correctRects.size() == 0){
                _timeLogic.reduceTime();
                _screen.cross(x, y, getCoordinator().getMyUserId());
            }
        }
        else{
            _gameModel.addHandledArea(correctRect, remainingMiliSecs);
        }

        _services.getRoomMsgHandler().sendTouched(x, y, _gameModel.getRemainingMiliSecs(), hintLeft, correctRect);
    }

    public void touchReceived(String userId, float x, float y, int remainingMiliSecs, int hintLeft, SimpleRectangle correctRect){
        if(correctRect == null){
            if(!userId.equals(getCoordinator().getMyUserId())){
                _timeLogic.reduceTime();
                _screen.cross(x, y, userId);
            }
        }
        else if(!_gameModel.isAreaAlreadyConfirmClicked(correctRect)) {
            if(!userId.equals(getCoordinator().getMyUserId())) _gameModel.setHintsLeft(hintLeft);
            boolean alreadyHandled = _gameModel.addHandledArea(correctRect, remainingMiliSecs);
            _gameModel.setConfirmAreaClickedBy(correctRect, userId);
            _screen.circle(correctRect, userId, alreadyHandled ? -1 : _gameModel.getHandledAreas().size());
            _gameModel.addUserClickedCount(userId);
        }
    }

    public ArrayList<SimpleRectangle> getCorrectRectsByTouchPosition(float x, float y){
        Vector2 imageSize = _screen.getImageSize();

        float finalY = imageSize.y - y;  //libgdx origin is at bottomleft
        ArrayList<SimpleRectangle> finalCorrectRects = new ArrayList();

        //expand the touch to a imaginary small square, for better touch experience
        int i = 0, expandSize = 5;
        while (i <= 3){
            float checkingX = 0, checkingY = 0;
            if(i == 0){         //top left corner
                checkingX = Math.max(0, x - expandSize);
                checkingY = finalY + expandSize;
            }
            else if(i == 1){         //top right corner
                checkingX = x + expandSize;
                checkingY = finalY + expandSize;
            }
            else if(i == 2){         //bottom left corner
                checkingX = Math.max(0, x - expandSize);
                checkingY = Math.max(0, finalY - expandSize);
            }
            else if(i == 3){         //bottom right corner
                checkingX = x + expandSize;
                checkingY = Math.max(0, finalY - expandSize);
            }

            ArrayList<SimpleRectangle> correctRects = _gameModel.getImageDetails().getTouchedCorrectRects(checkingX, checkingY);
            for(SimpleRectangle simpleRectangle : correctRects){
                boolean found = false;
                for(SimpleRectangle existed : finalCorrectRects){
                    if(existed.equals(simpleRectangle)){
                        found = true;
                        break;
                    }
                }

                if(!found){
                    finalCorrectRects.add(simpleRectangle);
                }
            }
            i++;
        }

        return finalCorrectRects;
    }

    public void checkGameEnded(int remainingSecs){
        if(_gameModel.getHandledAreas().size() == 5){
            _gameModel.setGameState(GameState.Pause);
            if(getCoordinator().getDecisionsMaker().meIsDecisionMaker()){
                _services.getRoomMsgHandler().sendWon(new WonStageModel(_gameModel.getStageNumber(),
                                        _gameModel.getScore().intValue(), remainingSecs, _gameModel.getHintsLeft(), "0", StageType.Normal));
            }
        }
    }


    private void gameOver(){
        _gameModel.setGameState(GameState.Lose);
        if(!_gameModel.isContinueChanceUsed()){
            coinRequestPhase();
        }
        showEndGameTable();
    }

    private void coinRequestPhase(){
        Threadings.delay(3000, new Runnable() {
            @Override
            public void run() {
                getCoordinator().coinsInputRequest(CoinRequestType.MyTeam, 1, new CoinListener() {
                    @Override
                    public void onEnoughCoins() {

                    }

                    @Override
                    public void onDeductCoinsDone(String s, Status status) {
                        if(status == Status.FAILED){
                            coinRequestPhase();
                        }
                        else{
                            _screen.hideEndGameTable();
                            _gameModel.setContinueChanceUsed(true);
                            sendGoToNextStageIfIsDecisionMaker(-1);
                        }
                    }
                });
            }
        });
    }

    private void circleAllAnswers(){
        if(_gameModel.getImageDetails() != null){
            for(SimpleRectangle simpleRectangle : _gameModel.getImageDetails().getCorrectSimpleRects()){
                if(!_gameModel.isAreaAlreadyHandled(simpleRectangle)){
                    _screen.circle(simpleRectangle, null, -1);
                }
            }
        }
    }

    private void showEndGameTable(){
        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                _screen.showEndGameTable();
                _screen.getEndGameTable().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        getCoordinator().finalizeGame(_scoresLogic.getFinalScoreDetails(), null, false);
                        getCoordinator().endGame();
                    }
                });
            }
        });
    }

    private void checkCanSwitchToReviewMode(){
        if(!Global.REVIEW_MODE){
            if(Gdx.app.getType() == Application.ApplicationType.Desktop){
                switchToReviewMode();
            }
            else{
                _services.getDatabase().checkIsAdmin(getCoordinator().getMyUserId(), new DatabaseListener<Boolean>() {
                    @Override
                    public void onCallback(Boolean obj, Status st) {
                        if(st == Status.SUCCESS && obj){
                            switchToReviewMode();
                        }
                    }
                });
            }

        }
    }

    private void switchToReviewMode(){
        Global.REVIEW_MODE = true;
        _gameModel.setGameState(GameState.BlockingReview);
        _screen.switchToReviewMode(_reviewLogic.getReviewActor());
        _imageStorage.setRandomize(false);
        _imageStorage.disposeAllImages();
        _imageStorage.initiateDownloadsIfNoImagesAndIsCoordinator();
        sendGoToNextStageIfIsDecisionMaker(0);
    }

    private void setListeners(){

        _gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onHandledAreasChanged() {
                super.onHandledAreasChanged();
                for(SimpleRectangle simpleRectangle : _gameModel.getHandledAreas()){
                    _screen.circle(simpleRectangle, simpleRectangle.getUserId(), -1);
                }
            }

            @Override
            public void onTimeFinished() {
                //allow a 1 sec tolerance time for game over
                if(getCoordinator().getDecisionsMaker().meIsDecisionMaker()){
                    Threadings.delay(1000, new Runnable() {
                        @Override
                        public void run() {
                            if(_gameModel.getGameState() == GameState.Playing){
                                _services.getRoomMsgHandler().sendLose(_gameModel);
                            }
                        }
                    });
                }
            }

            @Override
            public void onGameStateChanged(GameState oldState, GameState newState) {
                if(newState == GameState.BeforeContinue){
                    waitingContinue();
                }
                else if(newState == GameState.WaitingForNextStage){
                    if(!Global.REVIEW_MODE){
                        Threadings.delay(1000, new Runnable() {
                            @Override
                            public void run() {
                                sendGoToNextStageIfIsDecisionMaker(-1);
                            }
                        });
                    }
                }
                else if(newState == GameState.Lose){
                    Threadings.delay(2000, new Runnable() {
                        @Override
                        public void run() {
                            circleAllAnswers();
                            _services.getSoundsWrapper().playSounds(_services.getAssets().getSounds().getClickSound(5));
                        }
                    });

                    if(_gameModel.getStageType() == StageType.Normal){
                        Threadings.delay(5000, new Runnable() {
                            @Override
                            public void run() {
                                gameOver();
                            }
                        });
                    }
                    else if(_gameModel.getStageType() == StageType.Bonus){
                        Threadings.delay(7000, new Runnable() {
                            @Override
                            public void run() {
                                sendGoToNextStageIfIsDecisionMaker(-1);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCorrectClicked(SimpleRectangle correctRect, int remainingMiliSecsWhenClicked) {
                _screen.circle(correctRect, null, _gameModel.getHandledAreas().size());
                _gameModel.addFreezeMiliSecs();
                checkGameEnded(remainingMiliSecsWhenClicked);
            }

        });

        _screen.setBackKeyListener(new BackKeyListener() {
            @Override
            public void backPressed() {
                getCoordinator().abandon();
            }
        });

        _screen.getBlockTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        _screen.getBottomBarTable().addListener(new ActorGestureListener(){
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                checkCanSwitchToReviewMode();
                return super.longPress(actor, x, y);
            }
        });

        _hintsLogic.setHintsLogicListener(new HintsLogicListener() {
            @Override
            public void onHintClicked(Rectangle notYetHandledArea, int newHintLeft) {
                float x, y;
                x = notYetHandledArea.getX();
                y = notYetHandledArea.getY();
                y = _gameModel.getImageDetails().getGameImageHeight() - y;
                imageTouched(x, y, _gameModel.getRemainingMiliSecs(), newHintLeft);
            }
        });


        _stageImagesLogic.setStageImagesHandlerListener(new StageImagesHandlerListener() {
            @Override
            public void onTouch(float x, float y) {
                if(_gameModel.isPlaying()){
                    imageTouched(x, y, _gameModel.getRemainingMiliSecs(), _gameModel.getHintsLeft());
                }
            }

            @Override
            public void requestCircleAll() {
                circleAllAnswers();
            }

            @Override
            public void cancelCircleAll() {
                _screen.unCircleAll();
            }
        });

        //android only, when apps come back from background
        getCoordinator().addOnResumeRunnable(new Runnable() {
            @Override
            public void run() {

                _imageStorage.onResume();

                if(_currentImagePair == null) return;

                _safeThread = new SafeThread();

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        final int[] waitingImageCount = {0};

                        getCoordinator().getRemoteImage(_currentImagePair.getImageDetails().getImageOneUrl(), new WebImageListener() {
                            @Override
                            public void onLoaded(Texture texture) {
                                waitingImageCount[0]++;
                                if(_currentImagePair != null) _currentImagePair.setImageOne(texture);
                            }
                        });
                        getCoordinator().getRemoteImage(_currentImagePair.getImageDetails().getImageTwoUrl(), new WebImageListener() {
                            @Override
                            public void onLoaded(Texture texture) {
                                waitingImageCount[0]++;
                                if(_currentImagePair != null) _currentImagePair.setImageTwo(texture);
                            }
                        });

                        while (waitingImageCount[0] < 2 && !_safeThread.isKilled()){
                            Threadings.sleep(200);
                        }

                        if(!_safeThread.isKilled() && _currentImagePair != null){
                            _screen.onResumeRefreshImages(_currentImagePair.getImageOne(), _currentImagePair.getImageTwo());
                        }
                    }
                });
            }
        });

        getCoordinator().getDecisionsMaker().addDecisionMakerChangedListener(new DecisionMakerChangedListener() {
            @Override
            public void onChanged(String s) {
                sendGoToNextStageIfIsDecisionMaker(-1);
                _imageStorage.initiateDownloadsIfNoImagesAndIsCoordinator();
            }
        });

        getCoordinator().setUserStateListener(new UserStateListener() {
            @Override
            public void userAbandoned(String s) {
                if(waitingContinue){
                    if(getCoordinator().getAllConnectedPlayers().size() == 1 && getCoordinator().getGameDataHelper().hasData()){  //only me
                        getCoordinator().raiseGameFailedError(_services.getTexts().gameContinueFailed());
                    }
                }
            }

            @Override
            public void userConnected(String s) {
                _imageStorage.resendRedownloadCurrentImageStorage(s);
            }

            @Override
            public void userDisconnected(String s) {
                if(waitingContinue){
                    if(getCoordinator().getAllConnectedPlayers().size() == 1 && getCoordinator().getGameDataHelper().hasData()){  //only me
                        getCoordinator().raiseGameFailedError(_services.getTexts().gameContinueFailed());
                    }
                }
            }
        });

        _services.getRoomMsgHandler().setRoomMsgListener(new RoomMsgListener() {

            @Override
            public void onTouched(final TouchedPoint touchedPoint, final String userId) {
                touchReceived(userId, touchedPoint.x, touchedPoint.y, touchedPoint.getRemainingMiliSecs(),
                        touchedPoint.getHintLeft(), touchedPoint.getCorrectRect());
            }

            @Override
            public void onLose(GameModel loseGameModel) {
                Logs.show("YOU LOSE!! Bye!");
                _gameModel.setScore(loseGameModel.getScore(), true);
                _gameModel.setUserRecords(loseGameModel.getUserRecords());
                _gameModel.setRemainingMiliSecs(0, true);
                _gameModel.setGameState(GameState.Lose);
            }

            @Override
            public void onWon(WonStageModel wonStageModel) {
                _gameModel.setRemainingMiliSecs(wonStageModel.getRemainingSecs(), false);
                _gameModel.setHintsLeft(wonStageModel.getHintsLeft());
                _gameModel.setGameState(_currentImagePair == null ? GameState.WonWithoutContributions : GameState.Won);

                Logs.show("You win, time is: " + wonStageModel.getRemainingSecs());
            }

            @Override
            public void onDownloadImageRequest(ArrayList<String> ids) {
                _imageStorage.receivedDownloadRequest(ids);
            }

            @Override
            public void onGoToNextStage(String id, int stageNumber, StageType stageType, BonusType bonusType, String extra, int currentScore) {
                goToNewStage(id, stageNumber, stageType, bonusType, extra, currentScore);
            }

            @Override
            public void onStartPlaying(String id, int stageNumber, StageType stageType, BonusType bonusType, String extra) {
                onStartGameReceived(id, stageNumber, stageType, bonusType, extra);
            }

        });

        _reviewLogic.setReviewLogicListener(new ReviewLogicListener() {
            @Override
            public void onDeleted() {

            }

            @Override
            public void onGoToIndex(int index) {
                if(index != _gameModel.getImageDetails().getIndex() + 1){       //skip to next
                    _imageStorage.setCurrentIndex(index);
                    _imageStorage.disposeAllImages();
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            _imageStorage.initiateDownloadsIfNoImagesAndIsCoordinator();
                        }
                    });

                }
                sendGoToNextStageIfIsDecisionMaker(index);
            }
        });


    }

    @Override
    public void dispose() {
        if(_safeThread != null) _safeThread.kill();
        disposed = true;
    }

    public MainScreen getMainScreen() {
        return _screen;
    }

    public void setMainScreen(MainScreen _screen) {
        this._screen = _screen;
    }

    public TimeLogic getTimeLogic() {
        return _timeLogic;
    }

    public GameModel getGameModel() {
        return _gameModel;
    }

    public Services getServices() {
        return _services;
    }

}
