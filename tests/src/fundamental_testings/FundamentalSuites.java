package fundamental_testings;

/**
 * Created by SiongLeng on 1/12/2015.
 */
import connection_testings.TestFireBase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import scene_testings.TestGameList;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBroadCaster.class,
        TestPositions.class,
        TestDownloader.class,
        TestGamingKit.class
})
public class FundamentalSuites {
}

