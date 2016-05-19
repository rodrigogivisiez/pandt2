package fundamental_testings;

/**
 * Created by SiongLeng on 1/12/2015.
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBroadCaster.class,
        TestPositions.class,
        TestDownloader.class,
        TestGamingKit.class,
        TestGameLoader.class,
        TestUtils.class
})
public class FundamentalSuites {
}

