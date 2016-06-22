package abstracts;

import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class MockGamingKit extends GamingKit {

    @Override
    public void addListener(String classTag, Object listener) {
        super.addListener(classTag, listener);
    }

    @Override
    public void connect(Profile user) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void createAndJoinRoom() {
        onRoomJoined("123");
    }

    @Override
    public void sendRoomMessage(ChatMessage msg) {
        this.onRoomMessageReceived(msg, MockModel.mockProfile().getUserId());
    }

    @Override
    public void joinRoom(String roomId) {
        onRoomJoined("123");
    }

    @Override
    public void leaveRoom() {

    }

    @Override
    public void getRoomInfo(String roomId) {

    }

    @Override
    public void updateRoomMates(int updateRoomMatesCode, String msg) {
        for(UpdateRoomMatesListener listener : getUpdateRoomMatesListeners().values()){
            listener.onUpdateRoomMatesReceived(updateRoomMatesCode, msg, MockModel.mockProfile().getUserId());
        }
    }

    @Override
    public void privateUpdateRoomMates(String toUserId, int updateRoomMatesCode, String msg) {

    }

    @Override
    public void updateRoomMates(byte identifier, byte[] bytes) {

    }

    @Override
    public void privateUpdateRoomMates(String toUserId, byte identifier, byte[] bytes) {

    }

    @Override
    public void lockProperty(String key, String value) {

    }

    @Override
    public void recoverConnection() {

    }

    @Override
    public void dispose() {

    }
}
