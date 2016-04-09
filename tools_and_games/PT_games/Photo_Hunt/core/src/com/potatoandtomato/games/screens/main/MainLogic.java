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
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.review.ReviewLogic;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 5/4/2016.
 */
public class MainLogic extends GameLogic {

    private Services _services;
    private MainScreen _screen;
    private ImageDetails _imageDetails;
    private TimeLogic _timeLogic;
    private HintsLogic _hintsLogic;
    private ReviewLogic _reviewLogic;
    private GameModel _gameModel;
    private ImageStorage _imageStorage;

    public MainLogic(Services services, GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        this._services = services;
        _gameModel = new GameModel();
        _hintsLogic = new HintsLogic(_gameModel, services);
        _reviewLogic = new ReviewLogic(_gameModel, services, gameCoordinator);

        _imageStorage = new ImageStorage(services, gameCoordinator);
        _imageStorage.startMonitor();

        _screen = new MainScreen(services, gameCoordinator);
        _screen.populate(_hintsLogic.getHintsActor());
        setListeners();
    }

    public void init(){
        sendGoToNextStageIfIsDecisionMaker(-1);
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

    public void newStage(String id, StageType stageType, String extra){
        _imageStorage.pop(id, new ImageStorageListener() {
            @Override
            public void onPopped(final ImagePair imagePair) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _gameModel.setImageDetails(imagePair.getImageDetails());
                        _gameModel.setGameState(GameState.Playing);
                        _gameModel.clearHandledAreas();
                        _imageDetails = imagePair.getImageDetails();
                        _imageDetails.setGameImageSize((int) _screen.getImageSize().x, (int) _screen.getImageSize().y);
                        _gameModel.addStageNumber();
                        invalidateReviewLogic();

                        if(_timeLogic != null) _timeLogic.dispose();

                        _gameModel.convertStageNumberToRemainingSecs();

                        _timeLogic = new TimeLogic(_gameModel, getCoordinator(), new TimeLogicListener() {
                            @Override
                            public void onTimeFinished() {
                                timeFinished();
                            }
                        });

                        changeScreenImages(imagePair.getImageOne(), imagePair.getImageTwo());

                        _timeLogic.start();

                        _gameModel.setGameState(GameState.Playing);
                    }
                });
            }
        });
    }

    public void changeScreenImages(Texture texture1, Texture texture2){
        _screen.resetImages(texture1, texture2);
    }

    public void imageTouched(float x, float y, int remainingSecs, boolean usedHint){
        Vector2 imageSize = _screen.getImageSize();

        float finalY = imageSize.y - y;  //libgdx origin is at bottomleft
        SimpleRectangle correctRect = _imageDetails.getTouchedCorrectRect(x, finalY);
        if(correctRect == null){
            _timeLogic.reduceTime();
        }
        else if(!_gameModel.isAreaAlreadyHandled(correctRect)) {
            if(usedHint){
                minusAvailableHint();
            }
            _gameModel.addHandledArea(correctRect);
            Rectangle circleRect = new Rectangle();
            circleRect.setSize(correctRect.getWidth(), correctRect.getHeight());
            circleRect.setPosition(correctRect.getX(), imageSize.y - correctRect.getY()); //libgdx origin is at bottomleft
            _screen.circle(circleRect);
            checkGameEnded(remainingSecs);
        }
    }

    public void sendTouchedMsg(float x, float y, boolean hintUsed){
        _services.getRoomMsgHandler().sendTouched(new TouchedPoint(x, y, _timeLogic.getRemainingSecs(), hintUsed));
    }

    //allow a 1 sec tolerance time for game over
    public void timeFinished(){
        if(getCoordinator().meIsDecisionMaker()){
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    if(_gameModel.getGameState() == GameState.Ended){
                        return;
                    }
                    else{
                        _services.getRoomMsgHandler().sendLose();
                    }
                }
            });
        }
    }

    public void checkGameEnded(int remainingSecs){
        if(_gameModel.getHandledAreas().size() == 5){
            _gameModel.setGameState(GameState.Ended);
            _timeLogic.stop();
            if(getCoordinator().meIsDecisionMaker()){
                _services.getRoomMsgHandler().sendWon(new WonStageModel(_gameModel.getStageNumber(),
                                        _gameModel.getScore(), remainingSecs, "0", StageType.Normal));
            }
        }
    }

    private void minusAvailableHint(){
        if(!Global.REVIEW_MODE){
            _gameModel.setHintsLeft(_gameModel.getHintsLeft() - 1);
            _hintsLogic.invalidate();
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
        _gameModel.setGameState(GameState.Blocking);
        _screen.switchToReviewMode(_reviewLogic.getReviewActor());
        _imageStorage.setRandomize(false);
        _imageStorage.disposeAllImages();
        _imageStorage.initiateDownloadsIfNeeded();
        sendGoToNextStageIfIsDecisionMaker(0);
    }

    private void setListeners(){

        _hintsLogic.setHintsLogicListener(new HintsLogicListener() {
            @Override
            public void onHintClicked() {
                if(canClick()){
                    int i = 0;
                    Rectangle notYetHandledArea = null;
                    for(Rectangle rectangle : _imageDetails.getCorrectRects()){
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
                        imageTouched(x, y, _timeLogic.getRemainingSecs(), true);
                    }

                }
            }
        });

        _screen.getImageOneTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(canClick()){
                    sendTouchedMsg(x, y, false);
                    imageTouched(x, y, _timeLogic.getRemainingSecs(), false);
                }
            }
        });

        _screen.getImageTwoTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(canClick()){
                    sendTouchedMsg(x, y, false);
                    imageTouched(x, y, _timeLogic.getRemainingSecs(), false);
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
            public void onTouched(TouchedPoint touchedPoint, String userId) {
                imageTouched(touchedPoint.x, touchedPoint.y, touchedPoint.getRemainingSecs(), touchedPoint.isUsedHint());
            }

            @Override
            public void onLose() {
                Logs.show("YOU LOSE!! Bye!");
            }

            @Override
            public void onWon(WonStageModel wonStageModel) {
                Logs.show("You win, time is: " + wonStageModel.getRemainingSecs());
                Threadings.delay(5000, new Runnable() {
                    @Override
                    public void run() {
                        if(!Global.REVIEW_MODE){
                            sendGoToNextStageIfIsDecisionMaker(-1);
                        }
                    }
                });

            }

            @Override
            public void onDownloadImageRequest(ArrayList<String> ids) {
                _imageStorage.receivedDownloadRequest(ids);
            }

            @Override
            public void onGoToNextStage(String id, StageType stageType, String extra) {
                newStage(id, stageType, extra);
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

        _gameModel.setGameStateListener(new GameStateListener() {
            @Override
            public void onChanged(GameState newState) {
                _screen.refreshGameState(newState);
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

    private boolean canClick(){
        return _timeLogic != null && _timeLogic.isTimeRunning()
                && _gameModel.getHandledAreas().size() < 5 && _gameModel.getGameState() == GameState.Playing;
    }







    @Override
    public void dispose() {
        _imageStorage.dispose();
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

    public void setTimeLogic(TimeLogic _timeLogic) {
        this._timeLogic = _timeLogic;
    }

    public GameModel getGameModel() {
        return _gameModel;
    }

    public void setGameModel(GameModel _gameModel) {
        this._gameModel = _gameModel;
    }

    public void setImageStorage(ImageStorage _imageStorage) {
        this._imageStorage = _imageStorage;
    }
}
