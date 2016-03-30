package com.potatoandtomato.common.models;

import com.potatoandtomato.common.Player;
import com.potatoandtomato.common.utils.Strings;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by SiongLeng on 9/3/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaderboardRecord {

    private ArrayList<String> userIds;
    private double score;
    private Streak streak;

    @JsonIgnore
    private HashMap<String, String> userIdToNameMap;

    public LeaderboardRecord(ArrayList<Player> players){
        userIds = new ArrayList<String>();
        userIdToNameMap = new HashMap<String, String>();
        for(Player player :  players){
            userIds.add(player.getUserId());
            userIdToNameMap.put(player.getUserId(), player.getName());
        }
    }

    public LeaderboardRecord() {
        userIds = new ArrayList<String>();
        userIdToNameMap = new HashMap<String, String>();
    }

    @JsonIgnore
    public ArrayList<String> getUserNames() {
        ArrayList<String> result = new ArrayList<String>();
        for(String name : userIdToNameMap.values()) result.add(name);
        return result;
    }

    @JsonIgnore
    public void addUserName(String userId, String userName){
        userIdToNameMap.put(userId, userName);
    }

    @JsonIgnore
    public HashMap<String, String> getUserIdToNameMap() {
        return userIdToNameMap;
    }

    @JsonIgnore
    public void setUserIdToNameMap(HashMap<String, String> userNames) {
        this.userIdToNameMap = userNames;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public void addUserId(String userId){
        this.userIds.add(userId);
    }

    public double getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean containUser(String userId){
        return this.userIds.contains(userId);
    }

    public boolean usersMatched(ArrayList<String> matchingUserIds){
        for(String userId : matchingUserIds){
            if(!this.getUserIds().contains(userId)){
                return false;
            }
        }
        return this.getUserIds().size() == matchingUserIds.size();
    }

    @JsonIgnore
    public String getAllUsernameCommaSeparated(){
        return Strings.joinArr(this.getUserNames(), ", ");
    }

    public void addScore(double value){
        this.score += value;
    }

    public Streak getStreak() {
        if(streak == null) streak = new Streak();
        return streak;
    }

    public void setStreak(Streak streak) {
        this.streak = streak;
    }

    public void resetStreak(){
        this.setStreak(new Streak());
    }

    public void addScoresToRecord(ArrayList<ScoreDetails> scoreDetails){
        for(ScoreDetails detail : scoreDetails){
            if(detail.isAddOrMultiply()){
                this.addScore(detail.getValue());
            }
        }
    }

}
