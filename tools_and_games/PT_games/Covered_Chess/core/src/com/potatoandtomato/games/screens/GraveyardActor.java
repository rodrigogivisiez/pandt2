package com.potatoandtomato.games.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Patches;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.services.Texts;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class GraveyardActor extends Table {

    private Table _yellowGraveTable, _redGraveTable;
    private Table _yellowTotalRootTable, _redTotalRootTable;
    private Label _yellowTotalLabel, _redTotalLabel, _turnLabel, _turnCountLabel, _yellowPlayerLabel, _redPlayerLabel;
    private Label _yellowTimer, _redTimer;
    private Label _graveLabel;
    private MyAssets _assets;
    private Texts _texts;
    private GameCoordinator _gameCoordinator;
    private Image _pointLeftImage, _pointRightImage;
    private Container _turnCountContainer;

    public GraveyardActor(GameCoordinator gameCoordinator, Texts texts, MyAssets assets) {
        this._assets = assets;
        this._texts = texts;
        this._gameCoordinator = gameCoordinator;

        populate();
    }

    public void populate(){
        new DummyButton(this, _assets);

        Label.LabelStyle labelTotalStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR), Color.BLACK);
        Label.LabelStyle labelSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR), null);
        Label.LabelStyle labelXSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BlACKCONDENSEDITALIC), null);
        /////////////////////////
        //yellow total label
        ////////////////////////
        _yellowTotalRootTable = new Table();
        _yellowTotalRootTable.padLeft(10).padRight(10);
        _yellowTotalRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));

        Table yellowTotalTable = new Table();
        yellowTotalTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.YELLOW_CHESS_TOTAL)));
        _yellowTotalLabel = new Label("", labelTotalStyle);
        yellowTotalTable.add(_yellowTotalLabel).padLeft(1).padBottom(2);

        Table yellowPlayerTable = new Table();
        _yellowPlayerLabel = new Label(_gameCoordinator.getMyUniqueIndex() == 0 ? _texts.you() : _texts.enemy(), labelSmallStyle);
        _yellowPlayerLabel.setAlignment(Align.center);

        _yellowTimer = new Label("15:00", labelXSmallStyle);
        _yellowTimer.setAlignment(Align.center);
        yellowPlayerTable.add(_yellowPlayerLabel).expandX().fillX();
        yellowPlayerTable.row();
        yellowPlayerTable.add(_yellowTimer).expandX().fillX();

        _yellowTotalRootTable.add(yellowTotalTable);
        _yellowTotalRootTable.add(yellowPlayerTable).expandX().fillX().padLeft(10);

        /////////////////////////
        //red total label
        ////////////////////////
        _redTotalRootTable = new Table();
        _redTotalRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));
        _redTotalRootTable.padLeft(10).padRight(10);

        Table redTotalTable = new Table();
        redTotalTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.RED_CHESS_TOTAL)));
        _redTotalLabel = new Label("", labelTotalStyle);
        redTotalTable.add(_redTotalLabel).padLeft(1).padBottom(2);

        Table redPlayerTable = new Table();
        _redPlayerLabel = new Label(_gameCoordinator.getMyUniqueIndex() == 1 ? _texts.you() : _texts.enemy(), labelSmallStyle);
        _redPlayerLabel.setAlignment(Align.center);

        _redTimer = new Label("15:00", labelXSmallStyle);
        _redTimer.setAlignment(Align.center);
        redPlayerTable.add(_redPlayerLabel).expandX().fillX();
        redPlayerTable.row();
        redPlayerTable.add(_redTimer).expandX().fillX();

        _redTotalRootTable.add(redPlayerTable).expandX().fillX().padRight(10);
        _redTotalRootTable.add(redTotalTable);

        /////////////////////////
        //turn icons
        ////////////////////////
        _turnLabel = new Label("", new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XXL_HEAVY), Color.valueOf("56380a")));
        _turnLabel.setAlignment(Align.center);

        _turnCountLabel = new Label("", new Label.LabelStyle(
                                            _assets.getFonts().get(Fonts.FontId.HELVETICA_XXL_BlACKCONDENSEDITALIC_B_ffffff_56380a_1), null));
        _turnCountLabel.setOrigin(Align.center);
        _turnCountLabel.setSize(30, 20);
        _turnCountLabel.setPosition(0, 0);
        _turnCountContainer = new Container(_turnCountLabel);
        _turnCountContainer.setTransform(true);
        _turnCountContainer.setOrigin(Align.center);

        /////////////////////////
        //pointing icons
        ////////////////////////
        _pointLeftImage = new Image(_assets.getTextures().get(Textures.Name.POINT_LEFT_ICON));
        _pointRightImage = new Image(_assets.getTextures().get(Textures.Name.POINT_RIGHT_ICON));

        Table turnTable = new Table();
        turnTable.add(_turnLabel).expandX().fillX().colspan(3);
        turnTable.row();
        turnTable.add(_pointLeftImage).expandX().right().space(0, 10, 0, 10).height(20);
        turnTable.add(_turnCountContainer).height(20);
        turnTable.add(_pointRightImage).expandX().left().space(0, 10, 0, 10).height(20);

        ///////////////////////////
        //top info
        //////////////////////////
        Table topInfoTable = new Table();
        topInfoTable.add(_yellowTotalRootTable).padLeft(10).expandY().fillY().padTop(5).padBottom(2);
        topInfoTable.add(turnTable).expand().fill();
        topInfoTable.add(_redTotalRootTable).padRight(10).expandY().fillY().padTop(5).padBottom(2);
        topInfoTable.pad(2);
        topInfoTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));

        /////////////////////////
        //yellow grave table
        ////////////////////////
        _yellowGraveTable = new Table();
        _yellowGraveTable.align(Align.top);
        _yellowGraveTable.pad(10);
        _yellowGraveTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GRAVE_BG)));

        /////////////////////////
        //red grave table
        ////////////////////////
        _redGraveTable = new Table();
        _redGraveTable.align(Align.top);
        _redGraveTable.pad(10);
        _redGraveTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GRAVE_BG)));

        Label.LabelStyle graveLabelStyle = new Label.LabelStyle();
        graveLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXXL_REGULAR_B_000000_ffffff_3);
        _graveLabel = new Label(_texts.graveYard(), graveLabelStyle);
        _graveLabel.setAlignment(Align.center);

        Table graveTable = new Table();
        graveTable.pad(10);
        graveTable.add(_graveLabel).expandX().fillX().colspan(2);
        graveTable.row();
        graveTable.add(_yellowGraveTable).expand().fill().space(3);
        graveTable.add(_redGraveTable).expand().fill().space(3);
        graveTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));

        /////////////////////////
        //population
        ////////////////////////

        this.add(graveTable).expand().fill();
        this.row();
        this.add(topInfoTable).expandX().fillX();

        this.setSize(_gameCoordinator.getGameWidth(), 400);
        this.setPosition(0, _gameCoordinator.getGameHeight() - 50);
    }

    public void modelChanged(GraveModel graveModel){
        _redTotalLabel.setText(String .valueOf(graveModel.getLeftChessCountByColor(ChessColor.RED)));
        _yellowTotalLabel.setText(String .valueOf(graveModel.getLeftChessCountByColor(ChessColor.YELLOW)));

        _yellowGraveTable.clear();
        _redGraveTable.clear();

        for(ChessType chessType : graveModel.getGraveChesses()){
            addToGraveyard(chessType, chessType.name().startsWith("RED") ? _redGraveTable : _yellowGraveTable);
        }
    }

    public void setCountDownTime(ChessColor chessColor, String time){
        Label labelTime = chessColor == ChessColor.YELLOW ? _yellowTimer : _redTimer;
        labelTime.setText(time);
    }

    public void onBoardModelChanged(BoardModel boardModel){
        if(_gameCoordinator.getMyUniqueIndex() == boardModel.getCurrentTurnIndex()){
            _turnLabel.setText(_texts.yourTurn());
        }
        else if(boardModel.getCurrentTurnIndex() != -1){
            _turnLabel.setText(_texts.enemyTurn());
        }

        _yellowTotalRootTable.setBackground(new TextureRegionDrawable(boardModel.getCurrentTurnIndex() == 1 ?_assets.getTextures().get(Textures.Name.EMPTY) : _assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));
        //_yellowPlayerLabel.setVisible(boardModel.getCurrentTurnIndex() == 0);
        _pointLeftImage.getColor().a = boardModel.getCurrentTurnIndex() == 0 ? 1 : 0.2f;
        _redTotalRootTable.setBackground(new TextureRegionDrawable(boardModel.getCurrentTurnIndex() == 0 ?_assets.getTextures().get(Textures.Name.EMPTY) : _assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));
       // _redPlayerLabel.setVisible(boardModel.getCurrentTurnIndex() == 1);
        _pointRightImage.getColor().a = boardModel.getCurrentTurnIndex() == 1 ? 1 : 0.2f;

        if(!_turnCountLabel.getText().toString().equals(String.format("%02d", boardModel.getAccTurnCount()))){
            _turnCountContainer.getColor().a = 0f;
            _turnCountContainer.addAction(sequence(Actions.scaleTo(1.5f, 1.5f), parallel(fadeIn(0.3f), Actions.scaleTo(1, 1, 0.3f))));
            _turnCountLabel.setText(String.format("%02d", boardModel.getAccTurnCount()));
            if(boardModel.isSuddenDeath()){
                _turnCountLabel.getStyle().fontColor = Color.valueOf("ff0d0d");
            }
        }
    }

    public void addToGraveyard(ChessType chessType, Table grave){
        Image img = new Image(_assets.getTextures().getAnimalByType(chessType));
        if(grave.getChildren().size % 3 == 0 && grave.getChildren().size !=0) {
            grave.row();
        }
        grave.add(img).uniform().space(5);
    }

    public void expand(){
        this.addAction(Actions.moveBy(0, -(400 - 55), 0.5f));
    }

    public void hide(){
        this.addAction(Actions.moveBy(0, (400 - 55), 0.5f));
    }

    public Label getGraveLabel() {
        return _graveLabel;
    }
}