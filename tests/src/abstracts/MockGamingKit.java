package abstracts;

import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.helpers.services.Appwarp;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.mygdx.potatoandtomato.models.Profile;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import helpers.MockModel;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class MockGamingKit extends GamingKit {

    @Override
    public void addListener(Object listener) {
        super.addListener(listener);
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
    public void sendRoomMessage(String msg) {
        new Appwarp().onRoomMessageReceived(msg, MockModel.mockProfile().getUserId());
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
        for(UpdateRoomMatesListener listener : getUpdateRoomMatesListeners().values()){
            listener.onUpdateRoomMatesReceived(updateRoomMatesCode, msg, MockModel.mockProfile().getUserId());
        }
    }
}
