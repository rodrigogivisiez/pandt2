package com.potatoandtomato.common;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Team {

    ArrayList<Player> players;

    public Team() {
        players = new ArrayList();
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Player getPlayerByUserId(String userId){
        for(Player player : players){
            if(player.getUserId().equals(userId)){
                return player;
            }
        }
        return null;
    }

}
