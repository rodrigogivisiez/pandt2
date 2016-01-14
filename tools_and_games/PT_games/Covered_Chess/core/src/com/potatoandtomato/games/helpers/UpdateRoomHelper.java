package com.potatoandtomato.games.helpers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SiongLeng on 1/1/2016.
 */
public class UpdateRoomHelper {

    public static String convertToJson(int code, String msg){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            if(msg.equals("")) msg = "-";
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
