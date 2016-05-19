package com.mygdx.potatoandtomato.absintflis.services;

import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.UserIdSecretModel;
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

    void loginUser(String userId, String secret, RestfulApiListener<String> listener);

    void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, Room room,
                      Profile myProfile, RestfulApiListener<String> listener);

}
