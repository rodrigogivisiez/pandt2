package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootScene;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 1/12/2015.
 */
public class TestBoot extends TestAbstract {

    @Test
    public void testBootLogicScene(){

        BootLogic logic = new BootLogic(mock(PTScreen.class), new Textures(), new Fonts());
        Assert.assertEquals(true, ((Table) logic.getScene().getRoot()).hasChildren());

    }
}
