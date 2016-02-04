package cases;

import abstracts.TestAbstract;
import com.badlogic.gdx.math.Rectangle;
import com.potatoandtomato.games.helpers.ImageTouchVerifier;
import com.potatoandtomato.games.models.CorrectArea;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by SiongLeng on 1/2/2016.
 */
public class TestImageTouchVerifier extends TestAbstract {

    private String json = "{\\\"1\\\":\\\"172,242-227,279\\\",\\\"2\\\":\\\"8,246-45,293\\\",\\\"3\\\":\\\"270,82-368,154\\\",\\\"4\\\":\\\"500,149-553,191\\\",\\\"5\\\":\\\"451,0-519,85\\\",\\\"width\\\":600,\\\"height\\\":400}";

    @Test
    public void testCorrect(){
        ImageTouchVerifier imageTouchVerifier = new ImageTouchVerifier(json, 600, 400);
        CorrectArea correctArea = imageTouchVerifier.getConvertedTouchedCorrectArea(273, 83);
        Rectangle expectedResult = new Rectangle(270, 82, 368 - 270, 154 - 82);
        Assert.assertEquals(true, correctArea.toRectangle().equals(expectedResult));

    }

    @Test
    public void testCorrectAdvanced(){
        ImageTouchVerifier imageTouchVerifier = new ImageTouchVerifier(json, 400, 300);
        CorrectArea correctArea = imageTouchVerifier.getConvertedTouchedCorrectArea(301, 1);
        Rectangle expectedResult = new Rectangle(300, 0, 346 - 300, 63 - 0);
        Assert.assertEquals(true, correctArea.toRectangle().equals(expectedResult));
    }

    @Test
    public void testIncorrect(){
        ImageTouchVerifier imageTouchVerifier = new ImageTouchVerifier(json, 320, 240);
        CorrectArea correctArea = imageTouchVerifier.getConvertedTouchedCorrectArea(0, 0);
        Assert.assertEquals(null, correctArea);
    }

}
