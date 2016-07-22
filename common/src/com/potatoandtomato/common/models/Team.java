package com.potatoandtomato.common.models;

import com.potatoandtomato.common.utils.Strings;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Team {

    ArrayList<Player> players;
    LeaderboardRecord leaderboardRecord;
    int rank = 999;                               //ranking in leaderboard

    public Team() {
        players = new ArrayList();
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public ArrayList<Player> getPlayers() {
        if(players == null) return new ArrayList();
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }


    @JsonIgnore
    public ArrayList<Player> getPlayersSortedByIds() {
        ArrayList<Player> players = getPlayers();
        ArrayList<Player> result = new ArrayList();
        ArrayList<String> sortedPlayerIds = getPlayersUserIds();
        for(String playerId: sortedPlayerIds){
            for(Player player : players){
                if(player.getUserId().equals(playerId)){
                    result.add(player);
                    break;
                }
            }
        }

        return result;
    }


    @JsonIgnore
    public ArrayList<String> getPlayersUserIds(){
        ArrayList<String> result = new ArrayList<String>();
        for(Player p : players){
            result.add(p.getUserId());
        }
        Collections.sort(result);
        return result;
    }

    public boolean hasUser(String userId){
        return getPlayerByUserId(userId) != null;
    }

    public boolean matchedUsers(ArrayList<String> userIds){
        for(String userId : userIds){
            if(!hasUser(userId)){
                return false;
            }
        }
        return userIds.size() == this.getPlayersUserIds().size();
    }

    @JsonIgnore
    public Player getPlayerByUserId(String userId){
        for(Player player : players){
            if(player.getUserId().equals(userId)){
                return player;
            }
        }
        return null;
    }

    @JsonIgnore
    public String getPlayersIdsString(){
        ArrayList<String> userIdsClone = new ArrayList();
        for(Player player : players){
            userIdsClone.add(player.getUserId());
        }
        Collections.sort(userIdsClone);
        return Strings.joinArr(userIdsClone, ",");
    }

    @JsonIgnore
    public LeaderboardRecord getLeaderboardRecord() {
        if(leaderboardRecord == null) leaderboardRecord = new LeaderboardRecord(this.getPlayers());
        return leaderboardRecord;
    }

    @JsonIgnore
    public void setLeaderboardRecord(LeaderboardRecord leaderboardRecord) {
        this.leaderboardRecord = leaderboardRecord;
    }

    @JsonIgnore
    public int getRank() {
        return rank;
    }

    @JsonIgnore
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Team){
            for(Player p : this.getPlayers()){
                if(!((Team) o).hasUser(p.getUserId())){
                    return false;
                }
            }
            return ((Team) o).getPlayers().size() == this.getPlayers().size();
        }
        else{
            return super.equals(o);
        }
    }
}
