package com.potatoandtomato.games.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SiongLeng on 4/2/2016.
 */
public class TouchedData {

    private float x, y;
    private String imageId;

    public TouchedData(float x, float y, String imageId) {
        this.x = x;
        this.y = y;
        this.imageId = imageId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getImageId() {
        return imageId;
    }

    public TouchedData(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            x = (float) jsonObject.getDouble("x");
            y = (float) jsonObject.getDouble("y");
            imageId = jsonObject.getString("imageId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("x", x);
            jsonObject.put("y", y);
            jsonObject.put("imageId", imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
