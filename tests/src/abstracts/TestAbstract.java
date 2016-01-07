package abstracts;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.potatoandtomato.PTGame;
import com.potatoandtomato.common.Broadcaster;
import helpers.T_Services;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

import static helpers.T_Services.mockServices;
import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 1/12/2015.
 */
public abstract class TestAbstract {

    protected static PTGame _game;

    @BeforeClass
    public static void oneTimeSetUp() {

        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        Gdx.app = mock(Application.class);

        final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(mock(PTGame.class), config);
    }

    @Before
    public void setUp() throws Exception {
        Broadcaster.getInstance().clear();
    }

    @After
    public void tearDown() throws Exception {
        Broadcaster.getInstance().clear();
    }

    protected String getClassTag(){
        return this.getClass().getName();
    }

}