package abstracts;

import com.potatoandtomato.common.Player;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class MockPlayer {

    public static Player mockPlayer(String playerId, boolean isHost){
        Player player = new Player(playerId, playerId, isHost, true);
        return player;
    }

}
