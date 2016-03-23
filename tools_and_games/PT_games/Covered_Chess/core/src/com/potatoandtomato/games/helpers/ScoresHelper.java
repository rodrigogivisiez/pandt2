package com.potatoandtomato.games.helpers;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.Team;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.games.enums.ChessColor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 21/3/2016.
 */
public class ScoresHelper {

    private GameCoordinator coordinator;
    private Database database;
    private Team winnerTeam, loserTeam;

    public ScoresHelper(GameCoordinator coordinator, Database database) {
        this.coordinator = coordinator;
        this.database = database;
    }

    public void setIsMeWin(boolean won) {
        if(won){
            this.winnerTeam = coordinator.getMyTeam();
            this.loserTeam = coordinator.getEnemyTeams().get(0);        //only two team in this game
        }
        else{
            this.loserTeam = coordinator.getMyTeam();
            this.winnerTeam = coordinator.getEnemyTeams().get(0);        //only two team in this game
        }
    }

    public HashMap<Team, ArrayList<ScoreDetails>> getWinnerResult(){
        HashMap<Team, ArrayList<ScoreDetails>> result = new HashMap<Team, ArrayList<ScoreDetails>>();
        ArrayList<ScoreDetails> scoreDetails = new ArrayList<ScoreDetails>();
        scoreDetails.add(new ScoreDetails(20, "win!", true, true));
        result.put(winnerTeam, scoreDetails);
        return result;
    }

    public ArrayList<Team> getLoser(){
        ArrayList<Team> loser = new ArrayList<Team>();
        loser.add(this.loserTeam);
        return loser;
    }

}
