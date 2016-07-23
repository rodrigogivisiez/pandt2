package fundamental_testings;

import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.statics.Vars;
import com.potatoandtomato.common.utils.SafeDouble;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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

    @Test
    public void testSafeDouble(){

        try {
            ObjectMapper objectMapper = Vars.getObjectMapper();
            SafeDouble safeDouble = new SafeDouble(3.0);
            Assert.assertEquals(3.0, safeDouble.getValue(), 0);
            String res = objectMapper.writeValueAsString(safeDouble);
            Assert.assertEquals("3.0", res);

            safeDouble.setValue(999.0);
            Assert.assertEquals(999.0, safeDouble.getValue(), 0);

            safeDouble.setValue(999.1);
            Assert.assertEquals(999.1, safeDouble.getValue(), 0);

            ScoreDetails scoreDetails = new ScoreDetails(1, "test", true, true);
            String result = objectMapper.writeValueAsString(scoreDetails);
            ScoreDetails after = objectMapper.readValue(result, ScoreDetails.class);

            Assert.assertEquals(scoreDetails.getValue(), after.getValue(), 0);
            Assert.assertEquals(scoreDetails.getReason(), after.getReason());
            Assert.assertEquals(scoreDetails.isCanAddStreak(), after.isCanAddStreak());
            Assert.assertEquals(scoreDetails.isAddOrMultiply(), after.isAddOrMultiply());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
