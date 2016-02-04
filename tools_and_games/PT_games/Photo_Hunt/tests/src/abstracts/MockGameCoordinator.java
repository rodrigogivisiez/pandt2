package abstracts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.potatoandtomato.common.*;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class MockGameCoordinator extends GameCoordinator {

    public static int currentUserId = 0;

    public MockGameCoordinator() {
        super("", "", "", MockTeam.getTeams(1, 2), 300, 450, null, mock(SpriteBatch.class), String.valueOf(currentUserId), mock(IGameSandBox.class),
                null, "", null, mock(Broadcaster.class), mock(IDownloader.class));

        currentUserId++;
    }
}
