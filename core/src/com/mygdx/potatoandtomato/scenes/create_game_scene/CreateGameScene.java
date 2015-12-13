package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.helpers.controls.WebImage;
import com.mygdx.potatoandtomato.models.Assets;
import com.mygdx.potatoandtomato.models.Game;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class CreateGameScene extends SceneAbstract {

    Table _gameList;
    Table _gameDetailsParent, _gameDetails;
    ScrollPane _gameDetailsScroll;
    BtnEggDownward _createButton;
    boolean _first = true;

    public CreateGameScene(Assets assets) {
        super(assets);
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.createGameTitle(), false, _textures, _fonts);
        _root.align(Align.topLeft);

        //left game list START
        _gameList = new Table();
        _gameList.setBackground(new NinePatchDrawable(_textures.getIrregularBg()));
        _gameList.align(Align.top);
        //left game list END

        //right side game details START
        _gameDetailsParent = new Table();
        _gameDetailsParent.setBackground(new NinePatchDrawable(_textures.getIrregularBg()));
        _gameDetailsParent.align(Align.topLeft);
        _gameDetailsParent.padRight(15);
        _gameDetails = new Table();
        _gameDetailsScroll = new ScrollPane(_gameDetails);
        _gameDetailsScroll.setScrollingDisabled(true, false);
        _gameDetailsParent.add(_gameDetailsScroll).expand().fill();
        _gameDetailsParent.setVisible(false);

        _createButton = new BtnEggDownward(_textures, _fonts);
        _createButton.setText(_texts.create());
        _createButton.setPosition(60, -80);
        _gameDetailsParent.addActor(_createButton);

        //right side game details END

        _root.add(_gameList).width(120).expandY().fillY().padTop(25).padBottom(100).padLeft(-5).padRight(20);
        _root.add(_gameDetailsParent).width(230).expandY().fillY().padTop(25).padBottom(100).padRight(-5);

    }

    public Actor populateGame(Game game){

        Actor gameIcon = game != null ? new WebImage(game.getIconUrl(), _textures) : new Image(_textures.getComingSoon());
        _gameList.add(gameIcon).size(90).right().expandX().fillX().padBottom(7);
        _gameList.row();
        return gameIcon;

    }

    public void showGameDetails(final Game game){

        if(_first) {
            _gameDetailsParent.setVisible(true);
        }

        _gameDetailsParent.addAction(sequence(moveBy(_first ? 0 : 260, 0, _first ? 0 : 1f, Interpolation.bounceOut), new Action() {
                    @Override
                    public boolean act(float delta) {
                        changeGameDetails(game);
                        return true;
                    }
                }
        ));

        _first = false;
    }

    public void changeGameDetails(Game game){
        _gameDetails.clear();


        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = _fonts.getArialBold(17, Color.valueOf("976b2d"), 0, Color.BLACK, 0, Color.GRAY);

        Label.LabelStyle contentStyle1 = new Label.LabelStyle();
        contentStyle1.font = _fonts.getArial(14, Color.WHITE, 0, Color.BLACK, 0, Color.BLACK);

        Label.LabelStyle contentStyle2 = new Label.LabelStyle();
        contentStyle2.font = _fonts.getArial(11, Color.WHITE, 0, Color.BLACK, 0, Color.BLACK);

        WebImage gameLogo = new WebImage(game.getIconUrl(), _textures);
        Label detailsTitleLabel = new Label(_texts.details(), titleStyle);

        Table detailsTable = new Table();
        detailsTable.align(Align.left);
        detailsTable.setBackground(new NinePatchDrawable(_textures.getBlackRoundedBg()));
        Label nameLabel = new Label(String.format("- %s (v%s)", game.getName(), game.getVersion()), contentStyle1);
        Label playersLabel = new Label(String.format("- From %s to %s players", game.getMinPlayers(), game.getMaxPlayers()), contentStyle1);
        detailsTable.add(nameLabel).left();
        detailsTable.row();
        detailsTable.add(playersLabel).left();

        Label screenShotsTitleLabel = new Label(_texts.screenShots(), titleStyle);
        Table screenShotsTableParent = new Table();
        screenShotsTableParent.align(Align.left);
        screenShotsTableParent.setBackground(new NinePatchDrawable(_textures.getBlackRoundedBg()));
        Table screenShotsTable = new Table();
        ScrollPane screenShotsScroll = new ScrollPane(screenShotsTable);
        screenShotsScroll.setScrollingDisabled(false, true);
        screenShotsTableParent.add(screenShotsScroll).expand().fill();

        if(game.getScreenShots() != null){
            for(String ssUrl : game.getScreenShots()){
                WebImage screenShotImage = new WebImage(ssUrl, _textures);
                screenShotsTable.add(screenShotImage).size(100).padRight(10);
            }
        }

        Label descriptionTitleLabel = new Label(_texts.description(), titleStyle);
        Table descriptionTable = new Table();
        descriptionTable.align(Align.left);
        descriptionTable.setBackground(new NinePatchDrawable(_textures.getBlackRoundedBg()));
        Label descriptionLabel = new Label(game.getDescription(), contentStyle2);
        descriptionLabel.setWrap(true);
        descriptionTable.add(descriptionLabel).fill().expand();

        _gameDetails.add(gameLogo).size(120);
        _gameDetails.row();
        _gameDetails.add(detailsTitleLabel).left().padTop(10);
        _gameDetails.row();
        _gameDetails.add(detailsTable).expandX().fillX();
        _gameDetails.row();
        _gameDetails.add(screenShotsTitleLabel).left().padTop(10);
        _gameDetails.row();
        _gameDetails.add(screenShotsTableParent).expandX().fillX();
        _gameDetails.row();
        _gameDetails.add(descriptionTitleLabel).left().padTop(10);
        _gameDetails.row();
        _gameDetails.add(descriptionTable).expandX().fillX();

        _gameDetails.invalidate();
        _gameDetailsScroll.setScrollPercentY(0);

    }

}
