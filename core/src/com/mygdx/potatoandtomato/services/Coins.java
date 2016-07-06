package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.LockPropertyListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.services.CoinsListener;
import com.mygdx.potatoandtomato.absintflis.services.CoinsRetrieveListener;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.enums.ShopProducts;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.potatoandtomato.common.absints.CoinListener;
import com.mygdx.potatoandtomato.controls.CoinMachineControl;
import com.mygdx.potatoandtomato.controls.TopBarCoinControl;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.models.CoinsMeta;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.absints.ICoins;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.*;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 13/6/2016.
 */
public class Coins implements ICoins {

    private Coins _this;
    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private IPTGame iptGame;
    private Broadcaster broadcaster;
    private Confirm confirm;
    private Profile profile;
    private CoinMachineControl coinMachineControl;
    private IDatabase database;
    private GamingKit gamingKit;
    private PTScreen ptScreen;
    private IRestfulApi restfulApi;
    private SafeDouble myCoinsCount;
    private int expectingCoin;
    private String transactionId;
    private boolean puttingCoin;
    private boolean coinsAlreadyEnough;
    private ConcurrentHashMap<String, Integer> currentUsersPutCoinNumberMap;
    private CoinListener coinListener;
    private int deductedSuccessCoinsCount;
    private ShopProducts currentShopProducts;

    private ArrayList<String> noCoinUserIds;
    private ArrayList<Pair<String, String>> userIdToNamePairs;
    private ArrayList<TopBarCoinControl> topBarCoinControls;
    private ArrayList<String> monitoringUserIds;
    private ConcurrentHashMap<String, CoinsListener> tagTocoinsListenersMap;


    public Coins(Broadcaster broadcaster, Assets assets,
                 SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame, SpriteBatch batch,
                 Profile profile, IDatabase database, GamingKit gamingKit, IRestfulApi restfulApi,
                 Confirm confirm) {
        _this = this;
        this.broadcaster = broadcaster;
        this.confirm = confirm;
        this.restfulApi = restfulApi;
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.database = database;
        this.iptGame = iptGame;
        this.profile = profile;
        this.gamingKit = gamingKit;
        this.monitoringUserIds = new ArrayList();
        this.currentUsersPutCoinNumberMap = new ConcurrentHashMap();
        this.noCoinUserIds = new ArrayList();
        this.tagTocoinsListenersMap = new ConcurrentHashMap();
        this.myCoinsCount = new SafeDouble(0.0);

        coinMachineControl = new CoinMachineControl(broadcaster, assets, soundsPlayer, texts, iptGame, batch);
        topBarCoinControls = new ArrayList();

        setListeners();

    }

    public void profileReady(){
        addCoinMonitor(profile.getUserId(), profile.getDisplayName(99));
    }

    private void mePutCoin(){
        if(puttingCoin) return;

        if(getUserPutCoinCount(profile.getUserId()) + 1 > myCoinsCount.getValue().intValue()){
            return;
        }

        puttingCoin = true;

        coinMachineControl.putCoinAnimation(new Runnable() {
            @Override
            public void run() {
            }
        });

        gamingKit.updateRoomMates(UpdateRoomMatesCode.PUT_COIN, transactionId);
    }

    public void initCoinMachine(int expectingCoin, String transactionId, ArrayList<Pair<String, String>> userIdToNamePairs,
                                boolean requestFromOthers){
        coinsAlreadyEnough = false;
        puttingCoin = false;
        this.coinListener = null;
        this.currentUsersPutCoinNumberMap.clear();
        this.expectingCoin = expectingCoin;
        this.transactionId = transactionId;
        this.deductedSuccessCoinsCount = 0;
        this.userIdToNamePairs = userIdToNamePairs;
        sync(userIdToNamePairs);
        if(requestFromOthers) requestCoinsMachineStateFromOthers();
    }


    public void showCoinMachine(){
        coinMachineControl.show();
    }

    public void hideCoinMachine(){
        coinMachineControl.hide();
    }

    private void addCoinMonitor(final String userId, final String username){

        if(!monitoringUserIds.contains(userId)){
            monitoringUserIds.add(userId);
            database.monitorUserCoinsCount(userId, new DatabaseListener<Integer>(Integer.class) {
                @Override
                public void onCallback(Integer obj, Status st) {
                    if (obj == null) {
                        obj = 0;
                    }

                    if(obj == 0 && !noCoinUserIds.contains(userId)){
                        noCoinUserIds.add(userId);
                        for(CoinsListener coinsListener : tagTocoinsListenersMap.values()){
                            coinsListener.userHasCoinChanged(userId, false);
                        }
                    }
                    else if(obj != 0 && noCoinUserIds.contains(userId)){
                        noCoinUserIds.remove(userId);
                        for(CoinsListener coinsListener : tagTocoinsListenersMap.values()){
                            coinsListener.userHasCoinChanged(userId, true);
                        }
                    }

                    if(userId.equals(profile.getUserId())){
                        myCoinsCount.setValue((double) obj);

                        for (TopBarCoinControl topBarCoinControl : topBarCoinControls) {
                            topBarCoinControl.setCoinCount(obj, currentShopProducts);
                        }
                        currentShopProducts = null;
                    }
                }
            });
        }
    }

    private void removeCoinMonitor(String userId){
        if(monitoringUserIds.contains(userId) && !userId.equals(profile.getUserId())){
            monitoringUserIds.remove(userId);
            database.clearListenersByTag(userId);
            noCoinUserIds.remove(userId);
            coinMachineControl.removeUserTable(userId);
            currentUsersPutCoinNumberMap.remove(userId);
        }
    }

    private void coinPutReceived(String fromUserId, String receivedTransactionId){
        if(this.transactionId.equals(receivedTransactionId)){
            userAddCoin(fromUserId);
        }
    }



    private void userAddCoin(String fromUserId){
       if(!coinsAlreadyEnough){
           if(currentUsersPutCoinNumberMap.containsKey(fromUserId)){
               currentUsersPutCoinNumberMap.put(fromUserId, currentUsersPutCoinNumberMap.get(fromUserId) + 1);
           }
           else{
               currentUsersPutCoinNumberMap.put(fromUserId, 1);
           }

           sync(this.userIdToNamePairs);
           checkSufficientCoins();

           if(fromUserId.equals(profile.getUserId())){
               puttingCoin = false;
           }
       }
    }

    private void checkSufficientCoins(){
        int count = getTotalCoinsPut();
        if(count >= expectingCoin){
            coinsAlreadyEnough = true;
            if(coinListener != null) coinListener.onEnoughCoins();
        }
    }

    private int getTotalCoinsPut(){
        int count = 0;
        for(Integer value : currentUsersPutCoinNumberMap.values()){
            count += value;
        }
        return count;
    }

    //will run the runnable only if success in becoming the decision maker
    public void checkMeIsCoinsCollectedDecisionMaker(final Runnable isDecisionMakerRunnable){
        if(userIdToNamePairs.size() <= 1){
            isDecisionMakerRunnable.run();
        }
        else{
            gamingKit.removeLockPropertyListenersByClassTag(getClassTag());
            gamingKit.addListener(getClassTag(), new LockPropertyListener(transactionId) {
                @Override
                public void onLockSucceed() {
                    isDecisionMakerRunnable.run();
                }
            });

            gamingKit.lockProperty(transactionId, profile.getUserId());
        }
    }

    public void startDeductCoins(){
        if(currentUsersPutCoinNumberMap.containsKey(profile.getUserId())){
            database.deductUserCoins(profile.getUserId(),
                    myCoinsCount.getValue().intValue() - currentUsersPutCoinNumberMap.get(profile.getUserId()), new DatabaseListener() {
                @Override
                public void onCallback(Object obj, Status st) {
                    gamingKit.updateRoomMates(st == Status.SUCCESS ? UpdateRoomMatesCode.COINS_DEDUCTED_SUCCESS :
                                                    UpdateRoomMatesCode.COINS_DEDUCTED_FAILED, transactionId);
                }
            });
        }
        else{
            gamingKit.updateRoomMates(UpdateRoomMatesCode.COINS_DEDUCTED_SUCCESS, transactionId);
        }
    }

    private void coinDeductedSuccess(String userId){
        if(currentUsersPutCoinNumberMap.containsKey(userId)){
            deductedSuccessCoinsCount += currentUsersPutCoinNumberMap.get(userId);
        }

        if(deductedSuccessCoinsCount >= expectingCoin){
            if(coinListener != null) coinListener.onDeductCoinsDone(null, Status.SUCCESS);
        }
    }

    private void sync(ArrayList<Pair<String, String>> userIdToNamePairs){
        for(int i = monitoringUserIds.size() - 1; i >= 0; i--){
            String userId = monitoringUserIds.get(i);
            boolean found = false;
            for(Pair<String, String> pair : userIdToNamePairs){
                if(pair.getFirst().equals(userId)){
                    found = true;
                    break;
                }
            }
            if(!found){
                removeCoinMonitor(userId);
            }
        }

        for(Pair<String, String> pair : userIdToNamePairs){
            addCoinMonitor(pair.getFirst(), pair.getSecond());
        }

        for(Pair<String, String> pair : userIdToNamePairs){
            int insertedCoin = 0;
            if(currentUsersPutCoinNumberMap.containsKey(pair.getFirst())){
                insertedCoin = currentUsersPutCoinNumberMap.get(pair.getFirst());
            }
            coinMachineControl.updateUserTable(pair.getFirst(), pair.getSecond(),
                    insertedCoin, !noCoinUserIds.contains(pair.getFirst()));
        }

    }

    private int getUserPutCoinCount(String userId){
        if(currentUsersPutCoinNumberMap.containsKey(userId)){
            return currentUsersPutCoinNumberMap.get(userId);
        }
        else{
            return 0;
        }
    }

    public void reset(){
        for(String userId : monitoringUserIds){
            if(!userId.equals(profile.getUserId())){
                database.clearListenersByTag(userId);
            }
        }
        monitoringUserIds.clear();
        monitoringUserIds.add(profile.getUserId());

        coinMachineControl.hide();
        transactionId = "";
        coinListener = null;
        currentUsersPutCoinNumberMap.clear();
    }

    public void dispose(){
        for(String userId : monitoringUserIds){
            database.clearListenersByTag(userId);
        }

        coinMachineControl.hide();
        monitoringUserIds.clear();
        currentUsersPutCoinNumberMap.clear();
        topBarCoinControls.clear();
    }

    public void requestCoinsMachineStateFromOthers(){
        if(userIdToNamePairs.size() > 1){
            gamingKit.updateRoomMates(UpdateRoomMatesCode.REQUEST_COINS_STATE, transactionId);
        }
    }

    private void coinsMachineStateRequestReceived(String fromUserId){
        if(getTotalCoinsPut() > 0 && !fromUserId.equals(profile.getUserId())){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonObj jsonObj = new JsonObj();
                jsonObj.put("transactionId", transactionId);
                jsonObj.put("currentUsersPutCoinNumberMapJson", objectMapper.writeValueAsString(currentUsersPutCoinNumberMap));
                gamingKit.privateUpdateRoomMates(fromUserId, UpdateRoomMatesCode.COINS_STATE_RESPONSE, jsonObj.toString());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private void coinsMachineStateResponseReceived(String response){
        JsonObj jsonObj = new JsonObj(response);
        if(jsonObj.getString("transactionId").equals(transactionId)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ConcurrentHashMap<String, Integer> receivedUsersPutCoinNumberMap = objectMapper.readValue(jsonObj.getString("currentUsersPutCoinNumberMapJson"),
                        ConcurrentHashMap.class);
                for(String userId : receivedUsersPutCoinNumberMap.keySet()){
                    if(!currentUsersPutCoinNumberMap.containsKey(userId)){
                        currentUsersPutCoinNumberMap.put(userId, 0);
                    }

                    currentUsersPutCoinNumberMap.put(userId,
                            currentUsersPutCoinNumberMap.get(userId) + receivedUsersPutCoinNumberMap.get(userId));
                }
                sync(this.userIdToNamePairs);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void retrieveFreeCoins(final CoinsRetrieveListener coinsRetrieveListener){
        confirm.show(ConfirmIdentifier.Coins, texts.workingDoNotClose(), Confirm.Type.LOADING_NO_CANCEL, null);
        currentShopProducts = ShopProducts.PURSE;
        restfulApi.retrieveCoins(profile, new RestfulApiListener<RetrievableCoinsData>() {
            @Override
            public void onCallback(RetrievableCoinsData obj, Status st) {
                if(st == Status.SUCCESS){
                    coinsRetrieveListener.onFreeCoinsRetrieved(obj);
                }
                confirm.close(ConfirmIdentifier.Coins);
            }
        });
    }

    public void watchAds(){
        currentShopProducts = ShopProducts.ONE_COIN;
        broadcaster.broadcast(BroadcastEvent.SHOW_REWARD_VIDEO);
    }

    public void purchaseCoins(CoinProduct coinProduct){
        currentShopProducts = coinProduct.getShopProductType();
        confirm.show(ConfirmIdentifier.Coins, texts.workingDoNotClose(), Confirm.Type.LOADING_NO_CANCEL, null);
        broadcaster.subscribeOnceWithTimeout(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE, 60 * 1000, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                confirm.close(ConfirmIdentifier.Coins);
                if (st != Status.SUCCESS) {
                    confirm.show(ConfirmIdentifier.Coins, texts.purchaseFailed(), Confirm.Type.YES, null);
                }
            }
        });
        broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE, new Pair<String, IRestfulApi>(coinProduct.getId(), restfulApi));
    }



    public void setListeners(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                coinMachineControl.getCoinInsertRootTable().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        mePutCoin();
                    }
                });
            }
        });

        gamingKit.addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                if(code == UpdateRoomMatesCode.PUT_COIN){       //msg is transactionId
                    coinPutReceived(senderId, msg);
                }
                else if(code == UpdateRoomMatesCode.COINS_DEDUCTED_SUCCESS){
                    if(msg.equals(_this.transactionId)){        //msg is transactionId
                        coinDeductedSuccess(senderId);
                    }
                }
                else if(code == UpdateRoomMatesCode.COINS_DEDUCTED_FAILED){
                    if(msg.equals(_this.transactionId)){        //msg is transactionId
                        if(coinListener != null) coinListener.onDeductCoinsDone(senderId, Status.FAILED);
                    }
                }
                else if(code == UpdateRoomMatesCode.REQUEST_COINS_STATE){
                    if(msg.equals(_this.transactionId)){        //msg is transactionId
                        coinsMachineStateRequestReceived(senderId);
                    }
                }
                else if(code == UpdateRoomMatesCode.COINS_STATE_RESPONSE){
                    coinsMachineStateResponseReceived(msg);        //msg is json
                }
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });
    }

    public void addCoinsListener(String classTag, CoinsListener listener){
        tagTocoinsListenersMap.put(classTag, listener);

        for(String userID : noCoinUserIds){
            listener.userHasCoinChanged(userID, false);
        }

    }

    public void removeCoinsListenersByClassTag(String classTag){
        tagTocoinsListenersMap.remove(classTag);
    }

    public void render(float delta){
        coinMachineControl.render(delta);
    }

    public void resize(int width, int height){
        coinMachineControl.resize(width, height);
    }

    public ArrayList<CoinsMeta> getCurrentUsersPutCoinsMeta(){
        ArrayList<CoinsMeta> result = new ArrayList();
        for(String userId : currentUsersPutCoinNumberMap.keySet()){
            result.add(new CoinsMeta(userId, currentUsersPutCoinNumberMap.get(userId)));
        }
        return result;
    }

    private String getClassTag(){
        return this.getClass().getName();
    }

    public TopBarCoinControl getNewTopBarCoinControl(boolean disableClick) {
        TopBarCoinControl topBarCoinControl = new TopBarCoinControl(assets, myCoinsCount.getValue().intValue(),
                                            disableClick, ptScreen, soundsPlayer);
        topBarCoinControls.add(topBarCoinControl);
        return topBarCoinControl;
    }

    public void setPtScreen(PTScreen ptScreen) {
        this.ptScreen = ptScreen;
    }

    public void setCoinListener(CoinListener coinListener) {
        this.coinListener = coinListener;
    }

    public boolean checkUserHasCoin(String userId){
         return !noCoinUserIds.contains(userId);
    }

}
