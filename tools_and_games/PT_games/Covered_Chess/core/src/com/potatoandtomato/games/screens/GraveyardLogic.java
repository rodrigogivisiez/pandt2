package com.potatoandtomato.games.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.SoundsWrapper;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.GraveModel;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class GraveyardLogic {

    private GraveModel _graveModel;
    private GraveyardActor _graveyardActor;
    private SoundsWrapper _soundsWrapper;
    private boolean _showed;

    public GraveyardLogic(GraveModel graveModel, GameCoordinator gameCoordinator, Texts texts, Assets assets, SoundsWrapper soundsWrapper) {
        this._graveModel = graveModel;
        this._soundsWrapper = soundsWrapper;
        this._graveyardActor = new GraveyardActor(gameCoordinator, texts, assets);
        setListener();
    }

    public void invalidate(){
        _graveyardActor.modelChanged(getGraveModel());
    }

    public void addChessToGrave(ChessType chessType){
        _graveModel.addToGrave(chessType);
        invalidate();
    }

    public void switchTurn(){
        _graveModel.setCurrentTurnIndex(_graveModel.getCurrentTurnIndex() == 0 ? 1 : 0);
        invalidate();
    }

    public GraveModel getGraveModel() {
        return _graveModel;
    }

    public void setGraveModel(GraveModel _graveModel) {
        this._graveModel = _graveModel;
        invalidate();
    }

    public GraveyardActor getGraveyardActor() {
        return _graveyardActor;
    }

    public boolean isShowed() {
        return _showed;
    }

    public void setListener(){
        _graveyardActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(isShowed()){
                    _graveyardActor.hide();
                }
                else{
                    _graveyardActor.expand();
                }
                _showed = !_showed;
                _soundsWrapper.playSounds(Sounds.Name.OPEN_SLIDE);
            }
        });
    }

}
