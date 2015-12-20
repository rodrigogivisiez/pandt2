package helpers;

import abstracts.MockDB;
import abstracts.MockDownloader;
import abstracts.MockGamingKit;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Profile;
import org.mockito.Mock;

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
        return new Services(new Textures(), new Fonts(), new Texts(), preferences,
                new Profile(), databases, new Shaders(), new MockGamingKit(), downloader, new Chat());
    }

}
