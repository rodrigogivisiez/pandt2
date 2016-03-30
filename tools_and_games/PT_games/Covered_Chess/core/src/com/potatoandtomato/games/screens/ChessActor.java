package com.potatoandtomato.games.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.CloneableTable;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.helpers.Sizes;
import com.potatoandtomato.games.models.ChessModel;
import com.potatoandtomato.games.services.SoundsWrapper;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class ChessActor extends Table {

    private ChessActor _me;
    private Table _coverChess, _previewChess;
    private CloneableTable _animalChess;
    private MyAssets _assets;
    private boolean _expanded;
    private boolean _initialized;
    private Image _animalImage;
    private Image _glowChess;
    private boolean _alreadySetAnimalChessBg;
    private SoundsWrapper _soundsWrapper;
    private Table _statusTable;
    private Status _currentStatus;

    public Table getCoverChess() {
        return _coverChess;
    }

    public ChessActor(MyAssets assets, SoundsWrapper soundsWrapper) {
        _me = this;
        _soundsWrapper = soundsWrapper;
        _assets = assets;
        this.setTransform(true);

        _coverChess = new Table();

        _coverChess.setTransform(true);
        _coverChess.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.UNKNOWN_CHESS)));
        new DummyButton(_coverChess, _assets);

        _previewChess = new Table();
        _previewChess.setVisible(false);
        _previewChess.setTransform(true);
        _previewChess.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.PREVIEW_CHESS)));
        Image previewImage = new Image(_assets.getTextures().get(Textures.Name.PREVIEW_ICON));
        _previewChess.add(previewImage).pad(5);

        _animalChess = new CloneableTable();
        _animalChess.setVisible(false);
        _animalChess.setTransform(true);
        new DummyButton(_animalChess, _assets);

        setAnimal(ChessType.UNKNOWN);

        _glowChess = new Image(_assets.getTextures().get(Textures.Name.GLOW_CHESS));
        _glowChess.setVisible(false);

        _statusTable = new Table();
        _statusTable.setSize(30, 30);
        _statusTable.setPosition(-5, 37);
        _statusTable.setOrigin(Align.center);
        _statusTable.setTransform(true);
        _statusTable.setVisible(false);

        this.addActor(_glowChess);
        this.addActor(_animalChess);
        this.addActor(_previewChess);
        this.addActor(_coverChess);
        this.addActor(_statusTable);

    }

    public void previewChess(final boolean revealChess, final Runnable toRun){
        flipChessAnimation(_coverChess, revealChess ? _animalChess : _previewChess, new Runnable() {
            @Override
            public void run() {
                Threadings.delay(2000, new Runnable() {
                    @Override
                    public void run() {
                        flipChessAnimation(revealChess ? _animalChess : _previewChess, _coverChess, toRun);
                    }
                });
            }
        });
    }


    public void openChess(final Runnable toRun){
        flipChessAnimation(_coverChess, _animalChess, toRun);
    }

    private void flipChessAnimation(final Actor hidingChess, final Actor showingChess, final Runnable toRun){
        showingChess.setVisible(true);

        _soundsWrapper.playSounds(Sounds.Name.FLIP_CHESS);

        float duration = 0.2f;
        hidingChess.addAction(sequence(Actions.scaleTo(0, 1, duration / 2)));
        showingChess.clearActions();
        showingChess.addAction(sequence(scaleTo(0, 1), Actions.delay(duration / 2), Actions.scaleTo(1, 1, duration / 2), new Action(){
            @Override
            public boolean act(float delta) {
                showingChess.setScale(1, 1);
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
        if(chessType == ChessType.NONE){
            _animalChess.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        }
        else{
            String chessTypeString = chessType.name();

            TextureRegion animalChessRegion;
            if(selected) animalChessRegion = chessTypeString.startsWith("RED") ? _assets.getTextures().get(Textures.Name.RED_CHESS_SELECTED) : _assets.getTextures().get(Textures.Name.YELLOW_CHESS_SELECTED);
            else animalChessRegion = chessTypeString.startsWith("RED") ? _assets.getTextures().get(Textures.Name.RED_CHESS) : _assets.getTextures().get(Textures.Name.YELLOW_CHESS);

            TextureRegion coverChessRegion = selected ? _assets.getTextures().get(Textures.Name.UNKNOWN_CHESS_SELECTED) : _assets.getTextures().get(Textures.Name.UNKNOWN_CHESS);

            _animalChess.setBackground(new TextureRegionDrawable(animalChessRegion));
            _coverChess.setBackground(new TextureRegionDrawable(coverChessRegion));

            if(selected && !_expanded){
                moving(2, 2, -1, -1);
                _expanded = true;
            }

            if(!selected && _expanded){
                moving(-2, -2, 1, 1);
                _expanded = false;
            }
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
            fixChessSizePosition(_coverChess, 0);
            fixChessSizePosition(_animalChess, 0);
            fixChessSizePosition(_previewChess, 0);
            fixChessSizePosition(_glowChess, 25);
            this.setOrigin(this.getWidth()/2 , this.getHeight()/2);
            _initialized = true;
        }
    }

    private void fixChessSizePosition(Actor chessTable, int offsetSize){
        chessTable.setSize(this.getWidth() + offsetSize, this.getHeight() + offsetSize);
        chessTable.setPosition(Positions.centerX(this.getWidth(), this.getWidth() + offsetSize),
                Positions.centerY(this.getHeight(), this.getHeight() + offsetSize));
        chessTable.setOrigin(Align.center);
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
            _animalChess.setVisible(true);
            _statusTable.setVisible(true);
        }
        if(_currentStatus != chessModel.getStatus()){
            setStatusIcon(chessModel.getStatus(), false);
        }

        Logs.show("Invalidating chess: " + chessModel.getChessType());
    }

    public void showAbilityTriggered(final ChessType chessType, final boolean hideChessAnimal){

        Threadings.delay(300, new Runnable() {
            @Override
            public void run() {
                final Image fadeOutAnimalImage = new Image(_assets.getTextures().getAnimalByType(chessType));
                fadeOutAnimalImage.setOrigin(Align.center);

                Vector2 size = new Vector2(_animalImage.getWidth(),
                        Sizes.resize(_animalImage.getWidth(), _assets.getTextures().getAnimalByType(chessType)).y);

                fadeOutAnimalImage.setSize(size.x, size.y);
                Vector2 locationAtStage = Positions.actorLocalToStageCoord(_me);
                fadeOutAnimalImage.setPosition(locationAtStage.x + Positions.centerX(_me.getWidth(), size.x),
                        locationAtStage.y + Positions.centerY(_me.getHeight(), size.y));

                if(hideChessAnimal) _animalImage.setVisible(false);

                fadeOutAnimalImage.addAction(sequence(parallel(scaleTo(3.5f, 3.5f, 0.6f), fadeOut(0.6f)), new Action() {
                    @Override
                    public boolean act(float delta) {
                        fadeOutAnimalImage.remove();
                        _animalImage.setVisible(true);
                        return true;
                    }
                }));

                _me.getStage().addActor(fadeOutAnimalImage);
            }
        });
    }

    public void setStatusIcon(Status status, boolean animate){
        _statusTable.setScale(1, 1);

        if(status == Status.NONE){
            if(animate){
                _statusTable.addAction(sequence(Actions.scaleTo(0, 0, 0.3f, Interpolation.bounceOut), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _statusTable.clear();
                        _statusTable.getColor().a = 1;
                        return true;
                    }
                }));
            }
            else{
                _statusTable.clear();
            }
        }
        else{
            final Image imageStatus = new Image(_assets.getTextures().getStatus(status));
            imageStatus.setOrigin(Align.center);

            _statusTable.clear();
            _statusTable.add(imageStatus);

            if(animate){
                imageStatus.getColor().a = 0f;
                imageStatus.addAction(sequence(scaleTo(0, 0), fadeIn(0f), scaleTo(1, 1, 0.2f, Interpolation.bounce)));
            }
        }
        _currentStatus = status;

    }
}

