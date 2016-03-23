package com.potatoandtomato.common.models;

import com.potatoandtomato.common.Player;
import com.potatoandtomato.common.models.ScoreDetails;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 18/3/2016.
 */
public class EndGameResult {

    private ArrayList<ScoreDetails> _scoreDetails;
    private boolean _streakEnabled, _won;
    private ArrayList<Player> _myTeam;

    public EndGameResult() {
    }

    public EndGameResult(ArrayList<ScoreDetails> _scoreDetails, boolean _streakEnabled, boolean _won) {
        this._scoreDetails = _scoreDetails;
        this._streakEnabled = _streakEnabled;
        this._won = _won;
    }

    public EndGameResult(ArrayList<ScoreDetails> _scoreDetails, boolean _streakEnabled, boolean _won, ArrayList<Player> _myTeam) {
        this._scoreDetails = _scoreDetails;
        this._streakEnabled = _streakEnabled;
        this._won = _won;
        this._myTeam = _myTeam;
    }

    public ArrayList<Player> getMyTeam() {
        return _myTeam;
    }

    public void setMyTeam(ArrayList<Player> _myTeam) {
        this._myTeam = _myTeam;
    }

    public void setScoreDetails(ArrayList<ScoreDetails> _scoreDetails) {
        this._scoreDetails = _scoreDetails;
    }

    public void setStreakEnabled(boolean _streakEnabled) {
        this._streakEnabled = _streakEnabled;
    }

    public void setWon(boolean _won) {
        this._won = _won;
    }

    public boolean isWon() {
        return _won;
    }

    public boolean isStreakEnabled() {
        return _streakEnabled;
    }

    public ArrayList<ScoreDetails> getScoreDetails() {
        return _scoreDetails;
    }

    public boolean isEmpty(){
        return (_myTeam == null || _myTeam.size() == 0);
    }

}
