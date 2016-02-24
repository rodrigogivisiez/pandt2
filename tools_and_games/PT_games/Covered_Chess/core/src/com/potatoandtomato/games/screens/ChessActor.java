package com.potatoandtomato.games.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.CloneableTable;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.ChessModel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class ChessActor extends Table {

    private Table _coverChess;
    private CloneableTable _animalChess;
    private Assets _assets;
    private Label _msgLabel;
    private boolean _expanded;
    private boolean _initialized;
    private Image _animalImage;
    private Image _glowChess;
    private boolean _alreadySetAnimalChessBg;

    public Table getCoverChess() {
        return _coverChess;
    }

    public ChessActor(Assets assets) {
        _assets = assets;
        _coverChess = new Table();

        _coverChess.setTransform(true);
        _coverChess.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.UNKNOWN_CHESS)));
        new DummyButton(_coverChess, _assets);

        _animalChess = new CloneableTable();
        _animalChess.setTransform(true);
        _animalChess.setOrigin(Align.center);
        new DummyButton(_animalChess, _assets);

        setAnimal(ChessType.UNKNOWN);

        _glowChess = new Image(_assets.getTextures().get(Textures.Name.GLOW_CHESS));
        _glowChess.setVisible(false);

        this.addActor(_glowChess);
        this.addActor(_animalChess);
        this.addActor(_coverChess);
    }

    public void openChess(final Runnable toRun){
        if(_animalImage != null) _animalImage.setVisible(true);

        float duration = 0.2f;
        _coverChess.addAction(sequence(Actions.scaleTo(0, 1, duration / 2)));
        _animalChess.addAction(sequence(scaleTo(0, 1), Actions.delay(duration / 2), Actions.scaleTo(1, 1, duration / 2), new Action() {
            @Override
            public boolean act(float delta) {
                _animalChess.clearActions();
                _animalChess.setScale(1, 1);
                if(toRun != null) toRun.run();
                return true;
            }
        }));

    }

    public boolean openChess(float startX, float endX){

        if(startX < endX){
            float originalEndX = endX;
            endX = startX;
            startX = originalEndX;
        }

        float openPercentage = ((startX - (endX)) / this.getWidth());
        openPercentage = 1 - openPercentage;
        if(openPercentage > 1) openPercentage = 1;

        if(openPercentage > 0.25){
            _animalChess.addAction(scaleTo(0, 1));
            _coverChess.addAction(Actions.scaleTo(openPercentage, 1, 0.1f));
            return false;
        }
        else{
            return true;
        }
    }

    public void resetOpenChess(){
        _animalChess.addAction(scaleTo(0, 1));
        _coverChess.addAction(Actions.scaleTo(1, 1, 0.1f));
    }

    public void setAnimal(ChessType chessType){
        _animalChess.clear();
        if(chessType != ChessType.NONE){
            _animalImage = new Image();
            _animalChess.add(_animalImage).pad(5);
            new DummyButton(_animalChess, _assets);

            TextureRegion region = null;
            region = _assets.getTextures().getAnimalByType(chessType);
            if(region != null)  _animalImage.setDrawable(new TextureRegionDrawable(region));
        }

    }


    public void setSurface(boolean selected, ChessType chessType){
        if(_expanded != selected && chessType != ChessType.NONE || !_alreadySetAnimalChessBg){
            String chessTypeString = chessType.name();

            TextureRegion animalChessRegion;
            if(selected) animalChessRegion = chessTypeString.startsWith("RED") ? _assets.getTextures().get(Textures.Name.RED_CHESS_SELECTED) : _assets.getTextures().get(Textures.Name.YELLOW_CHESS_SELECTED);
            else animalChessRegion = chessTypeString.startsWith("RED") ? _assets.getTextures().get(Textures.Name.RED_CHESS) : _assets.getTextures().get(Textures.Name.YELLOW_CHESS);

            TextureRegion coverChessRegion = selected ? _assets.getTextures().get(Textures.Name.UNKNOWN_CHESS_SELECTED) : _assets.getTextures().get(Textures.Name.UNKNOWN_CHESS);

            _animalChess.setBackground(new TextureRegionDrawable(animalChessRegion));
            _coverChess.setBackground(new TextureRegionDrawable(coverChessRegion));

            if(selected)  moving(2, 2, -1, -1);
            else moving(-2, -2, 1, 1);

            _expanded = selected;
            _alreadySetAnimalChessBg = true;
        }

        if(chessType == ChessType.NONE){
            _animalChess.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        }
    }

    public void moving(float addedWidth, float addedHeight, float addedX, float addedY){
        _animalChess.setWidth(_animalChess.getWidth() + addedWidth);
        _animalChess.setHeight(_animalChess.getHeight() + addedHeight);
        _animalChess.setPosition(_animalChess.getX() + addedX, _animalChess.getY() + addedY);

        _coverChess.setWidth(_coverChess.getWidth() + addedWidth);
        _coverChess.setHeight(_coverChess.getHeight() + addedHeight);
        _coverChess.setPosition(_coverChess.getX() + addedX, _coverChess.getY() + addedY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(!_initialized){
            _coverChess.setSize(this.getWidth(), this.getHeight());
            _coverChess.setPosition(Positions.centerX(this.getWidth(), this.getWidth()),
                    Positions.centerY(this.getHeight(), this.getHeight()));
            _coverChess.setOrigin(Align.center);
            _animalChess.setSize(this.getWidth(), this.getHeight());
            _animalChess.setPosition(Positions.centerX(this.getWidth(), this.getWidth()),
                    Positions.centerY(this.getHeight(), this.getHeight()));
            _animalChess.setOrigin(Align.center);

            int glowSize = 25;
            _glowChess.setSize(this.getWidth()+ glowSize, this.getHeight()+ glowSize);
            _glowChess.setPosition(Positions.centerX(this.getWidth(), this.getWidth()+ glowSize),
                    Positions.centerY(this.getHeight(), this.getHeight()+ glowSize));
            _initialized = true;
        }
    }

    public Actor clone(){
        _animalChess.setScaleX(1);
        return _animalChess.clone();
    }

    public void invalidate(ChessModel chessModel){
        setAnimal(chessModel.getChessType());
        setSurface(chessModel.getSelected(), chessModel.getChessType());
        this.getColor().a = chessModel.getDragging() ? 0 : 1;
        _glowChess.setVisible(chessModel.getFocusing());
        if(chessModel.getOpened()){
            _coverChess.setVisible(false);
        }


    }

}

