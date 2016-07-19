package abstracts;

import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 19/5/2016.
 */
public class MockRestfulApi implements IRestfulApi {
    @Override
    public void createNewUser(RestfulApiListener<UserIdSecretModel> listener) {
        listener.onCallback(new UserIdSecretModel("testUserId", "testUserSecret"), Status.SUCCESS);
    }

    @Override
    public void createNewUserWithFacebookProfile(FacebookProfile facebookProfile, RestfulApiListener<UserIdSecretModel> listener) {

    }

    @Override
    public void loginUser(String userId, String secret, FacebookProfile facebookProfile, RestfulApiListener<String> listener) {
        listener.onCallback("testUserToken", Status.SUCCESS);
    }


    @Override
    public void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, Room room, Profile myProfile, RestfulApiListener<String> listener) {

    }

    @Override
    public void getRetrievableCoinsData(Profile myProfile, RestfulApiListener<RetrievableCoinsData> listener) {

    }

    @Override
    public void retrieveCoins(Profile myProfile, RestfulApiListener<RetrievableCoinsData> listener) {

    }

    @Override
    public void purchasedProducts(String productId, String productToken, String orderId, Profile myProfile, int phase, RestfulApiListener<String> listener) {

    }

    @Override
    public void reviveStreak(String teamUserIdsString, ArrayList<CoinsMeta> coinsMetas, String gameAbbr, String roomId, String roundCounter, RestfulApiListener<String> listener) {

    }

    @Override
    public void useCoins(ArrayList<CoinsMeta> coinsMetas, String transactionId, int expectingCoins, String purpose, RestfulApiListener<String> listener) {

    }
}
