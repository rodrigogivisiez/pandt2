package com.potatoandtomato.games.helpers;

import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by SiongLeng on 1/1/2016.
 */
public class UpdateRoomHelper {

    private ObjectMapper objectMapper;

    public UpdateRoomHelper() {
        this.objectMapper = new ObjectMapper();
    }

    public HashMap<String, String> jsonToMap(String json){
        HashMap<String, String> map = new HashMap<>();
        try {
            map = objectMapper.readValue(json, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String convertToJson(int code, String msg){
        HashMap<String, String> map = new HashMap<>();
        map.put("code", String.valueOf(code));
        if(msg.equals("")) msg = "-";
        map.put("msg", msg);

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

}
