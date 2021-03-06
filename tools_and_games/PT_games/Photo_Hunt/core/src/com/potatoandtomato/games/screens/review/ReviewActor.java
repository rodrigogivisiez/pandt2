package com.potatoandtomato.games.screens.review;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Patches;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 9/4/2016.
 */
public class ReviewActor extends Table {

    private Table _this;
    private Services services;
    private MyAssets assets;
    private Label skipLabel, goToLabel, deleteLabel;
    private TextField goToTextField;
    private Image blockingReviewImage;

    public ReviewActor(Services services) {
        _this = this;
        this.services = services;
        this.assets = services.getAssets();
        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label.LabelStyle labelStyleBig = new Label.LabelStyle(
                        assets.getFonts().get(Fonts.FontId.ENCHANTED_MAX_REGULAR_B_FFFFFF_563500_4), Color.valueOf("ffe9c0"));

                TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
                textFieldStyle.font = assets.getFonts().get(Fonts.FontId.ENCHANTED_XXL_REGULAR);
                textFieldStyle.fontColor = Color.BLACK;
                textFieldStyle.background = new NinePatchDrawable(assets.getPatches().get(Patches.Name.WHITE_ROUNDED_BG));
                textFieldStyle.cursor = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CURSOR_BLACK));
                textFieldStyle.cursor.setMinWidth(2);

                goToTextField = new TextField("", textFieldStyle);
                goToLabel = new Label("Go", labelStyleBig);

                skipLabel = new Label("Skip", labelStyleBig);


                deleteLabel = new Label("Delete", labelStyleBig);

                blockingReviewImage = new Image(assets.getTextures().get(Textures.Name.TRANS_BLACK_BG));
                blockingReviewImage.setFillParent(true);
                blockingReviewImage.setVisible(false);

                _this.add(goToTextField).size(100, 30).padRight(20).padLeft(80);
                _this.add(goToLabel).padRight(40);

                _this.add(skipLabel);
                _this.add(deleteLabel).padLeft(20);

                _this.addActor(blockingReviewImage);
            }
        });


    }

    public void refreshDesign(final GameModel gameModel){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                goToTextField.setText(String.valueOf(gameModel.getImageDetails().getIndex()));
                deleteLabel.setText("Delete");
            }
        });
    }

    public void updateBlockingReview(final GameModel gameModel){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(gameModel.getGameState() == GameState.BlockingReview){
                    blockingReviewImage.setVisible(true);
                }
                else{
                    blockingReviewImage.setVisible(false);
                }
            }
        });
    }

    public Label getSkipLabel() {
        return skipLabel;
    }

    public Label getGoToLabel() {
        return goToLabel;
    }

    public Label getDeleteLabel() {
        return deleteLabel;
    }

    public TextField getGoToTextField() {
        return goToTextField;
    }
}
