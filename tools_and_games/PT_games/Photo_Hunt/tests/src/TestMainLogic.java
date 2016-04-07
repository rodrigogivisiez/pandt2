import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.ImageStorageListener;
import com.potatoandtomato.games.absintf.mockings.MockModel;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.ImageDetails;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.WonStageModel;
import com.potatoandtomato.games.screens.main.ImageStorage;
import com.potatoandtomato.games.screens.main.MainLogic;
import com.potatoandtomato.games.screens.main.MainScreen;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;
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
    public void testTimeFinished(){

        Services services =  Mockings.mockServices();

        MainLogic mainLogic = new MainLogic(services, _game.getCoordinator()){
            @Override
            public void changeScreenImages(Texture texture1, Texture texture2) {

            }
        };
        GameModel gameModel = Mockito.spy(new GameModel());
        doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return 2;
            }
        }).when(gameModel).convertStageNumberToRemainingSecs();

        mainLogic.setGameModel(gameModel);

        ImageStorage imageStorage = new ImageStorage(services, _game.getCoordinator()){
            @Override
            public void pop(String id, ImageStorageListener listener) {
                listener.onPopped(MockModel.mockImagePair());
            }
        };

        mainLogic.setImageStorage(imageStorage);

        mainLogic.newStage("1", StageType.Normal, "");

        Threadings.sleep(4000);

        verify(services.getRoomMsgHandler(), times(1)).sendLose();

    }

    @Test
    public void testWin(){

        Services services =  Mockings.mockServices();

        MainLogic mainLogic = Mockito.spy(new MainLogic(services, _game.getCoordinator()){
            @Override
            public void changeScreenImages(Texture texture1, Texture texture2) {

            }
        });

        MainScreen mainScreen = Mockito.mock(MainScreen.class);
        doAnswer(new Answer<Vector2>() {
            @Override
            public Vector2 answer(InvocationOnMock invocation) throws Throwable {
                return new Vector2(178, 440);
            }
        }).when(mainScreen).getImageSize();

        mainLogic.setMainScreen(mainScreen);

        ImageStorage imageStorage = new ImageStorage(services, _game.getCoordinator()){
            @Override
            public void pop(String id, ImageStorageListener listener) {
                listener.onPopped(MockModel.mockImagePair());
            }
        };

        mainLogic.setImageStorage(imageStorage);

        mainLogic.newStage("1", StageType.Normal, "");

        Threadings.sleep(200);

        mainLogic.imageTouched(47, 303, 1);
        mainLogic.imageTouched(10, 358, 1);
        mainLogic.imageTouched(108, 60, 1);
        mainLogic.imageTouched(123, 198, 0);

        verify(services.getRoomMsgHandler(), times(0)).sendWon(any(WonStageModel.class));
        Assert.assertEquals(GameState.Playing, mainLogic.getGameModel().getGameState());
        Assert.assertEquals(true, mainLogic.getTimeLogic().isTimeRunning());

        mainLogic.imageTouched(55, 422, 0);
        mainLogic.imageTouched(55, 422, 0);

        Threadings.sleep(2000);

        verify(services.getRoomMsgHandler(), times(1)).sendWon(any(WonStageModel.class));
        Assert.assertEquals(GameState.Ended, mainLogic.getGameModel().getGameState());
        Assert.assertEquals(false, mainLogic.getTimeLogic().isTimeRunning());

        verify(mainLogic, times(0)).timeFinished();

    }

    @Test
    public void testWrongTouched(){
        Services services = Mockings.mockServices();

        MainLogic mainLogic = Mockito.spy(new MainLogic(services, _game.getCoordinator()){
            @Override
            public void changeScreenImages(Texture texture1, Texture texture2) {

            }
        });

        MainScreen mainScreen = Mockito.mock(MainScreen.class);
        doAnswer(new Answer<Vector2>() {
            @Override
            public Vector2 answer(InvocationOnMock invocation) throws Throwable {
                return new Vector2(178, 440);
            }
        }).when(mainScreen).getImageSize();

        mainLogic.setMainScreen(mainScreen);


        ImageStorage imageStorage = new ImageStorage(services, _game.getCoordinator()){
            @Override
            public void pop(String id, ImageStorageListener listener) {
                listener.onPopped(MockModel.mockImagePair());
            }
        };

        mainLogic.setImageStorage(imageStorage);

        mainLogic.newStage("1", StageType.Normal, "");

        Threadings.sleep(200);

        mainLogic.setTimeLogic(Mockito.mock(TimeLogic.class));

        mainLogic.imageTouched(1, 1, 30);
        verify(mainLogic.getTimeLogic(), times(1)).reduceTime();

        mainLogic.imageTouched(1, 1, 30);
        mainLogic.imageTouched(1, 1, 30);
        verify(mainLogic.getTimeLogic(), times(3)).reduceTime();
    }




}
