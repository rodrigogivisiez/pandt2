package com.potatoandtomato.common.utils;

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

    public JsonObj(String json){
        try {
            _jsonObject= new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public Integer getInt(String key){
        try {
            Integer value = (Integer) _jsonObject.get(key);
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJSONObject(){
        return _jsonObject;
    }

    @Override
    public String toString() {
        return _jsonObject.toString();
    }
}
