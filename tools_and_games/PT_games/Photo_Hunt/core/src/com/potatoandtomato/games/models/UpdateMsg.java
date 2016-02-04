package com.potatoandtomato.games.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class UpdateMsg {

    private int updateCode;
    private String msg;

    public UpdateMsg(int updateCode, String msg) {
        this.updateCode = updateCode;
        this.msg = msg;
    }

    public UpdateMsg(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            this.updateCode = jsonObject.getInt("code");
            this.msg = jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getUpdateCode() {
        return updateCode;
    }

    public String getMsg() {
        return msg;
    }

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", getUpdateCode());
            jsonObject.put("msg", getMsg());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


}
