package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.InGameUpdateListener;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.abs.image_getter.PeekImageListener;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.Service;
import com.potatoandtomato.games.models.TouchedData;
import com.potatoandtomato.games.models.UpdateMsg;
import com.potatoandtomato.games.screens.loading_screen.LoadingLogic;
import com.potatoandtomato.games.screens.play_screen.PlayLogic;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class MainController implements Disposable {

    private MainController _me;
    private GameCoordinator _coordinator;
    private Service _service;
    private PlayLogic _currentPlayLogic;
    private String _currentPlayingId;
    private ArrayList<TouchedData> _touchedQueue;
    private boolean _isStageReady;
    private LoadingLogic _loadingLogic;

    public MainController(final GameCoordinator _coordinator, Service _service) {
        this._me = this;
        this._coordinator = _coordinator;
        this._service = _service;
        _touchedQueue = new ArrayList<TouchedData>();

        _coordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                UpdateMsg updateMsg = new UpdateMsg(s);
                if(updateMsg.getUpdateCode() == UpdateCode.START_STAGE){
                    goToNextStage(updateMsg.getMsg());
                }
                else if(updateMsg.getUpdateCode() == UpdateCode.TOUCHED_IMAGE){
                    imageTouched(new TouchedData(updateMsg.getMsg()), s1);
                }
            }
        });
    }

    public void init(){
        _loadingLogic = new LoadingLogic(_me);
        _coordinator.getGame().setScreen(_loadingLogic.getScreen());
        sendNextStage();
    }


    public void sendNextStage(){
        _isStageReady = false;

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                if(_coordinator.meIsDecisionMaker()){
                    _service.getImageGetter().peekImage(new PeekImageListener() {
                        @Override
                        public void onImagePeeked(ImagePair imagePair) {
                            _coordinator.sendRoomUpdate(new UpdateMsg(UpdateCode.START_STAGE, imagePair.getId()).toJson());
                        }
                    });
                }
            }
        });
    }

    public void goToNextStage(final String imageDataId){

        System.out.println("image id: " + imageDataId);

        if(_currentPlayLogic != null){
            _currentPlayLogic.dispose();
        }

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _currentPlayLogic = new PlayLogic(_me);
                _coordinator.getGame().setScreen(_currentPlayLogic.getScreen());
                setLogicImages(imageDataId);
            }
        });

        _currentPlayingId = imageDataId;
    }

    public void imageTouched(TouchedData data, String sender){
        if(sender.equals(_coordinator.getUserId())) return;

        if(!_isStageReady || !_currentPlayingId.equals(data.getImageId())){
            _touchedQueue.add(data);
        }
        else{
            _currentPlayLogic.imageTouched(data.getX(), data.getY(), false);
        }
    }

    public void stageReady(){
        _isStageReady = true;
        for(TouchedData data : _touchedQueue){
            if(data.getImageId().equals(_currentPlayingId)){
                _currentPlayLogic.imageTouched(data.getX(), data.getY(), false);
            }
        }
        _touchedQueue.clear();
    }

    public void setLogicImages(final String imageDataId){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    final ImagePair imagePair = _service.getImageGetter().popImageById(imageDataId);
                    if(imagePair != null){
                        if(imageDataId.equals(_currentPlayingId)){
                            Threadings.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    _currentPlayLogic.setImages(imagePair);
                                    stageReady();
                                }
                            });
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void dispose() {
        _currentPlayLogic.dispose();
    }

    public Service getService() {
        return _service;
    }

    public GameCoordinator getCoordinator() {
        return _coordinator;
    }
}
