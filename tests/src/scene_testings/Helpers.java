package scene_testings;

import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Texts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Helpers {

    public static Assets mockAssets(){
        return new Assets(new Textures(), new Fonts(), new Texts());
    }

}
