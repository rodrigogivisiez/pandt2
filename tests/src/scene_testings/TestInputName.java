package scene_testings;

import abstracts.MockDB;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.input_name_scene.InputNameLogic;
import com.mygdx.potatoandtomato.scenes.input_name_scene.InputNameScene;
import com.potatoandtomato.common.Status;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by SiongLeng on 12/1/2016.
 */
public class TestInputName extends TestAbstract {

    @Test
    public void testInputNamesSceneLogic(){
        InputNameLogic logic = new InputNameLogic(mock(PTScreen.class), T_Services.mockServices());
        logic.onInit();
        logic.onShow();
        InputNameScene scene = (InputNameScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testSaveNameSuccess(){
        PTScreen screen = mock(PTScreen.class);
        Services services = T_Services.mockServices();
        final boolean[] waiting = {true};
        IDatabase database = new MockDB(){
            @Override
            public void updateProfile(Profile profile, DatabaseListener listener) {
                Assert.assertEquals("john", profile.getGameName());
                super.updateProfile(profile, listener);
                waiting[0] = false;
            }

            @Override
            public void getProfileByGameNameLower(String gameName, DatabaseListener<Profile> listener) {
                listener.onCallback(null, Status.SUCCESS);
            }

        };
        services.setDatabase(database);
        InputNameLogic logic = Mockito.spy(new InputNameLogic(screen, services));
        logic.saveNameIfValid("john                    ");
        verify(screen, times(1)).toScene(SceneEnum.GAME_LIST);
        Assert.assertEquals("john", services.getProfile().getGameName());
        while (waiting[0]){
            Threadings.sleep(100);
        }


    }

    @Test
    public void testSaveNameFailed(){
        PTScreen screen = mock(PTScreen.class);
        InputNameLogic logic = Mockito.spy(new InputNameLogic(screen, T_Services.mockServices()));
        logic.saveNameIfValid("   ");
        verify(screen, times(0)).toScene(SceneEnum.GAME_LIST);
    }



}
