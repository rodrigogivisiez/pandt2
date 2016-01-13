package scene_testings;

/**
 * Created by SiongLeng on 1/12/2015.
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBoot.class,
        TestGameList.class,
        TestCreateGame.class,
        TestPrerequisite.class,
        TestRoom.class,
        TestChat.class,
        TestSettings.class,
        TestInvite.class,
        TestConfirm.class,
        TestGameSandBox.class,
        TestInputName.class
})
public class SceneSuites {
}
