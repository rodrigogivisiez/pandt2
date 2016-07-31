package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.enums.CoinMachineTabType;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.DateTimes;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


/**
 * Created by SiongLeng on 13/6/2016.
 */
public class CoinMachineControl {

    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private Table root;
    private Image overlayImage;
    private Table tabsTable, contentTable, bottomPartTable;
    private Table tabContentTable;
    private Table dismissTable;
    private Button leftButton, rightButton;
    private Table potatoSpeechTable, tomatoSpeechTable;
    private Table coinMachineTable, yourCoinCountTable;
    private Table bottomPartDismissContainer, contentDismissContainer;
    private Table tomatoMouthTable, potatoMouthTable;
    private ScrollPane playersScrollPane;
    private Table retrieveCoinTable;
    private Table purchaseCoinTable;
    private Table coinInsertRootTable, toInsertCoinsRootTable, coinInsertAnimationTable;
    private TextButton buyCoinsTabButton, retrieveCoinsTabButton, playerInsertCoinStatusTabButton;
    private TextButton retrieveCoinsButton, dismissButton;
    private Label toInsertCoinLabel;
    private Image toInsertCoinBlinkImage;

    private Stage stage;
    private Image coinAngleImage, coinFlatImage;
    private IPTGame iptGame;
    private SpriteBatch batch;
    private boolean visible;
    private SafeThread tomatoMouthSafeThread, potatoMouthSafeThread;


    public CoinMachineControl(Broadcaster broadcaster,
                              Assets assets, SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame, SpriteBatch batch) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.iptGame = iptGame;
        this.batch = batch;

        populate();
        populateTabContent();
        switchTab(CoinMachineTabType.PlayersInsertCoinStatus);
        setInternalListeners();

        invalidate();

        broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                invalidate();
            }
        });
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                overlayImage = new Image(assets.getTextures().get(Textures.Name.LESS_TRANS_BLACK_BG));
                overlayImage.getColor().a = 0.5f;
                overlayImage.setFillParent(true);
                overlayImage.setVisible(false);

                root = new Table();
                root.setVisible(false);
                root.align(Align.top);

                tabsTable = new Table();
                Table buyCoinTabTable = getTabDesign(texts.buyCoinsTabTitle());
                buyCoinsTabButton = buyCoinTabTable.findActor("textButton");

                Table retrieveCoinTabTable = getTabDesign(texts.freeCoinsTabTitle());
                retrieveCoinsTabButton = retrieveCoinTabTable.findActor("textButton");

                Table playerInsertCoinStatusTabTable = getTabDesign(texts.coinsInsertedTabTitle());
                playerInsertCoinStatusTabButton = playerInsertCoinStatusTabTable.findActor("textButton");

                tabsTable.add(buyCoinTabTable).expandY().fillY().height(45).width(115.7f).fillX().space(6);
                tabsTable.add(retrieveCoinTabTable).expandY().fillY().height(45).width(115.7f).fillX().space(6);
                tabsTable.add(playerInsertCoinStatusTabTable).expandY().fillY().height(45).width(115.7f).fillX().space(6);

                contentTable = new Table();
                contentTable.align(Align.top);
                contentTable.padTop(20);
                contentTable.padBottom(8);

                tabContentTable = new Table();
                tabContentTable.setName("innerTable");
                tabContentTable.setSize(270, 170);
                tabContentTable.setClip(true);
                tabContentTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.BLUE_FRAME_PIXEL)));

                leftButton = getPixelButtonDesign(true);
                rightButton = getPixelButtonDesign(false);
                contentDismissContainer = new Table();
                contentDismissContainer.setSize(115.7f, 45);

                contentTable.add(leftButton).padLeft(10).padRight(10);
                contentTable.add(tabContentTable)
                        .height(tabContentTable.getHeight()).width(tabContentTable.getWidth()).expandX().fillX();
                contentTable.add(rightButton).padLeft(10).padRight(10);
                contentTable.addActor(contentDismissContainer);

                bottomPartTable = new Table();
                bottomPartTable.align(Align.top);
                bottomPartTable.padLeft(5);
                bottomPartTable.padRight(5);

                potatoSpeechTable = getSpeechTableDesign(true);
                potatoSpeechTable.setVisible(false);
                tomatoSpeechTable = getSpeechTableDesign(false);
                tomatoSpeechTable.setVisible(false);

                yourCoinCountTable = getYourCoinsTableDesign();

                dismissTable = getTabDesign("QUIT");
                dismissButton = dismissTable.findActor("textButton");

                coinMachineTable = getCoinMachineTableDesign();

                bottomPartDismissContainer = new Table();

                bottomPartTable.add(potatoSpeechTable).left().expandX();
                bottomPartTable.add(tomatoSpeechTable).right().expandX();

                bottomPartTable.row();

                bottomPartTable.add(yourCoinCountTable).size(72, 46).left().bottom().padBottom(6).padLeft(10);
                bottomPartTable.add(bottomPartDismissContainer).expandY().size(115.7f, 45).right().bottom();

            }
        });
    }

    private void populateTabContent(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                ////////////////////////////////////////////
                //players table
                ///////////////////////////////////////////
                Table playersTable = new Table();
                playersTable.align(Align.top);
                playersTable.setName("playersTable");
                playersScrollPane = new ScrollPane(playersTable);

                ///////////////////////////////////////////////////////
                //retrieve coins table
                ///////////////////////////////////////////////////
                retrieveCoinTable = getRetrieveCoinTableDesign();

                /////////////////////////////////////////////////
                //purchase coins table
                /////////////////////////////////////////////////
                purchaseCoinTable = new Table();
            }
        });
    }


    public void invalidate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(stage == null){
                    StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                    viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                    stage = new Stage(viewPort, batch);
                    iptGame.addInputProcessor(stage, 9, false);
                    stage.addActor(overlayImage);
                    stage.addActor(root);

                }
                else{
                    if(stage.getViewport().getWorldWidth() != Positions.getWidth()
                            || stage.getViewport().getWorldHeight() != Positions.getHeight()){
                        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                        viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                        stage.setViewport(viewPort);
                    }
                }

                if(Global.IS_POTRAIT){
                    root.clearChildren();
                    root.setSize(Positions.getWidth(), Positions.getHeight() - 70);
                    root.setY(0);

                    //root.add(contentTable).padTop(-15).expandX().fillX().height(120);

                    tabsTable.setPosition(0, root.getY() + root.getHeight() - tabsTable.getPrefHeight());
                    tabsTable.setWidth(Positions.getWidth());
                    tabsTable.setHeight(tabsTable.getPrefHeight());

                    coinMachineTable.setSize(coinMachineTable.getPrefWidth(), coinMachineTable.getPrefHeight());
                    coinMachineTable.setPosition(114, -47);

                    bottomPartTable.padTop(0);
                    bottomPartTable.setSize(Positions.getWidth(), 120);
                    bottomPartTable.setPosition(0, 0);
                    bottomPartDismissContainer.add(dismissTable).expand().fill();

                    contentTable.setSize(Positions.getWidth(), contentTable.getPrefHeight());
                    contentTable.setPosition(0, bottomPartTable.getY() + bottomPartTable.getHeight());


                    Table componentsTable = new Table();
                    componentsTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));
                    componentsTable.setSize(Positions.getWidth(), bottomPartTable.getHeight() + contentTable.getHeight());
                    componentsTable.setY(tabsTable.getY() - componentsTable.getHeight() + 20);

                    componentsTable.addActor(contentTable);
                    componentsTable.addActor(bottomPartTable);
                    componentsTable.addActor(coinMachineTable);

                    root.addActor(componentsTable);
                    root.addActor(tabsTable);
                }
                else{
                    root.clearChildren();
                    root.setSize(Positions.getWidth(), Positions.getHeight());
                    root.setY(40);

                    contentTable.setSize(340, contentTable.getPrefHeight());
                    contentTable.setPosition(300, 0);
                    contentDismissContainer.setPosition(contentTable.getWidth() - contentDismissContainer.getWidth(),
                            - contentDismissContainer.getHeight() + 9);
                    contentDismissContainer.add(dismissTable).expand().fill();

                    bottomPartTable.padTop(20);
                    bottomPartTable.setSize(350, contentTable.getPrefHeight());
                    bottomPartTable.setPosition(0, 0);

                    coinMachineTable.setSize(coinMachineTable.getPrefWidth(), coinMachineTable.getPrefHeight());
                    coinMachineTable.setPosition(110, bottomPartTable.getY() + coinMachineTable.getHeight() / 2 + 5);

                    Table componentsTable = new Table();
                    componentsTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));
                    componentsTable.addActor(contentTable);
                    componentsTable.addActor(bottomPartTable);

                    componentsTable.setSize(Positions.getWidth(), contentTable.getPrefHeight());
                    componentsTable.setPosition(0, 50);

                    tabsTable.setPosition(Positions.getWidth() - tabsTable.getPrefWidth(),
                            componentsTable.getY() + componentsTable.getHeight() - 20);


                    root.addActor(componentsTable);
                    root.addActor(tabsTable);
                    root.addActor(coinMachineTable);

                }
            }
        });
    }

    public void show(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(visible) return;
                visible = true;
                switchTab(CoinMachineTabType.PlayersInsertCoinStatus);
                overlayImage.setVisible(true);
                root.clearActions();
                root.setX(Positions.getWidth() + 10);
                root.setVisible(true);
                root.addAction(moveTo(Positions.getWidth() - root.getWidth(), root.getY(), 0.3f));
            }
        });

    }

    public void hide(final Runnable onDone){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(!visible) return;
                root.clearActions();
                root.addAction(sequence(moveTo(Positions.getWidth() + 10, root.getY(), 0.3f), new RunnableAction(){
                    @Override
                    public void run() {
                        hideSpeech();
                        overlayImage.setVisible(false);
                        root.setVisible(false);
                        toInsertCoinBlinkImage.clearActions();
                        toInsertCoinBlinkImage.getColor().a = 0f;
                        toInsertCoinLabel.clearActions();
                        toInsertCoinLabel.getColor().a = 1f;
                        purchaseCoinTable.clear();
                        visible = false;
                        onDone.run();
                    }
                }));
            }
        });

    }

    public void updateExpectingCoins(final int newExpecting){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                String originalExpectingCoinString = toInsertCoinLabel.getText().toString();
                int originalExpectingCoinCount = 0;
                if(!originalExpectingCoinString.equals("")){
                    originalExpectingCoinCount = Integer.valueOf(originalExpectingCoinString.replace("x", ""));
                }

                toInsertCoinLabel.clearActions();
                toInsertCoinLabel.getColor().a = 1f;
                toInsertCoinBlinkImage.clearActions();
                toInsertCoinBlinkImage.getColor().a = 0f;

                if(newExpecting == 0){
                    toInsertCoinBlinkImage.addAction(forever(sequence(fadeIn(0.1f), fadeOut(0.1f), new RunnableAction(){
                        @Override
                        public void run() {
                            soundsPlayer.playSoundEffect(Sounds.Name.COIN_INSERTED);
                        }
                    })));
                    toInsertCoinLabel.addAction(fadeOut(0.1f));
                }
                else if(newExpecting < originalExpectingCoinCount){
                    toInsertCoinBlinkImage.addAction(sequence(fadeIn(0.1f), fadeOut(0.1f), fadeIn(0.1f), fadeOut(0.1f)));
                    soundsPlayer.playSoundEffect(Sounds.Name.COIN_INSERTED);
                }

                toInsertCoinLabel.setText("x" + newExpecting);

            }
        });
    }

    public void updateUserTable(final String userId, final String userName, final int insertedCoins, final boolean hasCoin){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                String newText = String.format("x%s", insertedCoins);
                if(!hasCoin) newText = "noCoin";

                Label existedCoinCountLabel = null;

                Table playersTable = playersScrollPane.findActor("playersTable");

                Table existUserTable = playersTable.findActor(userId);
                if(existUserTable != null){
                    existedCoinCountLabel = existUserTable.findActor("coinCountLabel");
                    if(existedCoinCountLabel != null && existedCoinCountLabel.getText().toString().equals(newText)){
                        return;
                    }

                    existUserTable.clearActions();
                }

                final Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ARCADE_S_REGULAR),
                        Color.valueOf("fff08d"));

                Table userTable = new Table();
                userTable.padBottom(3);
                userTable.setName(userId);
                Label usernameLabel = new Label(Strings.cutOff(userName, 18), labelStyle);

                Table coinTable = new Table();
                if((hasCoin || insertedCoins > 0) && !newText.equals("noCoin")){
                    Image coinImage = new Image(assets.getTextures().get(Textures.Name.COIN_ICON_SMALL));
                    Label coinCountLabel = new Label(newText, labelStyle);
                    coinCountLabel.setName("coinCountLabel");
                    coinTable.add(coinImage).padRight(5);
                    coinTable.add(coinCountLabel);
                }
                else{
                    Image coinImage = new Image(assets.getTextures().get(Textures.Name.NO_COIN_ICON_BLACK));
                    Label coinCountLabel = new Label(newText, labelStyle);
                    coinCountLabel.setName("coinCountLabel");
                    coinCountLabel.setVisible(false);
                    coinTable.add(coinImage);
                    coinTable.add(coinCountLabel).size(0, 0);
                }

                userTable.add(usernameLabel).expandX().fillX().padRight(2);
                userTable.add(coinTable);

                if(existUserTable != null){
                    Cell cell = playersTable.getCell(existUserTable);
                    cell.setActor(userTable);

                    if(insertedCoins > 0){
                        Label newCoinCountLabel = userTable.findActor("coinCountLabel");

                        if(existedCoinCountLabel != null && newCoinCountLabel != null){
                            if(existedCoinCountLabel.getText().toString().equals(newCoinCountLabel.getText().toString())) {
                                return;
                            }
                        }

                        labelStyle.fontColor = Color.GREEN;
                        userTable.addAction(sequence(delay(2f), new RunnableAction(){
                            @Override
                            public void run() {
                                labelStyle.fontColor = Color.valueOf("fff08d");
                            }
                        }));
                    }

                }
                else{
                    playersTable.add(userTable).expandX().fillX().padLeft(3).padRight(3);
                    playersTable.row();
                }
            }
        });
    }

    public void removeUserTable(final String userId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table playersTable = playersScrollPane.findActor("playersTable");
                Actor existUserTable = playersTable.findActor(userId);
                if(existUserTable != null){
                    existUserTable.remove();
                }
            }
        });
    }

    public void updatePurse(final int retrievableCount, final int nextCoinInSecs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                PurseControl purseControl = retrieveCoinTable.findActor("purseControl");
                Label coinCountLabel = retrieveCoinTable.findActor("coinCountLabel");
                Label nextCoinLabel = retrieveCoinTable.findActor("nextCoinLabel");

                purseControl.changeCoinsNumber(retrievableCount);
                coinCountLabel.setText("x" + retrievableCount);
                if(nextCoinInSecs <= -1){
                    nextCoinLabel.setText(texts.maxPurseTextForShop());
                }
                else{
                    nextCoinLabel.setText(DateTimes.getDurationString(nextCoinInSecs));
                }
            }
        });
    }

    public void updateMyCoinsCount(final int newCount){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                ((Label) yourCoinCountTable.findActor("yourCoinCountLabel")).setText(String.valueOf(newCount));
            }
        });
    }

    public void animateNoCoin(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                final Color normalColor = Color.valueOf("2fe400");
                final Color warningColor = Color.valueOf("ff0000");

                yourCoinCountTable.clearActions();
                yourCoinCountTable.addAction(repeat(5, sequence(delay(0.1f), new RunnableAction(){
                    @Override
                    public void run() {
                        yourCoinCountTable.setColor(warningColor);
                        for(Actor actor : yourCoinCountTable.getChildren()){
                            actor.setColor(warningColor);
                        }
                    }
                }, delay(0.1f), new RunnableAction(){
                    @Override
                    public void run() {
                        yourCoinCountTable.setColor(normalColor);
                        for(Actor actor : yourCoinCountTable.getChildren()){
                            actor.setColor(normalColor);
                        }
                    }
                })));

            }
        });
    }

    public void updateDismissText(final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                dismissButton.setText(text);
            }
        });
    }

    public void putCoinAnimation(final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                final Image frame1 = new Image(assets.getTextures().get(Textures.Name.INSERT_COIN_FRAME_ONE));
                final Image frame2 = new Image(assets.getTextures().get(Textures.Name.INSERT_COIN_FRAME_TWO));
                final Image frame3 = new Image(assets.getTextures().get(Textures.Name.INSERT_COIN_FRAME_THREE));

                frame1.setPosition(-23, 0);
                coinInsertAnimationTable.addActor(frame1);

                frame2.getColor().a = 0f;
                frame2.setPosition(-3, 5);
                coinInsertAnimationTable.addActor(frame2);

                Table frame3Table = new Table();
                frame3Table.setClip(true);
                frame3Table.setSize(10, 55);
                frame3Table.setPosition(8, 6);

                frame3.getColor().a = 0f;
                frame3.setPosition(1, 1);
                frame3.setHeight(50);
                frame3Table.addActor(frame3);

                coinInsertAnimationTable.addActor(frame3Table);

                frame1.addAction(sequence(moveBy(3f, 0f, 0.06f), parallel(fadeOut(0.1f), new RunnableAction(){
                    @Override
                    public void run() {
                        frame2.addAction(sequence(parallel(fadeIn(0f), moveBy(2f, 0f, 0.1f)), parallel(fadeOut(0.1f), new RunnableAction(){
                            @Override
                            public void run() {
                                soundsPlayer.playSoundEffect(Sounds.Name.COIN_TO_SLOT);

                                frame3.addAction(sequence(fadeIn(0f), moveBy(0f, -60f, 0.7f, Interpolation.exp5Out), new RunnableAction(){
                                    @Override
                                    public void run() {
                                        frame1.remove();
                                        frame2.remove();
                                        frame3.remove();
                                        switchTab(CoinMachineTabType.PlayersInsertCoinStatus);
                                        onFinish.run();
                                    }
                                }));
                            }
                        })));
                    }
                })));

//                coinAngleImage.addAction(sequence(fadeIn(0f), moveBy(1f, 0f, 0.1f), fadeOut(0.1f), new RunnableAction(){
//                    @Override
//                    public void run() {
//                        coinFlatImage.addAction(sequence(fadeIn(0f), moveBy(0f, -70f, 0.5f, Interpolation.exp5Out), new RunnableAction(){
//                            @Override
//                            public void run() {
//                                coinFlatImage.getColor().a = 0f;
//                                coinFlatImage.setY(0);
//                                coinAngleImage.setX(0);
//                                if(onFinish!= null) onFinish.run();
//                            }
//                        }));
//                    }
//                }));
            }
        });
    }

    public void switchTab(final CoinMachineTabType tabType){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                tabContentTable.clear();

                setTabButtonSelectedStyle(buyCoinsTabButton, false);
                setTabButtonSelectedStyle(playerInsertCoinStatusTabButton, false);
                setTabButtonSelectedStyle(retrieveCoinsTabButton, false);

                if(tabType == CoinMachineTabType.PlayersInsertCoinStatus){
                    setTabButtonSelectedStyle(playerInsertCoinStatusTabButton, true);
                    tabContentTable.pad(20, 20, 20, 20);
                    tabContentTable.add(playersScrollPane).expand().fill();
                    enableLeftRight(false, false);
                }
                else if(tabType == CoinMachineTabType.RetrieveCoins){
                    setTabButtonSelectedStyle(retrieveCoinsTabButton, true);
                    tabContentTable.pad(10, 10, 10, 10);
                    tabContentTable.add(retrieveCoinTable).expand().fill();
                    enableLeftRight(false, false);
                }
                else if(tabType == CoinMachineTabType.PurchaseCoins){
                    setTabButtonSelectedStyle(buyCoinsTabButton, true);
                    tabContentTable.pad(0);
                    purchaseCoinTable.setFillParent(true);
                    tabContentTable.addActor(purchaseCoinTable);
                    enableLeftRight(true, true);
                }
            }
        });
    }

    private void setTabButtonSelectedStyle(TextButton textButton, boolean selected){
        if(selected){
            textButton.setColor(Color.valueOf("0c00f8"));
        }
        else{
            textButton.setColor(Color.valueOf("ffffff"));
        }
    }

    public boolean canGoToNextProduct(){
        return (purchaseCoinTable.getName() == null || !purchaseCoinTable.getName().equals("animating"));
    }

    public void goToNextProduct(final boolean next, final CoinProduct coinProduct, final RunnableArgs<TextButton> onDone){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label.LabelStyle priceLabelStyle = new Label.LabelStyle(
                        assets.getFonts().get(Fonts.FontId.DIGIVOLVE_XXL_REGULAR), Color.valueOf("fff08d"));
                Label priceLabel = new Label(coinProduct.getCurrency() + coinProduct.getPrice(), priceLabelStyle);
                priceLabel.setAlignment(Align.center);

                Image coinIconImage = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
                Label.LabelStyle coinCountLabelStyle = new Label.LabelStyle(
                        assets.getFonts().get(Fonts.FontId.DIGIVOLVE_50_REGULAR), Color.valueOf("fff08d"));
                Label coinCountLabel = new Label("x"+String.valueOf(coinProduct.getCount()), coinCountLabelStyle);

                TextButton buyButton = getPixelTextButtonDesign(texts.btnTextBuyNow());

                Table productTable = new Table();
                productTable.add(priceLabel).expandX().fillX().colspan(2).padBottom(10);
                productTable.row();
                productTable.add(coinIconImage).size(60, 60).right().padRight(10);
                productTable.add(coinCountLabel).left();
                productTable.row();
                productTable.add(buyButton).width(130).colspan(2).padTop(10);
                productTable.setFillParent(true);

                if(purchaseCoinTable.getChildren().size != 0){
                    purchaseCoinTable.setName("animating");
                    final Actor oldProductTable = purchaseCoinTable.getChildren().get(0);
                    for(int i = 1; i < purchaseCoinTable.getChildren().size; i++){
                        purchaseCoinTable.getChildren().get(i).remove();
                    }
                    if(next){
                        productTable.setPosition(tabContentTable.getWidth(), 0);
                        oldProductTable.addAction(moveBy(-tabContentTable.getWidth(), 0, 0.5f));
                        productTable.addAction(sequence(moveBy(-tabContentTable.getWidth(), 0, 0.5f), new RunnableAction(){
                            @Override
                            public void run() {
                                oldProductTable.clear();
                                oldProductTable.remove();
                                purchaseCoinTable.setName("");
                            }
                        }));
                    }
                    else{
                        productTable.setPosition(-tabContentTable.getWidth(), 0);
                        oldProductTable.addAction(moveBy(tabContentTable.getWidth(), 0, 0.5f));
                        productTable.addAction(sequence(moveBy(tabContentTable.getWidth(), 0, 0.5f), new RunnableAction(){
                            @Override
                            public void run() {
                                oldProductTable.clear();
                                oldProductTable.remove();
                                purchaseCoinTable.setName("");
                            }
                        }));
                    }
                }
                purchaseCoinTable.addActor(productTable);

                onDone.run(buyButton);
            }
        });
    }

    public void enableLeftRight(final boolean enableLeft, final boolean enableRight){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                leftButton.setVisible(true);
                rightButton.setVisible(true);

                if(!enableLeft) leftButton.setVisible(false);
                if(!enableRight) rightButton.setVisible(false);
            }
        });
    }

    public void startSpeechAnimation(final boolean potato){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                SafeThread safeThread;
                final Table mouthTable;
                if(potato){
                   if(potatoMouthSafeThread != null && !potatoMouthSafeThread.isKilled()) return;
                    potatoMouthSafeThread = new SafeThread();
                    safeThread = potatoMouthSafeThread;
                    mouthTable = potatoMouthTable;
                    soundsPlayer.playSoundEffectLoop(Sounds.Name.EIGHT_BIT_SPEAKING_POTATO);
                }
                else{
                    if(tomatoMouthSafeThread != null && !tomatoMouthSafeThread.isKilled()) return;
                    tomatoMouthSafeThread = new SafeThread();
                    safeThread = tomatoMouthSafeThread;
                    mouthTable = tomatoMouthTable;
                    soundsPlayer.playSoundEffectLoop(Sounds.Name.EIGHT_BIT_SPEAKING_TOMATO);
                }


                boolean open = false;
                final Image openImage = mouthTable.findActor("open");
                final Image closeImage = mouthTable.findActor("close");
                final Image smileImage = mouthTable.findActor("smile");
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        openImage.setVisible(false);
                        closeImage.setVisible(false);
                        smileImage.setVisible(false);
                    }
                });

                while (true){
                    if(safeThread.isKilled()) break;
                    if(!open){
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if(openImage != null) openImage.setVisible(false);
                                if(closeImage != null) closeImage.setVisible(true);
                            }
                        });
                    }
                    else{
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if(openImage != null) openImage.setVisible(true);
                                if(closeImage != null)closeImage.setVisible(false);
                            }
                        });
                    }
                    open = !open;
                    Threadings.sleep(200);
                }

            }
        });
    }

    public void stopSpeechAnimation(final boolean potato){
        SafeThread safeThread;
        final Table mouthTable;
        if(potato){
            safeThread = potatoMouthSafeThread;
            mouthTable = potatoMouthTable;
            soundsPlayer.stopSoundEffectLoop(Sounds.Name.EIGHT_BIT_SPEAKING_POTATO);
        }
        else{
            safeThread = tomatoMouthSafeThread;
            mouthTable = tomatoMouthTable;
            soundsPlayer.stopSoundEffectLoop(Sounds.Name.EIGHT_BIT_SPEAKING_TOMATO);
        }

        if(safeThread != null) safeThread.kill();

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                final Image openImage = mouthTable.findActor("open");
                final Image closeImage = mouthTable.findActor("close");
                final Image smileImage = mouthTable.findActor("smile");

                openImage.setVisible(false);
                closeImage.setVisible(false);
                smileImage.setVisible(true);
            }
        });
    }

    public void hideSpeech(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                potatoSpeechTable.setVisible(false);
                tomatoSpeechTable.setVisible(false);
            }
        });
    }


    public void updateSpeechText(final boolean potato, final String addingChar, final String word){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label msgLabel;
                ScrollPane scrollPane;
                if(potato){
                    msgLabel = potatoSpeechTable.findActor("messageLabel");
                    scrollPane = potatoSpeechTable.findActor("scrollPane");
                    if(!potatoSpeechTable.isVisible()){
                        potatoSpeechTable.setVisible(true);
                    }
                }
                else{
                    msgLabel = tomatoSpeechTable.findActor("messageLabel");
                    scrollPane = tomatoSpeechTable.findActor("scrollPane");
                    if(!tomatoSpeechTable.isVisible()){
                        tomatoSpeechTable.setVisible(true);
                    }
                }

                String currentMsg = msgLabel.getText().toString();

                String[] temp = currentMsg.split("\n");

                String currentLineMsg = "";
                if(temp.length > 0){
                    currentLineMsg = temp[temp.length - 1];
                }

                boolean newLine = false;
                int remainder = currentLineMsg.length() % 12;
                if(remainder + word.length() >= 12 && currentMsg.length() > 0 &&
                        !String.valueOf(currentMsg.charAt(currentMsg.length() - 1)).equals("\n")){
                    currentMsg += "\n";
                    newLine = true;
                }

                if(newLine && !addingChar.equals(" ") || !newLine){
                    currentMsg += addingChar;
                }

                msgLabel.setText(currentMsg);
                scrollPane.setScrollPercentY(100);
            }
        });
    }

    public void clearSpeechText(final boolean potato){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label msgLabel;
                if(potato){
                    msgLabel = potatoSpeechTable.findActor("messageLabel");
                }
                else{
                    msgLabel = tomatoSpeechTable.findActor("messageLabel");
                }

                msgLabel.setText("");
            }
        });
    }


    private Table getTabDesign(String text){
        Table tabTable = new Table();
        tabTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));
        tabTable.pad(6);

        TextButton textButton = getPixelTextButtonDesign(text);
        textButton.setName("textButton");
        tabTable.add(textButton).expand().fill();

        return tabTable;
    }

    private TextButton getPixelTextButtonDesign(String text){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.WHITE_BUTTON_PIXEL));
        textButtonStyle.down = new TextureRegionDrawable(assets.getTextures().get(Textures.Name.WHITE_BUTTON_PIXEL_ONPRESS));
        textButtonStyle.font = assets.getFonts().get(Fonts.FontId.DIGIVOLVE_S_REGULAR);
        TextButton textButton = new TextButton(text, textButtonStyle);
        textButton.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                soundsPlayer.playSoundEffect(Sounds.Name.EIGHT_BIT_BUTTON_2);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        return textButton;
    }

    private Button getPixelButtonDesign(boolean isLeft){
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(
                isLeft ? assets.getTextures().get(Textures.Name.LEFT_BUTTON_PIXEL) :
                        assets.getTextures().get(Textures.Name.RIGHT_BUTTON_PIXEL
                        )
        );
        final Button pixelButton = new Button(buttonStyle);
        pixelButton.setColor(Color.valueOf("0c00ff"));

        pixelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, final int button) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        pixelButton.setColor(Color.valueOf("ffffff"));
                        soundsPlayer.playSoundEffect(Sounds.Name.EIGHT_BIT_BUTTON_1);
                    }
                });
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        pixelButton.setColor(Color.valueOf("0c00ff"));
                    }
                });
                super.touchUp(event, x, y, pointer, button);
            }
        });

        return pixelButton;
    }

    private Table getSpeechTableDesign(boolean isLeft){
        Table table = new Table();
        table.align(Align.top);
        table.setBackground(new TextureRegionDrawable(isLeft  ? assets.getTextures().get(Textures.Name.SPEECH_LEFT_PIXEL) :
                assets.getTextures().get(Textures.Name.SPEECH_RIGHT_PIXEL)));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = assets.getFonts().get(Fonts.FontId.DIGIVOLVE_S_REGULAR);
        labelStyle.fontColor = Color.BLACK;
        Label messageLabel = new Label("", labelStyle);
        messageLabel.setName("messageLabel");
        messageLabel.setAlignment(Align.topLeft);
        messageLabel.setWrap(true);

        ScrollPane scrollPane = new ScrollPane(messageLabel);
        scrollPane.setName("scrollPane");

        if(isLeft){
            table.add(scrollPane).expand().fill().pad(10, 10, 10, 25).height(40);
        }
        else{
            table.add(scrollPane).expand().fill().pad(10, 27, 10, 8).height(40);
        }

        table.setSize(112, 62);
        return table;
    }

    private Table getYourCoinsTableDesign(){
        Table yourCoinCountTable = new Table();
        yourCoinCountTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.GREEN_FRAME_PIXEL)));

        Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.DIGIVOLVE_XS_REGULAR),
                Color.valueOf("2fe400"));
        Label.LabelStyle labelStyle2 = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.DIGIVOLVE_S_REGULAR),
                Color.valueOf("2fe400"));
        Label label = new Label("Your Coins:", labelStyle);
        Label yourCoinCountLabel = new Label("0", labelStyle2);
        yourCoinCountLabel.setName("yourCoinCountLabel");
        yourCoinCountTable.add(label);
        yourCoinCountTable.row();
        yourCoinCountTable.add(yourCoinCountLabel);

        return yourCoinCountTable;
    }

    private Table getCoinMachineTableDesign(){
        int padSize = 3;

        Table coinMachineTable = new Table();
        coinMachineTable.pad(padSize);

        Image bgImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_ROOT_BG));
        bgImage.setFillParent(true);

        coinInsertRootTable = new Table();
        new DummyButton(coinInsertRootTable, assets);
        coinInsertRootTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.COIN_MACHINE_BG)));
        new DummyButton(coinInsertRootTable, assets);

        Table coinInsertTable = new Table();
        coinInsertTable.align(Align.left);
        Image blinkingImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_BLINK_BG));
        blinkingImage.setFillParent(true);
        //blinkingImage.addAction(forever(sequence(fadeIn(1f), fadeOut(1f))));

        Image holeImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_HOLE));

        Color labelColor = Color.valueOf("dedfe0");
        Table labelsTable = new Table();
        Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.IMPACT_S_REGULAR), labelColor);

        Label insertLabel = new Label(texts.insert().toUpperCase(), labelStyle);
        Image separatorImage = new Image(assets.getTextures().get(Textures.Name.COIN_MACHINE_SEPARATOR));
        separatorImage.setColor(labelColor);

        Label coinLabel = new Label(texts.coin().toUpperCase(), labelStyle);
        labelsTable.add(insertLabel);
        labelsTable.row();
        labelsTable.add(separatorImage);
        labelsTable.row();
        labelsTable.add(coinLabel);
        labelsTable.addAction(forever(sequence(fadeIn(0.7f), alpha(0.3f, 0.7f))));

        coinInsertAnimationTable = new Table();
        coinInsertAnimationTable.setSize(20, 72);
        coinInsertAnimationTable.setPosition(0, 0);

        coinInsertTable.addActor(blinkingImage);
        coinInsertTable.add(holeImage).pad(2, 2, 2, 0);
        coinInsertTable.add(labelsTable).expand().fill();
        coinInsertTable.addActor(coinInsertAnimationTable);

        coinInsertRootTable.add(coinInsertTable).expand().fill().pad(padSize);

        toInsertCoinsRootTable = new Table();
        toInsertCoinsRootTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.COIN_MACHINE_USERS_BG)));

        Image coinIcon = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
        toInsertCoinsRootTable.add(coinIcon).size(45, 45);

        Label.LabelStyle toInsertCoinLabelStyle = new Label.LabelStyle(
                assets.getFonts().get(Fonts.FontId.DIGIVOLVE_XL_REGULAR_B_ffffff_580202_1), null);
        toInsertCoinLabel = new Label("", toInsertCoinLabelStyle);
        toInsertCoinLabel.setFillParent(true);
        toInsertCoinLabel.setAlignment(Align.bottomRight);

        toInsertCoinBlinkImage = new Image(assets.getTextures().get(Textures.Name.FULL_WHITE_BG));
        toInsertCoinBlinkImage.getColor().a = 0f;
        toInsertCoinBlinkImage.setFillParent(true);

        toInsertCoinsRootTable.addActor(toInsertCoinLabel);
        toInsertCoinsRootTable.addActor(toInsertCoinBlinkImage);

        Table mascotsTable = new Table();
        mascotsTable.setSize(129, 85);
        mascotsTable.setPosition(-2, 70);
        Image mascotsImage = new Image(assets.getTextures().get(Textures.Name.MASCOTS_PIXEL));
        mascotsTable.add(mascotsImage);

        tomatoMouthTable = new Table();
        tomatoMouthTable.setSize(5, 5);
        tomatoMouthTable.setPosition(96, 18);
        Image tomatoMouthOpenImage = new Image(assets.getTextures().get(Textures.Name.TOMATO_OPEN_MOUTH));
        tomatoMouthOpenImage.setX(- tomatoMouthOpenImage.getPrefWidth() / 2);
        tomatoMouthOpenImage.setY(- tomatoMouthOpenImage.getPrefHeight() / 2);
        tomatoMouthOpenImage.setVisible(false);
        tomatoMouthOpenImage.setName("open");
        Image tomatoMouthSmileImage = new Image(assets.getTextures().get(Textures.Name.TOMATO_SMILE_MOUTH));
        tomatoMouthSmileImage.setX(- tomatoMouthSmileImage.getPrefWidth() / 2);
        tomatoMouthSmileImage.setY(- tomatoMouthSmileImage.getPrefHeight() / 2);
        tomatoMouthSmileImage.setName("smile");
        Image tomatoMouthCloseImage = new Image(assets.getTextures().get(Textures.Name.TOMATO_CLOSE_MOUTH));
        tomatoMouthCloseImage.setX(- tomatoMouthCloseImage.getPrefWidth() / 2);
        tomatoMouthCloseImage.setY(-1);
        tomatoMouthCloseImage.setVisible(false);
        tomatoMouthCloseImage.setName("close");
        tomatoMouthTable.addActor(tomatoMouthOpenImage);
        tomatoMouthTable.addActor(tomatoMouthSmileImage);
        tomatoMouthTable.addActor(tomatoMouthCloseImage);

        potatoMouthTable = new Table();
        potatoMouthTable.setSize(5, 5);
        potatoMouthTable.setPosition(43, 20);
        Image potatoMouthOpenImage = new Image(assets.getTextures().get(Textures.Name.POTATO_OPEN_MOUTH));
        potatoMouthOpenImage.setX(- potatoMouthOpenImage.getPrefWidth() / 2);
        potatoMouthOpenImage.setY(- potatoMouthOpenImage.getPrefHeight() / 2);
        potatoMouthOpenImage.setName("open");
        potatoMouthOpenImage.setVisible(false);
        Image potatoMouthSmileImage = new Image(assets.getTextures().get(Textures.Name.POTATO_SMILE_MOUTH));
        potatoMouthSmileImage.setX(- potatoMouthSmileImage.getPrefWidth() / 2);
        potatoMouthSmileImage.setY(- potatoMouthSmileImage.getPrefHeight() / 2);
        potatoMouthSmileImage.setName("smile");
        Image potatoMouthCloseImage = new Image(assets.getTextures().get(Textures.Name.POTATO_CLOSE_MOUTH));
        potatoMouthCloseImage.setX(- potatoMouthCloseImage.getPrefWidth() / 2);
        potatoMouthCloseImage.setY(-3);
        potatoMouthCloseImage.setVisible(false);
        potatoMouthCloseImage.setName("close");
        potatoMouthTable.addActor(potatoMouthOpenImage);
        potatoMouthTable.addActor(potatoMouthSmileImage);
        potatoMouthTable.addActor(potatoMouthCloseImage);



        mascotsTable.addActor(potatoMouthTable);
        mascotsTable.addActor(tomatoMouthTable);

        Image mascotHands = new Image(assets.getTextures().get(Textures.Name.MASCOT_HANDS_PIXEL));
        mascotHands.setPosition(11, 70);

        coinMachineTable.addActor(mascotsTable);
        coinMachineTable.addActor(bgImage);
        coinMachineTable.add(coinInsertRootTable).width(60).height(70).padRight(padSize).padBottom(8);
        coinMachineTable.add(toInsertCoinsRootTable).width(60).expandY().fillY().padBottom(8).padRight(2);
        coinMachineTable.addActor(mascotHands);


        return coinMachineTable;
    }

    private Table getRetrieveCoinTableDesign(){
        Table root = new Table();
        root.align(Align.top);

        Label.LabelStyle nextCoinLabelStyle1 = new Label.LabelStyle(
                assets.getFonts().get(Fonts.FontId.ARCADE_XS_REGULAR), Color.valueOf("fff08d"));
        Label.LabelStyle nextCoinLabelStyle2 = new Label.LabelStyle(
                assets.getFonts().get(Fonts.FontId.ARCADE_S_REGULAR), Color.valueOf("fff08d"));

        Label nextCoinLabelTitle = new Label(texts.growthRateForShop(), nextCoinLabelStyle1);
        nextCoinLabelTitle.setAlignment(Align.left);
        Label nextCoinLabel = new Label("-", nextCoinLabelStyle2);
        nextCoinLabel.setName("nextCoinLabel");
        nextCoinLabel.setAlignment(Align.left);

        PurseControl purseControl = new PurseControl(assets, -76);
        purseControl.setName("purseControl");
        Table lowerLeftTable = new Table();
        lowerLeftTable.add(purseControl);

        Image coinIcon = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
        Label.LabelStyle coinCountLabelStyle = new Label.LabelStyle(
                assets.getFonts().get(Fonts.FontId.DIGIVOLVE_XXL_REGULAR), Color.valueOf("fff08d"));
        Label coinCountLabel = new Label("x0", coinCountLabelStyle);
        coinCountLabel.setName("coinCountLabel");
        coinCountLabel.setAlignment(Align.left);
        retrieveCoinsButton = getPixelTextButtonDesign(texts.btnTextRetrieveCoins());

        Table lowerRightTable = new Table();
        lowerRightTable.add(coinIcon).size(45, 45).padRight(15).padBottom(10);
        lowerRightTable.add(coinCountLabel).expandX().fillX();
        lowerRightTable.row();
        lowerRightTable.add(nextCoinLabelTitle).colspan(2).expandX().fillX().padBottom(5);
        lowerRightTable.row();
        lowerRightTable.add(nextCoinLabel).colspan(2).expandX().fillX().padBottom(10);
        lowerRightTable.row();
        lowerRightTable.add(retrieveCoinsButton).colspan(2).expandX().fillX();

        root.row();
        root.add(lowerLeftTable).size(purseControl.getWidth(), purseControl.getHeight()).padTop(20);
        root.add(lowerRightTable).expand().fill();
        return root;
    }


    public void render(float delta){
        if(visible){
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }


    public void setInternalListeners(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                overlayImage.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                    }
                });

                getBuyCoinsTabButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        switchTab(CoinMachineTabType.PurchaseCoins);
                    }
                });

                getPlayerInsertCoinStatusTabButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        switchTab(CoinMachineTabType.PlayersInsertCoinStatus);
                    }
                });

                getRetrieveCoinsTabButton().addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        switchTab(CoinMachineTabType.RetrieveCoins);
                    }
                });
            }
        });
    }

    public Table getCoinInsertRootTable() {
        return coinInsertRootTable;
    }

    public TextButton getBuyCoinsTabButton() {
        return buyCoinsTabButton;
    }

    public TextButton getRetrieveCoinsTabButton() {
        return retrieveCoinsTabButton;
    }

    public TextButton getPlayerInsertCoinStatusTabButton() {
        return playerInsertCoinStatusTabButton;
    }

    public Button getLeftButton() {
        return leftButton;
    }

    public Button getRightButton() {
        return rightButton;
    }

    public TextButton getRetrieveCoinsButton() {
        return retrieveCoinsButton;
    }

    public TextButton getDismissButton() {
        return dismissButton;
    }

    public Table getToInsertCoinsRootTable() {
        return toInsertCoinsRootTable;
    }

    public String getClassTag(){
        return this.getClass().getName();
    }

    public boolean isVisible() {
        return visible;
    }



}
