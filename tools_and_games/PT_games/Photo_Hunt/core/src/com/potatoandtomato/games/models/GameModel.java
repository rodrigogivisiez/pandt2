package com.potatoandtomato.games.models;

import com.potatoandtomato.games.enums.GameState;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class GameModel {

    private int stageNumber;
    private HashMap<String, Integer> userRecords;
    private int score;
    private GameState gameState;
    private ArrayList<SimpleRectangle> handledAreas;
    private int remainingSecs;


    public GameModel(int stageNumber, int score) {
        this.stageNumber = stageNumber;
        this.score = score;
        this.userRecords = new HashMap();
        this.handledAreas = new ArrayList();
    }

    public GameModel() {
        this.userRecords = new HashMap();
        this.handledAreas = new ArrayList();
    }

    public int getRemainingSecs() {
        return remainingSecs;
    }

    public void setRemainingSecs(int remainingSecs) {
        this.remainingSecs = remainingSecs;
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
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    @JsonIgnore
    public void addStageNumber(){
        stageNumber++;
    }

    @JsonIgnore
    public void convertStageNumberToRemainingSecs(){
        if(stageNumber < 10){
            this.setRemainingSecs(30);
        }
        else{
            this.setRemainingSecs(10);
        }
    }

    @JsonIgnore
    public void addUserClickedCount(String userId){
        if(!userRecords.containsKey(userId)){
            userRecords.put(userId, 1);
        }
        else{
            userRecords.put(userId, userRecords.get(userId) + 1);
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
    public void addHandledArea(SimpleRectangle rectangle){
        if(!isAreaAlreadyHandled(rectangle)){
            handledAreas.add(rectangle);
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


}
