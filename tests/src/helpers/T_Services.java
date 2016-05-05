package helpers;

import abstracts.MockDB;
import abstracts.MockDownloader;
import abstracts.MockGamingKit;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.absints.ITutorials;
import com.potatoandtomato.common.assets.Assets;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class T_Services {

    public static Services mockServices(){
        return mockServices(new MockDB());
    }

    public static Services mockServices(IDatabase databases){
        return mockServices(databases, new MockDownloader());
    }

    public static Services mockServices(IDownloader downloader){
        return mockServices(new MockDB(), downloader);
    }

    public static Services mockServices(IDatabase databases, IDownloader downloader){
        Preferences preferences = new Preferences("potatoandtomato_test");
        preferences.deleteAll();

        PTAssetsManager manager = new PTAssetsManager(new InternalFileHandleResolver(), mock(PTGame.class));
        Animations animations = new Animations(manager);
        Patches patches = new Patches();
        Sounds sounds = new Sounds(manager);
        Textures textures = new Textures(manager, "ui_pack.atlas");
        Fonts fonts = new Fonts(manager);

        Assets assets  = new Assets(manager, fonts, animations, sounds, patches, textures);

        assets.loadSync(null);
        MockGamingKit gamingKit = new MockGamingKit();
        Broadcaster broadcaster = new Broadcaster();
        Profile profile = MockModel.mockProfile();

        return new Services(assets, new Texts(), preferences,
                profile, databases, new Shaders(), gamingKit, downloader,
                new Chat(gamingKit, new Texts(), assets, mock(SpriteBatch.class), mock(IPTGame.class),
                        mock(Recorder.class), mock(IUploader.class), mock(SoundsPlayer.class), broadcaster),
                new Socials(preferences, broadcaster), new GCMSender(), new Confirm(mock(SpriteBatch.class), mock(PTGame.class), assets, broadcaster),
                new Notification(mock(SpriteBatch.class), assets, mock(PTGame.class), broadcaster), mock(Recorder.class), mock(IUploader.class),
                mock(SoundsPlayer.class), mock(VersionControl.class), broadcaster, mock(ITutorials.class));
    }

}
