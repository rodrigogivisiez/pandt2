import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.assets.AnimationAssets;
import com.potatoandtomato.common.assets.FontAssets;
import com.potatoandtomato.common.assets.PatchAssets;
import com.potatoandtomato.common.assets.SoundAssets;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.*;
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

    public static  Services mockServices(GameCoordinator gameCoordinator){
        AssetManager manager = gameCoordinator.getAssetManager(true);

        MyAssets myAssets = new MyAssets(manager, new Fonts(manager),
                new Animations(manager), Mockito.mock(Sounds.class), Mockito.mock(PatchAssets.class),
                new Textures(manager, "pack.atlas"));

        myAssets.loadBasic(null);


        Services services = new Services(myAssets, Mockito.mock(SoundsWrapper.class), Mockito.mock(Database.class),
                Mockito.mock(Texts.class), Mockito.mock(RoomMsgHandler.class));
        return  services;
    }

}
