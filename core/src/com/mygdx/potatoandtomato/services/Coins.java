package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.services.CoinListener;
import com.mygdx.potatoandtomato.controls.CoinMachineControl;
import com.mygdx.potatoandtomato.controls.TopBarCoinControl;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.MultiHashMap;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Threadings;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 13/6/2016.
 */
public class Coins {

    private Coins _this;
    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private IPTGame iptGame;
    private Profile profile;
    private CoinMachineControl coinMachineControl;
    private IDatabase database;
    private GamingKit gamingKit;
    private int myCoinsCount;
    private int expectingCoin;
    private String transactionId;
    private boolean puttingCoin;
    private boolean coinsAlreadyEnough;
    private MultiHashMap<String, String> transactionIdsToPutCoinUserMap;
    private ConcurrentHashMap<String, Integer> currentUsersPutCoinNumberMap;
    private CoinListener coinListener;
    private int deductedSuccessCoinsCount;
    private boolean waitingRefresh;

    private ArrayList<String> noCoinUserIds;
    private ArrayList<Pair<String, String>> userIdToNamePairs;
    private ArrayList<TopBarCoinControl> topBarCoinControls;
    private ArrayList<String> monitoringUserIds;


    public Coins(Broadcaster broadcaster, Assets assets,
                 SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame, SpriteBatch batch,
                 Profile profile, IDatabase database, GamingKit gamingKit) {
        _this = this;
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.database = database;
        this.iptGame = iptGame;
        this.profile = profile;
        this.gamingKit = gamingKit;
        this.monitoringUserIds = new ArrayList();
        this.transactionIdsToPutCoinUserMap = new MultiHashMap();
        this.currentUsersPutCoinNumberMap = new ConcurrentHashMap();
        this.noCoinUserIds = new ArrayList();

        coinMachineControl = new CoinMachineControl(broadcaster, assets, soundsPlayer, texts, iptGame, batch);
        topBarCoinControls = new ArrayList();

        setListeners();
    }

    public void profileReady(){
        addCoinMonitor(profile.getUserId(), profile.getDisplayName(99));
    }

    private void mePutCoin(){
        if(puttingCoin) return;

        puttingCoin = true;

        coinMachineControl.putCoinAnimation(new Runnable() {
            @Override
            public void run() {
                puttingCoin = false;
            }
        });

        gamingKit.updateRoomMates(UpdateRoomMatesCode.PUT_COIN, transactionId);
    }

    public void initCoinMachine(int expectingCoin, String transactionId, ArrayList<Pair<String, String>> userIdToNamePairs){
        coinsAlreadyEnough = false;
        puttingCoin = false;
        waitingRefresh = false;
        this.coinListener = null;
        this.currentUsersPutCoinNumberMap.clear();
        this.expectingCoin = expectingCoin;
        this.transactionId = transactionId;
        this.deductedSuccessCoinsCount = 0;
        this.userIdToNamePairs = userIdToNamePairs;
        syncUsers(userIdToNamePairs);
        runStoredTransactionsIdsAction();
    }

    private void runStoredTransactionsIdsAction(){
        if(transactionIdsToPutCoinUserMap.containsKey(transactionId)){
            for(String userId : transactionIdsToPutCoinUserMap.get(transactionId)){
                userAddCoin(userId);
            }
            transactionIdsToPutCoinUserMap.remove(transactionId);
        }
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
            database.monitorUserCoinsCount(profile.getUserId(), new DatabaseListener<Integer>() {
                @Override
                public void onCallback(Integer obj, Status st) {
                    if (obj == null) {
                        obj = 0;
                    }

                    if(obj == 0 && !noCoinUserIds.contains(userId)){
                        noCoinUserIds.add(userId);
                    }
                    else if(obj != 0 && noCoinUserIds.contains(userId)){
                        noCoinUserIds.remove(userId);
                    }

                    if(userId.equals(profile.getUserId())){
                        myCoinsCount = obj;

                        for (TopBarCoinControl topBarCoinControl : topBarCoinControls) {
                            topBarCoinControl.setCoinCount(obj);
                        }
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
        }
    }

    private void coinPutReceived(String fromUserId, String receivedTransactionId){
        if(!this.transactionId.equals(receivedTransactionId)){
            transactionIdsToPutCoinUserMap.put(receivedTransactionId, fromUserId);
        }
        else{
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

           refreshCoinsMachine();
           checkSufficientCoins();
       }
    }

    private void checkSufficientCoins(){
        int count = 0;
        for(Integer value : currentUsersPutCoinNumberMap.values()){
            count += value;
        }

        if(count >= expectingCoin){
            coinsAlreadyEnough = true;
            coinListener.onEnoughCoins();
        }
    }

    public void startDeductCoins(){
        if(currentUsersPutCoinNumberMap.containsKey(profile.getUserId())){
            database.deductUserCoins(profile.getUserId(),
                    myCoinsCount - currentUsersPutCoinNumberMap.get(profile.getUserId()), new DatabaseListener() {
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
            coinListener.onDeductCoinsDone(null, Status.SUCCESS);
        }
    }

    private void syncUsers(ArrayList<Pair<String, String>> userIdToNamePairs){
        for(int i = monitoringUserIds.size() - 1; i >= 0; i--){
            String userId = monitoringUserIds.get(i);
            if(!userId.equals(profile.getUserId())){
                database.clearListenersByTag(userId);
                monitoringUserIds.remove(userId);
            }
        }

        ArrayList<String> toRemoveUserIds = new ArrayList();

        for(Pair<String, String> pair : userIdToNamePairs){
            toRemoveUserIds.add(pair.getFirst());
            addCoinMonitor(pair.getFirst(), pair.getSecond());
        }

        for(String userId : monitoringUserIds){
            if(toRemoveUserIds.contains(userId)) {
                toRemoveUserIds.remove(userId);
            }
        }

        for(String userId : toRemoveUserIds){
            removeCoinMonitor(userId);
        }
    }


    public void reset(){
        for(String userId : monitoringUserIds){
            database.clearListenersByTag(userId);
        }

        monitoringUserIds.clear();
        transactionIdsToPutCoinUserMap.clear();
        currentUsersPutCoinNumberMap.clear();
        topBarCoinControls.clear();
    }

    private void refreshCoinsMachine(){
        for(Pair<String, String> pair : userIdToNamePairs){
            int insertedCoin = 0;
            if(currentUsersPutCoinNumberMap.containsKey(pair.getFirst())){
                insertedCoin = currentUsersPutCoinNumberMap.get(pair.getFirst());
            }
            coinMachineControl.updateUserTable(pair.getFirst(), pair.getSecond(),
                    insertedCoin, !noCoinUserIds.contains(pair.getFirst()));
        }

    }

    public void requestCoinsMachineStateFromOthers(){
        if(userIdToNamePairs.size() > 1 && !waitingRefresh){
            waitingRefresh = true;
            gamingKit.updateRoomMates(UpdateRoomMatesCode.REQUEST_COINS_STATE, transactionId);
        }
    }

    private void coinsMachineStateRequestReceived(String fromUserId){
        if(!waitingRefresh && !fromUserId.equals(profile.getUserId())){
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
        if(waitingRefresh){
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

                    waitingRefresh = false;
                    runStoredTransactionsIdsAction();
                    refreshCoinsMachine();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
                        coinListener.onDeductCoinsDone(senderId, Status.FAILED);
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

    public void render(float delta){
        coinMachineControl.render(delta);
    }

    public void resize(int width, int height){
        coinMachineControl.resize(width, height);
    }

    private String getClassTag(){
        return this.getClass().getName();
    }

    public TopBarCoinControl getNewTopBarCoinControl() {
        TopBarCoinControl topBarCoinControl = new TopBarCoinControl(assets, myCoinsCount);
        topBarCoinControls.add(topBarCoinControl);
        return topBarCoinControl;
    }

    public void setCoinListener(CoinListener coinListener) {
        this.coinListener = coinListener;
    }
}
