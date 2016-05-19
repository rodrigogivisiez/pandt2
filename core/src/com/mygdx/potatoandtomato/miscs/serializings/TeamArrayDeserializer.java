package com.mygdx.potatoandtomato.miscs.serializings;

import com.mygdx.potatoandtomato.models.RoomUser;
import com.potatoandtomato.common.models.Team;
import com.shaded.fasterxml.jackson.core.JsonParser;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.core.ObjectCodec;
import com.shaded.fasterxml.jackson.databind.DeserializationContext;
import com.shaded.fasterxml.jackson.databind.JsonDeserializer;
import com.shaded.fasterxml.jackson.databind.JsonNode;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by SiongLeng on 19/5/2016.
 */
public class TeamArrayDeserializer extends JsonDeserializer<ArrayList<Team>> {
    @Override
    public ArrayList<Team> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ArrayList<Team> result = new ArrayList();

        ObjectCodec oc = jsonParser.getCodec();

        JsonNode node = oc.readTree(jsonParser);

        ObjectMapper mapper = new ObjectMapper();

        Iterator<Map.Entry<String, JsonNode>> nodeIterator = node.fields();

        while (nodeIterator.hasNext()) {

            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();

            for(int i = result.size(); i < Integer.valueOf(entry.getKey()); i++){
                result.add(new Team());
            }

            result.add(mapper.treeToValue(entry.getValue(), Team.class));
        }

        return result;
    }
}
