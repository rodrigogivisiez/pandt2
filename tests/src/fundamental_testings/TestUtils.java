package fundamental_testings;

import com.potatoandtomato.common.utils.Strings;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by SiongLeng on 18/3/2016.
 */
public class TestUtils {

    @Test
    public void testLexicalCompareString(){
        Assert.assertEquals(true, Strings.isLargerLexically("b", "a"));
        Assert.assertEquals(false, Strings.isLargerLexically("a", "b"));
        Assert.assertEquals(false, Strings.isLargerLexically("A", "c"));
        Assert.assertEquals(true, Strings.isLargerLexically("ab", "c"));
    }


}
