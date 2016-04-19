package com.potatoandtomato.games.models;

import com.badlogic.gdx.math.MathUtils;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 19/4/2016.
 */
public class LightTimingModel {

    private ArrayList<TimePeriodModel> timePeriodModels;

    public LightTimingModel() {
        timePeriodModels = new ArrayList();
    }

    public ArrayList<TimePeriodModel> getTimePeriodModels() {
        return timePeriodModels;
    }

    public void setTimePeriodModels(ArrayList<TimePeriodModel> timePeriodModels) {
        this.timePeriodModels = timePeriodModels;
    }

    public void randomize(){
        int count = MathUtils.random(5, 7);
        for(int i =0; i < count; i++){
            float start = MathUtils.random(i * (100 / count), (i + 1) * (100 / count));
            float end = Math.min(MathUtils.random(start + 10, start + 15), 100);
            timePeriodModels.add(new TimePeriodModel(start, end));
        }
    }

    public String toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
