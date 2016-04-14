import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.ImageStorageListener;
import com.potatoandtomato.games.absintf.mockings.MockModel;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.SimpleRectangle;
import com.potatoandtomato.games.models.WonStageModel;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.main.ImageStorage;
import com.potatoandtomato.games.screens.main.MainLogic;
import com.potatoandtomato.games.screens.main.MainScreen;
import com.potatoandtomato.games.screens.review.ReviewLogic;
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
        gameModel.setRemainingMiliSecs(0);
        verify(mainLogic.getServices().getRoomMsgHandler(), times(0)).sendLose();

    }

    @Test
    public void testWin(){

        GameModel gameModel = new GameModel();

        MainLogic mainLogic = Mockito.spy(getMainLogic(gameModel));
        Services services = mainLogic.getServices();

        mainLogic.goToNewStage("1", StageType.Normal, "");

        Threadings.sleep(200);

        String userId = _game.getCoordinator().getMyUserId();

        mainLogic.imageTouched(userId, 47, 303, 1, false);
        mainLogic.imageTouched(userId, 10, 358, 1, false);
        mainLogic.imageTouched(userId, 108, 60, 1, false);
        mainLogic.imageTouched(userId, 123, 198, 0, false);

        verify(services.getRoomMsgHandler(), times(0)).sendWon(any(WonStageModel.class));
        Assert.assertEquals(GameState.Playing, mainLogic.getGameModel().getGameState());

        mainLogic.imageTouched(userId, 55, 422, 0, false);
        mainLogic.imageTouched(userId, 55, 422, 0, false);

        Threadings.sleep(3000);

        verify(services.getRoomMsgHandler(), times(1)).sendWon(any(WonStageModel.class));
        Assert.assertEquals(GameState.Ended, mainLogic.getGameModel().getGameState());

        verify(services.getRoomMsgHandler(), times(0)).sendLose();
    }

    @Test
    public void testWrongAndCorrectTouched(){

        GameModel gameModel = Mockito.spy(new GameModel());

        MainLogic mainLogic = Mockito.spy(getMainLogic(gameModel));

        mainLogic.goToNewStage("1", StageType.Normal, "");

        Threadings.sleep(200);


        String userId = _game.getCoordinator().getMyUserId();

        mainLogic.imageTouched(userId, 1, 1, 30, false);
        verify(mainLogic.getTimeLogic(), times(1)).reduceTime();

        mainLogic.imageTouched(userId, 1, 1, 30, false);
        mainLogic.imageTouched(userId, 1, 1, 30, false);
        verify(mainLogic.getTimeLogic(), times(3)).reduceTime();

        mainLogic.imageTouched(userId, 55, 422, 0, false);
        verify(gameModel, times(1)).addHandledArea(any(SimpleRectangle.class), any(String.class));

    }


    private MainLogic getMainLogic(GameModel gameModel){
        Services services =  Mockings.mockServices(_game.getCoordinator());

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

        MainLogic mainLogic = new MainLogic(_game.getCoordinator(), services, mock(TimeLogic.class),
                mock(HintsLogic.class), mock(ReviewLogic.class), mock(UserCountersLogic.class), mock(StageCounterLogic.class),
                imageStorage, gameModel){
            @Override
            public void changeScreenImages(Texture texture1, Texture texture2) {

            }
        };

        mainLogic.setMainScreen(mainScreen);

        return mainLogic;

    }



}
