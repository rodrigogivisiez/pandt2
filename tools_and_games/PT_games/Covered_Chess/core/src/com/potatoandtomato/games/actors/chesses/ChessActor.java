package com.potatoandtomato.games.actors.chesses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.games.actors.DummyButton;
import com.potatoandtomato.games.actors.chesses.enums.ChessType;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Positions;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class ChessActor extends Table {

    private Table _frontChess, _backChess;
    private Assets _assets;
    private Label _msgLabel;
    private boolean _selected;
    private boolean _initialized;
    private boolean _opened;
    private Image _animalImage;
    private ChessType _chessType;

    public Table getFrontChess() {
        return _frontChess;
    }

    public Table getBackChess() {
        return _backChess;
    }

    public ChessActor(Assets assets) {
        _assets = assets;
        _frontChess = new Table();

        _frontChess.setTransform(true);
        _frontChess.setBackground(new TextureRegionDrawable(_assets.getUnknownPawn()));
        new DummyButton(_frontChess, _assets);

        _backChess = new Table();
        _backChess.setTransform(true);
        _backChess.setOrigin(Align.center);
        new DummyButton(_backChess, _assets);

        setChessType(ChessType.UNKNOWN);

        this.addActor(_backChess);
        this.addActor(_frontChess);
       // setChessType(ChessType.ELEPHANT);

    }

    public void openChess(boolean useAnimation){
        if(_animalImage != null) _animalImage.setVisible(true);
        if(!useAnimation){
            _backChess.setVisible(true);
            _frontChess.setVisible(false);
        }
        else{
            float duration = 0.2f;
            _frontChess.addAction(sequence(Actions.scaleTo(0, 1, duration / 2)));
            _backChess.addAction(sequence(scaleTo(0, 1), Actions.delay(duration / 2), Actions.scaleTo(1, 1, duration / 2)));
        }
        _opened = true;
    }

    public boolean openChess(float startX, float endX){
        if(_opened) return false;

        if(startX < endX){
            float originalEndX = endX;
            endX = startX;
            startX = originalEndX;
        }

        float openPercentage = ((startX - (endX)) / this.getWidth());
        openPercentage = 1 - openPercentage;
        if(openPercentage > 1) openPercentage = 1;

        if(openPercentage > 0.25){
            _backChess.addAction(scaleTo(0, 1));
            _frontChess.addAction(Actions.scaleTo(openPercentage, 1, 0.1f));
            return false;
        }
        else{
            if(!_selected) setSelected(true);
            openChess(true);
            return true;
        }
    }

    public void resetOpenChess(){
        if(!isOpened()){
            _backChess.addAction(scaleTo(0, 1));
            _frontChess.addAction(Actions.scaleTo(1, 1, 0.1f));
        }
    }

    public void setChessType(ChessType chessType){
        _backChess.clear();
        _chessType = chessType;
        _animalImage = new Image();
        _backChess.add(_animalImage).pad(5);
        _animalImage.setVisible(false);


        String chessTypeString = chessType.name();

        TextureRegion region = null;

        if(chessTypeString.endsWith("ELEPHANT")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedElephant();
            else region = _assets.getYellowElephant();
        }
        else if(chessTypeString.endsWith("MOUSE")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedMouse();
            else region = _assets.getYellowMouse();
        }
        else if(chessTypeString.endsWith("CAT")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedCat();
            else region = _assets.getYellowCat();
        }
        else if(chessTypeString.endsWith("DOG")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedDog();
            else region = _assets.getYellowDog();
        }
        else if(chessTypeString.endsWith("LION")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedLion();
            else region = _assets.getYellowLion();
        }
        else if(chessTypeString.endsWith("TIGER")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedTiger();
            else region = _assets.getYellowTiger();
        }
        else if(chessTypeString.endsWith("WOLF")){
            if(chessTypeString.startsWith("RED")) region = _assets.getRedWolf();
            else region = _assets.getYellowWolf();
        }
        else if(chessTypeString.endsWith("RED")){
            _backChess.setBackground(new TextureRegionDrawable(_assets.getRedPawn()));
        }
        else if(chessTypeString.endsWith("YELLOW")){
            _backChess.setBackground(new TextureRegionDrawable(_assets.getYellowPawn()));
        }
        else{
            _frontChess.setBackground(new TextureRegionDrawable(_assets.getUnknownPawn()));
        }

        if(region != null)  _animalImage.setDrawable(new TextureRegionDrawable(region));
    }

    public void setContent(String text){
        _backChess.clear();

        _msgLabel = new Label(text, new Label.LabelStyle(_assets.getBlackBold1(), Color.BLACK));
        _backChess.add(_msgLabel);
        _msgLabel.addAction(sequence(fadeOut(0f), fadeIn(0.5f)));
    }

    public void setSelected(boolean selected) {
        if(selected == _selected) return;
        else{
            _selected = selected;
        }

        setChessSurface();
    }

    public void setChessSurface(){
        String chessTypeString = _chessType.name();

        if(_selected){
            if(chessTypeString.startsWith("RED")){
                _backChess.setBackground(new TextureRegionDrawable(_assets.getRedPawnSelected()));
            }
            else if(chessTypeString.startsWith("YELLOW")){
                _backChess.setBackground(new TextureRegionDrawable(_assets.getYellowPawnSelected()));
            }
            _frontChess.setBackground(new TextureRegionDrawable(_assets.getUnknownPawnSelected()));

            _backChess.setWidth(_backChess.getWidth() + 2);
            _backChess.setHeight(_backChess.getHeight() + 2);
            _backChess.setPosition(_backChess.getX() - 1f, _backChess.getY() - 1f);

            _frontChess.setWidth(_frontChess.getWidth() + 2);
            _frontChess.setHeight(_frontChess.getHeight() + 2);
            _frontChess.setPosition(_frontChess.getX() - 1f, _frontChess.getY() - 1f);
        }
        else{
            if(chessTypeString.startsWith("RED")){
                _backChess.setBackground(new TextureRegionDrawable(_assets.getRedPawn()));
            }
            else if(chessTypeString.startsWith("YELLOW")){
                _backChess.setBackground(new TextureRegionDrawable(_assets.getYellowPawn()));
            }
            _frontChess.setBackground(new TextureRegionDrawable(_assets.getUnknownPawn()));

            _backChess.setWidth(_backChess.getWidth() - 2);
            _backChess.setHeight(_backChess.getHeight() - 2);
            _backChess.setPosition(_backChess.getX() + 1f, _backChess.getY() + 1f);

            _frontChess.setWidth(_frontChess.getWidth() - 2);
            _frontChess.setHeight(_frontChess.getHeight() - 2);
            _frontChess.setPosition(_frontChess.getX() + 1f, _frontChess.getY() + 1f);
        }
    }

    public boolean isOpened() {
        return _opened;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(!_initialized){
            _frontChess.setSize(this.getWidth(), this.getHeight());
            _frontChess.setPosition(Positions.centerX(this.getWidth(), this.getWidth()),
                    Positions.centerY(this.getHeight(), this.getHeight()));
            _frontChess.setOrigin(Align.center);
            _backChess.setSize(this.getWidth(), this.getHeight());
            _backChess.setPosition(Positions.centerX(this.getWidth(), this.getWidth()),
                    Positions.centerY(this.getHeight(), this.getHeight()));
            _backChess.setOrigin(Align.center);
            //_frontChess.setSize(this.getWidth(), this.getHeight());
            //_backChess.setSize(this.getWidth(), this.getHeight());
            _initialized = true;
        }
    }

    public Table clone(){
        Table table = new Table();
        table.setSize(this.getWidth(), this.getHeight());
        table.setBackground(_backChess.getBackground());
        Image img2 = new Image(_animalImage.getDrawable());
        table.add(img2);
        table.setTransform(true);
        return table;
    }

    public ChessType getChessType() {
        return _chessType;
    }

    public boolean isYellow(){
        return _chessType.name().startsWith("YELLOW");
    }

    public Drawable getAnimalDrawable(){
        return _animalImage.getDrawable();
    }

}

