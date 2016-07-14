package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.shaded.apache.http.NameValuePair;
import org.shaded.apache.http.message.BasicNameValuePair;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SiongLeng on 13/5/2016.
 */
public class RestfulApi implements IRestfulApi {

    private final String USER_AGENT = "Mozilla/5.0";

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
    public void loginUser(String userId, String secret, FacebookProfile facebookProfile, final RestfulApiListener<String> listener) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("userId", userId));
        nameValuePairs.add(new BasicNameValuePair("secret", secret));
        nameValuePairs.add(new BasicNameValuePair("fbUserId", facebookProfile == null ? "" : facebookProfile.getUserId()));
        nameValuePairs.add(new BasicNameValuePair("fbToken", facebookProfile == null ? "" : facebookProfile.getToken()));
        callApi("login_user", nameValuePairs, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                if(st == Status.FAILED || obj == null){
                    listener.onCallback("", Status.FAILED);
                }
                else{
                    if(obj.equals("USER_NOT_FOUND") || obj.equals("FAIL_CONNECT")){
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

    @Override
    public void getRetrievableCoinsData(Profile myProfile, final RestfulApiListener<RetrievableCoinsData> listener) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("userId", myProfile.getUserId()));
        nameValuePairs.add(new BasicNameValuePair("userToken", myProfile.getToken()));
        nameValuePairs.add(new BasicNameValuePair("retrieveRequest", "0"));
        callApi("retrieve_coins", nameValuePairs, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String json, Status st) {
                if(st == Status.FAILED || Strings.isEmpty(json)){
                    listener.onCallback(null, Status.FAILED);
                }
                else{
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        RetrievableCoinsData data = objectMapper.readValue(json, RetrievableCoinsData.class);
                        listener.onCallback(data, Status.SUCCESS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void retrieveCoins(Profile myProfile, final RestfulApiListener<RetrievableCoinsData> listener) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("userId", myProfile.getUserId()));
        nameValuePairs.add(new BasicNameValuePair("userToken", myProfile.getToken()));
        nameValuePairs.add(new BasicNameValuePair("retrieveRequest", "1"));
        callApi("retrieve_coins", nameValuePairs, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String json, Status st) {
                if(st == Status.FAILED || Strings.isEmpty(json)){
                    listener.onCallback(null, Status.FAILED);
                }
                else{
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        RetrievableCoinsData data = objectMapper.readValue(json, RetrievableCoinsData.class);
                        listener.onCallback(data, Status.SUCCESS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void purchasedProducts(String productId, String productToken, String orderId, Profile myProfile, int phase,
                                  final RestfulApiListener<String> listener) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
        nameValuePairs.add(new BasicNameValuePair("userId", myProfile.getUserId()));
        nameValuePairs.add(new BasicNameValuePair("userToken", myProfile.getToken()));
        nameValuePairs.add(new BasicNameValuePair("productId", productId));
        nameValuePairs.add(new BasicNameValuePair("productToken", productToken));
        nameValuePairs.add(new BasicNameValuePair("orderId", orderId));
        nameValuePairs.add(new BasicNameValuePair("phase", String.valueOf(phase)));
        callApi("purchase", nameValuePairs, listener);
    }

    @Override
    public void reviveStreak(String teamUserIdsString, ArrayList<CoinsMeta> coinsMetas, String gameAbbr, String roomId,
                             String roundCounter, RestfulApiListener<String> listener) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String coinsMetaJson = objectMapper.writeValueAsString(coinsMetas);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("teamUserIdsString", teamUserIdsString));
            nameValuePairs.add(new BasicNameValuePair("coinsMetaJson", coinsMetaJson));
            nameValuePairs.add(new BasicNameValuePair("gameAbbr", gameAbbr));
            nameValuePairs.add(new BasicNameValuePair("roomId", roomId));
            nameValuePairs.add(new BasicNameValuePair("roundCounter", roundCounter));
            callApi("revive_streaks", nameValuePairs, listener);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void useCoins(ArrayList<CoinsMeta> coinsMetas, String transactionId, int expectingCoins, String purpose, RestfulApiListener<String> listener) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String coinsMetaJson = objectMapper.writeValueAsString(coinsMetas);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("transactionId", transactionId));
            nameValuePairs.add(new BasicNameValuePair("coinsMetaJson", coinsMetaJson));
            nameValuePairs.add(new BasicNameValuePair("expectingCoins", String.valueOf(expectingCoins)));
            nameValuePairs.add(new BasicNameValuePair("purpose", purpose));
            callApi("use_coins", nameValuePairs, listener);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void callApi(String name, RestfulApiListener listener){
        callApi(name, new ArrayList<NameValuePair>(), listener);
    }

    private void callApi(final String name,  final List<NameValuePair> nameValuePairs, final RestfulApiListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                try{
                    String url = Terms.RESTFUL_URL() + name;
                    URL obj = new URL(url);

                    HttpURLConnection con;

                    if(url.startsWith("https")){
                        con = (HttpsURLConnection) obj.openConnection();
                    }
                    else{
                        con = (HttpURLConnection) obj.openConnection();
                    }

                    //add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    String urlParameters = "";

                    for(NameValuePair nameValuePair : nameValuePairs){
                        urlParameters += nameValuePair.getName() + "=" + nameValuePair.getValue() + "&";
                    }

                    if(nameValuePairs.size() > 0){
                        urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
                    }


                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
//                    System.out.println("\nSending 'POST' request to URL : " + url);
//                    System.out.println("Post parameters : " + urlParameters);
//                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    //System.out.println(response.toString());

                    String responseText = response.toString();
                    if(responseText.equals("-1")){
                        if(listener != null)  listener.onCallback(responseText, Status.FAILED);
                    }
                    else{
                        if(listener != null)  listener.onCallback(responseText, Status.SUCCESS);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                    if(listener != null)  listener.onCallback(null, Status.FAILED);
                }
            }
        });

    }

}
