package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
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
import com.potatoandtomato.games.screens.announcements.BonusAnnouncement;
import com.potatoandtomato.games.screens.announcements.GameOverAnnouncement;
import com.potatoandtomato.games.screens.announcements.GameStartingAnnouncement;
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
    private StageImagesHandler _stageImagesHandler;

    public MainLogic(GameCoordinator gameCoordinator, Services _services,
                     TimeLogic _timeLogic, HintsLogic _hintsLogic, ReviewLogic _reviewLogic, UserCountersLogic _userCounterLogic,
                     StageCounterLogic _stageCounterLogic, ScoresLogic _scoresLogic,
                     ImageStorage _imageStorage, GameModel _gameModel, StageImagesHandler _stageImagesHandler) {
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
        this._stageImagesHandler = _stageImagesHandler;

        _screen = new MainScreen(_services, gameCoordinator);
        _screen.populate(_timeLogic.getTimeActor(), _hintsLogic.getHintsActor(),
                                _userCounterLogic.getUserCountersActor(), _stageCounterLogic.getStageCounterActor(),
                                _scoresLogic.getScoresActor());

        _stageImagesHandler.init(_screen.getImageOneTable(), _screen.getImageTwoTable(),
                                    _screen.getImageOneInnerTable(), _screen.getImageTwoInnerTable());
        setListeners();

    }

    public void init(){
        _imageStorage.startMonitor();
        _gameModel.setGameState(GameState.Close);

        _screen.showAnnouncement(new GameStartingAnnouncement(_services));

        Threadings.delay(Global.START_PREPARE_TIME, new Runnable() {
            @Override
            public void run() {
                sendGoToNextStageIfIsDecisionMaker(-1);
            }
        });
    }

    public void onContinue(){

    }

    //index = -1 mean peek next item in imagestorage, else peek item by index
    public void sendGoToNextStageIfIsDecisionMaker(int index){
        if(getCoordinator().meIsDecisionMaker()) {
            _imageStorage.peek(index, new ImageStorageListener() {
                @Override
                public void onPeeked(ImagePair imagePair) {
//                    _services.getRoomMsgHandler().sendGotoNextStage(imagePair.getImageDetails().getId(),
//                                                StageType.Normal, BonusType.NONE, "");

                    _services.getRoomMsgHandler().sendGotoNextStage(imagePair.getImageDetails().getId(),
                            StageType.Bonus, BonusType.LIGHTING, "");

                }
            });
        }
    }

    public void goToNewStage(String id, final StageType stageType, final BonusType bonusType, final String extra){
        _imageStorage.pop(id, new ImageStorageListener() {
            @Override
            public void onPopped(final ImagePair imagePair) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _screen.clearAnnouncement();
                        imagePair.getImageDetails().setGameImageSize((int) _screen.getImageSize().x, (int) _screen.getImageSize().y);
                        _gameModel.setImageDetails(imagePair.getImageDetails());
                        _gameModel.clearHandledAreas();
                        _gameModel.setStageType(stageType);

                        if(stageType == StageType.Normal){
                            _gameModel.addStageNumber();
                            _gameModel.setGameState(GameState.Playing);
                            invalidateReviewLogic();
                        }
                        else if(stageType == StageType.Bonus){
                            _gameModel.setGameState(GameState.Close);
                            _screen.showAnnouncement(new BonusAnnouncement(_services, bonusType));
                            Threadings.delay(1000, new Runnable() {
                                @Override
                                public void run() {
                                    _screen.clearAnnouncement();
                                    _gameModel.addStageNumber();
                                    _gameModel.setGameState(GameState.Playing);
                                }
                            });

                        }

                        changeScreenImages(imagePair.getImageOne(), imagePair.getImageTwo());
                        _stageImagesHandler.beforeStartStage(stageType, bonusType, extra);
                    }
                });
            }
        });
    }

    public void changeScreenImages(Texture texture1, Texture texture2){
        _screen.resetImages(texture1, texture2);
    }

    public void imageTouched(String userId, float x, float y, int remainingMiliSecs, boolean usedHint){
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



        if(correctRect == null){
            _timeLogic.reduceTime();
            _screen.cross(x, y, userId);
        }
        else if(!_gameModel.isAreaAlreadyHandled(correctRect)) {
            if(usedHint && !Global.REVIEW_MODE){
                _gameModel.minusHintLeft();
            }
            _gameModel.addHandledArea(correctRect, userId, remainingMiliSecs);
        }
    }

    public void checkGameEnded(int remainingSecs){
        if(_gameModel.getHandledAreas().size() == 5){
            _gameModel.setGameState(GameState.Pause);
            if(getCoordinator().meIsDecisionMaker()){
                _services.getRoomMsgHandler().sendWon(new WonStageModel(_gameModel.getStageNumber(),
                                        _gameModel.getScore(), remainingSecs, "0", StageType.Normal));
            }
        }
    }

    private void gameLost(){
        if(_gameModel.getStageType() == StageType.Normal){
            _gameModel.setGameState(GameState.Close);
            _screen.showAnnouncement(new GameOverAnnouncement(_services));
        }
        else if(_gameModel.getStageType() == StageType.Bonus){
            sendGoToNextStageIfIsDecisionMaker(-1);
        }
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
                if(getCoordinator().meIsDecisionMaker()){
                    Threadings.delay(1000, new Runnable() {
                        @Override
                        public void run() {
                            if(_gameModel.getGameState() == GameState.Playing){
                                _services.getRoomMsgHandler().sendLose();
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
            public void onCorrectClicked(SimpleRectangle correctRect, String userId, int remainingMiliSecsWhenClicked) {
                Vector2 imageSize = _screen.getImageSize();
                Rectangle circleRect = new Rectangle();
                circleRect.setSize(correctRect.getWidth(), correctRect.getHeight());
                circleRect.setPosition(correctRect.getX(), imageSize.y - correctRect.getY()); //libgdx origin is at bottomleft
                _screen.circle(circleRect, userId);

                _gameModel.addFreezeMiliSecs();
                checkGameEnded(remainingMiliSecsWhenClicked);
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

        _services.getRoomMsgHandler().setRoomMsgListener(new RoomMsgListener() {
            @Override
            public void onTouched(final TouchedPoint touchedPoint, final String userId) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        imageTouched(userId, touchedPoint.x, touchedPoint.y, touchedPoint.getRemainingMiliSecs(), touchedPoint.isUsedHint());
                    }
                });
            }

            @Override
            public void onLose() {
                Logs.show("YOU LOSE!! Bye!");
                _gameModel.setGameState(GameState.Lose);
            }

            @Override
            public void onWon(WonStageModel wonStageModel) {
                _gameModel.setRemainingMiliSecs(wonStageModel.getRemainingSecs(), false);
                _gameModel.setGameState(GameState.Won);

                Logs.show("You win, time is: " + wonStageModel.getRemainingSecs());
            }

            @Override
            public void onDownloadImageRequest(ArrayList<String> ids) {
                _imageStorage.receivedDownloadRequest(ids);
            }

            @Override
            public void onGoToNextStage(String id, StageType stageType, BonusType bonusType, String extra) {
                goToNewStage(id, stageType, bonusType, extra);
            }

        });

        _stageImagesHandler.setStageImagesHandlerListener(new StageImagesHandlerListener() {
            @Override
            public void onTouch(float x, float y) {
                if(_gameModel.isPlaying()){
                    _services.getRoomMsgHandler().sendTouched(x, y, false, _gameModel.getRemainingMiliSecs());
                    imageTouched(getCoordinator().getMyUserId(), x, y,  _gameModel.getRemainingMiliSecs(), false);
                }
            }
        });

        getCoordinator().setUserStateListener(new UserStateListener() {
            @Override
            public void userAbandoned(String s) {

            }

            @Override
            public void userConnected(String s) {

            }

            @Override
            public void userDisconnected(String s) {

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
}
