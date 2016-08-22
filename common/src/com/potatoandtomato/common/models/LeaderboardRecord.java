package com.potatoandtomato.common.models;

import com.potatoandtomato.common.utils.Strings;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 9/3/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaderboardRecord {

    private ArrayList<String> userIds;
    private double score;
    private Streak streak;
    private String leaderId;

    @JsonIgnore
    private ConcurrentHashMap<String, String> userIdToNameMap;

    @JsonIgnore
    private ConcurrentHashMap<String, String> userIdToCountryMap;

    public LeaderboardRecord(ArrayList<Player> players){
        userIds = new ArrayList<String>();
        userIdToNameMap = new ConcurrentHashMap<String, String>();
        userIdToCountryMap = new ConcurrentHashMap<String, String>();
        for(Player player :  players){
            userIds.add(player.getUserId());
            userIdToNameMap.put(player.getUserId(), player.getName());
            userIdToCountryMap.put(player.getUserId(), player.getCountry());
        }
    }

    public LeaderboardRecord() {
        userIds = new ArrayList<String>();
        userIdToNameMap = new ConcurrentHashMap<String, String>();
        userIdToCountryMap = new ConcurrentHashMap<String, String>();
    }

    @JsonIgnore
    public ArrayList<String> getUserNames() {
        ArrayList<String> result = new ArrayList<String>();
        for(String name : userIdToNameMap.values()) result.add(name);
        return result;
    }

    @JsonIgnore
    public String getUserNameByUserId(String userId){
        if(!Strings.isEmpty(userId) && userIdToNameMap.containsKey(userId)){
            return userIdToNameMap.get(userId);
        }
        else{
            return "";
        }
    }

    @JsonIgnore
    public void addUserName(String userId, String userName){
        userIdToNameMap.put(userId, userName);
    }

    @JsonIgnore
    public ConcurrentHashMap<String, String> getUserIdToNameMap() {
        return userIdToNameMap;
    }

    @JsonIgnore
    public void setUserIdToNameMap(ConcurrentHashMap<String, String> userNames) {
        this.userIdToNameMap = userNames;
    }

    @JsonIgnore
    public void addUserCountry(String userId, String country){
        if(country != null){
            userIdToCountryMap.put(userId, country);
        }
    }

    @JsonIgnore
    public ConcurrentHashMap<String, String> getUserIdToCountryMap() {
        return userIdToCountryMap;
    }

    @JsonIgnore
    public void setUserIdToCountryMap(ConcurrentHashMap<String, String> userIdToCountryMap) {
        this.userIdToCountryMap = userIdToCountryMap;
    }

    @JsonIgnore
    public String getUserCountryByUserId(String userId){
        if(!Strings.isEmpty(userId) && userIdToCountryMap.containsKey(userId)){
            return userIdToCountryMap.get(userId);
        }
        else{
            return "";
        }
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

    public void setScore(double score) {
        this.score = score;
    }

    public String getLeaderId() {
        if(Strings.isEmpty(leaderId) || !this.getUserIdToNameMap().containsKey(leaderId)){
            for(String userID : this.getUserIds()){
                leaderId = userID;
                break;
            }
        }
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
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

    @JsonIgnore
      public String getLeaderName(){
        String leaderId = getLeaderId();
        if(Strings.isEmpty(leaderId)){
            return "";
        }
        String leaderName = this.getUserIdToNameMap().get(getLeaderId());
        return leaderName;
    }

    @JsonIgnore
    public ArrayList<String> getNonLeaderIds(){
        ArrayList<String> result = new ArrayList();

        String leaderId = getLeaderId();
        for(String userId : this.getUserIds()){
            if(!userId.equals(leaderId)){
                result.add(userId);
            }
        }

        return result;
    }


    public void addScore(double value){
        this.score += value;
    }

    @JsonIgnore
    public void addScoresToRecord(ArrayList<ScoreDetails> scoreDetails){
        for(ScoreDetails detail : scoreDetails){
            if(detail.isAddOrMultiply()){
                this.addScore(detail.getValue());
            }
        }
    }

    @JsonIgnore
    public Streak getStreak() {
        if(streak == null) streak = new Streak(0, 0);
        return streak;
    }

    @JsonIgnore
    public void setStreak(Streak streak) {
        this.streak = streak;
    }

    @JsonIgnore
    public void addStreakToRecord(ArrayList<ScoreDetails> scoreDetails){
        for(ScoreDetails detail : scoreDetails){
            if(detail.isCanAddStreak()){
                this.getStreak().addStreak(1);
            }
        }
    }

}
