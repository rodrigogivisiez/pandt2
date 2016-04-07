import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.potatoandtomato.games.PhotoHuntGame;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public abstract class TestAbstract {

    protected static PhotoHuntGame _game;

    @BeforeClass
    public static void oneTimeSetUp() {

        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        Gdx.app = mock(Application.class);

        final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(mock(PhotoHuntGame.class), config);
        _game = new PhotoHuntGame("photo_hunt");
        _game.getCoordinator().setSpriteBatch(mock(SpriteBatch.class));
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    protected String getClassTag(){
        return this.getClass().getName();
    }

}
