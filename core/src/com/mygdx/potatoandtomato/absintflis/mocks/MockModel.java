package com.mygdx.potatoandtomato.absintflis.mocks;

import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.models.EndGameResult;
import com.potatoandtomato.common.Player;
import com.potatoandtomato.common.models.ScoreDetails;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class MockModel {

    public static Game mockGame(){
        Game g = new Game();
        g.setVersion("1.00");
        g.setAbbr("covered_chess");
        g.setGameUrl("http://www.potato-and-tomato.com/covered_chess/core.jar");
        g.setIconUrl("http://www.potato-and-tomato.com/covered_chess/icon.png");
        g.setMaxPlayers("100");
        g.setMinPlayers("1");
        g.setTeamMaxPlayers("10");
        g.setTeamMinPlayers("1");
        g.setTeamCount("10");
        g.setName("Covered Chess");
        g.setCommonVersion("1");
        g.setDescription("This is just a mock model of game.");
        g.setLeaderbordTypeEnum(LeaderboardType.Accumulate);
        return g;
    }

    public static Profile mockProfile(String userId){
        Profile p = new Profile();
        p.setFacebookUserId("fb123");
        p.setUserId(userId);
        p.setGameName("Jonjo Shelvey");
        return p;
    }

    public static Profile mockProfile(){
       return mockProfile("123");
    }

    public static Room mockRoom(String id){
        Room r = new Room();
        r.setGame(mockGame());
        r.setHost(mockProfile());
        r.setOpen(false);
        r.setPlaying(false);
        r.setRoomId("12");
        r.setRoundCounter(0);
        if(id != null) r.setId(id);
        HashMap<String, RoomUser> roomUsers = new HashMap();
        RoomUser user1 = new RoomUser();
        RoomUser user2 = new RoomUser();
        user1.setProfile(mockProfile());
        user1.setSlotIndex(0);
        user1.setReady(true);
        user2.setProfile(mockProfile("another"));
        user2.setSlotIndex(99);
        user2.setReady(true);
        roomUsers.put(user1.getProfile().getUserId(), user1);
        roomUsers.put(user2.getProfile().getUserId(), user2);
        r.setRoomUsers(roomUsers);
        return r;
    }

    public static ChatMessage mockChatMessage(){
        ChatMessage c = new ChatMessage("test message", ChatMessage.FromType.USER, MockModel.mockProfile().getUserId());
        return c;
    }

    public static GameHistory mockGameHistory(){
        GameHistory gameHistory = new GameHistory();
        gameHistory.setPlayedWith(mockProfile("another"));
        gameHistory.setNameOfGame(mockGame().getName());
        gameHistory.setCreationDate(System.currentTimeMillis() / 1000L);
        return gameHistory;
    }

    public static EndGameData mockEndGameData(){
        ArrayList<ScoreDetails> scoreDetails = new ArrayList<ScoreDetails>();
        scoreDetails.add(new ScoreDetails(100, "5 win", true, true));

        ArrayList<Player> myTeams = new ArrayList<Player>();
        myTeams.add(new Player("abc", "1", true, true));

        EndGameResult endGameResult = new EndGameResult();
        endGameResult.setScoreDetails(scoreDetails);
        endGameResult.setWon(true);
        endGameResult.setStreakEnabled(true);
        endGameResult.setMyTeam(myTeams);

        EndGameData endGameData = new EndGameData(MockModel.mockRoom("1"), "1");
        endGameData.setEndGameResult(endGameResult);

        return endGameData;
    }


}
