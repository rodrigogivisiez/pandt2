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
}
