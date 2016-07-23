package com.potatoandtomato.common.helpers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameDataContractAbstract;
import com.potatoandtomato.common.absints.IDisconnectOverlayControl;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.enums.RoomUpdateType;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.statics.Texts;
import com.potatoandtomato.common.statics.Vars;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.OneTimeRunnable;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 29/5/2016.
 */
public class GameDataHelper implements Disposable {

    private String myUserId;
    private ConcurrentHashMap<String, Boolean> usersHasDataMap;
    private GameDataContractAbstract gameDataContract;
    private DecisionsMaker decisionsMaker;
    private IGameSandBox gameSandBox;
    private SafeThread safeThread, safeThread2;
    private int failToleranceMiliSecs;
    private boolean dataGeneratedBefore;
    private boolean activated;
    private ArrayList<Runnable> toRunWhenHaveData;
    private IPTGame iptGame;
    private GameCoordinator gameCoordinator;
    private IDisconnectOverlayControl disconnectOverlayControl;
    private boolean comeBackFromRecoverConnection;
    private boolean finalized;
    private OneTimeRunnable onGameDataReceivedRunnable;

    public GameDataHelper(ArrayList<Team> teams, String myUserId,
                          DecisionsMaker decisionsMaker, IGameSandBox gameSandBox, IPTGame iptGame,
                          IDisconnectOverlayControl disconnectOverlayControl, GameCoordinator coordinator) {
        this.myUserId = myUserId;
        this.gameSandBox = gameSandBox;
        this.decisionsMaker = decisionsMaker;
        this.disconnectOverlayControl = disconnectOverlayControl;
        usersHasDataMap = new ConcurrentHashMap();
        toRunWhenHaveData = new ArrayList();
        this.gameCoordinator = coordinator;
        this.iptGame = iptGame;

        teamsInit(teams);
    }

    public void teamsInit(ArrayList<Team> teams){
        for(Team team : teams){
            for(Player player : team.getPlayers()){
                usersHasDataMap.put(player.getUserId(), false);
            }
        }
    }

    public void initGameDataHelper(GameDataContractAbstract gameDataContract) {
        initGameDataHelper(gameDataContract, 20000);
    }

    public void initGameDataHelper(GameDataContractAbstract gameDataContract, int failToleranceMiliSecs) {
        activated = true;
        this.gameDataContract = gameDataContract;
        this.failToleranceMiliSecs = failToleranceMiliSecs;
    }

    public void userConnectionChanged(String userId, boolean isConnected){
       if(gameDataContract != null){
           if(!isConnected){
               usersHasDataMap.put(userId, false);
               if(userId.equals(myUserId)){
                   gameDataContract.onGameDataOutdated();
                   meHaveDataChanged();
               }
           }
           else{
               if(userId.equals(myUserId)){
                   comeBackFromRecoverConnection = true;
                   if(!hasData()){
                       if(usersHasDataMap.size() == 1){        //only one player in this game and thats me, directly resume
                           receivedGameData(gameDataToJsonString(gameDataContract.getCurrentGameData()));
                       }
                       else{
                           startGameDataRequestMonitorThread();
                       }
                   }
               }
               else{
                   if(meIsHigherDecisionMakerPriorityWithData()){
                       sendGameData(true, userId);
                   }
               }
           }
       }
    }

    public void onGameStarted(boolean isContinue){
        //if game data contract is null mean game is not using game data helper
        if(gameDataContract != null){
            if(isContinue){
                dataGeneratedBefore = true;
            }
            else{
                startDecisionMakerBroadcastInitialDataMonitorThread();
            }

            startGameDataRequestMonitorThread();
        }
    }

    public void broadcastGameData(){
        sendGameData(false, "");
    }

    public void startGameDataRequestMonitorThread(){
        if(safeThread != null) safeThread.kill();

        safeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int elapsed = 0;

                while (true){
                    int sleepMilisecs = 1000;

                    if(comeBackFromRecoverConnection)
                        disconnectOverlayControl.showResumingGameOverlay((failToleranceMiliSecs - elapsed) / 1000);

                    Threadings.sleep(sleepMilisecs);

                    if(safeThread.isKilled() || hasData()){
                        disconnectOverlayControl.hideOverlay();
                        break;
                    }
                    else{
                        elapsed += sleepMilisecs;
                        if(elapsed > failToleranceMiliSecs){
                            gameDataFailedToRetrieve(ErrorType.TimeOut);
                            disconnectOverlayControl.hideOverlay();
                            break;
                        }

                        if(elapsed == failToleranceMiliSecs / 2){
                            gameSandBox.sendUpdate(RoomUpdateType.GameDataRequest, "");
                        }
                    }
                }
            }
        });
    }

    public void startDecisionMakerBroadcastInitialDataMonitorThread(){
        safeThread2 = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(hasData() || safeThread2.isKilled() || dataGeneratedBefore){
                        break;
                    }
                    else{
                        if(decisionsMaker.getDecisionMakerUserId().equals(myUserId)){
                            broadcastGameData();
                        }
                    }
                    Threadings.sleep(5000);
                }
            }
        });
    }

    private String gameDataToJsonString(String gameData){
        ObjectMapper objectMapper = Vars.getObjectMapper();
        String usersHasDataMapJson = null;
        try {
            usersHasDataMapJson = objectMapper.writeValueAsString(usersHasDataMap);
            JsonObj jsonObj = new JsonObj();
            jsonObj.put("usersHasDataMapJson", usersHasDataMapJson);
            jsonObj.put("gameData", gameData);
            return jsonObj.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void sendGameData(boolean isPrivate, String toUserId){
        if(finalized){
            if(isPrivate){
                gameSandBox.sendPrivateUpdate(RoomUpdateType.GameData, toUserId, ErrorType.GameFinalizedAlready.name());
            }
            else{
                gameSandBox.sendUpdate(RoomUpdateType.GameData, ErrorType.GameFinalizedAlready.name());
            }
        }
        else{
            String gameData;
            if(!dataGeneratedBefore){
                gameData = gameDataContract.generateGameData();
                dataGeneratedBefore = true;
            }
            else{
                gameData = gameDataContract.getCurrentGameData();
            }

            String msg = gameDataToJsonString(gameData);

            if(isPrivate){
                gameSandBox.sendPrivateUpdate(RoomUpdateType.GameData, toUserId, msg);
            }
            else{
                gameSandBox.sendUpdate(RoomUpdateType.GameData, msg);
            }
        }
    }

    public void receivedGameData(String msg){
        dataGeneratedBefore = true;
        comeBackFromRecoverConnection = false;

        if(hasData()){
           return;
        }

        if(msg.equals(ErrorType.GameFinalizedAlready.name())){
            gameDataFailedToRetrieve(ErrorType.GameFinalizedAlready);
        }
        else{
            try {
                JsonObj jsonObj = new JsonObj(msg);
                String usersHasDataMapJson = jsonObj.getString("usersHasDataMapJson");
                String gameData = jsonObj.getString("gameData");

                ObjectMapper objectMapper = Vars.getObjectMapper();
                ConcurrentHashMap<String, Boolean> receivedUsersHasDataMap = objectMapper.readValue(usersHasDataMapJson, ConcurrentHashMap.class);
                receivedUsersHasDataMap.put(myUserId, true);
                usersHasDataMap = receivedUsersHasDataMap;
                meHaveDataChanged();

                gameDataContract.onGameDataReceived(gameData);

                if(onGameDataReceivedRunnable != null) onGameDataReceivedRunnable.run();

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receivedGameDataRequest(String fromUserId){
        if(meIsHigherDecisionMakerPriorityWithData()){
            sendGameData(true, fromUserId);
        }
    }

    public void gameDataFailedToRetrieve(ErrorType errorType){
        if(safeThread != null) safeThread.kill();

        if(!gameDataContract.onFailedRetrieve()){
            if(errorType == ErrorType.GameFinalizedAlready){
                gameCoordinator.raiseGameFailedError(Texts.gameAlreadyFinalized);
            }
            else if(errorType == ErrorType.TimeOut){
                gameCoordinator.raiseGameFailedError(Texts.failedToExchangeGameData);
            }
        }
    }

    private String getUserWithGameData(){
        ArrayList<String> userIdsWithData = new ArrayList();
        for(String userId : usersHasDataMap.keySet()){
            if(usersHasDataMap.get(userId)){
                userIdsWithData.add(userId);
            }
        }

        if(userIdsWithData.size() > 0){
            return userIdsWithData.get(MathUtils.random(0, userIdsWithData.size() - 1));
        }
        else{
            return "";
        }
    }

    public boolean hasData(){
        if(!usersHasDataMap.containsKey(myUserId)){
            return false;
        }
        else{
            return usersHasDataMap.get(myUserId);
        }
    }

    public void setMyUserId(String userId){
        myUserId = userId;
    }

    public boolean meIsHigherDecisionMakerPriorityWithData(){
        ArrayList<String> userIds = decisionsMaker.getDecisionMakersSequence();
        for(String userId : userIds){
            if(usersHasDataMap.get(userId)){
                return userId.equals(myUserId);
            }
        }
        return false;
    }

    public void meHaveDataChanged(){
        if(hasData()){
            for(Runnable toRun : toRunWhenHaveData){
                toRun.run();
            }
            toRunWhenHaveData.clear();
        }
    }

    public void addToRunWhenHaveData(Runnable toRun){
        toRunWhenHaveData.add(toRun);
    }

    public boolean isActivated() {
        return activated;
    }

    public void setOnGameDataReceivedRunnable(OneTimeRunnable onGameDataReceivedRunnable) {
        this.onGameDataReceivedRunnable = onGameDataReceivedRunnable;
        if(this.hasData() || gameDataContract == null){     //not using game data helper
            onGameDataReceivedRunnable.run();
        }
    }

    //game is finalize, sending of gamedata is not allowed anymore
    public void gameFinalized(){
        finalized = true;
    }

    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
        if(safeThread2 != null) safeThread2.kill();
        disconnectOverlayControl.hideOverlay();
    }

    private enum ErrorType{
        TimeOut, GameFinalizedAlready
    }

}
