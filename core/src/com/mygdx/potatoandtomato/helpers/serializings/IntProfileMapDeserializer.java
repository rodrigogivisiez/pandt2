package com.mygdx.potatoandtomato.helpers.serializings;

import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.shaded.fasterxml.jackson.core.JsonParser;
import com.shaded.fasterxml.jackson.core.ObjectCodec;
import com.shaded.fasterxml.jackson.databind.DeserializationContext;
import com.shaded.fasterxml.jackson.databind.JsonDeserializer;
import com.shaded.fasterxml.jackson.databind.JsonNode;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class IntProfileMapDeserializer extends JsonDeserializer<Map<String, RoomUser>> {

    @Override
    public HashMap<String, RoomUser> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        HashMap<String, RoomUser> result = new HashMap();
        ObjectCodec oc = jsonParser.getCodec();

        JsonNode node = oc.readTree(jsonParser);

        ObjectMapper mapper = new ObjectMapper();

        Iterator<Map.Entry<String, JsonNode>> nodeIterator = node.fields();

        while (nodeIterator.hasNext()) {

            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();
            result.put(entry.getKey(), mapper.treeToValue(entry.getValue(), RoomUser.class));
        }

        return result;
    }
}