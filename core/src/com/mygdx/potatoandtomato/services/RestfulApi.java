package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.UserIdSecretModel;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.Threadings;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.shaded.apache.http.HttpEntity;
import org.shaded.apache.http.HttpResponse;
import org.shaded.apache.http.NameValuePair;
import org.shaded.apache.http.client.HttpClient;
import org.shaded.apache.http.client.entity.UrlEncodedFormEntity;
import org.shaded.apache.http.client.methods.HttpPost;
import org.shaded.apache.http.impl.client.DefaultHttpClient;
import org.shaded.apache.http.message.BasicNameValuePair;
import org.shaded.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SiongLeng on 13/5/2016.
 */
public class RestfulApi implements IRestfulApi {

    @Override
    public void createNewUser(final RestfulApiListener<UserIdSecretModel> listener) {
        callApi("create_user", new RestfulApiListener<String>() {
            @Override
            public void onCallback(String json, Status st) {
                if(st == Status.SUCCESS && json != null && !json.equals("-1")){
                    JsonObj jsonObj =new JsonObj(json);
                    listener.onCallback(new UserIdSecretModel(jsonObj.getString("userId"),jsonObj.getString("secret")), Status.SUCCESS);
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
    }

    @Override
    public void createNewUserWithFacebookProfile(FacebookProfile facebookProfile, final RestfulApiListener<UserIdSecretModel> listener) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("fbUserId", facebookProfile.getUserId()));
        nameValuePairs.add(new BasicNameValuePair("fbUsername", facebookProfile.getName()));
        nameValuePairs.add(new BasicNameValuePair("fbToken", facebookProfile.getToken()));
        callApi("create_user", nameValuePairs, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String json, Status st) {
                if(st == Status.SUCCESS && json != null && !json.equals("-1")){
                    JsonObj jsonObj =new JsonObj(json);
                    listener.onCallback(new UserIdSecretModel(jsonObj.getString("userId"),jsonObj.getString("secret")), Status.SUCCESS);
                }
                else{
                    listener.onCallback(null, Status.FAILED);
                }
            }
        });
    }

    @Override
    public void loginUser(String userId, String secret, final RestfulApiListener<String> listener) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("userId", userId));
        nameValuePairs.add(new BasicNameValuePair("secret", secret));
        callApi("login_user", nameValuePairs, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if(st == Status.FAILED || obj == null){
                    listener.onCallback("", Status.FAILED);
                }
                else{
                    if(obj.equals("USER_NOT_FOUND")){
                        listener.onCallback(obj, Status.FAILED);
                    }
                    else{
                        listener.onCallback(obj, Status.SUCCESS);
                    }
                }
            }
        });
    }

    @Override
    public void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers,
                             Room room, Profile myProfile, RestfulApiListener<String> listener) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            HashMap<String, ArrayList<ScoreDetails>> processedWinners = new HashMap();
            ArrayList<String> processedLosers = new ArrayList();

            for (Map.Entry<Team, ArrayList<ScoreDetails>> entry : winners.entrySet()) {
                Team team = entry.getKey();
                ArrayList<ScoreDetails> scoreDetails = entry.getValue();

                processedWinners.put(team.getPlayersIdsString(), scoreDetails);
            }

            for(Team team : losers){
                processedLosers.add(team.getPlayersIdsString());
            }

            String winnersJson = objectMapper.writeValueAsString(processedWinners);
            String losersJson = objectMapper.writeValueAsString(processedLosers);
            String roomJson = objectMapper.writeValueAsString(room);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("winnersJson", winnersJson));
            nameValuePairs.add(new BasicNameValuePair("losersJson", losersJson));
            nameValuePairs.add(new BasicNameValuePair("roomJson", roomJson));
            nameValuePairs.add(new BasicNameValuePair("userId", myProfile.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("userToken", myProfile.getToken()));

            callApi("save_score", nameValuePairs, listener);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            if(listener != null) listener.onCallback("", Status.FAILED);
        }

    }

    private void callApi(String name, RestfulApiListener listener){
        callApi(name, new ArrayList<NameValuePair>(), listener);
    }

    private void callApi(final String name,  final List<NameValuePair> nameValuePairs, final RestfulApiListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpPost post = new HttpPost( Terms.RESTFUL_URL() + name);
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = null;
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String responseText = EntityUtils.toString(entity);

                    if(responseText.equals("-1")){
                        if(listener != null)  listener.onCallback(responseText, Status.FAILED);
                    }
                    else{
                        if(listener != null)  listener.onCallback(responseText, Status.SUCCESS);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if(listener != null)  listener.onCallback(null, Status.FAILED);
                }
            }
        });

    }

}
