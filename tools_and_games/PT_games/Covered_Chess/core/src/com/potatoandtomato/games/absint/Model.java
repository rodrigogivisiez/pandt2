package com.potatoandtomato.games.absint;

import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by SiongLeng on 22/2/2016.
 */
public abstract class Model {

    public String toJson() {
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            return  mapper1.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
