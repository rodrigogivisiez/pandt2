package com.mygdx.potatoandtomato.helpers.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class JsonObj {

    private JSONObject _jsonObject;

    public JsonObj() {
        _jsonObject = new JSONObject();
    }

    public void put(String key, Object value){
        try {
            _jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Object get(String key){
        try {
            Object value = _jsonObject.get(key);
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(String key){
        try {
            String value = (String) _jsonObject.get(key);
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
