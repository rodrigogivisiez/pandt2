package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.controls.DummyButton;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.enums.ProductAction;
import com.mygdx.potatoandtomato.enums.ShopProducts;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.utils.DateTimes;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopScene extends SceneAbstract {

    private Table purseImagesTable, productsTable, watchAdsItemTable, growthRateTable;
    private Label purseCountLabel;
    private Actor watchVideoAdsButton;
    private Actor retrieveCoinsButton;

    public ShopScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        TopBar topBar = new TopBar(_root, _services.getTexts().shopTitle(), false, _assets, _screen, _services.getCoins());
        topBar.setDarkTheme();

        _root.align(Align.top);
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));

        ////////////////////////////////////
        //Background things
        /////////////////////////////////////

        Image floorImage = new Image(_assets.getTextures().get(Textures.Name.FLOOR_BG));

        Image arcadeBgImage = new Image(_assets.getTextures().get(Textures.Name.ARCADE_BG));
        arcadeBgImage.setPosition(0, 405);


        Image arcadeWorldImage = new Image(_assets.getTextures().get(Textures.Name.SHOP_ARCADE_WORLD));
        arcadeWorldImage.getColor().a = 0.5f;

        arcadeWorldImage.addAction(forever(sequence(fadeIn(1f), alpha(0.5f, 1f))));

        //////////////////////////////////////////////////
        //Mascots things
        ///////////////////////////////////////////////
        Table mascotsTable = new Table();
        Image tomatoImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_SUNGLASS));
        tomatoImage.setOrigin(Align.bottom);
        Image potatoImage = new Image(_assets.getTextures().get(Textures.Name.POTATO_SUNGLASS));
        potatoImage.setOrigin(Align.bottom);
        mascotsTable.add(potatoImage).size(potatoImage.getPrefWidth(), potatoImage.getPrefHeight()).padRight(8);
        mascotsTable.add(tomatoImage).size(tomatoImage.getPrefWidth(), tomatoImage.getPrefHeight());

        tomatoImage.addAction(forever(sequence(Actions.rotateBy(-1f, 0.5f), Actions.rotateBy(1f, 0.5f))));
        potatoImage.addAction(forever(sequence(Actions.rotateBy(-0.5f, 0.4f), Actions.rotateBy(0.5f, 0.4f))));

        final Animator potatoFlipCoinAnimator = new Animator(0.025f, _assets.getAnimations().get(Animations.Name.POTATO_FLIP_COIN), false);
        potatoFlipCoinAnimator.setPosition(83, 358);
        potatoFlipCoinAnimator.setPaused(true);
        potatoFlipCoinAnimator.addAction(forever(sequence(Actions.rotateBy(-1f, 0.4f), Actions.rotateBy(1f, 0.4f))));

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Threadings.sleep(MathUtils.random(6, 20) * 1000);
                    if(disposed) break;
                    potatoFlipCoinAnimator.setPaused(false);
                    potatoFlipCoinAnimator.replay();
                    _services.getSoundsPlayer().playSoundEffect(Sounds.Name.COIN_FLIP);
                }
            }
        });

        Image tomatoLeftHandImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_SUNGLASS_LEFT_HAND));
        tomatoLeftHandImage.setPosition(173, 338);
        tomatoLeftHandImage.setOrigin(Align.center);

        Image tomatoRightHandImage = new Image(_assets.getTextures().get(Textures.Name.TOMATO_SUNGLASS_RIGHT_HAND));
        tomatoRightHandImage.setOrigin(Align.bottom);
        tomatoRightHandImage.setPosition(241, 341);

        tomatoLeftHandImage.addAction(forever(sequence(parallel(Actions.moveBy(-1f, 2f, 1f), Actions.rotateBy(2f, 1f)),
                parallel(Actions.moveBy(1f, -2f, 0.3f), Actions.rotateBy(-2f, 0.3f)))));
        tomatoRightHandImage.addAction(forever(sequence(Actions.rotateBy(-2f, 0.4f), Actions.rotateBy(2f, 0.4f))));

        //////////////////////////////////////////////////////////////
        //Shop content
        /////////////////////////////////////////////////////////////
        final Table shopTable = new Table();
        shopTable.align(Align.top);
        shopTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SHOP_TABLE)));

        Table purseTable = getPurseTable();

        productsTable = new Table();
        ScrollPane productScrollPane = new ScrollPane(productsTable);

        shopTable.add(purseTable).expandX().fillX().padTop(25).padLeft(50).padRight(28);
        shopTable.row();
        shopTable.add(productScrollPane).expand().fill().padTop(20).padLeft(20).padRight(28);

        ///////////////////////////////////////////////////////////////
        //population of root
        /////////////////////////////////////////////////////////////////
        _root.addActor(floorImage);
        _root.addActor(arcadeBgImage);
        _root.add(arcadeWorldImage).padTop(13);
        _root.row();
        _root.add(mascotsTable).expandX().fillX().padTop(59);
        _root.row();
        _root.addActor(potatoFlipCoinAnimator);
        _root.add(shopTable).expandX().fillX().height(349).padTop(-8);
        _root.addActor(tomatoLeftHandImage);
        _root.addActor(tomatoRightHandImage);

    }

    public Table getPurseTable(){
        Table purseRootTable = new Table();
        purseRootTable.align(Align.top);
        purseRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));

        Image backPurseImage = new Image(_assets.getTextures().get(Textures.Name.PURSE_BACK));
        Image frontPurseImage = new Image(_assets.getTextures().get(Textures.Name.PURSE_FRONT));

        purseImagesTable = new Table();
        purseImagesTable.setSize(140, 200);
        purseImagesTable.setPosition(-13, -90);
        purseImagesTable.align(Align.top);

        Image coin1 = new Image(_assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
        coin1.setName("coin1");
        coin1.setVisible(false);
        coin1.setPosition(15, 150);

        Image coin2 = new Image(_assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
        coin2.setName("coin2");
        coin2.setVisible(false);
        coin2.setPosition(36, 147);

        Image coin3 = new Image(_assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
        coin3.setName("coin3");
        coin3.setVisible(false);
        coin3.setPosition(63, 155);

        Image coin4 = new Image(_assets.getTextures().get(Textures.Name.PURSE_COIN_SLEEP));
        coin4.setName("coin4");
        coin4.setVisible(false);
        coin4.setPosition(40, 187);

        purseImagesTable.add(backPurseImage).padLeft(-10);
        purseImagesTable.row();

        purseImagesTable.addActor(coin1);
        purseImagesTable.addActor(coin3);
        purseImagesTable.addActor(coin2);
        purseImagesTable.addActor(coin4);

        purseImagesTable.add(frontPurseImage).padTop(-20);

        Table topContentTable = new Table();
        topContentTable.align(Align.left);
        topContentTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));

        Label.LabelStyle labelBigStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XXXL_REGULAR), Color.WHITE);
        Label xLabel = new Label("x", labelBigStyle);
        purseCountLabel = new Label("?", labelBigStyle);

        Label.LabelStyle labelSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD), Color.WHITE);
        Table detailsTable = new Table();
        detailsTable.align(Align.right);
        Label labelTitle = new Label(_texts.purseTitle(), labelSmallStyle);
        labelTitle.setAlignment(Align.right);
        labelTitle.setWrap(true);

        growthRateTable = new Table();
        Label labelGrowthRate = new Label(_texts.growthRate(), labelSmallStyle);
        labelGrowthRate.setName("labelGrowthRate");
        labelGrowthRate.setAlignment(Align.right);
        Label nextCoinTimerLabel = new Label("?", labelSmallStyle);
        nextCoinTimerLabel.setAlignment(Align.right);
        nextCoinTimerLabel.setName("nextCoinTimerLabel");

        growthRateTable.add(labelGrowthRate).expandX().fillX();
        growthRateTable.row();
        growthRateTable.add(nextCoinTimerLabel).expandX().fillX();

        detailsTable.add(labelTitle).expandX().fillX();
        detailsTable.row();
        detailsTable.add(growthRateTable).expandX().fillX();

        topContentTable.add(xLabel).padLeft(140).padTop(15);
        topContentTable.add(purseCountLabel).padLeft(5).padTop(15);
        topContentTable.add(detailsTable).expandX().fillX().padRight(10);

        Table retrieveButton = getWoodButton(_services.getTexts().retrieveCoins());
        retrieveCoinsButton = retrieveButton;

        purseRootTable.add(topContentTable).expandX().fillX().height(63);
        purseRootTable.row();
        purseRootTable.add(retrieveButton).right().pad(4, 0, 7, 7);
        purseRootTable.addActor(purseImagesTable);

        return purseRootTable;
    }

    public void setProductsDesign(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table productTable = getSellingItemTable(ShopProducts.ONE_COIN, "30 sec video ads",
                                                    _texts.watchAds(), ProductAction.WatchVideo);
                Table productTable2 = getSellingItemTable(ShopProducts.FIVE_COINS, "RM 20.00",
                                                    _texts.buyCoins(), ProductAction.Buy);
                Table productTable3 = getSellingItemTable(ShopProducts.FIFTEEN_COINS, "RM 30.00", _texts.buyCoins()
                                                    , ProductAction.Buy);
                Table productTable4 = getSellingItemTable(ShopProducts.HUNDRED_COINS, "RM 40.00", _texts.buyCoins()
                                                     , ProductAction.Buy);
                Table productTable5 = getSellingItemTable(ShopProducts.HUNDRED_COINS, "RM 40.00", _texts.buyCoins()
                                                     , ProductAction.Buy);
                Table productTable6 = getSellingItemTable(ShopProducts.HUNDRED_COINS, "RM 40.00", _texts.buyCoins()
                                                    , ProductAction.Buy);

                productsTable.add(productTable).space(5).uniformX().expandX().fillX();
                productsTable.add(productTable2).space(5).uniformX().expandX().fillX();
                productsTable.row();
                productsTable.add(productTable3).space(5).uniformX().expandX().fillX();
                productsTable.add(productTable4).space(5).uniformX().expandX().fillX();
                productsTable.row();
                productsTable.add(productTable5).space(5).uniformX().expandX().fillX();
                productsTable.add(productTable6).space(5).uniformX().expandX().fillX();
            }
        });
    }

    public Table getSellingItemTable(ShopProducts shopProducts, String price, String buttonText, ProductAction productAction){
        Table itemRootTable = new Table();
        itemRootTable.align(Align.top);
        itemRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));

        Table topContentTable = new Table();
        topContentTable.align(Align.left);
        topContentTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));

        TextureRegion itemTextureRegion = null;
        String title = "";

        switch (shopProducts){
            case ONE_COIN:
                itemTextureRegion = _assets.getTextures().get(Textures.Name.COIN_ONE);
                title = String.format(_texts.xCoin(), 1);
                break;
            case FIVE_COINS:
                itemTextureRegion = _assets.getTextures().get(Textures.Name.COIN_FIVE);
                title = String.format(_texts.xCoin(), 5);
                break;
            case FIFTEEN_COINS:
                itemTextureRegion = _assets.getTextures().get(Textures.Name.COIN_FIFTEEN);
                title = String.format(_texts.xCoin(), 15);
                break;
            case HUNDRED_COINS:
                itemTextureRegion = _assets.getTextures().get(Textures.Name.COIN_BAG);
                title = String.format(_texts.xCoin(), 100);
                break;
        }

        Image itemImage = new Image();
        itemImage.setDrawable(new TextureRegionDrawable(itemTextureRegion));
        Table itemImageTable = new Table();

        float width = itemTextureRegion.getRegionWidth();
        float height = itemTextureRegion.getRegionHeight();
        if(itemTextureRegion.getRegionWidth() > 60){
            width = 60;
            height = Sizes.resize(60, itemTextureRegion).y;
        }


        itemImageTable.add(itemImage).size(width, height);

        Label.LabelStyle labelSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD), Color.WHITE);
        Label titleLabel = new Label(title, labelSmallStyle);
        titleLabel.setAlignment(Align.left);
        Label priceLabel = new Label(price, labelSmallStyle);
        priceLabel.setAlignment(Align.left);

        Table detailsTable = new Table();
        detailsTable.add(titleLabel).expandX().fillX();
        detailsTable.row();
        detailsTable.add(priceLabel).expandX().fillX();

        topContentTable.add(itemImageTable).width(70).expandY().fillY();
        topContentTable.add(detailsTable).expand().fill();

        Table retrieveButton = getWoodButton(buttonText);

        if(productAction == ProductAction.WatchVideo){
            watchVideoAdsButton = retrieveButton;
            watchAdsItemTable = itemRootTable;
        }

        itemRootTable.add(topContentTable).expandX().fillX().height(63);
        itemRootTable.row();
        itemRootTable.add(retrieveButton).right().pad(7, 0, 7, 7);

        return itemRootTable;
    }

    public void setCanWatchAds(final boolean hasAds){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                setIsOutOfStock(watchAdsItemTable, !hasAds);
            }
        });
    }

    public void setIsOutOfStock(final Table itemRootTable, final boolean isOutOfStock){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(isOutOfStock){
                    Table outOfStockTable = new Table();
                    outOfStockTable.setName("outOfStockTable");
                    outOfStockTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LESS_TRANS_BLACK_BG)));
                    outOfStockTable.add(new Image(_assets.getTextures().get(Textures.Name.OUT_OF_STOCK_ICON))).pad(10);
                    outOfStockTable.setFillParent(true);

                    itemRootTable.addActor(outOfStockTable);
                }
                else{
                    if(itemRootTable.findActor("outOfStockTable") != null) itemRootTable.findActor("outOfStockTable").remove();
                }
            }
        });
    }

    public void refreshPurseDesign(final RetrievableCoinsData retrievableCoinsData){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Actor coin1 = purseImagesTable.findActor("coin1");
                Actor coin2 = purseImagesTable.findActor("coin2");
                Actor coin3 = purseImagesTable.findActor("coin3");
                Actor coin4 = purseImagesTable.findActor("coin4");

                coin1.setVisible(false);
                coin2.setVisible(false);
                coin3.setVisible(false);
                coin4.setVisible(false);

                if(retrievableCoinsData == null){

                    purseCountLabel.setText("?");
                }
                else{
                    if(retrievableCoinsData.getCanRetrieveCoinsCount() > 0 && retrievableCoinsData.getCanRetrieveCoinsCount() <= 1){
                        coin1.setVisible(true);
                    }
                    else if(retrievableCoinsData.getCanRetrieveCoinsCount() > 1 && retrievableCoinsData.getCanRetrieveCoinsCount() <= 2){
                        coin1.setVisible(true);
                        coin2.setVisible(true);
                    }
                    else if(retrievableCoinsData.getCanRetrieveCoinsCount() > 2 && retrievableCoinsData.getCanRetrieveCoinsCount() <= 3){
                        coin1.setVisible(true);
                        coin2.setVisible(true);
                        coin3.setVisible(true);
                    }
                    else if(retrievableCoinsData.getCanRetrieveCoinsCount() > 3){
                        coin1.setVisible(true);
                        coin2.setVisible(true);
                        coin3.setVisible(true);
                        coin4.setVisible(true);
                    }

                    purseCountLabel.setText(String.valueOf(retrievableCoinsData.getCanRetrieveCoinsCount()));
                }
            }
        });
    }

    public void refreshNextCoinTimer(final int nextCoinInSecs, final boolean maxCoinReached){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                Label labelGrowthRate = growthRateTable.findActor("labelGrowthRate");
                Label nextCoinTimerLabel = growthRateTable.findActor("nextCoinTimerLabel");

                if(maxCoinReached){
                    labelGrowthRate.setText(_texts.maxPurse());
                    nextCoinTimerLabel.setVisible(false);
                }
                else{
                    labelGrowthRate.setText(_texts.growthRate());
                    nextCoinTimerLabel.setText(DateTimes.getDurationString(nextCoinInSecs));
                    nextCoinTimerLabel.setVisible(true);
                }
            }
        });
    }

    public Table getWoodButton(String text){
        Table buttonTable = new Table();
        new DummyButton(buttonTable, _assets);
        buttonTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SHOP_WOOD_BTN)));

        Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.PIZZA_S_REGULAR_B_ffffff_000000_1),
                                        null);
        Label buttonLabel = new Label(text, labelStyle);

        buttonTable.add(buttonLabel).pad(3, 15, 3, 15);

        return buttonTable;
    }

    public Actor getWatchVideoAdsButton(){
        return watchVideoAdsButton;
    }

    public Actor getRetrieveCoinsButton() {
        return retrieveCoinsButton;
    }
}
