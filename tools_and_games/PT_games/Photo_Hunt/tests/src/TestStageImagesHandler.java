import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.screens.main.StageImagesLogic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by SiongLeng on 18/4/2016.
 */
public class TestStageImagesHandler extends TestAbstract {

    @Test
    public void testInverted(){
        BonusType bonusType = BonusType.INVERTED;
        StageImagesLogic stageImagesLogic = getStageImagesHandler();

        Table imageTwoTable = new Table();
        imageTwoTable.setWidth(400);
        imageTwoTable.setHeight(200);
        stageImagesLogic.init(new Table(), imageTwoTable, new Table() ,imageTwoTable);

        stageImagesLogic.beforeStartStage(StageType.Bonus, bonusType, "");

        Vector2 result = stageImagesLogic.processTouch(10, 20, false);
        Assert.assertEquals(10, result.x, 0);
        Assert.assertEquals(20, result.y, 0);

        result = stageImagesLogic.processTouch(10, 20, true);
        Assert.assertEquals(390, result.x, 0);
        Assert.assertEquals(180, result.y, 0);
    }

    @Test
    public void testLooping(){
        BonusType bonusType = BonusType.LOOPING;
        StageImagesLogic stageImagesLogic = getStageImagesHandler();

        Table imageTwoTable = new Table();
        imageTwoTable.setWidth(400);
        imageTwoTable.setHeight(200);
        stageImagesLogic.init(new Table(), imageTwoTable, new Table() ,imageTwoTable);

        stageImagesLogic.beforeStartStage(StageType.Bonus, bonusType, "");

        Vector2 result = stageImagesLogic.processTouch(600, 20, true);
        Assert.assertEquals(200, result.x, 0);
        Assert.assertEquals(20, result.y, 0);
    }

    private StageImagesLogic getStageImagesHandler(){
        return new StageImagesLogic(_game.getCoordinator(), Mockings.mockServices(_game.getCoordinator()), new GameModel());
    }


}
