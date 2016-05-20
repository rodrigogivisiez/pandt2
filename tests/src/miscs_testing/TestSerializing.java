package miscs_testing;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.models.Room;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by SiongLeng on 20/5/2016.
 */
public class TestSerializing extends TestAbstract {

    @Test
    public void testTeamSerializer(){
        Room room = MockModel.mockRoom("1");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(room);

            Room afterRoom = objectMapper.readValue(json, Room.class);

            Assert.assertEquals(room.getId(), afterRoom.getId());
            Assert.assertEquals(room.getRoomUsersMap().size(), afterRoom.getRoomUsersMap().size());
            Assert.assertEquals(room.getTeams().size(), afterRoom.getTeams().size());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
