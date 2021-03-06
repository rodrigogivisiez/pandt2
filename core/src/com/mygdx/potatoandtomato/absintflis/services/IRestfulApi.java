package com.mygdx.potatoandtomato.absintflis.services;

import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 13/5/2016.
 */
public interface IRestfulApi {

    void createNewUser(RestfulApiListener<UserIdSecretModel> listener);

    void createNewUserWithFacebookProfile(FacebookProfile facebookProfile, RestfulApiListener<UserIdSecretModel> listener);

    void loginUser(String userId, String secret, FacebookProfile facebookProfile, RestfulApiListener<String> listener);

    void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, Room room,
                      Profile myProfile, RestfulApiListener<String> listener);

    void getRetrievableCoinsData(Profile myProfile, RestfulApiListener<RetrievableCoinsData> listener);

    void retrieveCoins(Profile myProfile, RestfulApiListener<RetrievableCoinsData> listener);

    void purchasedProducts(String productId, String productToken, String orderId, Profile myProfile, int phase, RestfulApiListener<String> listener);

    void reviveStreak(String teamUserIdsString, ArrayList<CoinsMeta> coinsMetas, String gameAbbr,
                                    String roomId, String roundCounter, RestfulApiListener<String> listener);


    void useCoins(ArrayList<CoinsMeta> coinsMetas, String transactionId, int expectingCoins,
                      String purpose, RestfulApiListener<String> listener);

    void watchedAds(String userId, RestfulApiListener<String> listener);

}
