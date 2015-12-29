package com.potatoandtomato.games.actors.chesses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.actors.chesses.enums.ChessColor;
import com.potatoandtomato.games.actors.chesses.enums.ChessType;
import com.potatoandtomato.games.helpers.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class ChessActor extends Group {

    private ChessColor _chessColor;
    private ChessType _chessType;
    private Table _root;
    private TextureRegion _animalImage;
    private Assets _assets;
    private Label _msgLabel;
    private boolean _selected;
    private Image _selectedBaseImage;
    private boolean _initialized;

    public ChessActor(Assets assets) {
        _assets = assets;
        _chessType = ChessType.UNKOWN;
        _root = new Table();
        _root.setFillParent(true);
        _msgLabel = new Label("", new Label.LabelStyle(_assets.getBlackBold1(), Color.BLACK));
        _root.add(_msgLabel);

        setChessColor(ChessColor.UNKNOWN);

        _selectedBaseImage = new Image(_assets.getPawnSelectedBase());
        _selectedBaseImage.setVisible(false);
        this.addActor(_selectedBaseImage);

        this.addActor(_root);

    }

    public void setChessColor(ChessColor chessColor){
        _chessColor = chessColor;
        if(_chessColor == ChessColor.UNKNOWN){
            _root.setBackground(new TextureRegionDrawable(_assets.getUnknownPawn()));
        }
        else if(_chessColor == ChessColor.RED){
            _root.setBackground(new TextureRegionDrawable(_assets.getRedPawn()));
        }
        else if(_chessColor == ChessColor.YELLOW){
            _root.setBackground(new TextureRegionDrawable(_assets.getYellowPawn()));
        }
    }


    public void setContent(String text){
        _msgLabel.setText(text);
        _msgLabel.addAction(sequence(fadeOut(0f), fadeIn(0.5f)));

    }

    public void setSelected(boolean _selected) {
        this._selected = _selected;
        _selectedBaseImage.setVisible(_selected);
        _initialized = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        super.draw(batch, parentAlpha);

        if(!_initialized){
            _selectedBaseImage.setWidth(this.getWidth() * 1.578f);
            _selectedBaseImage.setHeight(this.getHeight() * 1.578f);
            _selectedBaseImage.setPosition(this.getWidth()/2 - _selectedBaseImage.getWidth()/2,
                    this.getHeight()/2 - _selectedBaseImage.getHeight() /2 - (this.getHeight() * 5/100));
            _initialized = true;
        }

    }
}
