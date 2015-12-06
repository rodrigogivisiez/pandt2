package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Assets;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.mygdx.potatoandtomato.scenes.social_login_scene.SocialLoginLogic;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class TestSocialLogin extends TestAbstract {

    @Test
    public void testSocialLoginLogicScene(){
        SocialLoginLogic logic = new SocialLoginLogic(mock(PTScreen.class), Helpers.mockAssets());
        Assert.assertEquals(true, ((Table) logic.getScene().getRoot()).hasChildren());
    }

}
