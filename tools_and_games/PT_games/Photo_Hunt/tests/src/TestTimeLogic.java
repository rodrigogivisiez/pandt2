import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.screens.time_bar.CastleLogic;
import com.potatoandtomato.games.screens.time_bar.KingLogic;
import com.potatoandtomato.games.screens.time_bar.KnightLogic;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public class TestTimeLogic extends TestAbstract {

    @Test
    public void testTimeOut(){

        GameModel gameModel = new GameModel();
        gameModel.setRemainingMiliSecs(1000, true);

        TimeLogic timeLogic = getTimeLogicAndRestart(gameModel);

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onTimeFinished() {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
    }

    @Test
    public void testFreezed(){

        GameModel gameModel = Mockito.spy(new GameModel());
        gameModel.setRemainingMiliSecs(1000, true);

        TimeLogic timeLogic = getTimeLogicAndRestart(gameModel);

        gameModel.addFreezeMiliSecs();

        Threadings.sleep(1000);
        Assert.assertEquals(true, gameModel.getFreezingMiliSecs() < 2000 && gameModel.getFreezingMiliSecs() > 0);
        Assert.assertEquals(1000, timeLogic.getGameModel().getRemainingMiliSecs());


        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onTimeFinished() {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
        Assert.assertEquals(0, gameModel.getFreezingMiliSecs());

    }

    @Test
    public void testStopAndRestart(){
        GameModel gameModel = Mockito.spy(new GameModel());
        gameModel.setRemainingMiliSecs(5000, true);

        TimeLogic timeLogic = getTimeLogicAndRestart(gameModel);

        Threadings.sleep(500);

        timeLogic.stop();

        Threadings.sleep(1000);

        gameModel.setRemainingMiliSecs(1000, true);
        timeLogic.restart();

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onTimeFinished() {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
        Assert.assertEquals(0, gameModel.getFreezingMiliSecs());

    }



    private TimeLogic getTimeLogicAndRestart(GameModel gameModel){

        TimeLogic timeLogic = new TimeLogic(Mockings.mockServices(_game.getCoordinator()), _game.getCoordinator(),
                Mockito.mock(KingLogic.class), Mockito.mock(CastleLogic.class), Mockito.mock(KnightLogic.class), gameModel);
        timeLogic.restart();
        return timeLogic;
    }



}
