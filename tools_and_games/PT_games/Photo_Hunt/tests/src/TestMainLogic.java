import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.ImageStorageListener;
import com.potatoandtomato.games.absintf.mockings.MockModel;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.SimpleRectangle;
import com.potatoandtomato.games.models.WonStageModel;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.main.*;
import com.potatoandtomato.games.screens.review.ReviewLogic;
import com.potatoandtomato.games.screens.scores.ScoresLogic;
import com.potatoandtomato.games.screens.stage_counter.StageCounterLogic;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;
import com.potatoandtomato.games.screens.user_counters.UserCountersLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class TestMainLogic extends TestAbstract {

    @Test
    public void testLose(){

        GameModel gameModel = new GameModel();

        MainLogic mainLogic = Mockito.spy(getMainLogic(gameModel));
        gameModel.setRemainingMiliSecs(0, true);
        verify(mainLogic.getServices().getRoomMsgHandler(), times(0)).sendLose();

    }

    @Test
    public void testWin(){

        GameModel gameModel = new GameModel();

        MainLogic mainLogic = Mockito.spy(getMainLogic(gameModel));
        Services services = mainLogic.getServices();

        mainLogic.goToNewStage("1", StageType.Normal, BonusType.NONE, "");

        Threadings.sleep(200);

        mainLogic.imageTouched(47, 303, 1, 3);
        mainLogic.imageTouched(10, 358, 1, 3);
        mainLogic.imageTouched(108, 60, 1, 3);
        mainLogic.imageTouched(123, 198, 0, 3);

        Threadings.sleep(200);

        verify(services.getRoomMsgHandler(), times(0)).sendWon(any(WonStageModel.class));
        Assert.assertEquals(GameState.Playing, mainLogic.getGameModel().getGameState());

        mainLogic.imageTouched(55, 422, 0, 3);
        mainLogic.imageTouched(55, 422, 0, 3);

        Threadings.sleep(3000);

        verify(services.getRoomMsgHandler(), times(1)).sendWon(any(WonStageModel.class));
        Assert.assertEquals(GameState.Pause, mainLogic.getGameModel().getGameState());

        verify(services.getRoomMsgHandler(), times(0)).sendLose();
    }

    @Test
    public void testWrongAndCorrectTouched(){

        GameModel gameModel = Mockito.spy(new GameModel());

        MainLogic mainLogic = Mockito.spy(getMainLogic(gameModel));

        mainLogic.goToNewStage("1", StageType.Normal, BonusType.NONE, "");

        Threadings.sleep(200);

        mainLogic.imageTouched(1, 1, 30, 3);
        verify(mainLogic.getTimeLogic(), times(1)).reduceTime();

        mainLogic.imageTouched(1, 1, 30, 3);
        mainLogic.imageTouched(1, 1, 30, 3);
        verify(mainLogic.getTimeLogic(), times(3)).reduceTime();

        mainLogic.imageTouched( 55, 422, 0, 3);
        verify(gameModel, times(1)).addHandledArea(any(SimpleRectangle.class), any(Integer.class));

    }


    private MainLogic getMainLogic(GameModel gameModel){
        final Services services =  Mockings.mockServices(_game.getCoordinator());

        ImageStorage imageStorage = new ImageStorage(services, _game.getCoordinator()){
            @Override
            public void pop(String id, ImageStorageListener listener) {
                listener.onPopped(MockModel.mockImagePair());
            }
        };

        MainScreen mainScreen = Mockito.mock(MainScreen.class);
        doAnswer(new Answer<Vector2>() {
            @Override
            public Vector2 answer(InvocationOnMock invocation) throws Throwable {
                return new Vector2(178, 440);
            }
        }).when(mainScreen).getImageSize();

        StageStateLogic stageStateLogic = mock(StageStateLogic.class);
        doAnswer(new Answer<Table>() {
            @Override
            public Table answer(InvocationOnMock invocation) throws Throwable {
                return new StageStateActor(services);
            }
        }).when(stageStateLogic).getStageStateActor();


        MainLogic mainLogic = new MainLogic(_game.getCoordinator(), services, mock(TimeLogic.class),
                mock(HintsLogic.class), mock(ReviewLogic.class), mock(UserCountersLogic.class), mock(StageCounterLogic.class),
                mock(ScoresLogic.class), imageStorage, gameModel, mock(StageImagesLogic.class), stageStateLogic){
            @Override
            public boolean meIsThisStageDecisionMaker() {
                return true;
            }
        };

        mainLogic.setMainScreen(mainScreen);

        return mainLogic;

    }



}
