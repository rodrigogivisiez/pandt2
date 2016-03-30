package scene_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.helpers.services.Confirm;
import com.mygdx.potatoandtomato.models.Services;
import helpers.T_Services;
import org.junit.Test;

/**
 * Created by SiongLeng on 6/1/2016.
 */
public class TestConfirm extends TestAbstract {

    @Test
    public void testConfirm(){

        Services services = T_Services.mockServices();
        final boolean[] waiting = {true};

        Confirm _confirm = services.getConfirm();
        _confirm.show("test", Confirm.Type.YESNO, new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {

            }
        });

    }

}
