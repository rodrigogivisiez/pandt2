package scene_testings;

import abstracts.MockDB;
import abstracts.TestAbstract;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.LeaderBoardScene;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.*;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.assets.Assets;
import helpers.Mockings;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 18/3/2016.
 */
public class TestEndGameLeaderboard extends TestAbstract {

    @Test
    public void testEndGameLeaderboardLogicScene(){
        EndGameLeaderBoardLogic logic = new EndGameLeaderBoardLogic(Mockings.mockPTScreen(), T_Services.mockServices(), MockModel.mockEndGameData(),
                MockModel.mockEndGameData().getEndGameResult().getMyTeam());
        LeaderBoardScene scene = (LeaderBoardScene) logic.getScene();
        Assert.assertEquals(false, ((Table) scene.getRoot()).hasChildren());        //false becoz is postrunnable populate
    }


    @Test
    public void testWin(){

        final EndGameData endGameData = MockModel.mockEndGameData();
        endGameData.getRoom().getGame().setLeaderbordTypeEnum(LeaderboardType.Accumulate);

        EndGameResult endGameResult = endGameData.getEndGameResult();
        endGameResult.setWon(true);

        Profile profile = MockModel.mockProfile();
        Team team = new Team();
        team.addPlayer(new Player(profile.getGameName(), profile.getUserId(), true, 0));

        ArrayList<ScoreDetails> scoreDetails = new ArrayList<ScoreDetails>();
        scoreDetails.add(new ScoreDetails(900, "test", true, true));

        HashMap<Team, ArrayList<ScoreDetails>> winnerScoreDetails = new HashMap();
        winnerScoreDetails.put(team, scoreDetails);
        endGameResult.setWinnersScoreDetails(winnerScoreDetails);

        final ArrayList<LeaderboardRecord> leaderboardRecords = getSampleLeaderboardRecords(false);

        MockDB mockDB = new MockDB(){
            @Override
            public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
                listener.onCallback(leaderboardRecords, Status.SUCCESS);
            }

            @Override
            public void getTeamHighestLeaderBoardRecordAndStreak(Game game, ArrayList<String> teamUserIds, DatabaseListener<LeaderboardRecord> listener) {
                listener.onCallback(leaderboardRecords.get(2), Status.SUCCESS);
            }
        };

        Confirm mockConfirm = new Confirm(mock(SpriteBatch.class), mock(IPTGame.class), mock(Assets.class), mock(Broadcaster.class)){
            @Override
            public void show(String msg, Type type, ConfirmResultListener listener) {
                listener.onResult(ConfirmResultListener.Result.YES);
            }

            @Override
            public void invalidate() {
            }
        };

        Services services = T_Services.mockServices();
        services.setDatabase(mockDB);
        services.setConfirm(mockConfirm);

        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(Mockings.mockPTScreen(), services, endGameData, endGameResult.getMyTeam()){
            @Override
            public void addScoresRecur(int index, Runnable onFinish) {
                onFinish.run();
            }

            @Override
            public void moveUpRankAnimation(int fromRank, int toRank, boolean starAnimate, Runnable onFinish) {
                onFinish.run();
            }
        });

        logic.onShow();

        Threadings.sleep(500);
        verify(logic, times(1)).winnerHandling();
        verify(logic, times(0)).loserHandling();
        Threadings.sleep(6000);
        Assert.assertEquals(1, logic.getAfterRanking());
        Assert.assertEquals(5, logic.getUpperRankingDifferencePercent());
        Assert.assertEquals(11, logic.getMyLeaderboardRecord().getStreak().getStreakCount());

    }

//    @Test
//    public void testAccLose(){
//        final EndGameData endGameData = MockModel.mockEndGameData();
//        endGameData.getRoom().getGame().setLeaderbordTypeEnum(LeaderboardType.Accumulate);
//
//        EndGameResult endGameResult = endGameData.getEndGameResult();
//        endGameResult.setWon(false);
//        endGameResult.getWinnersScoreDetails().clear();
//
//        final ArrayList<LeaderboardRecord> leaderboardRecords = getSampleLeaderboardRecords(false);
//        final boolean[] called = new boolean[1];
//
//        MockDB mockDB = new MockDB(){
//            @Override
//            public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
//                listener.onCallback(leaderboardRecords, Status.SUCCESS);
//            }
//
//            @Override
//            public void getTeamHighestLeaderBoardRecordAndStreak(Game game, ArrayList<String> teamUserIds, DatabaseListener<LeaderboardRecord> listener) {
//                listener.onCallback(leaderboardRecords.get(2), Status.SUCCESS);
//            }
//
//        };
//
//        Confirm mockConfirm = new Confirm(mock(SpriteBatch.class), mock(IPTGame.class), mock(Assets.class), mock(Broadcaster.class)){
//            @Override
//            public void show(String msg, Type type, ConfirmResultListener _listener) {
//                _listener.onResult(ConfirmResultListener.Result.YES);
//            }
//
//            @Override
//            public void invalidate() {
//            }
//        };
//
//        Services services = T_Services.mockServices();
//        services.setDatabase(mockDB);
//        services.setConfirm(mockConfirm);
//
//        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(Mockings.mockPTScreen(), services, endGameData, endGameResult.getMyTeam()));
//        logic.onShow();
//
//        Threadings.sleep(500);
//        verify(logic, times(0)).winnerHandling();
//        verify(logic, times(1)).loserHandling();
//        Threadings.sleep(6000);
//        Assert.assertEquals(true, called[0]);
//    }

    @Test
    public void testNormalLose(){

        final EndGameData endGameData = MockModel.mockEndGameData();
        endGameData.getRoom().getGame().setLeaderbordTypeEnum(LeaderboardType.Normal);
        endGameData.getRoom().getGame().setStreakEnabled(false);

        EndGameResult endGameResult = endGameData.getEndGameResult();
        endGameResult.setWon(false);

        final ArrayList<LeaderboardRecord> leaderboardRecords = getSampleLeaderboardRecords(false);
        final boolean[] called = new boolean[1];

        MockDB mockDB = new MockDB(){
            @Override
            public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
                listener.onCallback(leaderboardRecords, Status.SUCCESS);
            }

            @Override
            public void getTeamHighestLeaderBoardRecordAndStreak(Game game, ArrayList<String> teamUserIds,  DatabaseListener<LeaderboardRecord> listener) {
                listener.onCallback(leaderboardRecords.get(2), Status.SUCCESS);
            }
        };

        Confirm mockConfirm = new Confirm(mock(SpriteBatch.class), mock(IPTGame.class), mock(Assets.class), mock(Broadcaster.class)){
            @Override
            public void show(String msg, Type type, ConfirmResultListener listener) {
                listener.onResult(ConfirmResultListener.Result.YES);
            }

            @Override
            public void invalidate() {
            }
        };

        Services services = T_Services.mockServices();
        services.setDatabase(mockDB);
        services.setConfirm(mockConfirm);

        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(Mockings.mockPTScreen(), services, endGameData, endGameResult.getMyTeam()));

        logic.onShow();

        Threadings.sleep(500);
        verify(logic, times(0)).winnerHandling();
        verify(logic, times(1)).loserHandling();
        Threadings.sleep(6000);
        Assert.assertEquals(false, called[0]);
    }

    @Test
    public void testProcessOtherTeamAndSortingAccumulate(){
        ArrayList<LeaderboardRecord> afterLeaderboardRecords = testProcessOtherTeam(LeaderboardType.Accumulate, getSampleLeaderboardRecords(false));

        Assert.assertEquals(true, afterLeaderboardRecords.get(0).getUserIds().contains("0"));
        Assert.assertEquals(true, afterLeaderboardRecords.get(1).getUserIds().contains("1"));
        Assert.assertEquals(2000, afterLeaderboardRecords.get(1).getScore(), 0);
        Assert.assertEquals(true, afterLeaderboardRecords.get(2).getUserIds().contains("3"));
    }

    @Test
    public void testProcessOtherTeamAndSortingNormal(){
        ArrayList<LeaderboardRecord> afterLeaderboardRecords = testProcessOtherTeam(LeaderboardType.Normal, getSampleLeaderboardRecords(false));

        Assert.assertEquals(true, afterLeaderboardRecords.get(0).getUserIds().contains("0"));
        Assert.assertEquals(true, afterLeaderboardRecords.get(1).getUserIds().contains("3"));
        Assert.assertEquals(true, afterLeaderboardRecords.get(2).getUserIds().contains("1"));
        Assert.assertEquals(1000, afterLeaderboardRecords.get(2).getScore(), 0);
    }


    @Test
    public void testProcessOtherTeamWithTeamNotInLeaderboard(){
        ArrayList<LeaderboardRecord> afterLeaderboardRecords = testProcessOtherTeam(LeaderboardType.Normal, new ArrayList());

        Assert.assertEquals(true, afterLeaderboardRecords.get(0).getUserIds().contains("1"));
        Assert.assertEquals(1000, afterLeaderboardRecords.get(0).getScore(), 0);
        Assert.assertEquals(1, afterLeaderboardRecords.size());

    }

    @Test
    public void testProcessOtherTeamWithTeamNotInLeaderboard2(){
        ArrayList<LeaderboardRecord> records = getSampleLeaderboardRecords(false);
        records.remove(records.get(2));
        ArrayList<LeaderboardRecord> afterLeaderboardRecords2 = testProcessOtherTeam(LeaderboardType.Normal, records);
        Assert.assertEquals(true, afterLeaderboardRecords2.get(2).getUserIds().contains("1"));
        Assert.assertEquals(1000, afterLeaderboardRecords2.get(2).getScore(), 0);
        Assert.assertEquals(3, afterLeaderboardRecords2.size());
    }

    @Test
    public void testProcessOtherTeamWithTeamNotInLeaderboard3(){
        ArrayList<LeaderboardRecord> records = getSampleLeaderboardRecords(true);
        records.remove(records.get(2));
        ArrayList<LeaderboardRecord> afterLeaderboardRecords2 = testProcessOtherTeam(LeaderboardType.Normal, records);
        Assert.assertEquals(true, afterLeaderboardRecords2.get(0).getUserIds().contains("1"));
        Assert.assertEquals(1000, afterLeaderboardRecords2.get(0).getScore(), 0);
        Assert.assertEquals(3, afterLeaderboardRecords2.size());
    }

    @Test
    public void testProcessOtherTeamWithTeamNotInLeaderboard4(){
        int original =  Global.LEADERBOARD_COUNT;
        Global.LEADERBOARD_COUNT = 2;

        ArrayList<LeaderboardRecord> afterLeaderboardRecords = testProcessOtherTeam(LeaderboardType.Normal, getSampleLeaderboardRecords(true));

        Assert.assertEquals(true, afterLeaderboardRecords.get(0).getUserIds().contains("1"));
        Assert.assertEquals(1000, afterLeaderboardRecords.get(0).getScore(), 0);
        Assert.assertEquals(true, afterLeaderboardRecords.get(1).getUserIds().contains("0"));
        Assert.assertEquals(2, afterLeaderboardRecords.size());

        Global.LEADERBOARD_COUNT = original;
    }


    private ArrayList<LeaderboardRecord> testProcessOtherTeam(LeaderboardType leaderboardType, ArrayList<LeaderboardRecord> leaderboardRecords){
        final EndGameData endGameData = MockModel.mockEndGameData();
        endGameData.getRoom().getGame().setLeaderbordTypeEnum(leaderboardType);
        endGameData.getRoom().getGame().setStreakEnabled(false);

        EndGameResult endGameResult = endGameData.getEndGameResult();
        endGameResult.setWon(false);

        Team myTeam = new Team();
        myTeam.addPlayer(new Player("", MockModel.mockProfile().getUserId(), true, 0));
        endGameResult.setMyTeam(myTeam.getPlayers());

        ArrayList<Team> losersTeam = new ArrayList<>();
        losersTeam.add(myTeam);

        endGameResult.setLoserTeams(losersTeam);

        Services services = T_Services.mockServices();

        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(Mockings.mockPTScreen(), services, endGameData, endGameResult.getMyTeam()){
            @Override
            public void getLeaderBoardAndMyCurrentRank() {
            }
        });

        logic.setLeaderboardRecords(leaderboardRecords);
        logic.processOtherTeamScoresAndStreaks();
        return  logic.getLeaderboardRecords();
    }


    private ArrayList<LeaderboardRecord> getSampleLeaderboardRecords(boolean lowMark){
        final LeaderboardRecord leaderboardRecord1 = new LeaderboardRecord();
        leaderboardRecord1.addUserId("0");
        leaderboardRecord1.addUserName("0", "0");
        leaderboardRecord1.setScore(lowMark ? 200 : 2000);

        final LeaderboardRecord leaderboardRecord3 = new LeaderboardRecord();
        leaderboardRecord3.addUserId("3");
        leaderboardRecord3.addUserName("3", "3");
        leaderboardRecord3.setScore(lowMark ? 150 : 1500);

        final LeaderboardRecord leaderboardRecord2 = new LeaderboardRecord();
        leaderboardRecord2.addUserId("1");
        leaderboardRecord2.addUserName("1", "1");
        leaderboardRecord2.setScore(lowMark ? 100 : 1000);
        leaderboardRecord2.getStreak().setStreakCount(10);

        ArrayList<LeaderboardRecord> records = new ArrayList<LeaderboardRecord>();
        records.add(leaderboardRecord1);
        records.add(leaderboardRecord3);
        records.add(leaderboardRecord2);

        return  records;
    }

}
