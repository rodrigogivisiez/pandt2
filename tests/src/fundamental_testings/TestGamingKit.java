package fundamental_testings;

import abstracts.MockGamingKit;
import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public class TestGamingKit extends TestAbstract {

    @Test
    public void testAddRemoveListeners(){

        GamingKit kit = new MockGamingKit();
        kit.addListener(getClassTag(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ClientConnectionStatus st) {

            }
        });

        Assert.assertEquals(1, kit.getConnectionChangedListeners().size());

        kit.removeListenersByClassTag(getClassTag());

        Assert.assertEquals(0, kit.getConnectionChangedListeners().size());

    }

}
