package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickLogic;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickScene;
import com.mygdx.potatoandtomato.scenes.social_login_scene.SocialLoginLogic;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class TestMascotPick extends TestAbstract {

    @Test
    public void testMascotPickLogicScene(){
        MascotPickLogic logic = new MascotPickLogic(mock(PTScreen.class), Helpers.mockAssets());
        MascotPickScene scene = (MascotPickScene) logic.getScene();
        scene.choosedMascot(MascotEnum.POTATO);
        scene.choosedMascot(MascotEnum.TOMATO);
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());

    }


}
