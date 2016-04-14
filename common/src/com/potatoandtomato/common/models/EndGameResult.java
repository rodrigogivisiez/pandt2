package com.potatoandtomato.common.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 18/3/2016.
 */
public class EndGameResult {

    private HashMap<Team, ArrayList<ScoreDetails>> _winnersScoreDetails;
    private ArrayList<Team> _loserTeams;
    private boolean  _won;
    private ArrayList<Player> _myTeam;

    public EndGameResult() {
    }

    public EndGameResult(HashMap<Team, ArrayList<ScoreDetails>> _winnersScoreDetails,
                            ArrayList<Team> _loserTeams, boolean _won, ArrayList<Player> _myTeam) {
        this._winnersScoreDetails = _winnersScoreDetails;
        this._loserTeams = _loserTeams;
        this._won = _won;
        this._myTeam = _myTeam;
    }

    public ArrayList<Team> getLoserTeams() {
        if(_loserTeams == null) _loserTeams = new ArrayList<Team>();
        return _loserTeams;
    }

    public void setLoserTeams(ArrayList<Team> _loserTeams) {
        this._loserTeams = _loserTeams;
    }

    public ArrayList<Player> getMyTeam() {
        return _myTeam;
    }

    public void setMyTeam(ArrayList<Player> _myTeam) {
        this._myTeam = _myTeam;
    }

    public void setWon(boolean _won) {
        this._won = _won;
    }

    public boolean isWon() {
        return _won;
    }

    public ArrayList<ScoreDetails> getMyTeamWinnerScoreDetails(String userId){
        for(Team winnerTeam : _winnersScoreDetails.keySet()){
            if(winnerTeam.hasUser(userId)){
                return _winnersScoreDetails.get(winnerTeam);
            }
        }
        return new ArrayList();
    }

    public HashMap<Team, ArrayList<ScoreDetails>> getWinnersScoreDetails() {
        if(_winnersScoreDetails == null) return new HashMap();
        return _winnersScoreDetails;
    }

    public void setWinnersScoreDetails(HashMap<Team, ArrayList<ScoreDetails>> _winnersScoreDetails) {
        this._winnersScoreDetails = _winnersScoreDetails;
    }

    public boolean isEmpty(){
        return (_myTeam == null || _myTeam.size() == 0);
    }

}
