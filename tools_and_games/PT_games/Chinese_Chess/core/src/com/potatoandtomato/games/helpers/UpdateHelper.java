package com.potatoandtomato.games.helpers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class UpdateHelper {

    public static String toJson(int code, String msg){
        System.out.println("SEND MSG CODE: "+ code);

        JSONObject jsonObject = new JSONObject();
        try {
            if(msg.equals("")) msg = "-";
            jsonObject.put("msg", msg);
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

}
