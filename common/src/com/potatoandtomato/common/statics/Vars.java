package com.potatoandtomato.common.statics;

import com.shaded.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by SiongLeng on 23/7/2016.
 */
public class Vars {

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null) objectMapper = new ObjectMapper();
        return objectMapper;
    }
}
