import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.assets.AnimationAssets;
import com.potatoandtomato.common.assets.FontAssets;
import com.potatoandtomato.common.assets.PatchAssets;
import com.potatoandtomato.common.assets.SoundAssets;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.services.Database;
import com.potatoandtomato.games.services.RoomMsgHandler;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;
import org.mockito.Mockito;

/**
 * Created by SiongLeng on 7/4/2016.
 */
public class Mockings {

    public static  Services mockServices(){
        MyAssets myAssets = new MyAssets(Mockito.mock(AssetManager.class), Mockito.mock(FontAssets.class),
                Mockito.mock(AnimationAssets.class), Mockito.mock(SoundAssets.class), Mockito.mock(PatchAssets.class),
                Mockito.mock(Textures.class));

        Services services = new Services(myAssets, Mockito.mock(SoundsWrapper.class), Mockito.mock(Database.class),
                Mockito.mock(Texts.class), Mockito.mock(RoomMsgHandler.class));
        return  services;
    }

}
