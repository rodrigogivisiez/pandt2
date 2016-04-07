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
import com.mygdx.potatoandtomato.helpers.services.Confirm;
import com.mygdx.potatoandtomato.models.EndGameData;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.LeaderBoardScene;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.models.EndGameResult;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.ScoreDetails;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 18/3/2016.
 */
public class TestEndGameLeaderboard extends TestAbstract {

    @Test
    public void testEndGameLeaderboardLogicScene(){
        EndGameLeaderBoardLogic logic = new EndGameLeaderBoardLogic(mock(PTScreen.class), T_Services.mockServices(), MockModel.mockEndGameData(),
                MockModel.mockEndGameData().getEndGameResult().getMyTeam());
        LeaderBoardScene scene = (LeaderBoardScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }


    @Test
    public void testWin(){

        final EndGameData endGameData = MockModel.mockEndGameData();
        endGameData.getRoom().getGame().setLeaderbordTypeEnum(LeaderboardType.Accumulate);

        EndGameResult endGameResult = endGameData.getEndGameResult();
        endGameResult.setWon(true);

        ArrayList<ScoreDetails> scoreDetails = new ArrayList<ScoreDetails>();
        scoreDetails.add(new ScoreDetails(900, "test", true, true));
        endGameResult.setScoreDetails(scoreDetails);

        final ArrayList<LeaderboardRecord> leaderboardRecords = getSampleLeaderboardRecords();

        MockDB mockDB = new MockDB(){
            @Override
            public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
                listener.onCallback(leaderboardRecords, Status.SUCCESS);
            }

            @Override
            public void getAccLeaderBoardRecordAndStreak(Room room, ArrayList<String> userIds, DatabaseListener<LeaderboardRecord> listener) {
                listener.onCallback(leaderboardRecords.get(2), Status.SUCCESS);
            }
        };

        Confirm mockConfirm = new Confirm(mock(SpriteBatch.class), mock(IPTGame.class), mock(Assets.class), mock(Broadcaster.class)){
            @Override
            public void show(String msg, Type type, ConfirmResultListener _listener) {
                _listener.onResult(ConfirmResultListener.Result.YES);
            }

            @Override
            public void invalidate() {
            }
        };

        Services services = T_Services.mockServices();
        services.setDatabase(mockDB);
        services.setConfirm(mockConfirm);

        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(mock(PTScreen.class), services, endGameData, endGameResult.getMyTeam()){
            @Override
            public void addScoresRecur(int index, Runnable onFinish) {
                onFinish.run();
            }

            @Override
            public void moveUpRankAnimation(int fromRank, int toRank, Runnable onFinish) {
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

    @Test
    public void testAccLose(){
        final EndGameData endGameData = MockModel.mockEndGameData();
        endGameData.getRoom().getGame().setLeaderbordTypeEnum(LeaderboardType.Accumulate);

        EndGameResult endGameResult = endGameData.getEndGameResult();
        endGameResult.setWon(false);

        final ArrayList<LeaderboardRecord> leaderboardRecords = getSampleLeaderboardRecords();
        final boolean[] called = new boolean[1];

        MockDB mockDB = new MockDB(){
            @Override
            public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
                listener.onCallback(leaderboardRecords, Status.SUCCESS);
            }

            @Override
            public void getAccLeaderBoardRecordAndStreak(Room room, ArrayList<String> userIds, DatabaseListener<LeaderboardRecord> listener) {
                listener.onCallback(leaderboardRecords.get(2), Status.SUCCESS);
            }

            @Override
            public void streakRevive(ArrayList<String> userIds, Room room, DatabaseListener listener) {
                Assert.assertEquals(true, userIds.contains(endGameData.getEndGameResult().getMyTeam().get(0).getUserId()));
                Assert.assertEquals(endGameData.getRoom(), room);
                called[0] = true;
            }
        };

        Confirm mockConfirm = new Confirm(mock(SpriteBatch.class), mock(IPTGame.class), mock(Assets.class), mock(Broadcaster.class)){
            @Override
            public void show(String msg, Type type, ConfirmResultListener _listener) {
                _listener.onResult(ConfirmResultListener.Result.YES);
            }

            @Override
            public void invalidate() {
            }
        };

        Services services = T_Services.mockServices();
        services.setDatabase(mockDB);
        services.setConfirm(mockConfirm);

        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(mock(PTScreen.class), services, endGameData, endGameResult.getMyTeam()));

        logic.onShow();

        Threadings.sleep(500);
        verify(logic, times(0)).winnerHandling();
        verify(logic, times(1)).loserHandling();
        Threadings.sleep(6000);
        Assert.assertEquals(true, called[0]);
    }

    @Test
    public void testNormalLose(){

        final EndGameData endGameData = MockModel.mockEndGameData();
        endGameData.getRoom().getGame().setLeaderbordTypeEnum(LeaderboardType.Normal);
        endGameData.getRoom().getGame().setStreakEnabled(false);

        EndGameResult endGameResult = endGameData.getEndGameResult();
        endGameResult.setWon(false);

        final ArrayList<LeaderboardRecord> leaderboardRecords = getSampleLeaderboardRecords();
        final boolean[] called = new boolean[1];

        MockDB mockDB = new MockDB(){
            @Override
            public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {
                listener.onCallback(leaderboardRecords, Status.SUCCESS);
            }

            @Override
            public void getAccLeaderBoardRecordAndStreak(Room room, ArrayList<String> userIds, DatabaseListener<LeaderboardRecord> listener) {
                listener.onCallback(leaderboardRecords.get(2), Status.SUCCESS);
            }

            @Override
            public void streakRevive(ArrayList<String> userIds, Room room, DatabaseListener listener) {
                called[0] = true;
            }
        };

        Confirm mockConfirm = new Confirm(mock(SpriteBatch.class), mock(IPTGame.class), mock(Assets.class), mock(Broadcaster.class)){
            @Override
            public void show(String msg, Type type, ConfirmResultListener _listener) {
                _listener.onResult(ConfirmResultListener.Result.YES);
            }

            @Override
            public void invalidate() {
            }
        };

        Services services = T_Services.mockServices();
        services.setDatabase(mockDB);
        services.setConfirm(mockConfirm);

        EndGameLeaderBoardLogic logic = Mockito.spy(new EndGameLeaderBoardLogic(mock(PTScreen.class), services, endGameData, endGameResult.getMyTeam()));

        logic.onShow();

        Threadings.sleep(500);
        verify(logic, times(0)).winnerHandling();
        verify(logic, times(1)).loserHandling();
        Threadings.sleep(6000);
        Assert.assertEquals(false, called[0]);
    }

    private ArrayList<LeaderboardRecord> getSampleLeaderboardRecords(){
        final LeaderboardRecord leaderboardRecord1 = new LeaderboardRecord();
        leaderboardRecord1.addUserId("0");
        leaderboardRecord1.addUserName("0", "0");
        leaderboardRecord1.setScore(2000);

        final LeaderboardRecord leaderboardRecord3 = new LeaderboardRecord();
        leaderboardRecord3.addUserId("3");
        leaderboardRecord3.addUserName("3", "3");
        leaderboardRecord3.setScore(1500);

        final LeaderboardRecord leaderboardRecord2 = new LeaderboardRecord();
        leaderboardRecord2.addUserId("1");
        leaderboardRecord2.addUserName("1", "1");
        leaderboardRecord2.setScore(1000);
        leaderboardRecord2.getStreak().setStreakCount(10);

        ArrayList<LeaderboardRecord> records = new ArrayList<LeaderboardRecord>();
        records.add(leaderboardRecord1);
        records.add(leaderboardRecord3);
        records.add(leaderboardRecord2);

        return  records;
    }

}
