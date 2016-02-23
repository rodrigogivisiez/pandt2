package com.potatoandtomato.games.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Positions;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class TerrainActor extends Table {

    private Assets _assets;
    private Image _glowingTile;
    private boolean _selected;
    private Table _chessTable;
    private Table _greenTile, _redTile;
    private Label _percentLabel;
    private boolean _initialized;
    private Image _arrowLeft, _arrowRight, _arrowUp, _arrowDown;
    private Table _battleTable;


    public TerrainActor(Assets _assets, ChessActor chessActor) {
        this._assets = _assets;
        this.setBackground(new TextureRegionDrawable(_assets.getTextures().getBlackBgTrans()));
        new DummyButton(this, _assets);

        _glowingTile = new Image(_assets.getTextures().getGlowingTile());
        _glowingTile.setVisible(false);
        _glowingTile.setTouchable(Touchable.disabled);
        this.addActor(_glowingTile);

        _chessTable = new Table();
        this.add(_chessTable).expand().fill();
        setChessActor(chessActor);

        _greenTile = new Table();
        _greenTile.setBackground(new TextureRegionDrawable(_assets.getTextures().getGreenTile()));
        _greenTile.setFillParent(true);
        _greenTile.getColor().a = 0;
        this.addActor(_greenTile);

        _redTile = new Table();
        _redTile.setBackground(new TextureRegionDrawable(_assets.getTextures().getRedTile()));
        _redTile.setFillParent(true);
        _redTile.setVisible(false);
        this.addActor(_redTile);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _assets.getFonts().getBlackNormal1Green();
        _percentLabel = new Label("", labelStyle);
        _percentLabel.setTouchable(Touchable.disabled);
        _greenTile.add(_percentLabel);

        _battleTable = new Table();
        _battleTable.setFillParent(true);
        this.addActor(_battleTable);
    }

    public void setChessActor(ChessActor chessActor){
        _chessTable.clear();
        _chessTable.add(chessActor).size(55);
    }

    public void setSelected(boolean selected){
        if(_selected != selected){
            _selected = selected;
            if(_selected){
                _glowingTile.setSize(130, 135);
                _glowingTile.setPosition(
                        Positions.centerX(this.getWidth(), _glowingTile.getWidth()) + 2,
                        Positions.centerY(this.getHeight(), _glowingTile.getHeight()) + 1);
                _glowingTile.addAction(sequence(fadeOut(0f), fadeIn(0.25f)));
                _glowingTile.setVisible(true);
            }
            else{
                _glowingTile.addAction(sequence(fadeOut(0.25f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _glowingTile.setSize(0, 0);
                        return true;
                    }
                }));
            }
        }
    }

    public void showPercent(int percent, int leftTopRightBottom){
        if(percent != -1){
            _percentLabel.setText(percent + "%");
        }
        else{
            _percentLabel.setText("");
        }

        _greenTile.addAction(fadeIn(0.3f));
        hideAllArrows();
        if(leftTopRightBottom == 0) {
            _arrowLeft.setVisible(true);
        }
        else if(leftTopRightBottom == 1){
            _arrowUp.setVisible(true);
        }
        else if(leftTopRightBottom == 2){
            _arrowRight.setVisible(true);
        }
        else if(leftTopRightBottom == 3){
            _arrowDown.setVisible(true);
        }
    }

    public void hidePercent(){
        _greenTile.addAction(fadeOut(0.1f));
        hideAllArrows();
    }

    private void hideAllArrows(){
        if(_arrowLeft != null) _arrowLeft.setVisible(false);
        if(_arrowRight != null) _arrowRight.setVisible(false);
        if(_arrowUp != null) _arrowUp.setVisible(false);
        if(_arrowDown != null) _arrowDown.setVisible(false);
    }

    public void showCanMoveTo(){
        _redTile.setVisible(true);
    }

    public void hideCanMoveTo(){
        _redTile.setVisible(false);
    }

    public void showBattle(){
        Image battleCloud = new Image(_assets.getTextures().getBattleCloud());
        _battleTable.add(battleCloud).size(65, 65);
        battleCloud.setOrigin(Align.center);
        battleCloud.addAction(forever(sequence(Actions.rotateBy(-5, 0.1f), Actions.rotateBy(5, 0.1f))));

        Image battleEffect = new Image(_assets.getTextures().getBattleEffect());
        battleEffect.setFillParent(true);
        battleEffect.setOrigin(Align.center);
        _battleTable.addActor(battleEffect);
        battleEffect.addAction(forever(sequence(Actions.scaleTo(1.1f, 1.1f, 0.3f), Actions.scaleTo(1f, 1f, 0.3f))));
    }

    public void hideBattle(){
        _battleTable.clear();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(!_initialized){
            _initialized = true;
            initializeArrows();
        }

    }

    private void initializeArrows(){
        float width = 17;
        float height = 20;

        Vector2 coords = Positions.actorLocalToStageCoord(this);
        _arrowLeft = new Image(_assets.getTextures().getArrowRight());
        _arrowLeft.setSize(width, height);
        _arrowLeft.setTouchable(Touchable.disabled);
        _arrowLeft.setPosition(coords.x + Positions.centerX(this.getWidth(), _arrowLeft.getWidth()) - this.getWidth()/2 + _arrowLeft.getWidth()/ 2 ,
                coords.y + Positions.centerY(this.getHeight(), _arrowLeft.getHeight()));
        _arrowLeft.setVisible(false);
        _arrowLeft.addAction(forever(sequence(Actions.moveBy(-3, 0, 0.3f), Actions.moveBy(3, 0, 0.3f))));
        this.getStage().addActor(_arrowLeft);

        _arrowRight = new Image(_assets.getTextures().getArrowLeft());
        _arrowRight.setSize(width, height);
        _arrowRight.setTouchable(Touchable.disabled);
        _arrowRight.setPosition(coords.x + Positions.centerX(this.getWidth(), _arrowRight.getWidth()) + this.getWidth()/2 - _arrowRight.getWidth()/ 2 ,
                coords.y + Positions.centerY(this.getHeight(), _arrowRight.getHeight()));
        _arrowRight.setVisible(false);
        _arrowRight.addAction(forever(sequence(Actions.moveBy(3, 0, 0.3f), Actions.moveBy(-3, 0, 0.3f))));
        this.getStage().addActor(_arrowRight);

        _arrowUp = new Image(_assets.getTextures().getArrowDown());
        _arrowUp.setSize(width, height);
        _arrowUp.setTouchable(Touchable.disabled);
        _arrowUp.setPosition(coords.x + Positions.centerX(this.getWidth(), _arrowUp.getWidth()) ,
                coords.y + Positions.centerY(this.getHeight(), _arrowUp.getHeight()) + this.getHeight()/2 - _arrowUp.getHeight()/ 2);
        _arrowUp.setVisible(false);
        _arrowUp.addAction(forever(sequence(Actions.moveBy(0, 3, 0.3f), Actions.moveBy(0, -3, 0.3f))));
        this.getStage().addActor(_arrowUp);

        _arrowDown = new Image(_assets.getTextures().getArrowUp());
        _arrowDown.setSize(width, height);
        _arrowDown.setTouchable(Touchable.disabled);
        _arrowDown.setPosition(coords.x + Positions.centerX(this.getWidth(), _arrowDown.getWidth()) ,
                coords.y + Positions.centerY(this.getHeight(), _arrowDown.getHeight()) - this.getHeight()/2 + _arrowDown.getHeight() / 2);
        _arrowDown.setVisible(false);
        _arrowDown.addAction(forever(sequence(Actions.moveBy(0, -3, 0.3f), Actions.moveBy(0, 3, 0.3f))));
        this.getStage().addActor(_arrowDown);


    }


}
