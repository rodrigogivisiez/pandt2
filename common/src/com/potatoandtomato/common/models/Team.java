package com.potatoandtomato.common.models;

import java.util.ArrayList;

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
        return players;
    }

    public ArrayList<String> getPlayersUserIds(){
        ArrayList<String> result = new ArrayList<String>();
        for(Player p : players){
            result.add(p.getUserId());
        }
        return result;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
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

    public Player getPlayerByUserId(String userId){
        for(Player player : players){
            if(player.getUserId().equals(userId)){
                return player;
            }
        }
        return null;
    }

    public LeaderboardRecord getLeaderboardRecord() {
        if(leaderboardRecord == null) leaderboardRecord = new LeaderboardRecord(this.getPlayers());
        return leaderboardRecord;
    }

    public void setLeaderboardRecord(LeaderboardRecord leaderboardRecord) {
        this.leaderboardRecord = leaderboardRecord;
    }

    public int getRank() {
        return rank;
    }

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
