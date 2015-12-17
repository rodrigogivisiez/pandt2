package abstracts;

import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class MockGamingKit extends GamingKit {

    @Override
    public void connect(String username) {

    }

    @Override
    public void createAndJoinRoom() {
        onRoomJoined("123");
    }

    @Override
    public void joinRoom(String roomId) {
        onRoomJoined("123");
    }

    @Override
    public void leaveRoom() {

    }

    @Override
    public void updateRoomMates(int updateRoomMatesCode, String msg) {

    }
}
