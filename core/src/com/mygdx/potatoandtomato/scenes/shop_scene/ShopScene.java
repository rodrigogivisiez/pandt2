package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.controls.DummyButton;
import com.mygdx.potatoandtomato.controls.PurseControl;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.enums.ProductAction;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.utils.DateTimes;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 16/6/2016.
 */
public class ShopScene extends SceneAbstract {

    private Table productsTable, watchAdsItemTable, growthRateTable,
                    loadingTable, shopContentTable;
    private Label purseCountLabel;
    private Actor retrieveCoinsButton;
    private HashMap<Integer, Image> screensMap;
    private ShopArcadeScreensAnimation shopArcadeScreensAnimation;
    private PurseControl purseControl;

    public ShopScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        screensMap = new HashMap();

        topBar = new TopBar(_root, _services.getTexts().shopSceneTitle(), false, _assets, _screen, _services.getCoins());
        topBar.setDarkTheme();

        _root.align(Align.top);
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));

        ////////////////////////////////////
        //Background things
        /////////////////////////////////////

        Image floorImage = new Image(_assets.getTextures().get(Textures.Name.FLOOR_BG));

        Image arcadeBgImage = new Image(_assets.getTextures().get(Textures.Name.ARCADE_MACHINES));
        arcadeBgImage.setPosition(0, 405);

        Table arcadeScreensTable = new Table();
        arcadeScreensTable.align(Align.top);
        arcadeScreensTable.setSize(197, 100);
        arcadeScreensTable.setPosition(80, Positions.getHeight() - 119 - arcadeScreensTable.getHeight());

        Image arcadeFadedScreens = new Image(_assets.getTextures().get(Textures.Name.ARCADE_SCREENS));
        arcadeScreensTable.add(arcadeFadedScreens).colspan(6);
        arcadeScreensTable.row();

        for(int i = 1; i<= 12; i++){
            TextureRegion region = _assets.getTextures().getArcadeScreen(i);
            Image screenImage = new Image(region);
            screenImage.getColor().a = 0.2f;
            arcadeScreensTable.add(screenImage);
            if(i == 6) arcadeScreensTable.row();

            screensMap.put(i, screenImage);
        }
        shopArcadeScreensAnimation = new ShopArcadeScreensAnimation(screensMap);

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

        loadingTable = new Table();
        loadingTable.setTransform(true);
        Label loadingLabel = new Label(_texts.loading(),
                    new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_ITALIC), null));
        loadingTable.add(loadingLabel);
        loadingTable.setFillParent(true);

        shopContentTable = new Table();
        shopContentTable.setFillParent(true);
        shopContentTable.setTransform(true);
        shopContentTable.setPosition(Positions.getWidth(), 0);

        Table purseTable = getPurseTable();

        productsTable = new Table();
        productsTable.align(Align.topLeft);
        ScrollPane productScrollPane = new ScrollPane(productsTable);

        shopContentTable.add(purseTable).expandX().fillX().padTop(25).padLeft(50).padRight(28);
        shopContentTable.row();
        shopContentTable.add(productScrollPane).expand().fill().padTop(20).padLeft(20).padRight(28);

        shopTable.addActor(loadingTable);
        shopTable.addActor(shopContentTable);

        ///////////////////////////////////////////////////////////////
        //population of root
        /////////////////////////////////////////////////////////////////
        _root.addActor(floorImage);
        _root.addActor(arcadeScreensTable);
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

        purseControl = new PurseControl(_assets, 0);
        purseControl.setPosition(-13, -90);

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
        Label labelGrowthRate = new Label(_texts.growthRateForShop(), labelSmallStyle);
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

        TextButton retrieveButton = getWoodButton(_services.getTexts().btnTextRetrieveCoins());
        retrieveCoinsButton = retrieveButton;

        purseRootTable.add(topContentTable).expandX().fillX().height(63);
        purseRootTable.row();
        purseRootTable.add(retrieveButton).size(retrieveButton.getPrefWidth(), retrieveButton.getPrefHeight()).right().pad(4, 0, 7, 7);
        purseRootTable.addActor(purseControl);

        return purseRootTable;
    }

    public void setProductsDesign(final ArrayList<CoinProduct> coinProducts){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                productsTable.clearChildren();
                for(CoinProduct coinProduct : coinProducts){
                    if(coinProduct.getPrice() >= 0){
                        Table productTable = getSellingItemTable(coinProduct);
                        productsTable.add(productTable).space(5).uniformX().expandX().fillX();
                        if(productsTable.getChildren().size % 2 == 0){
                            productsTable.row();
                        }
                    }
                }

                for(int i = coinProducts.size(); i < 2; i++){
                    productsTable.add(new Table()).space(5).uniformX().expandX().fillX();
                }
            }
        });
    }

    public Table getSellingItemTable(CoinProduct coinProduct){
        ProductAction productAction = ProductAction.Buy;
        if(coinProduct.getId().equals(Terms.WATCH_ADS_ID)){
            productAction = ProductAction.WatchVideo;
        }

        Table itemRootTable = new Table();
        itemRootTable.align(Align.top);
        itemRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));

        Table topContentTable = new Table();
        topContentTable.align(Align.left);
        topContentTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));

        TextureRegion itemTextureRegion = null;
        String title =  String.format(productAction == ProductAction.WatchVideo ? _texts.freexCoin() : _texts.xCoin(),
                                    coinProduct.getCount());
        itemTextureRegion = _assets.getTextures().get(coinProduct.getTextureNameFromCoinCount());

        Image itemImage = new Image();
        itemImage.setName("itemImage");
        itemImage.setDrawable(new TextureRegionDrawable(itemTextureRegion));
        Table itemImageTable = new Table();

        float width = itemTextureRegion.getRegionWidth();
        float height = itemTextureRegion.getRegionHeight();
        if(itemTextureRegion.getRegionWidth() > 60){
            width = 60;
            height = Sizes.resize(60, itemTextureRegion).y;
        }
        itemImage.setSize(width, height);

        itemImageTable.add(itemImage).size(width, height);

        Label.LabelStyle labelSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD), Color.WHITE);
        Label titleLabel = new Label(title, labelSmallStyle);
        titleLabel.setAlignment(Align.left);
        titleLabel.setWrap(true);

         Label priceLabel = new Label(coinProduct.getCurrency() + " " + coinProduct.getPriceText(), labelSmallStyle);
        if(productAction == ProductAction.WatchVideo){
            priceLabel.setText(coinProduct.getDescription());
        }
        priceLabel.setWrap(true);
        priceLabel.setAlignment(Align.left);

        Table detailsTable = new Table();
        detailsTable.add(titleLabel).expandX().fillX();
        detailsTable.row();
        detailsTable.add(priceLabel).expandX().fillX();

        topContentTable.add(itemImageTable).width(70).expandY().fillY();
        topContentTable.add(detailsTable).expand().fill();

        TextButton retrieveButton = getWoodButton(productAction != ProductAction.WatchVideo ? _texts.btnTextBuyNow() : _texts.btnTextWatchAds());
        retrieveButton.setName(coinProduct.getId());

        if(productAction == ProductAction.WatchVideo){
            watchAdsItemTable = itemRootTable;
        }

        itemRootTable.add(topContentTable).expandX().fillX().height(63);
        itemRootTable.row();
        itemRootTable.add(retrieveButton).size(retrieveButton.getPrefWidth(), retrieveButton.getPrefHeight()).right().pad(7, 0, 7, 7);

        return itemRootTable;
    }

    public void setCanWatchAds(final boolean hasAds){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(watchAdsItemTable != null){
                    setIsOutOfStock(watchAdsItemTable, !hasAds);
                    Image itemImage = watchAdsItemTable.findActor("itemImage");
                    itemImage.clearActions();
                    if(hasAds){
                        itemImage.setOrigin(Align.center);
                        itemImage.addAction(forever(sequence(repeat(3, sequence(rotateBy(2f, 0.1f), rotateBy(-2f, 0.1f))), delay(1f))));
                    }
                }
            }
        });
    }

    public void setIsOutOfStock(final Table itemRootTable, final boolean isOutOfStock){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(isOutOfStock){
                    Table outOfStockTable = new Table();
                    outOfStockTable.getColor().a = 0.3f;
                    outOfStockTable.setName("outOfStockTable");
                    outOfStockTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LESS_TRANS_BLACK_BG)));
                    outOfStockTable.add(new Image(_assets.getTextures().get(Textures.Name.OUT_OF_STOCK_ICON))).pad(10);
                    outOfStockTable.setFillParent(true);
                    Table dummyButton = new DummyButton(outOfStockTable, _assets);


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
                if(retrievableCoinsData == null){
                    purseCountLabel.setText("?");
                }
                else{
                    purseControl.changeCoinsNumber(retrievableCoinsData.getCanRetrieveCoinsCount());
                    purseCountLabel.setText(String.valueOf(retrievableCoinsData.getCanRetrieveCoinsCount()));
                }
            }
        });
    }

    public void finishLoading(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.SLIDING);
                loadingTable.addAction(Actions.moveBy(-Positions.getWidth(), 0, 0.5f));
                shopContentTable.addAction(sequence(Actions.moveBy(-Positions.getWidth(), 0, 0.5f), new RunnableAction(){
                    @Override
                    public void run() {
                        loadingTable.remove();
                    }
                }));
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
                    labelGrowthRate.setText(_texts.maxPurseTextForShop());
                    nextCoinTimerLabel.setVisible(false);
                }
                else{
                    labelGrowthRate.setText(_texts.growthRateForShop());
                    nextCoinTimerLabel.setText(DateTimes.getDurationString(nextCoinInSecs));
                    nextCoinTimerLabel.setVisible(true);
                }
            }
        });
    }

    public void randomAnimateStyle(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                shopArcadeScreensAnimation.start();
            }
        });
    }

    public TextButton getWoodButton(String text){

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_S_REGULAR_B_ffffff_000000_1);
        textButtonStyle.up = new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SHOP_WOOD_BTN));
        textButtonStyle.down = new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SHOP_WOOD_BTN_ONPRESS));
        TextButton woodButton = new TextButton(text, textButtonStyle);
        woodButton.getLabelCell().pad(3, 8, 3, 8);
        woodButton.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.WOOD_BTN_CLICK);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        return woodButton;
    }

    public Actor getProductButtonById(String productId){
        return productsTable.findActor(productId);
    }

    public Actor getRetrieveCoinsButton() {
        return retrieveCoinsButton;
    }

    @Override
    public void dispose() {
        super.dispose();
        if(shopArcadeScreensAnimation != null) shopArcadeScreensAnimation.dispose();
    }
}
