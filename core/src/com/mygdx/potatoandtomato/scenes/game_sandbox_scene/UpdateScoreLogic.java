package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.Team;
import com.potatoandtomato.common.models.Streak;
import com.potatoandtomato.common.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 19/3/2016.
 */
public class UpdateScoreLogic {
    
    private ArrayList<Team> _resetStreakTeam;
    private Services _services;
    private Room _room;

    public UpdateScoreLogic(Services services, Room room) {
        this._services = services;
        this._room = room;
        this._resetStreakTeam = new ArrayList<Team>();
    }

    public void update(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers){
        for (Map.Entry<Team, ArrayList<ScoreDetails>> entry : winners.entrySet()) {
            Team winnerTeam = entry.getKey();
            ArrayList<ScoreDetails> scoreDetails = entry.getValue();
            updateWinnerTeam(winnerTeam, scoreDetails);
        }

        for(Team loserTeam : losers){
            updateLoserTeam(loserTeam);
        }
    }

    private void updateWinnerTeam(Team winnerTeam, ArrayList<ScoreDetails> scoreDetails){
        winnerTeam.getLeaderboardRecord().addScoresToRecord(scoreDetails);

        int addingStreak = 0;
        for(ScoreDetails detail : scoreDetails){
            if(detail.canAddStreak()) addingStreak++;
        }

        switch (getCurrentStreakStatus(winnerTeam.getLeaderboardRecord().getStreak())){
            case 0:
                winnerTeam.getLeaderboardRecord().getStreak().addStreakCount(addingStreak);
                break;
            case 1:
                _resetStreakTeam.add(winnerTeam);
                winnerTeam.getLeaderboardRecord().resetStreak();
                winnerTeam.getLeaderboardRecord().getStreak().addStreakCount(addingStreak);
                break;
            case 2:
                winnerTeam.getLeaderboardRecord().getStreak().addStreakCount(addingStreak);
                break;
            case 3:
                winnerTeam.getLeaderboardRecord().getStreak().addStreakCount(addingStreak);
                break;
            case 4:
                _resetStreakTeam.add(winnerTeam);
                winnerTeam.getLeaderboardRecord().resetStreak();
                winnerTeam.getLeaderboardRecord().getStreak().addStreakCount(addingStreak);
                break;
            case 5:
                winnerTeam.getLeaderboardRecord().getStreak().addStreakCount(addingStreak);
                break;
        }

        _services.getDatabase().saveLeaderBoardRecord(_room, winnerTeam.getLeaderboardRecord(), null);
    }

    private void updateLoserTeam(Team loserTeam){
        switch (getCurrentStreakStatus(loserTeam.getLeaderboardRecord().getStreak())){
            case 0:
                _resetStreakTeam.add(loserTeam);
                loserTeam.getLeaderboardRecord().resetStreak();
                break;
            case 1:
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoomId(_room.getId());
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoundNumber(_room.getRoundCounter());
                break;
            case 2:
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoomId(_room.getId());
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoundNumber(_room.getRoundCounter());
                break;
            case 3:
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoomId(_room.getId());
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoundNumber(_room.getRoundCounter());
                break;
            case 4:
                _resetStreakTeam.add(loserTeam);
                loserTeam.getLeaderboardRecord().resetStreak();
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoomId(_room.getId());
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoundNumber(_room.getRoundCounter());
                break;
            case 5:
                _resetStreakTeam.add(loserTeam);
                loserTeam.getLeaderboardRecord().resetStreak();
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoomId(_room.getId());
                loserTeam.getLeaderboardRecord().getStreak().setLastLoseRoundNumber(_room.getRoundCounter());
                break;
        }

        _services.getDatabase().saveLeaderBoardRecord(_room, loserTeam.getLeaderboardRecord(), null);
    }

    public boolean isTeamReset(Team team){
        for(Team resetTeam : _resetStreakTeam){
            if(resetTeam.equals(team)){
                return true;
            }
        }
        return false;
    }


    //////////////////////////////////////////////////////////////////////
    //Status representation
    //0: last lose = empty, last revive = xxxx
    //1: last lose = xxxx, last revive = empty
    //2: last lose = empty, last revive = empty
    //3: last lose = xxxx, last revive = xxxx
    //4: last lose = xxxx, last revive = yyyy, xxxx > yyyy
    //5: last lose = xxxx, last revive = yyyy, xxxx < yyyy
    //////////////////////////////////////////////////////////////////////
    private int getCurrentStreakStatus(Streak streak){
        if(Strings.isEmpty(streak.getLastLoseRoomId()) && !Strings.isEmpty(streak.getLastReviveRoomId())){
            return 0;
        }
        else if(!Strings.isEmpty(streak.getLastLoseRoomId()) && Strings.isEmpty(streak.getLastReviveRoomId())){
            return 1;
        }
        else if(Strings.isEmpty(streak.getLastLoseRoomId()) && Strings.isEmpty(streak.getLastReviveRoomId())){
            return 2;
        }
        else if(!Strings.isEmpty(streak.getLastLoseRoomId()) && !Strings.isEmpty(streak.getLastReviveRoomId()) &&
                streak.getLastLoseRoomId().equals(streak.getLastReviveRoomId()) &&
                streak.getLastLoseRoundNumber() == streak.getLastReviveRoundNumber()){
            return 3;
        }
        else if(!Strings.isEmpty(streak.getLastLoseRoomId()) && !Strings.isEmpty(streak.getLastReviveRoomId())){
            if(!streak.isLastReviveLargerThanLastLose()){
                return 4;
            }
            else{
                return 5;
            }
        }
        return 2;
    }













}
