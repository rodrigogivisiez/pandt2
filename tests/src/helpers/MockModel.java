package helpers;

import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.RoomUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class MockModel {

    public static Game mockGame(){
        Game g = new Game();
        g.setVersion("1");
        g.setAbbr("covered_chess");
        g.setGameUrl("http://www.potato-and-tomato.com/covered_chess/core.jar");
        g.setAssetUrl("http://www.potato-and-tomato.com/covered_chess/assets.zip");
        g.setMaxPlayers("2");
        g.setMinPlayers("2");
        g.setTeamMaxPlayers("1");
        g.setTeamMinPlayers("1");
        return g;
    }

    public static Profile mockProfile(String userId){
        Profile p = new Profile();
        p.setMascotEnum(MascotEnum.TOMATO);
        p.setFacebookUserId("fb123");
        p.setUserId(userId);
        return p;
    }

    public static Profile mockProfile(){
       return mockProfile("123");
    }

    public static Room mockRoom(String id){
        Room r = new Room();
        r.setGame(mockGame());
        r.setHost(mockProfile());
        r.setOpen(false);
        r.setPlaying(true);
        r.setRoomId("12");
        if(id != null) r.setId(id);
        r.setRoundCounter(2);
        HashMap<String, RoomUser> roomUsers = new HashMap<>();
        RoomUser user1 = new RoomUser();
        RoomUser user2 = new RoomUser();
        user1.setProfile(mockProfile());
        user1.setSlotIndex(0);
        user2.setProfile(mockProfile("another"));
        user2.setSlotIndex(99);
        roomUsers.put(user1.getProfile().getUserId(), user1);
        roomUsers.put(user2.getProfile().getUserId(), user2);
        r.setRoomUsers(roomUsers);
        return r;
    }




}
