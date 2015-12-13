package abstracts;

import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Game;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class MockDB implements IDatabase {
    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        listener.onCallback(1, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void loginAnonymous(DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId("12345");
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setFacebookUserId(facebookUserId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void updateProfile(Profile profile) {

    }

    @Override
    public void createUserByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getAllGames(DatabaseListener<ArrayList<Game>> listener) {
        ArrayList<Game> gameList = new ArrayList<>();
        Game game = new Game();
        game.setVersion("1");
        game.setName("Test");
        game.setAssetUrl("");
        game.setDescription("d");
        game.setMaxPlayers("2");
        game.setMinPlayers("2");
        game.setAbbr("abb");
        game.setScreenShots(null);
        game.setGameUrl("");
        game.setIconUrl("http://www.potato-and-tomato.com/covered_chess/icon.png");
        gameList.add(game);
        listener.onCallback(gameList, DatabaseListener.Status.SUCCESS);
    }
}
