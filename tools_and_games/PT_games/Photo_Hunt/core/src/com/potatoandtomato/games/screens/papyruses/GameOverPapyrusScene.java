package com.potatoandtomato.games.screens.papyruses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.ColorUtils;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 25/5/2016.
 */
public class GameOverPapyrusScene extends PapyrusSceneAbstract {

    private Services services;
    private MyAssets assets;
    private Table root;
    private Table _this;
    private GameModel gameModel;
    private GameCoordinator gameCoordinator;

    public GameOverPapyrusScene(Services services, Table root, GameModel gameModel, GameCoordinator gameCoordinator) {
        this.services = services;
        this.root = root;
        this.assets = services.getAssets();
        this.gameModel = gameModel;
        this.gameCoordinator = gameCoordinator;
        _this = this;

        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.align(Align.left);

                Image gameOverSceneImage = new Image(assets.getTextures().get(Textures.Name.GAME_OVER_SCENE));

                Table leftTable = new Table();
                leftTable.setSize(gameOverSceneImage.getPrefWidth(), gameOverSceneImage.getPrefHeight());
                leftTable.addAction(forever(sequence(Actions.moveBy(-0.4f, -0.4f, 0.1f), Actions.moveBy(0.4f, 0.4f, 0.1f))));
                leftTable.add(gameOverSceneImage);

                Label gameOverLabel = new Label(services.getTexts().gameOverText(),
                            new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_XXXL_REGULAR_B_FFFFFF_0c0904_2), Color.valueOf("cf953d")));
                gameOverLabel.setAlignment(Align.center);

                Table clicksCountTable = new Table();
                clicksCountTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.GAME_OVER_TRANS_BG)));
                HashMap<String, Integer> userRecords = gameModel.getUserRecords();
                for(int i = 0; i < 8; i++){
                    Player player = gameCoordinator.getPlayerByUniqueIndex(i);
                    String userName = services.getTexts().noPlayer();;
                    int totalClick = 0;
                    if(!Strings.isEmpty(player.getUserId())){
                        userName = Strings.cutOff(player.getName(), 10);
                        if(userRecords.containsKey(player.getUserId())){
                            totalClick = userRecords.get(player.getUserId());
                        }
                    }

                    Image circleImage = new Image(assets.getTextures().get(Textures.Name.WHITE_CIRCLE));
                    circleImage.setColor(ColorUtils.getUserColorByIndex(i));

                    Label userNameLabel = new Label(userName,
                            new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_L_REGULAR), Color.BLACK));
                    userNameLabel.setAlignment(Align.left);

                    Label countLabel = new Label(String.valueOf(totalClick),
                            new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_XXL_REGULAR), Color.BLACK));

                    Table userClickCountTable = new Table();
                    userClickCountTable.align(Align.left);
                    userClickCountTable.add(circleImage).padRight(10).padTop(2).center();
                    userClickCountTable.add(userNameLabel).expandX().fillX();
                    userClickCountTable.add(countLabel);

                    clicksCountTable.add(userClickCountTable).padLeft(10).padRight(10).expand().fill();
                    if((i + 1) % 2 == 0 && i >= 0) clicksCountTable.row();
                }

                Table scoresTable = new Table();
                scoresTable.padLeft(10).padRight(10);
                scoresTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.GAME_OVER_TRANS_BG)));
                Label finalScoreLabel = new Label(services.getTexts().totalScores(),
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_XXXL_REGULAR), Color.BLACK));
                Label scoreNumberLabel =  new Label(String.format("%,d", gameModel.getScore().intValue()),
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_XXXL_REGULAR), Color.BLACK));
                scoreNumberLabel.setAlignment(Align.right);

                scoresTable.add(finalScoreLabel);
                scoresTable.add(scoreNumberLabel).expand().fill();

                Table rightTable = new Table();
                rightTable.align(Align.top);
                rightTable.padRight(40).padTop(5).padBottom(14);
                rightTable.add(gameOverLabel).expandX().fillX();
                rightTable.row();
                rightTable.add(clicksCountTable).expand().fill();
                rightTable.row();
                rightTable.add(scoresTable).expandX().fillX().padTop(8);

                _this.add(leftTable).padLeft(38).padRight(20);
                _this.add(rightTable).expand().fill();
                services.getSoundsWrapper().playMusic(Sounds.Name.GAME_OVER_MUSIC);
            }
        });
    }



    @Override
    public void dispose() {
        super.dispose();
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                services.getSoundsWrapper().stopMusic(Sounds.Name.GAME_OVER_MUSIC);
            }
        });
    }
}