package scene_testings;

/**
 * Created by SiongLeng on 1/12/2015.
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBoot.class,
        TestMascotPick.class,
        TestGameList.class,
        TestCreateGame.class,
        TestPrerequisite.class
})
public class SceneSuites {
}
