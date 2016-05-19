package helpers;

import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.absints.IPTGame;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 11/5/2016.
 */
public class Mockings {

    public static PTScreen mockPTScreen(){
        PTScreen ptScreen = mock(PTScreen.class);
        doAnswer(new Answer<IPTGame>() {
            @Override
            public IPTGame answer(InvocationOnMock invocation) throws Throwable {
                return mock(PTGame.class);
            }
        }).when(ptScreen).getGame();

        return ptScreen;
    }

}
