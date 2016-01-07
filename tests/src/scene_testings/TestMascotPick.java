package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickLogic;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickScene;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class TestMascotPick extends TestAbstract {

    @Test
    public void testMascotPickLogicScene(){
        MascotPickLogic logic = new MascotPickLogic(mock(PTScreen.class), T_Services.mockServices());
        MascotPickScene scene = (MascotPickScene) logic.getScene();
        scene.choosedMascot(MascotEnum.POTATO);
        scene.choosedMascot(MascotEnum.TOMATO);
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }


    @Test
    public void testUpdateMascot(){

        final boolean[] called = {false};
        Services mockServices = T_Services.mockServices(mock(IDatabase.class));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if(((Profile) arguments[0]).getMascotEnum() == MascotEnum.TOMATO) called[0] = true;
                return null;
            }
        }).when(mockServices.getDatabase()).updateProfile(any(Profile.class), any(DatabaseListener.class));


        MascotPickLogic logic = new MascotPickLogic(mock(PTScreen.class), mockServices);
        logic.updateMascot(MascotEnum.TOMATO);
        Assert.assertEquals(MascotEnum.TOMATO, mockServices.getProfile().getMascotEnum());
        Assert.assertEquals(true, called[0]);
    }


}
