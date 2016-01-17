package com.potatoandtomato.games.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.ChessType;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class ChessActor extends Table {

    private Assets _assets;
    private ChessType _type;
    private int _col, _row;
    private boolean _selected;

    public ChessActor(ChessType type, Assets assets, int col, int row, int chessWordWidth, int chessWordHeight) {

        this._assets = assets;
        this._type = type;
        this._col = col;
        this._row = row;

        if(chessWordHeight == 0) chessWordHeight = 20;
        if(chessWordWidth == 0) chessWordWidth = 28;

        getChessDesign(false, chessWordWidth, chessWordHeight);
    }

    public void setColRow(int col, int row){
        this._col = col;
        this._row = row;
    }

    public int getCol(){
        return _col;
    }

    public int getRow(){
        return _row;
    }



    public Table getChessDesign(boolean cloning, int chessWordWidth, int chessWordHeight){
        Table rootTable = this;
        if(cloning){
            rootTable = new Table();
        }

        rootTable.setBackground(new TextureRegionDrawable(_assets.getChess()));
        Image chessWord = new Image(getRegionByChessType(_type));
        rootTable.add(chessWord).size(chessWordWidth, chessWordHeight);

        Image chessSelected = new Image(_assets.getChessSelected());
        chessSelected.setSize(43, 43);
        chessSelected.setPosition(-2, -1);
        chessSelected.setName("chessSelected");
        chessSelected.setVisible(false);
        rootTable.addActor(chessSelected);

        Image dummyImage = new Image(_assets.getEmpty());
        dummyImage.setFillParent(true);
        rootTable.addActor(dummyImage);
        return rootTable;
    }

    public void setSelected(boolean selected){
        _selected = selected;
        if(selected){
            this.findActor("chessSelected").setVisible(true);
        }
        else{
            this.findActor("chessSelected").setVisible(false);
        }
    }

    public boolean isSelected() {
        return _selected;
    }

    public ChessType getType() {
        return _type;
    }

    private TextureRegion getRegionByChessType(ChessType chessType){
        switch (chessType){

            case RED_BING:
                return _assets.getRedBing();
            case RED_PAO:
                return _assets.getRedPao();
            case RED_CHE:
                return _assets.getRedChe();
            case RED_MA:
                return _assets.getRedMa();
            case RED_XIANG:
                return _assets.getRedXiang();
            case RED_SHI:
                return _assets.getRedShi();
            case RED_SHUAI:
                return _assets.getRedShuai();

            case BLACK_BING:
                return _assets.getBlackBing();
            case BLACK_PAO:
                return _assets.getBlackPao();
            case BLACK_CHE:
                return _assets.getBlackChe();
            case BLACK_MA:
                return _assets.getBlackMa();
            case BLACK_XIANG:
                return _assets.getBlackXiang();
            case BLACK_SHI:
                return _assets.getBlackShi();
            case BLACK_SHUAI:
                return _assets.getBlackShuai();
        }

        return null;
    }

    public Table clone(){
        Table clone = getChessDesign(true, 28 , 20);
        clone.setSize(this.getWidth(), this.getHeight());
        clone.findActor("chessSelected").setVisible(true);
        return clone;
    }


}
