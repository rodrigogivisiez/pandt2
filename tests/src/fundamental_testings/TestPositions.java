package fundamental_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class TestPositions extends TestAbstract {

    @Test
    public void testCenterX(){
        Positions.setWidth(360);
        Assert.assertEquals(140, Positions.centerX(80), 0);
    }


}
