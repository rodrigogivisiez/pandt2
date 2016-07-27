package com.mygdx.potatoandtomato.scenes.create_game_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.controls.WebImage;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.controls.AutoDisposeTable;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class CreateGameScene extends SceneAbstract {

    Table _gameList;
    Table _gameDetailsParent;
    AutoDisposeTable _gameDetails;
    ScrollPane _gameDetailsScroll;
    BtnEggDownward _createButton;
    Actor _firstGameTable;

    public CreateGameScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public BtnEggDownward getCreateButton() {
        return _createButton;
    }


    @Override
    public void populateRoot() {
        topBar = new TopBar(_root, _texts.createGameSceneTitle(), false, _assets, _screen, _services.getCoins());
        Table clippedRoot = new Table();
        clippedRoot.align(Align.topLeft);
        clippedRoot.setClip(true);
        clippedRoot.setPosition(0, 0);
        clippedRoot.setWidth(Positions.getWidth());
        clippedRoot.setHeight(Positions.getHeight() - _root.getPadTop());
        _root.addActor(clippedRoot);


        //left game list START
        _gameList = new Table();
        _gameList.align(Align.top);
        _gameList.padRight(10).padTop(10);
        //left game list END

        //right side game details START
        _gameDetailsParent = new Table();
        _gameDetailsParent.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_TALL)));
        _gameDetailsParent.align(Align.topLeft);
        _gameDetailsParent.padRight(75).padLeft(10).padTop(10).padBottom(15);
        _gameDetails = new AutoDisposeTable();
        _gameDetailsScroll = new ScrollPane(_gameDetails);
        _gameDetailsScroll.setScrollingDisabled(true, false);
        _gameDetailsParent.add(_gameDetailsScroll).expand().fill().padBottom(20);

        Image pointLeftImage = new Image(_assets.getTextures().get(Textures.Name.POINT_LEFT_ICON));
        Vector2 sizes = Sizes.resize(140, _assets.getTextures().get(Textures.Name.POINT_LEFT_ICON));
        Label.LabelStyle pickAGameLabelStyle = new Label.LabelStyle();
        pickAGameLabelStyle.fontColor = Color.valueOf("573801");
        pickAGameLabelStyle.font = _assets.getFonts().get(Fonts.FontId.HELVETICA_XL_CONDENSED_S_a05e00_1_1);
        Label pickAGameLabel = new Label(_texts.pickAGame(), pickAGameLabelStyle);
        pointLeftImage.addAction(forever(sequence(moveBy(-10, 0, 1f), moveBy(10, 0, 1f))));
        _gameDetails.add(pointLeftImage).size(sizes.x, sizes.y);
        _gameDetails.row();
        _gameDetails.add(pickAGameLabel).padTop(10);

        _createButton = new BtnEggDownward(_assets, _services.getSoundsPlayer());
        _createButton.setText(_texts.btnTextCreate());
        _createButton.setPosition(60, -80);
        _createButton.setVisible(false);
        _gameDetailsParent.addActor(_createButton);

        //right side game details END

        clippedRoot.add(_gameList).width(120).expandY().fillY().padTop(25).padBottom(100).padRight(20);
        clippedRoot.add(_gameDetailsParent).width(310).expandY().fillY().padTop(25).padBottom(100).padRight(-5);

    }

    public void populateGame(final Game game, final RunnableArgs<Actor> runnableArgs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final Table gameTable = new Table();
                final Table innerTable = new Table();
                final Actor gameIcon = new WebImage(game.getIconUrl(), _assets, _services.getBroadcaster(), _ptGame);

                innerTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_FAT)));

                innerTable.add(gameIcon).size(90).right().expandX().fillX().padLeft(150).padTop(5).padBottom(10).padRight(15);
                //innerTable.getColor().a = 0;
                innerTable.setSize(210, 110);
                //innerTable.setPosition(-200, 0);
                gameTable.addActor(innerTable);
                _gameList.add(gameTable).width(innerTable.getWidth()).height(innerTable.getHeight()).padLeft(-100).padBottom(7);
                _gameList.row();

               // innerTable.addAction(sequence(fadeIn(0f), moveBy(200, 0, 1f, Interpolation.bounceOut)));

                runnableArgs.run(gameIcon);

                if(_firstGameTable == null){
                    _firstGameTable = innerTable;
                }

            }
        });
    }

    public void showGameDetails(final Game game){
        Threadings.renderFor(2f);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _gameDetailsParent.clearActions();
                _gameDetailsParent.addAction(sequence(moveBy(260, 0, 1f, Interpolation.bounceOut), new RunnableAction() {
                            @Override
                            public void run() {
                                changeGameDetails(game);
                            }
                        }
                ));
            }
        });
    }

    public void changeGameDetails(final Game game){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _gameDetails.clear();
                _gameDetails.align(Align.top);

                Label.LabelStyle titleStyle = new Label.LabelStyle();
                titleStyle.fontColor = Color.valueOf("573801");
                titleStyle.font = _assets.getFonts().get(Fonts.FontId.HELVETICA_XL_CONDENSED_S_a05e00_1_1);

                Label.LabelStyle contentStyle1 = new Label.LabelStyle();
                contentStyle1.fontColor = Color.valueOf("fff0bb");
                contentStyle1.font = _assets.getFonts().get(Fonts.FontId.HELVETICA_M_BOLD);

                Label.LabelStyle contentStyle2 = new Label.LabelStyle();
                contentStyle2.fontColor = Color.valueOf("fff0bb");
                contentStyle2.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

                WebImage gameLogo = new WebImage(game.getIconUrl(), _assets, _services.getBroadcaster(), _ptGame);
                Label detailsTitleLabel = new Label(_texts.details(), titleStyle);

                Table detailsTable = new Table();
                detailsTable.align(Align.left);
                detailsTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
                detailsTable.pad(10);
                detailsTable.padRight(20);
                Label nameLabel = new Label(game.getName(), contentStyle1);
                Label playersLabel = new Label(String.format(_texts.xPlayers(), game.getMinPlayers(), game.getMaxPlayers()), contentStyle2);
                Label versionAndUpdatedLabel = new Label(String.format(_texts.version(), game.getVersion()) +
                        " (" + game.getLastUpdatedAgo() + ")", contentStyle2);
                Label gameSizeLabel = new Label(String.format(_texts.xMb(), game.getGameSizeInMb()), contentStyle2);
                detailsTable.add(nameLabel).left();
                detailsTable.row();
                detailsTable.add(playersLabel).left();
                detailsTable.row();
                detailsTable.add(versionAndUpdatedLabel).left();
                detailsTable.row();
                detailsTable.add(gameSizeLabel).left();

                Label descriptionTitleLabel = new Label(_texts.description(), titleStyle);
                Table descriptionTable = new Table();
                descriptionTable.align(Align.left);
                descriptionTable.pad(10);
                descriptionTable.padRight(20);
                Image descriptionTableBackground = new Image(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG));
                descriptionTableBackground.setFillParent(true);
                descriptionTable.addActor(descriptionTableBackground);
                Label descriptionLabel = new Label(game.getDescription(), contentStyle2);
                descriptionLabel.setWrap(true);
                descriptionTable.add(descriptionLabel).fill().expand();

                _gameDetails.padLeft(15).padBottom(25);
                _gameDetails.add(gameLogo).size(120).padRight(20);
                _gameDetails.row();
                _gameDetails.add(detailsTitleLabel).left().padTop(5);
                _gameDetails.row();
                _gameDetails.add(detailsTable).expandX().fillX();
                _gameDetails.row();
                _gameDetails.add(descriptionTitleLabel).left().padTop(5);
                _gameDetails.row();
                _gameDetails.add(descriptionTable).expandX().fillX();

                _gameDetails.invalidate();
                _gameDetailsScroll.setScrollPercentY(0);
                _createButton.setVisible(true);
            }
        });
    }


    public Actor getFirstGameTable() {
        return _firstGameTable;
    }
}
