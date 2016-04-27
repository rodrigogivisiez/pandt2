package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.BackKeyListener;
import com.potatoandtomato.common.absints.GameLogic;
import com.potatoandtomato.common.absints.UserStateListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.*;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.*;
import com.potatoandtomato.games.screens.announcements.*;
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
    private ScoresLogic _scoresLogic;
    private GameModel _gameModel;
    private ImageStorage _imageStorage;
    private StageImagesLogic _stageImagesLogic;
    private String _currentDecisionMaker;
    private boolean _waitingContinue;
    private boolean _attachGameModelOnFinish;

    public MainLogic(GameCoordinator gameCoordinator, Services _services,
                     TimeLogic _timeLogic, HintsLogic _hintsLogic, ReviewLogic _reviewLogic, UserCountersLogic _userCounterLogic,
                     StageCounterLogic _stageCounterLogic, ScoresLogic _scoresLogic,
                     ImageStorage _imageStorage, GameModel _gameModel, StageImagesLogic _stageImagesLogic) {
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

        _screen = new MainScreen(_services, gameCoordinator);
        _screen.populate(_timeLogic.getTimeActor(), _hintsLogic.getHintsActor(),
                                _userCounterLogic.getUserCountersActor(), _stageCounterLogic.getStageCounterActor(),
                                _scoresLogic.getScoresActor());

        _stageImagesLogic.init(_screen.getImageOneTable(), _screen.getImageTwoTable(),
                                    _screen.getImageOneInnerTable(), _screen.getImageTwoInnerTable());
        setListeners();


    }

    public void init(){
        _currentDecisionMaker = getCoordinator().getDecisionMaker();
        _imageStorage.startMonitor();
        _gameModel.setGameState(GameState.Close);

        _screen.showAnnouncement(new GameStartingAnnouncement(_services));

        Threadings.delay(Global.ClOSE_DOOR_BUFFER_TIME, new Runnable() {
            @Override
            public void run() {
                sendGoToNextStageIfIsDecisionMaker(-1);
            }
        });
    }

    public void onContinue(){
        _waitingContinue = true;
        _imageStorage.startMonitor();
        _gameModel.setGameState(GameState.Close);

        _screen.showAnnouncement(new WaitContinueAnnouncement(_services));
    }

    //index = -1 mean peek next item in imagestorage, else peek item by index
    public void sendGoToNextStageIfIsDecisionMaker(int index){
        if(meIsThisStageDecisionMaker()) {
            _imageStorage.peek(index, new ImageStorageListener() {
                @Override
                public void onPeeked(ImagePair imagePair) {

                    StageType stageType = StageType.Normal;
                    BonusType bonusType = BonusType.NONE;
                    if(_gameModel.isNextStageBonus()){
                        stageType = StageType.Bonus;
                        bonusType = BonusType.random();

                        bonusType = BonusType.LIGHTING;

                    }
                    String extra = StageImagesLogic.generateBonusTypeExtra(bonusType, getCoordinator().getPlayersByConnectionState(true));

                    _services.getRoomMsgHandler().sendGotoNextStage(imagePair.getImageDetails().getId(),
                            stageType, bonusType, extra,
                            _attachGameModelOnFinish ? _gameModel : null);

                    _attachGameModelOnFinish = false;
                }
            });
        }
    }

    public void goToNewStage(String id, final StageType stageType, final BonusType bonusType, final String extra){
        _currentDecisionMaker = getCoordinator().getDecisionMaker();
        _screen.clearAnnouncement();
        _gameModel.setImageDetails(null);
        _gameModel.clearHandledAreas();
        _gameModel.setStageType(stageType);
        _gameModel.addStageNumber();



        if(stageType == StageType.Normal){
            invalidateReviewLogic();
            Threadings.delay(200, new Runnable() {
                @Override
                public void run() {
                    _gameModel.setGameState(GameState.Playing);
                }
            });
        }
        else if(stageType == StageType.Bonus){
            _gameModel.setGameState(GameState.Close);
            _screen.showAnnouncement(new BonusAnnouncement(_services, bonusType));
            Threadings.delay(Global.ClOSE_DOOR_BUFFER_TIME, new Runnable() {
                @Override
                public void run() {
                    _screen.clearAnnouncement();
                    _gameModel.setGameState(GameState.Playing);
                }
            });
        }

        _imageStorage.pop(id, new ImageStorageListener() {
            @Override
            public void onPopped(final ImagePair imagePair) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (imagePair == null) {
                            _screen.showMessages(_services.getTexts().slowMessage());
                            return;
                        }

                        imagePair.getImageDetails().setGameImageSize((int) _screen.getImageSize().x, (int) _screen.getImageSize().y);
                        _gameModel.setImageDetails(imagePair.getImageDetails());

                        final Runnable setImageRunnable = new Runnable() {
                            @Override
                            public void run() {
                                _screen.setImages(imagePair.getImageOne(), imagePair.getImageTwo());
                                _stageImagesLogic.beforeStartStage(stageType, bonusType, extra);
                            }
                        };

                        if(stageType == StageType.Normal){
                            setImageRunnable.run();
                        }
                        else if(stageType == StageType.Bonus){
                            Threadings.delay(Global.ClOSE_DOOR_BUFFER_TIME, new Runnable() {
                                @Override
                                public void run() {
                                    setImageRunnable.run();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //only call by self
    public void imageTouched(float x, float y, int remainingMiliSecs, int hintLeft){
        SimpleRectangle correctRect = getCorrectRectByTouchPosition(x, y);

        if(correctRect == null){
            _timeLogic.reduceTime();
            _screen.cross(x, y, getCoordinator().getMyUserId());
        }
        else if(!_gameModel.isAreaAlreadyHandled(correctRect)) {
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
            boolean alreadyHandled = _gameModel.addHandledArea(correctRect, remainingMiliSecs);
            _gameModel.setConfirmAreaClickedBy(correctRect, userId);
            _screen.circle(correctRect, userId, alreadyHandled ? -1 : _gameModel.getHandledAreas().size());
            _gameModel.addUserClickedCount(userId);
            _gameModel.setHintsLeft(hintLeft);
        }
    }

    public SimpleRectangle getCorrectRectByTouchPosition(float x, float y){
        Vector2 imageSize = _screen.getImageSize();

        float finalY = imageSize.y - y;  //libgdx origin is at bottomleft
        SimpleRectangle correctRect = null;

        //expand the touch to a imaginary small square, for better touch experience
        int i = 0, expandSize = 5;
        while (correctRect == null && i <= 3){
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

            correctRect = _gameModel.getImageDetails().getTouchedCorrectRect(checkingX, checkingY);
            i++;
        }

        return correctRect;
    }

    public void checkGameEnded(int remainingSecs){
        if(_gameModel.getHandledAreas().size() == 5){
            _gameModel.setGameState(GameState.Pause);
            if(meIsThisStageDecisionMaker()){
                _services.getRoomMsgHandler().sendWon(new WonStageModel(_gameModel.getStageNumber(),
                                        _gameModel.getScore(), remainingSecs, "0", StageType.Normal));
            }
        }
    }

    private void gameLost(){
        if(_gameModel.getStageType() == StageType.Normal){
            gameOver();
        }
        else if(_gameModel.getStageType() == StageType.Bonus){
            sendGoToNextStageIfIsDecisionMaker(-1);
        }
    }

    private void gameOver(){
        _screen.refreshGameState(GameState.Close);      //dont change lose state for continue player
        _screen.showAnnouncement(new GameOverAnnouncement(_services));

        getCoordinator().beforeEndGame(_scoresLogic.getFinalScoreDetails(), null);
        getCoordinator().endGame();
    }

    private void gameLoseAllGameModelHolderDc(){
        _screen.showAnnouncement(new ContinueFailedAnnouncement(_services));

        getCoordinator().beforeEndGame(null, null);
        getCoordinator().endGame();
    }

    private void reconstructGameModelIfWaitingContinue(GameModel gameModel){
        if(_waitingContinue){
            _waitingContinue = false;
            this._gameModel.copyGameModelDataToThis(gameModel);
            _scoresLogic.refreshAllScores();
        }
    }

    public boolean meIsThisStageDecisionMaker(){
        return (_currentDecisionMaker != null && _currentDecisionMaker.equals(getCoordinator().getMyUserId()));
    }

    public void invalidateReviewLogic(){
        _reviewLogic.invalidate();
    }

    private void checkCanSwitchToReviewMode(){
        if(!Global.REVIEW_MODE){
            switch(Gdx.app.getType()) {
                case Android:
                    _services.getDatabase().checkIsAdmin(getCoordinator().getMyUserId(), new DatabaseListener<Boolean>() {
                        @Override
                        public void onCallback(Boolean obj, Status st) {
                            if(st == Status.SUCCESS && obj){
                                switchToReviewMode();
                            }
                        }
                    });
                    break;
                case Desktop:
                    switchToReviewMode();
                    break;
            }
        }
    }

    private void switchToReviewMode(){
        Global.REVIEW_MODE = true;
        _gameModel.setGameState(GameState.BlockingReview);
        _screen.switchToReviewMode(_reviewLogic.getReviewActor());
        _imageStorage.setRandomize(false);
        _imageStorage.disposeAllImages();
        _imageStorage.initiateDownloadsIfNeeded();
        sendGoToNextStageIfIsDecisionMaker(0);
    }

    private void setListeners(){

        _gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onTimeFinished() {
                //allow a 1 sec tolerance time for game over
                if(meIsThisStageDecisionMaker()){
                    Threadings.delay(1000, new Runnable() {
                        @Override
                        public void run() {
                            if(_gameModel.getGameState() == GameState.Playing){
                                _services.getRoomMsgHandler().sendLose();
                                _attachGameModelOnFinish = false;
                            }
                        }
                    });
                }
            }

            @Override
            public void onGameStateChanged(GameState newState) {
                if(newState == GameState.Ended){
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
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            gameLost();
                        }
                    });
                }
                _screen.refreshGameState(newState);
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
                if(_gameModel.getGameState() != GameState.Lose){
                    getCoordinator().abandon();
                }
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
                x = notYetHandledArea.getX() + 1;
                y = notYetHandledArea.getY() + 1;
                y = _gameModel.getImageDetails().getGameImageHeight() - y;
                imageTouched(x, y, _gameModel.getRemainingMiliSecs(), newHintLeft);
            }
        });

        _services.getRoomMsgHandler().setRoomMsgListener(new RoomMsgListener() {
            @Override
            public void onTouched(final TouchedPoint touchedPoint, final String userId) {
                if(!_waitingContinue){
                    touchReceived(userId, touchedPoint.x, touchedPoint.y, touchedPoint.getRemainingMiliSecs(),
                            touchedPoint.getHintLeft(), touchedPoint.getCorrectRect());
                }
            }

            @Override
            public void onLose() {
                if(!_waitingContinue) {
                    Logs.show("YOU LOSE!! Bye!");
                    _gameModel.setGameState(GameState.Lose);
                }
            }

            @Override
            public void onWon(WonStageModel wonStageModel) {
                if(!_waitingContinue) {
                    _gameModel.setRemainingMiliSecs(wonStageModel.getRemainingSecs(), false);
                    _gameModel.setGameState(GameState.Won);

                    Logs.show("You win, time is: " + wonStageModel.getRemainingSecs());
                }
            }

            @Override
            public void onDownloadImageRequest(ArrayList<String> ids) {
                _imageStorage.receivedDownloadRequest(ids);
            }

            @Override
            public void onGoToNextStage(String id, StageType stageType, BonusType bonusType, String extra, GameModel gameModel) {
                reconstructGameModelIfWaitingContinue(gameModel);
                goToNewStage(id, stageType, bonusType, extra);
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
                for(SimpleRectangle simpleRectangle : _gameModel.getImageDetails().getCorrectSimpleRects()){
                    _screen.circle(simpleRectangle, getCoordinator().getMyUserId(), -1);
                }
            }

            @Override
            public void cancelCircleAll() {
                _screen.unCircleAll();
            }
        });

        getCoordinator().setUserStateListener(new UserStateListener() {
            @Override
            public void userAbandoned(String s) {
                if(s.equals(_currentDecisionMaker)){
                    _currentDecisionMaker = getCoordinator().getDecisionMaker();
                }
            }

            @Override
            public void userConnected(String s) {
                _imageStorage.resendRedownloadCurrentImageStorage();
                _attachGameModelOnFinish = true;
            }

            @Override
            public void userDisconnected(String s) {
                if(getCoordinator().getPlayersByConnectionState(true).size() == 1){
                    //only me is connected, and nobody will send me game model for continue, deem as lose
                    if(_waitingContinue){
                        gameLoseAllGameModelHolderDc();
                        return;
                    }
                }

                if(s.equals(_currentDecisionMaker)){
                    _currentDecisionMaker = getCoordinator().getDecisionMaker();
                }
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
                    _imageStorage.initiateDownloadsIfNeeded();
                }
                sendGoToNextStageIfIsDecisionMaker(index);
            }
        });


    }







    @Override
    public void dispose() {
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

    public String getCurrentDecisionMaker() {
        return _currentDecisionMaker;
    }
}
