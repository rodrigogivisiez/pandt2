package com.potatoandtomato.games.models;

import com.badlogic.gdx.math.Rectangle;
import com.potatoandtomato.common.utils.SafeDouble;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.statics.Global;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class GameModel {

    private int stageNumber;
    private HashMap<String, Integer> userRecords;
    private SafeDouble score;
    private GameState gameState;
    private ArrayList<SimpleRectangle> handledAreas;
    private int remainingMiliSecs;
    private int hintsLeft;
    private int castleAttackedCount;
    private int freezingMiliSecs;
    private boolean continueChanceUsed;
    private ImageDetails imageDetails;
    private StageType stageType;
    private ArrayList<GameModelListener> listeners;

    public GameModel(int stageNumber, double score) {
        this.stageNumber = stageNumber;
        this.score = new SafeDouble(score);
        this.userRecords = new HashMap();
        this.handledAreas = new ArrayList();
        this.hintsLeft = 3;
        this.listeners = new ArrayList();
    }

    public GameModel() {
        this.userRecords = new HashMap();
        this.handledAreas = new ArrayList();
        this.hintsLeft = 3;
        this.listeners = new ArrayList();
    }

    public void copyGameModelDataToThis(GameModel gameModel){
        setStageType(gameModel.getStageType());
        setStageNumber(gameModel.getStageNumber());
        setUserRecords(gameModel.getUserRecords());
        setScore(gameModel.getScore(), true);
        setGameState(gameModel.getGameState());
        setRemainingMiliSecs(gameModel.getRemainingMiliSecs(), true);
        setFreezingMiliSecs(gameModel.getFreezingMiliSecs());
        setCastleAttackedCount(gameModel.getCastleAttackedCount());
        setHandledAreas(gameModel.getHandledAreas());
        setHintsLeft(gameModel.getHintsLeft());
        setContinueChanceUsed(gameModel.isContinueChanceUsed());
    }

    public boolean isContinueChanceUsed() {
        return continueChanceUsed;
    }

    public void setContinueChanceUsed(boolean continueChanceUsed) {
        this.continueChanceUsed = continueChanceUsed;
    }

    public int getCastleAttackedCount() {
        return castleAttackedCount;
    }

    public void setCastleAttackedCount(int castleAttackedCount) {
        this.castleAttackedCount = castleAttackedCount;
        for(GameModelListener listener : listeners){
            listener.onCastleAttackedCountChanged(castleAttackedCount);
        }
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {

        this.stageType = stageType;
        for(GameModelListener listener : listeners){
            listener.onStageTypeChanged(stageType);
        }
    }

    public ImageDetails getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(ImageDetails imageDetails) {
        this.imageDetails = imageDetails;
    }

    public int getHintsLeft() {
        return hintsLeft;
    }

    public void setHintsLeft(int hintsLeft) {
        if(this.hintsLeft != hintsLeft){
            this.hintsLeft = hintsLeft;
            for(GameModelListener listener : listeners){
                listener.onHintChanged(hintsLeft);
            }
        }
    }

    public void minusHintLeft(){
        if(!Global.REVIEW_MODE) {
            setHintsLeft(hintsLeft - 1);
        }
    }

    public int getRemainingMiliSecs() {
        return remainingMiliSecs;
    }

    public void setRemainingMiliSecs(int remainingMiliSecs, boolean notify) {
        if(Global.REVIEW_MODE && remainingMiliSecs < this.remainingMiliSecs){
            return;
        }
        this.remainingMiliSecs = remainingMiliSecs;
        if(notify){
            for(GameModelListener listener : listeners){
                listener.onRemainingMiliSecsChanged(remainingMiliSecs);
            }
        }

        if(this.remainingMiliSecs <= 0 && notify){
            for(GameModelListener listener : listeners){
                listener.onTimeFinished();
            }
        }
    }

    public void minusRemainingMiliSecs(int minus){
        setRemainingMiliSecs(this.remainingMiliSecs - minus, true);
    }

    public int getFreezingMiliSecs() {
        return freezingMiliSecs;
    }

    public void setFreezingMiliSecs(int freezingMiliSecs) {
        this.freezingMiliSecs = freezingMiliSecs;
        for(GameModelListener listener : listeners){
            listener.onFreezingMiliSecsChanged(freezingMiliSecs);
        }
    }

    public void addFreezeMiliSecs(){
        if(freezingMiliSecs < 0){
            freezingMiliSecs = 0;
        }

        if(freezingMiliSecs < 2000){
            setFreezingMiliSecs(freezingMiliSecs + 2000);
        }
        else{
            setFreezingMiliSecs(freezingMiliSecs + 500);
        }
    }

    public HashMap<String, Integer> getUserRecords() {
        return userRecords;
    }

    public void setUserRecords(HashMap<String, Integer> userRecords) {
        this.userRecords = userRecords;
    }

    public ArrayList<SimpleRectangle> getHandledAreas() {
        return handledAreas;
    }

    public void setHandledAreas(ArrayList<SimpleRectangle> handledAreas) {
        this.handledAreas = handledAreas;
        for(GameModelListener listener : listeners){
            listener.onHandledAreasChanged();
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState newGameState) {
        GameState oldState = this.gameState;

        if(oldState != newGameState){
            this.gameState = newGameState;
            if(listeners.size() > 0){
                Logs.show("Game state: " + newGameState);
            }
            for(GameModelListener listener : listeners){
                listener.onGameStateChanged(oldState, newGameState);
            }
        }
    }


    public Double getScore() {
        if(this.score == null){
            this.score = new SafeDouble(0.0);
        }
        return score.getValue();
    }

    public void setScore(Double score, boolean notify) {
        if(this.score == null){
            this.score = new SafeDouble(score);
        }
        else{
            this.score.setValue(score);
        }

        if(notify){
            for(GameModelListener listener : listeners){
                listener.onScoresChanged(this.score.getValue());
            }
        }

    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
        for(GameModelListener listener : listeners){
            listener.onStageNumberChanged(stageNumber);
        }
    }

    @JsonIgnore
    public void addStageNumber(){
        setStageNumber(stageNumber + 1);
    }

    @JsonIgnore
    public void convertStageNumberToRemainingMiliSecs(){
        double time =  getThisStageTotalMiliSecs();
        this.setRemainingMiliSecs((int) time, true);
    }

    @JsonIgnore
    public int getThisStageTotalMiliSecs(){
        double time = 0;
        if(this.stageType == StageType.Bonus){
            time = 60000;
        }
        else{
            time =  Math.max(60000 - (Math.pow(stageNumber, 1.5) * 1000), 0) +
                    Math.max(3000 - (Math.pow(stageNumber, 0.2) * 1000), 0) + 5500;
            //time = 60000 * 5;
        }

        return (int) time;
    }

    @JsonIgnore
    public int getThisStageTotalMovingMiliSecs(){
        return getThisStageTotalMiliSecs() - getThisStageTotalAtkMiliSecs();
    }

    @JsonIgnore
    public int getThisStageTotalAtkMiliSecs(){
        int atkMiliSecs = (int) (getThisStageTotalMiliSecs() * ((float) Global.ATTACK_TIME_PERCENT / 100));
        return Math.max(5000, atkMiliSecs);
    }


    @JsonIgnore
    public void addUserClickedCount(String userId){
        if(!userRecords.containsKey(userId)){
            userRecords.put(userId, 1);
        }
        else{
            userRecords.put(userId, userRecords.get(userId) + 1);
        }

        for(GameModelListener listener : listeners){
            listener.onAddedClickCount(userId, userRecords.get(userId));
        }
    }

    @JsonIgnore
    public int getUserClickedCount(String userId){
        if(!userRecords.containsKey(userId)){
            return 0;
        }
        else{
            return userRecords.get(userId);
        }
    }

    @JsonIgnore
    public boolean isAreaAlreadyHandled(SimpleRectangle rectangle){
        for(SimpleRectangle simpleRectangle : handledAreas){
            if(simpleRectangle.equals(rectangle)){
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isAreaAlreadyHandled(Rectangle rectangle){
        for(SimpleRectangle simpleRectangle : handledAreas){
            if(simpleRectangle.getX() == rectangle.getX() &&
                    simpleRectangle.getY() == rectangle.getY() &&
                    simpleRectangle.getHeight() == rectangle.getHeight() &&
                    simpleRectangle.getWidth() == rectangle.getWidth()){
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean addHandledArea(SimpleRectangle rectangle, int remainingMiliSecsWhenClicked){
        if(!isAreaAlreadyHandled(rectangle)){
            handledAreas.add(rectangle);
            for(GameModelListener listener : listeners){
                listener.onCorrectClicked(rectangle, remainingMiliSecsWhenClicked);
            }
            return false;
        }
        return true;
    }

    @JsonIgnore
    public boolean isAreaAlreadyConfirmClicked(SimpleRectangle rectangle){
        for(SimpleRectangle simpleRectangle : handledAreas){
            if(simpleRectangle.equals(rectangle)){
                return simpleRectangle.getUserId() != null;
            }
        }
        return false;
    }

    @JsonIgnore
    public void setConfirmAreaClickedBy(SimpleRectangle rectangle, String userId){
        for(SimpleRectangle simpleRectangle : handledAreas){
            if(simpleRectangle.equals(rectangle)){
                simpleRectangle.setUserId(userId);
            }
        }
    }

    @JsonIgnore
    public void clearHandledAreas(){
        handledAreas.clear();
    }

    @JsonIgnore
    public String toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @JsonIgnore
    public GameModelListener addGameModelListener(GameModelListener listener){
        this.listeners.add(listener);
        return listener;
    }

    @JsonIgnore
    public void removeGameModelListener(GameModelListener listener){
        this.listeners.remove(listener);
    }

    @JsonIgnore
    public boolean isPlaying(){
        return getHandledAreas().size() < 5 && getGameState() == GameState.Playing && getRemainingMiliSecs() > 0 &&
                imageDetails != null;
    }

    @JsonIgnore
    public boolean isNextStageBonus(){
        int nextStageNumber = stageNumber + 1;
        return nextStageNumber % Global.BONUS_STAGE_NUMBER == 0 && !Global.REVIEW_MODE;
    }

    public void dispose(){
        listeners.clear();
    }
}
