package com.potatoandtomato.common;

import com.potatoandtomato.common.models.LeaderboardRecord;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Team {

    ArrayList<Player> players;
    LeaderboardRecord leaderboardRecord;

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

    @Override
    public boolean equals(Object o) {
        if(o instanceof Team){
            for(Player p : this.getPlayers()){
                if(!((Team) o).hasUser(p.getUserId())){
                    return false;
                }
            }
            return true;
        }
        else{
            return super.equals(o);
        }
    }
}
