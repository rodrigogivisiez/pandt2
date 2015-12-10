package helpers;

import abstracts.MockDB;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.helpers.assets.*;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class T_Assets {

    public static Assets mockAssets(){
        return mockAssets(new MockDB());
    }

    public static Assets mockAssets(IDatabase databases){
        Preferences preferences = new Preferences("potatoandtomato_test");
        return new Assets(new Textures(), new Fonts(), new Texts(), preferences, new Profile(), databases, new Shaders());
    }


}
