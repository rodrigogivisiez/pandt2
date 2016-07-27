package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.services.Coins;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 10/12/2015.
 */
public class TopBar {

    private Table root;
    private String title;
    private boolean noPreviousScene;
    private Assets assets;
    private Coins coins;
    private Table topBarTable;
    private float barHeight = 70;
    private Image iconImg;
    private Label titleLabel;
    private PTScreen screen;
    private TopBarCoinControl topBarCoinControl;


    public TopBar(Table root, String title, boolean noPreviousScene,
                  Assets assets, PTScreen screen, Coins coins) {
        this.root = root;
        this.title = title;
        this.noPreviousScene = noPreviousScene;
        this.assets = assets;
        this.screen = screen;
        this.coins = coins;
        setTopBar();
        setIconListener();
    }

    public void setTopBar(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                topBarTable = new Table();
                topBarTable.setWidth(Positions.getWidth());
                topBarTable.setHeight(barHeight);
                topBarTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.TOP_BAR_BG)));
                topBarTable.setPosition(0, Positions.getHeight() - barHeight);

                TextureRegion iconRegion = noPreviousScene ? assets.getTextures().get(Textures.Name.QUIT_ICON) : assets.getTextures().get(Textures.Name.BACK_ICON);
                Vector2 iconSize = Sizes.resize(45, iconRegion);
                iconImg = new Image(iconRegion);
                iconImg.setSize(iconSize.x, iconSize.y);
                iconImg.setPosition(76f / 2 - iconSize.x / 2, barHeight / 2 - iconSize.y / 2);

                Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
                titleLabelStyle.font = assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_B_000000_fed271_3);
                titleLabel = new Label(title, titleLabelStyle);

                topBarCoinControl = coins.getNewTopBarCoinControl(false);

                topBarTable.addActor(iconImg);
                topBarTable.add(titleLabel).expand().fill().padLeft(90);
                topBarTable.add(topBarCoinControl).padBottom(5).padRight(5);

                root.padTop(barHeight);
                root.addActor(topBarTable);
            }
        });
    }

    public void setDarkTheme(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                topBarTable.clearChildren();

                iconImg.setDrawable(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.BACK_ICON_DARK)));

                Image darkImage = new Image(assets.getTextures().get(Textures.Name.LESS_TRANS_BLACK_BG));
                darkImage.setFillParent(true);

                Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
                titleLabelStyle.font = assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_B_000000_ffffff_3);
                titleLabel.setStyle(titleLabelStyle);

                topBarCoinControl = coins.getNewTopBarCoinControl(true);

                topBarTable.add(titleLabel).expand().fill().padLeft(90);
                topBarTable.addActor(darkImage);
                topBarTable.addActor(iconImg);
                topBarTable.add(topBarCoinControl).padBottom(5).padRight(5);
            }
        });
    }

    public void setIconListener(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                iconImg.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        screen.back();
                    }
                });
            }
        });
    }

    public void onShow(){
        topBarCoinControl.setShown(true);
    }

    public void onHide(){
        topBarCoinControl.setShown(false);
    }

    public TopBarCoinControl getTopBarCoinControl() {
        return topBarCoinControl;
    }
}
