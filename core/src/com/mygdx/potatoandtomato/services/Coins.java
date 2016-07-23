package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.LockPropertyListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.services.ClientInternalCoinListener;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.CoinMachineTabType;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.enums.ShopProducts;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.models.RetrievableCoinsData;
import com.potatoandtomato.common.statics.Vars;
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
import com.potatoandtomato.common.enums.SpeechActionType;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.SpeechAction;
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
    private ConnectionWatcher connectionWatcher;

    private Profile profile;
    private CoinMachineControl coinMachineControl;
    private IDatabase database;
    private GamingKit gamingKit;
    private PTScreen ptScreen;
    private IRestfulApi restfulApi;
    private SafeDouble myCoinsCount;
    private String coinsPurpose;
    private int expectingCoin;
    private String transactionId;
    private String dismissText;
    private boolean puttingCoin;
    private boolean coinsAlreadyEnough;
    private boolean waitingDeductCoinResult;
    private boolean cancelPutCoins;
    private ConcurrentHashMap<String, Integer> currentUsersPutCoinNumberMap;
    private CoinListener coinListener;
    private ShopProducts currentShopProduct;

    private SafeThread waitingDeductCoinResultSafeThread;
    private SafeThread tomatoSpeechSafeThread, potatoSpeechSafeThread;
    private ArrayList<SpeechAction> tomatoDefaultSpeechActions;
    private ArrayList<SpeechAction> potatoDefaultSpeechActions;
    private ArrayList<CoinProduct> coinProducts;
    private int currentProductIndex;
    private ArrayList<String> noCoinUserIds;
    private ArrayList<Pair<String, String>> userIdToNamePairs;
    private ArrayList<TopBarCoinControl> topBarCoinControls;
    private ArrayList<String> monitoringUserIds;
    private ArrayList<String> deductedSuccessTransactions;
    private HashMap<String, String> dismissedTransactionsMap;
    private ConcurrentHashMap<String, ClientInternalCoinListener> tagToClientInternalCoinListenersMap;
    private DataCaches dataCaches;


    public Coins(Broadcaster broadcaster, Assets assets,
                 SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame, SpriteBatch batch,
                 Profile profile, IDatabase database, GamingKit gamingKit, IRestfulApi restfulApi,
                 Confirm confirm, ConnectionWatcher connectionWatcher, DataCaches dataCaches) {
        _this = this;
        this.broadcaster = broadcaster;
        this.confirm = confirm;
        this.restfulApi = restfulApi;
        this.assets = assets;
        this.connectionWatcher = connectionWatcher;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.database = database;
        this.iptGame = iptGame;
        this.profile = profile;
        this.gamingKit = gamingKit;
        this.monitoringUserIds = new ArrayList();
        this.currentUsersPutCoinNumberMap = new ConcurrentHashMap();
        this.noCoinUserIds = new ArrayList();
        this.tagToClientInternalCoinListenersMap = new ConcurrentHashMap();
        this.deductedSuccessTransactions = new ArrayList();
        this.dismissedTransactionsMap = new HashMap();
        this.myCoinsCount = new SafeDouble(0.0);
        this.dataCaches = dataCaches;

        coinMachineControl = new CoinMachineControl(broadcaster, assets, soundsPlayer, texts, iptGame, batch);
        topBarCoinControls = new ArrayList();

        setListeners();

    }

    public void profileReady(){
        addCoinMonitor(profile.getUserId(), profile.getDisplayName(99));
    }

    private void mePutCoin(){
        if(puttingCoin || coinsAlreadyEnough) return;

        if(getUserPutCoinCount(profile.getUserId()) + 1 > myCoinsCount.getValue().intValue()){
            soundsPlayer.playSoundEffect(Sounds.Name.WRONG);
            startSpeech(CoinMachineTabType.NoMoreCoins);
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

    public void initCoinMachine(String coinsPurpose, int expectingCoin, String transactionId, ArrayList<Pair<String, String>> userIdToNamePairs,
                                boolean requestFromOthers, ArrayList<SpeechAction> potatoSpeechActions,
                                ArrayList<SpeechAction> tomatoSpeechActions, String dismissText){
        coinsAlreadyEnough = false;
        puttingCoin = false;
        cancelPutCoins = false;
        this.coinsPurpose = coinsPurpose;
        this.coinListener = null;
        this.currentUsersPutCoinNumberMap.clear();
        this.expectingCoin = expectingCoin;
        this.transactionId = transactionId;
        this.userIdToNamePairs = userIdToNamePairs;
        this.potatoDefaultSpeechActions = potatoSpeechActions;
        this.tomatoDefaultSpeechActions = tomatoSpeechActions;
        this.dismissText = dismissText;
        this.coinMachineControl.updateExpectingCoins(expectingCoin);
        this.coinMachineControl.updateDismissText(dismissText);
        sync(userIdToNamePairs);
        if(requestFromOthers) requestCoinsMachineStateFromOthers();

        database.signCoinDecreaseAgreement(profile.getUserId(), transactionId, null);
    }

    public void reinitCoinMachine(){
        hideCoinMachine();
        initCoinMachine(coinsPurpose, expectingCoin, transactionId, userIdToNamePairs, false,
                potatoDefaultSpeechActions, tomatoDefaultSpeechActions, dismissText);
        showCoinMachine(false);
    }


    public void showCoinMachine(boolean forceShow){
        if(forceShow ||
                !dismissedTransactionsMap.containsKey(transactionId) && !deductedSuccessTransactions.contains(transactionId)){
            confirm.close(Confirm.Type.YESNO);
            coinMachineControl.updateMyCoinsCount(myCoinsCount.getValue().intValue());
            coinMachineControl.show();
            refreshRetrievableCoins();
            refreshCoinMachineProducts();
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    startSpeech(CoinMachineTabType.PlayersInsertCoinStatus);
                }
            });
        }
    }

    public void hideCoinMachine(){
        coinMachineControl.hide(new Runnable() {
            @Override
            public void run() {
                stopSpeech(true);
                stopSpeech(false);
            }
        });
    }

    public void cancelPutCoins(){
        cancelPutCoins = true;
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
                        for(ClientInternalCoinListener clientInternalCoinListener : tagToClientInternalCoinListenersMap.values()){
                            clientInternalCoinListener.userHasCoinChanged(userId, false);
                        }
                    }
                    else if(obj != 0 && noCoinUserIds.contains(userId)){
                        noCoinUserIds.remove(userId);
                        for(ClientInternalCoinListener clientInternalCoinListener : tagToClientInternalCoinListenersMap.values()){
                            clientInternalCoinListener.userHasCoinChanged(userId, true);
                        }
                    }

                    if(userId.equals(profile.getUserId())){
                        myCoinsCount.setValue((double) obj);
                        coinMachineControl.updateMyCoinsCount(myCoinsCount.getValue().intValue());

                        for (TopBarCoinControl topBarCoinControl : topBarCoinControls) {
                            topBarCoinControl.setCoinCount(obj, currentShopProduct);
                        }
                        currentShopProduct = null;
                    }

                    updateCoinMachineUserTable(userId, username);
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
               coinMachineControl.updateMyCoinsCount(
                       myCoinsCount.getValue().intValue() - getUserPutCoinCount(profile.getUserId()));
               puttingCoin = false;
           }
       }
    }

    private void checkSufficientCoins(){
        int count = getTotalCoinsPut();
        updateExpectingCoins();
        if(count >= expectingCoin){
            coinsAlreadyEnough = true;
            startSpeech(CoinMachineTabType.EnoughCoinsInserted);
            Threadings.delayNoPost(2000, new Runnable() {
                @Override
                public void run() {
                    if(cancelPutCoins) return;
                    if (coinListener != null) coinListener.onEnoughCoins();
                }
            });
        }
    }

    private int getTotalCoinsPut(){
        int count = 0;
        for(Integer value : currentUsersPutCoinNumberMap.values()){
            count += value;
        }
        return count;
    }

    private void updateExpectingCoins(){
        int count = getTotalCoinsPut();
        this.coinMachineControl.updateExpectingCoins(expectingCoin - count);
    }

    //will run the runnable only if success in becoming the decision maker
    public void checkMeIsCoinsCollectedDecisionMaker(final Runnable isDecisionMakerRunnable){
        if(userIdToNamePairs  != null && userIdToNamePairs.size() <= 1){
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
        waitingDeductCoinResult = true;
        checkMeIsCoinsCollectedDecisionMaker(new Runnable() {
            @Override
            public void run() {
                callDeductCoins();
            }
        });

        startDeductCoinsWaitingThread();
    }

    private void callDeductCoins(){
        restfulApi.useCoins(getCurrentUsersPutCoinsMeta(), transactionId, expectingCoin, coinsPurpose, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if(st == Status.FAILED){
                    gamingKit.updateRoomMates(UpdateRoomMatesCode.COINS_DEDUCTED_FAILED, transactionId);
                }
                else{
                    gamingKit.updateRoomMates(UpdateRoomMatesCode.COINS_DEDUCTED_SUCCESS, transactionId);
                }
            }
        });
    }

    private void coinDeductedSuccess(){
        if(cancelPutCoins) return;
        waitingDeductCoinResult = false;
        if(waitingDeductCoinResultSafeThread != null) waitingDeductCoinResultSafeThread.kill();
        if(coinListener != null) coinListener.onDeductCoinsDone();
        deductedSuccessTransactions.add(transactionId);
        reset();
    }

    private void coinDeductFailed(){
        if(cancelPutCoins) return;
        waitingDeductCoinResult = false;
        if(waitingDeductCoinResultSafeThread != null) waitingDeductCoinResultSafeThread.kill();
        reinitCoinMachine();
    }

    //wait 10 secs, if no respond received, call deduct coins api myself
    private void startDeductCoinsWaitingThread(){
        waitingDeductCoinResultSafeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 20){
                    if(waitingDeductCoinResultSafeThread.isKilled()) return;
                    else{
                        Threadings.sleep(500);
                    }
                    i++;
                }

                //if still waiting, call deduct myself
                if(cancelPutCoins) return;
                if(waitingDeductCoinResult) callDeductCoins();
            }
        });

    }

    public void sync(ArrayList<Pair<String, String>> userIdToNamePairs){
        if(userIdToNamePairs == null) return;

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
            updateCoinMachineUserTable(pair.getFirst(), pair.getSecond());
        }

    }

    public void updateCoinMachineUserTable(String userId, String username){
        int insertedCoin = getUserPutCoinCount(userId);

        coinMachineControl.updateUserTable(userId, username,
                insertedCoin, !noCoinUserIds.contains(userId));
    }


    private int getUserPutCoinCount(String userId){
        if(currentUsersPutCoinNumberMap.containsKey(userId)){
            return currentUsersPutCoinNumberMap.get(userId);
        }
        else{
            return 0;
        }
    }

    private void dismissWindowsReceived(String fromUserId){
        if(!coinsAlreadyEnough){
            cancelPutCoins = true;
            dismissedTransactionsMap.put(transactionId, fromUserId);
            if(coinListener != null) coinListener.onDismiss(fromUserId);
            hideCoinMachine();
            clearAll();
        }
    }

    public void onUserDisconnected(String dcUserId){
        currentUsersPutCoinNumberMap.remove(dcUserId);
        updateExpectingCoins();
        sync(this.userIdToNamePairs);
    }

    /////////////////////////////////////////////////////
    //about speeching
    ////////////////////////////////////////////////////////
    private void startSpeech(final CoinMachineTabType coinMachineTabType){
        ArrayList<SpeechAction> potatoSpeechActions = null, tomatoSpeechActions = null;
        if(coinMachineTabType == CoinMachineTabType.PlayersInsertCoinStatus){
            potatoSpeechActions = potatoDefaultSpeechActions;
            tomatoSpeechActions = tomatoDefaultSpeechActions;
        }
        else if(coinMachineTabType == CoinMachineTabType.RetrieveCoins){
            Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> pair = texts.getRandomMascotsSpeechAboutPurse();
            potatoSpeechActions = pair.getFirst();
            tomatoSpeechActions = pair.getSecond();
        }
        else if(coinMachineTabType == CoinMachineTabType.PurchaseCoins){
            Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> pair = texts.getRandomMascotsSpeechAboutPurchaseCoins();
            potatoSpeechActions = pair.getFirst();
            tomatoSpeechActions = pair.getSecond();
        }
        else if(coinMachineTabType == CoinMachineTabType.EnoughCoinsInserted){
            Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> pair = texts.getRandomMascotsSpeechAboutEnoughCoin();
            potatoSpeechActions = pair.getFirst();
            tomatoSpeechActions = pair.getSecond();
        }
        else if(coinMachineTabType == CoinMachineTabType.NoMoreCoins){
            Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> pair = texts.getRandomMascotsSpeechAboutNoMoreCoins();
            potatoSpeechActions = pair.getFirst();
            tomatoSpeechActions = pair.getSecond();
        }


        if(potatoSpeechActions.size() > 0){
            stopSpeech(true);
            final ArrayList<SpeechAction> finalPotatoSpeechActions = potatoSpeechActions;
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    SafeThread safeThread = new SafeThread();
                    potatoSpeechSafeThread = safeThread;
                    for(SpeechAction speechAction : finalPotatoSpeechActions){
                        handleSpeech(true, speechAction);
                        if(safeThread.isKilled()) break;
                    }
                }
            });
        }

        if(tomatoSpeechActions.size() > 0){
            stopSpeech(false);
            final ArrayList<SpeechAction> finalTomatoSpeechActions = tomatoSpeechActions;
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    SafeThread safeThread = new SafeThread();
                    tomatoSpeechSafeThread = safeThread;
                    for(SpeechAction speechAction : finalTomatoSpeechActions){
                        handleSpeech(false, speechAction);
                        if(safeThread.isKilled()) break;
                    }
                }
            });
        }

    }

    private void handleSpeech(boolean potato, SpeechAction speechAction){
        if(speechAction.getSpeechActionType() == SpeechActionType.Add){
            SafeThread safeThread;
            if(potato){
                safeThread = potatoSpeechSafeThread;
            }
            else{
                safeThread = tomatoSpeechSafeThread;
            }

            coinMachineControl.startSpeechAnimation(potato);
            long eachCharDuration = speechAction.getEachCharDuration();

            String msg = processSpeech(speechAction.getMsg());
            String[] arr = msg.split(" ");
            for(String word : arr){
                for (int i = 0; i < word.length(); i++) {
                    if(safeThread.isKilled()) break;
                    coinMachineControl.updateSpeechText(potato, String.valueOf(word.charAt(i)), i == 0 ? word : "");
                    Threadings.sleep(eachCharDuration);
                }
                if(safeThread.isKilled()) break;
                coinMachineControl.updateSpeechText(potato, " ", " ");
                Threadings.sleep(eachCharDuration);
            }
            if(!safeThread.isKilled()) coinMachineControl.stopSpeechAnimation(potato);
        }
        else if(speechAction.getSpeechActionType() == SpeechActionType.Delay){
            Threadings.sleep(speechAction.getEachCharDuration());
        }
        else if(speechAction.getSpeechActionType() == SpeechActionType.Clear){
           coinMachineControl.clearSpeechText(potato);
        }
    }

    private void stopSpeech(final boolean potato){
        if(potato){
            if(potatoSpeechSafeThread != null) potatoSpeechSafeThread.kill();
        }
        else{
            if(tomatoSpeechSafeThread != null) tomatoSpeechSafeThread.kill();
        }
        coinMachineControl.stopSpeechAnimation(potato);
        coinMachineControl.clearSpeechText(potato);
    }

    private String processSpeech(String input){
        return input.replace("%expectingCoin%", String.valueOf(expectingCoin));
    }

    /////////////////////////////////////////////////////
    //about request coin machine states
    ////////////////////////////////////////////////////////
    public void requestCoinsMachineStateFromOthers(){
        if(userIdToNamePairs != null && userIdToNamePairs.size() > 1){
            gamingKit.updateRoomMates(UpdateRoomMatesCode.REQUEST_COINS_STATE, transactionId);
        }
    }

    private void coinsMachineStateRequestReceived(String fromUserId, String askingTransactionId){
        if(deductedSuccessTransactions.contains(askingTransactionId)){
            //coins already deducted success, no need reply state again, instead resend coin deduct success update to sender
            gamingKit.privateUpdateRoomMates(fromUserId, UpdateRoomMatesCode.COINS_DEDUCTED_SUCCESS, askingTransactionId);
        }
        else if(dismissedTransactionsMap.containsKey(askingTransactionId)){
            //similar to above
            gamingKit.privateUpdateRoomMates(fromUserId, UpdateRoomMatesCode.COINS_WINDOW_DISMISS,
                    dismissedTransactionsMap.get(askingTransactionId));
        }
        else{
            if(askingTransactionId.equals(this.transactionId) && getTotalCoinsPut() > 0 && !fromUserId.equals(profile.getUserId())){
                try {
                    ObjectMapper objectMapper = Vars.getObjectMapper();
                    JsonObj jsonObj = new JsonObj();
                    jsonObj.put("transactionId", transactionId);
                    jsonObj.put("currentUsersPutCoinNumberMapJson", objectMapper.writeValueAsString(currentUsersPutCoinNumberMap));
                    gamingKit.privateUpdateRoomMates(fromUserId, UpdateRoomMatesCode.COINS_STATE_RESPONSE, jsonObj.toString());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void coinsMachineStateResponseReceived(String response){
        JsonObj jsonObj = new JsonObj(response);
        if(jsonObj.getString("transactionId").equals(transactionId)){
            ObjectMapper objectMapper = Vars.getObjectMapper();
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
                updateExpectingCoins();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /////////////////////////////////////////////////////
    //about free coins
    ////////////////////////////////////////////////////////
    private RetrievableCoinsData currentRetrievableCoinsData;
    private SafeThread retrievableCoinsDataSafeThread;
    private CacheListener<RetrievableCoinsData> retrieveCoinsCacheListener;

    public void refreshRetrievableCoins(){
        if(retrievableCoinsDataSafeThread != null) retrievableCoinsDataSafeThread.kill();
        if(retrieveCoinsCacheListener != null) retrieveCoinsCacheListener.dispose();

        retrieveCoinsCacheListener = new CacheListener<RetrievableCoinsData>() {
            @Override
            public void onResult(RetrievableCoinsData result) {
                currentRetrievableCoinsData = result;
                retrievableCoinsChanged();
            }
        };
        dataCaches.getRetrieveCoinDataCache().getData(retrieveCoinsCacheListener);
    }

    public void retrievableCoinsChanged(){
        for(ClientInternalCoinListener clientInternalCoinListener : tagToClientInternalCoinListenersMap.values()){
            clientInternalCoinListener.retrievableCoinChanged(currentRetrievableCoinsData);
        }
        if(retrievableCoinsDataSafeThread != null) retrievableCoinsDataSafeThread.kill();

        if(currentRetrievableCoinsData.getCanRetrieveCoinsCount() < currentRetrievableCoinsData.getMaxRetrieveableCoins()){
            final SafeThread safeThread = new SafeThread();
            retrievableCoinsDataSafeThread = safeThread;
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    int duration = currentRetrievableCoinsData.getNextCoinInSecs();
                    while (true){
                        if(safeThread.isKilled()) break;
                        else{
                            coinMachineControl.updatePurse(currentRetrievableCoinsData.getCanRetrieveCoinsCount(), duration);
                            for(ClientInternalCoinListener clientInternalCoinListener : tagToClientInternalCoinListenersMap.values()){
                                clientInternalCoinListener.nextCoinTimeChanged(duration);
                            }
                            Threadings.sleep(1000);
                            duration--;
                            if(duration <= -1){
                                addPurseRetrievableCount();
                                break;
                            }
                        }
                    }
                }
            });
        }
        else{
            coinMachineControl.updatePurse(currentRetrievableCoinsData.getCanRetrieveCoinsCount(), -1);
        }

    }

    public void addPurseRetrievableCount(){
        currentRetrievableCoinsData.setCanRetrieveCoinsCount(currentRetrievableCoinsData.getCanRetrieveCoinsCount() + 1);
        currentRetrievableCoinsData.setNextCoinInSecs(currentRetrievableCoinsData.getSecsPerCoin());
        retrievableCoinsChanged();
    }


    public boolean retrieveFreeCoins(){
        if(currentRetrievableCoinsData != null && currentRetrievableCoinsData.getCanRetrieveCoinsCount() > 0){
            confirm.show(ConfirmIdentifier.Coins, texts.workingDoNotClose(), Confirm.Type.LOADING_NO_CANCEL, null);
            currentShopProduct = ShopProducts.PURSE;
            restfulApi.retrieveCoins(profile, new RestfulApiListener<RetrievableCoinsData>() {
                @Override
                public void onCallback(RetrievableCoinsData obj, Status st) {
                    if(st == Status.SUCCESS){
                        if(retrievableCoinsDataSafeThread != null) retrievableCoinsDataSafeThread.kill();
                        currentRetrievableCoinsData = obj;
                        retrievableCoinsChanged();
                        dataCaches.getRetrieveCoinDataCache().resetCache();
                    }
                    confirm.close(ConfirmIdentifier.Coins);
                }
            });
            return true;
        }
        else{
            return false;
        }
    }

    /////////////////////////////////////////////////////
    //about ads
    ////////////////////////////////////////////////////////
    public void watchAds(){
        currentShopProduct = ShopProducts.ONE_COIN;
        broadcaster.broadcast(BroadcastEvent.SHOW_REWARD_VIDEO);
    }


    /////////////////////////////////////////////////////
    //about purchase coins
    ////////////////////////////////////////////////////////
    public void refreshCoinMachineProducts(){
        getProducts(new ClientInternalCoinListener() {
            @Override
            public void onProductsRetrieved(ArrayList<CoinProduct> refreshedCoinProducts) {
                super.onProductsRetrieved(refreshedCoinProducts);
                coinProducts = refreshedCoinProducts;
                changeCoinMachineCurrentProduct(true, coinProducts.get(0));
            }
        });
    }

    public void getProducts(final ClientInternalCoinListener clientInternalCoinListener){
        broadcaster.subscribeOnce(BroadcastEvent.IAB_PRODUCTS_RESPONSE, new BroadcastListener<ArrayList<CoinProduct>>() {
            @Override
            public void onCallback(ArrayList<CoinProduct> refreshedCoinProducts, Status st) {
                if (st == Status.SUCCESS) {
                    clientInternalCoinListener.onProductsRetrieved(refreshedCoinProducts);
                }
            }
        });

        broadcaster.broadcast(BroadcastEvent.IAB_PRODUCTS_REQUEST, database);
    }

    public void changeCoinMachineCurrentProduct(boolean next, CoinProduct coinProduct){
        coinMachineControl.goToNextProduct(next, coinProduct, new RunnableArgs<TextButton>() {
            @Override
            public void run() {
                setBuyButtonListener(this.getFirstArg());
            }
        });
    }


    public void purchaseCoins(CoinProduct coinProduct){
        currentShopProduct = coinProduct.getShopProductType();
        confirm.show(ConfirmIdentifier.Coins, texts.workingDoNotClose(), Confirm.Type.LOADING_NO_CANCEL, null);
        broadcaster.subscribeOnceWithTimeout(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE, 60 * 1000, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                confirm.close(ConfirmIdentifier.Coins);
                if (st != Status.SUCCESS) {
                    confirm.show(ConfirmIdentifier.Coins, texts.confirmPurchaseFailed(), Confirm.Type.YES, null);
                }
            }
        });
        broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE, new Pair<String, IRestfulApi>(coinProduct.getId(), restfulApi));
    }

    /////////////////////////////////////////////////////
    //disposing
    ////////////////////////////////////////////////////////
    //clear inserted coins data, but not monitoring data
    public void clearAll(){
        coinMachineControl.updateMyCoinsCount(myCoinsCount.getValue().intValue());
        currentUsersPutCoinNumberMap.clear();
        puttingCoin = false;
        sync(this.userIdToNamePairs);
    }

    //remove everything except my own coin data
    public void reset(){
        for(String userId : monitoringUserIds){
            if(!userId.equals(profile.getUserId())){
                database.clearListenersByTag(userId);
                noCoinUserIds.remove(userId);
                coinMachineControl.removeUserTable(userId);
            }
        }

        monitoringUserIds.clear();
        monitoringUserIds.add(profile.getUserId());

        transactionId = "";
        coinListener = null;
        currentUsersPutCoinNumberMap.clear();
    }

    //remove everything
    public void dispose(){
        reset();

        for(String userId : monitoringUserIds){
            database.clearListenersByTag(userId);
        }

        coinMachineControl.removeUserTable(profile.getUserId());
        monitoringUserIds.clear();
        noCoinUserIds.clear();
        topBarCoinControls.clear();
    }


    /////////////////////////////////////////////////////
    //listeners and getters
    ////////////////////////////////////////////////////////
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

                coinMachineControl.getLeftButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        if(coinMachineControl.canGoToNextProduct()){
                            currentProductIndex--;
                            if(currentProductIndex < 0) currentProductIndex = coinProducts.size() - 1;
                            changeCoinMachineCurrentProduct(false, coinProducts.get(currentProductIndex));
                        }
                    }
                });

                coinMachineControl.getRightButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        if(coinMachineControl.canGoToNextProduct()){
                            currentProductIndex++;
                            if(currentProductIndex > coinProducts.size() - 1) currentProductIndex = 0;
                            changeCoinMachineCurrentProduct(true, coinProducts.get(currentProductIndex));
                        }
                    }
                });

                coinMachineControl.getRetrieveCoinsButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        if(!retrieveFreeCoins()) {
                            soundsPlayer.playSoundEffect(Sounds.Name.WRONG);
                        }
                    }
                });

                coinMachineControl.getDismissButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        if(!coinsAlreadyEnough) {
                            gamingKit.updateRoomMates(UpdateRoomMatesCode.COINS_WINDOW_DISMISS, profile.getUserId());
                        }
                    }
                });

                coinMachineControl.getBuyCoinsTabButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startSpeech(CoinMachineTabType.PurchaseCoins);
                    }
                });

                coinMachineControl.getPlayerInsertCoinStatusTabButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startSpeech(CoinMachineTabType.PlayersInsertCoinStatus);
                    }
                });

                coinMachineControl.getRetrieveCoinsTabButton().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startSpeech(CoinMachineTabType.RetrieveCoins);
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
                        coinDeductedSuccess();
                    }
                }
                else if(code == UpdateRoomMatesCode.COINS_DEDUCTED_FAILED){
                    if(msg.equals(_this.transactionId)){        //msg is transactionId
                        coinDeductFailed();
                    }
                }
                else if(code == UpdateRoomMatesCode.REQUEST_COINS_STATE){
                    coinsMachineStateRequestReceived(senderId, msg);
                }
                else if(code == UpdateRoomMatesCode.COINS_STATE_RESPONSE){
                    coinsMachineStateResponseReceived(msg);        //msg is json
                }
                else if(code == UpdateRoomMatesCode.COINS_WINDOW_DISMISS){
                    dismissWindowsReceived(msg);
                }
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });

        connectionWatcher.addConnectionWatcherListener(getClassTag(), new ConnectionWatcherListener() {
            @Override
            public void onConnectionResume() {
                requestCoinsMachineStateFromOthers();
            }

            @Override
            public void onConnectionHalt() {
                currentUsersPutCoinNumberMap.clear();
                sync(userIdToNamePairs);
            }
        });

    }

    public void setBuyButtonListener(TextButton buyButton){
        buyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                purchaseCoins(coinProducts.get(currentProductIndex));
            }
        });
    }

    public void addCoinsListener(String classTag, ClientInternalCoinListener listener){
        tagToClientInternalCoinListenersMap.put(classTag, listener);

        for(String userID : noCoinUserIds){
            listener.userHasCoinChanged(userID, false);
        }

    }

    public void removeCoinsListenersByClassTag(String classTag){
        tagToClientInternalCoinListenersMap.remove(classTag);

        for(ClientInternalCoinListener clientInternalCoinListener : tagToClientInternalCoinListenersMap.values()){
            if(clientInternalCoinListener.isMonitorNextCoin()){
                return;
            }
        }

        if(retrievableCoinsDataSafeThread != null) retrievableCoinsDataSafeThread.kill();
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
            if(currentUsersPutCoinNumberMap.get(userId) > 0){
                result.add(new CoinsMeta(userId, currentUsersPutCoinNumberMap.get(userId)));
            }
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
