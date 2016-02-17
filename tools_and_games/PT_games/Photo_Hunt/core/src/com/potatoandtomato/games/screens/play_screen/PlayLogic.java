package com.potatoandtomato.games.screens.play_screen;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.common.Status;
import com.potatoandtomato.games.abs.database.DatabaseListener;
import com.potatoandtomato.games.abs.screens.LogicAbstract;
import com.potatoandtomato.games.helpers.ImageTouchVerifier;
import com.potatoandtomato.games.helpers.MainController;
import com.potatoandtomato.games.helpers.UpdateCode;
import com.potatoandtomato.games.models.CorrectArea;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.TouchedData;
import com.potatoandtomato.games.models.UpdateMsg;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class PlayLogic extends LogicAbstract {

    private PlayScreen _screen;
    private ImagePair _imagePair;
    private ImageTouchVerifier _verifier;
    private ArrayList<CorrectArea> _handledCorrectAreas;

    public PlayLogic(MainController mainController) {
        super(mainController);

        _handledCorrectAreas = new ArrayList<CorrectArea>();

        _screen = new PlayScreen(getGameCoordinator(), getAssets());

//        _imagePair = new ImagePair(new TextureRegionDrawable(_assets.getSampleOne()), new TextureRegionDrawable(_assets.getSampleTwo()), json);
//        _screen.populateImages(_imagePair.getImageOne(), _imagePair.getImageTwo());

        _screen.getImageOneTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                imageTouched(x, y, true);
            }
        });

        _screen.getImageTwoTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                imageTouched(x, y, true);
            }
        });

        _screen.getLabelDelete().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_screen.getLabelDelete().getText().toString().equals("Confirm Del?")){
                    getService().getDatabase().removeImageById(_imagePair.getId(), new DatabaseListener() {
                        @Override
                        public void onCallback(Object obj, Status st) {
                            if(st == Status.SUCCESS){
                                _screen.getLabelDelete().setText("Deleting...");
                                getMainController().sendNextStage();
                            }
                        }
                    });

                }
                else if(_screen.getLabelDelete().getText().toString().equals("Deleting...")){

                }
                else{
                    _screen.getLabelDelete().setText("Confirm Del?");
                }

            }
        });

        _screen.getLabelNext().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_screen.getLabelNext().getText().toString().equals("Next")){
                    getMainController().sendNextStage();
                    _screen.getLabelNext().setText("Processing...");
                }

            }
        });

        _screen.getLabelAbandon().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getGameCoordinator().abandon();
            }
        });

        getService().getDatabase().getTotalImagesCount(new DatabaseListener<Long>() {
            @Override
            public void onCallback(Long obj, Status st) {
                _screen.getLabelItemsCount().setText(String.valueOf(obj + 1));
            }
        });

        _screen.getLabelGo().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_screen.getLabelGo().getText().toString().equals("Go")){
                    getService().getImageGetter().goToIndex(Integer.valueOf(_screen.getTextNumber().getText()));
                    _screen.getLabelGo().setText("Processing...");
                    getMainController().sendNextStage();
                }
            }
        });

    }


    public void setImages(ImagePair imagePair){
        _imagePair = imagePair;
        _screen.getTextNumber().setText(String.valueOf(imagePair.getIndex()));
        _screen.setImageOne(imagePair.getImageOne());
        _screen.setImageTwo(imagePair.getImageTwo());
    }

    public void imageTouched(final float x, final float y, final boolean sendNotify){

        if(sendNotify){
            getGameCoordinator().sendRoomUpdate(new UpdateMsg(UpdateCode.TOUCHED_IMAGE,
                    new TouchedData(x, y, _imagePair.getId()).toString()).toJson());
        }

        _screen.onImageSizeCalculated(new Runnable() {
            @Override
            public void run() {
                float finalY = y;

                finalY = _screen.getImageHeight() - finalY;  //libgdx origin is at bottomleft
                CorrectArea correctArea = getImageTouchVerifier(_screen.getImageWidth(), _screen.getImageHeight())
                        .getConvertedTouchedCorrectArea(x, finalY);
                if(correctArea == null){
                    //todo wrong press
                }
                else if(!_handledCorrectAreas.contains(correctArea)){
                    _handledCorrectAreas.add(correctArea);
                    Rectangle rectangle = correctArea.toRectangle();
                    rectangle.setPosition(rectangle.getX(), _screen.getImageHeight() - rectangle.getY());  //libgdx origin is at bottomleft
                    _screen.drawEllipse(rectangle);
                    checkGameEnded();
                }
            }
        });





    }


    private void checkGameEnded(){
        if(_handledCorrectAreas.size() == 5){
            //getMainController().sendNextStage();
        }
    }

    private ImageTouchVerifier getImageTouchVerifier(float imageWidth, float imageHeight){
        if(_verifier == null){
            _verifier = new ImageTouchVerifier(_imagePair.getMetaJson(), imageWidth, imageHeight);
        }
        return _verifier;
    }

    @Override
    public GameScreen getScreen() {
        return _screen;
    }


}
