package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.helpers.controls.WebImage;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.models.Services;
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

    public CreateGameScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public BtnEggDownward getCreateButton() {
        return _createButton;
    }


    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.createGameTitle(), false, _textures, _fonts, _screen);
        _root.align(Align.topLeft);

        //left game list START
        _gameList = new Table();
        _gameList.setBackground(new NinePatchDrawable(_textures.getIrregularBg()));
        _gameList.align(Align.top);
        _gameList.padRight(10).padTop(10);
        //left game list END

        //right side game details START
        _gameDetailsParent = new Table();
        _gameDetailsParent.setBackground(new NinePatchDrawable(_textures.getIrregularBg()));
        _gameDetailsParent.align(Align.topLeft);
        _gameDetailsParent.padRight(15).padLeft(10).padTop(10).padBottom(15);
        _gameDetails = new Table();
        _gameDetailsScroll = new ScrollPane(_gameDetails);
        _gameDetailsScroll.setScrollingDisabled(true, false);
        _gameDetailsParent.add(_gameDetailsScroll).expand().fill();

        Image pointLeftImage = new Image(_textures.getPointLeftIcon());
        Vector2 sizes = Sizes.resize(100, _textures.getPointLeftIcon());
        Label pickAGameLabel = new Label("Pick a game!", new Label.LabelStyle(_fonts.getPizzaFont(30, Color.WHITE, 0, Color.BLACK, 3, Color.GRAY), Color.WHITE));
        pointLeftImage.addAction(forever(sequence(moveBy(-10, 0, 1f), moveBy(10, 0, 1f))));
        _gameDetails.add(pointLeftImage).size(sizes.x, sizes.y);
        _gameDetails.row();
        _gameDetails.add(pickAGameLabel);

        _createButton = new BtnEggDownward(_textures, _fonts);
        _createButton.setText(_texts.create());
        _createButton.setPosition(60, -80);
        _createButton.setVisible(false);
        _gameDetailsParent.addActor(_createButton);

        //right side game details END

        _root.add(_gameList).width(120).expandY().fillY().padTop(25).padBottom(100).padLeft(-5).padRight(20);
        _root.add(_gameDetailsParent).width(230).expandY().fillY().padTop(25).padBottom(100).padRight(-5);

    }

    public Actor populateGame(Game game){

        Actor gameIcon = game != null ? new WebImage(game.getIconUrl(), _textures, _services.getDownloader()) : new Image(_textures.getComingSoon());
        _gameList.add(gameIcon).size(90).right().expandX().fillX().padBottom(7);
        _gameList.row();
        return gameIcon;

    }

    public void showGameDetails(final Game game){



        _gameDetailsParent.addAction(sequence(moveBy( 260, 0, 1f, Interpolation.bounceOut), new Action() {
                    @Override
                    public boolean act(float delta) {
                        changeGameDetails(game);
                        return true;
                    }
                }
        ));

    }

    public void changeGameDetails(Game game){
        _gameDetails.clear();


        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = _fonts.getPizzaFont(17, Color.valueOf("976b2d"), 1, Color.WHITE, 0, Color.GRAY);

        Label.LabelStyle contentStyle1 = new Label.LabelStyle();
        contentStyle1.font = _fonts.getNormal(14, Color.WHITE, 1, Color.GRAY, 1, Color.GRAY);

        Label.LabelStyle contentStyle2 = new Label.LabelStyle();
        contentStyle2.font = _fonts.getNormal(12, Color.WHITE, 1, Color.GRAY, 0, Color.BLACK);

        WebImage gameLogo = new WebImage(game.getIconUrl(), _textures, _services.getDownloader());
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
        screenShotsTable.align(Align.left);
        ScrollPane screenShotsScroll = new ScrollPane(screenShotsTable);
        screenShotsScroll.setScrollingDisabled(false, true);
        screenShotsTableParent.add(screenShotsScroll).expand().fill();

        if(game.getScreenShots() != null){
            for(String ssUrl : game.getScreenShots()){
                WebImage screenShotImage = new WebImage(ssUrl, _textures, _services.getDownloader());
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
        _createButton.setVisible(true);
    }

}
