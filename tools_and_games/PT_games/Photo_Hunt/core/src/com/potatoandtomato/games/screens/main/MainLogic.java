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
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.*;
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

    public MainLogic(GameCoordinator gameCoordinator, Services _services,
                     TimeLogic _timeLogic, HintsLogic _hintsLogic, ReviewLogic _reviewLogic, UserCountersLogic _userCounterLogic,
                     StageCounterLogic _stageCounterLogic, ScoresLogic _scoresLogic,
                     ImageStorage _imageStorage, GameModel _gameModel) {
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

        _screen = new MainScreen(_services, gameCoordinator);
        _screen.populate(_timeLogic.getTimeActor(), _hintsLogic.getHintsActor(),
                                _userCounterLogic.getUserCountersActor(), _stageCounterLogic.getStageCounterActor(),
                                _scoresLogic.getScoresActor());
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
                    _services.getRoomMsgHandler().sendGotoNextStage(imagePair.getImageDetails().getId(), StageType.Normal, "");
                }
            });
        }
    }

    public void goToNewStage(String id, StageType stageType, String extra){
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
                        _gameModel.setStageType(StageType.Normal);
                        _gameModel.addStageNumber();
                        //_gameModel.setStageNumber(20);
                        _gameModel.setGameState(GameState.Playing);

                        invalidateReviewLogic();

                        changeScreenImages(imagePair.getImageOne(), imagePair.getImageTwo());
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
        SimpleRectangle correctRect = _gameModel.getImageDetails().getTouchedCorrectRect(x, finalY);
        if(correctRect == null){
            _timeLogic.reduceTime();
            _screen.cross(x, y, userId);
        }
        else if(!_gameModel.isAreaAlreadyHandled(correctRect)) {
            if(usedHint && !Global.REVIEW_MODE){
                _gameModel.setHintsLeft(_gameModel.getHintsLeft() - 1);
            }
            _gameModel.addHandledArea(correctRect, userId);
            checkGameEnded(remainingMiliSecs);
        }
    }

    public void sendTouchedMsg(float x, float y, boolean hintUsed){
        _services.getRoomMsgHandler().sendTouched(new TouchedPoint(x, y,  _gameModel.getRemainingMiliSecs(), hintUsed));
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
                _screen.refreshGameState(newState);
            }

            @Override
            public void onCorrectClicked(SimpleRectangle correctRect, String userId) {
                Vector2 imageSize = _screen.getImageSize();
                Rectangle circleRect = new Rectangle();
                circleRect.setSize(correctRect.getWidth(), correctRect.getHeight());
                circleRect.setPosition(correctRect.getX(), imageSize.y - correctRect.getY()); //libgdx origin is at bottomleft
                _screen.circle(circleRect, userId);

                _gameModel.addFreezeMiliSecs();
            }
        });

        _hintsLogic.setHintsLogicListener(new HintsLogicListener() {
            @Override
            public void onHintClicked() {
                if(_gameModel.isPlaying() && _gameModel.getHintsLeft() > 0){
                    int i = 0;
                    Rectangle notYetHandledArea = null;
                    for(Rectangle rectangle : _gameModel.getImageDetails().getCorrectRects()){
                        if(!_gameModel.isAreaAlreadyHandled(rectangle)){
                            notYetHandledArea = rectangle;
                            break;
                        }
                        i++;
                    }

                    if(notYetHandledArea != null){
                        float x, y;
                        x = notYetHandledArea.getX() + 1;
                        y = notYetHandledArea.getY() + 1;
                        y = _screen.getImageSize().y - y;           //libgdx y is from bottom left
                        sendTouchedMsg(x, y, true);
                        imageTouched(getCoordinator().getMyUserId(), x, y, _gameModel.getRemainingMiliSecs(), true);
                    }
                }
            }
        });

        _screen.getImageOneTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_gameModel.isPlaying()){
                    sendTouchedMsg(x, y, false);
                    imageTouched(getCoordinator().getMyUserId(), x, y,  _gameModel.getRemainingMiliSecs(), false);
                }
            }
        });

        _screen.getImageTwoTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_gameModel.isPlaying()){
                    sendTouchedMsg(x, y, false);
                    imageTouched(getCoordinator().getMyUserId(), x, y,  _gameModel.getRemainingMiliSecs(), false);
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
            public void onGoToNextStage(String id, StageType stageType, String extra) {
                goToNewStage(id, stageType, extra);
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
