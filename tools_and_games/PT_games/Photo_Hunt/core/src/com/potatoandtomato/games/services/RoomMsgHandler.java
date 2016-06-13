package com.potatoandtomato.games.services;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.InGameUpdateListener;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.games.absintf.RoomMsgListener;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.RoomMsgType;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.SimpleRectangle;
import com.potatoandtomato.games.models.TouchedPoint;
import com.potatoandtomato.games.models.WonStageModel;
import com.shaded.fasterxml.jackson.core.JsonParseException;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.JsonMappingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class RoomMsgHandler {

    private GameCoordinator gameCoordinator;
    private RoomMsgListener listener;

    public RoomMsgHandler(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;

        gameCoordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                received(s, s1);
            }
        });
    }

    private void received(final String json, final String userId){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String msg = jsonObject.getString("msg");
            RoomMsgType type = RoomMsgType.valueOf(jsonObject.getString("type"));

            if(type == RoomMsgType.Touched){
                ObjectMapper objectMapper = new ObjectMapper();
                listener.onTouched(objectMapper.readValue(msg, TouchedPoint.class), userId);
            }
            else if(type == RoomMsgType.Lose){
                ObjectMapper objectMapper = new ObjectMapper();
                GameModel loseGameModel = objectMapper.readValue(msg, GameModel.class);
                listener.onLose(loseGameModel);
            }
            else if(type == RoomMsgType.Won){
                ObjectMapper objectMapper = new ObjectMapper();
                WonStageModel wonStageModel = objectMapper.readValue(msg, WonStageModel.class);
                listener.onWon(wonStageModel);
            }
            else if(type == RoomMsgType.Download){
                ArrayList<String> ids = Strings.split(msg, ",");
                listener.onDownloadImageRequest(ids);
            }
            else if(type == RoomMsgType.NextStage){
                JsonObj jsonObj = new JsonObj(msg);

                listener.onGoToNextStage(jsonObj.getString("id"), jsonObj.getInt("stageNumber"),
                        StageType.valueOf(jsonObj.getString("stageType")),
                        BonusType.valueOf(jsonObj.getString("bonusType")),
                        jsonObj.getString("extra"), jsonObj.getInt("currentScores"));
            }
            else if(type == RoomMsgType.StartPlaying){
                JsonObj jsonObj = new JsonObj(msg);

                listener.onStartPlaying(jsonObj.getString("id"), jsonObj.getInt("stageNumber"),
                        StageType.valueOf(jsonObj.getString("stageType")),
                        BonusType.valueOf(jsonObj.getString("bonusType")),
                        jsonObj.getString("extra"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String msg, RoomMsgType type){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", msg);
            jsonObject.put("type", type.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gameCoordinator.sendRoomUpdate(jsonObject.toString());
    }

    private void sendPrivate(String msg, String toUserId, RoomMsgType type){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", msg);
            jsonObject.put("type", type.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gameCoordinator.sendPrivateRoomUpdate(toUserId, jsonObject.toString());
    }

    public void sendTouched(float x, float y, int remaninigMiliSecs, int hintLeft, SimpleRectangle correctRect){
        TouchedPoint touchedPoint = new TouchedPoint(x, y,  remaninigMiliSecs, hintLeft, correctRect);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            send(objectMapper.writeValueAsString(touchedPoint), RoomMsgType.Touched);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendWon(WonStageModel wonStageModel){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            send(objectMapper.writeValueAsString(wonStageModel), RoomMsgType.Won);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendLose(GameModel loseGameModel){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            send(objectMapper.writeValueAsString(loseGameModel), RoomMsgType.Lose);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendGotoNextStage(String id, int stageNumber, StageType stageType, BonusType bonusType, String extra, int currentScores){
        JsonObj jsonObj = new JsonObj();
        jsonObj.put("id", id);
        jsonObj.put("bonusType", bonusType.name());
        jsonObj.put("stageType", stageType.name());
        jsonObj.put("stageNumber", stageNumber);
        jsonObj.put("extra", extra);
        jsonObj.put("currentScores", currentScores);
        send(jsonObj.toString(), RoomMsgType.NextStage);
    }

    public void sendDownloadImageRequest(ArrayList<String> imageIds){
        if(imageIds.size() > 0){
            String msg = Strings.joinArr(imageIds, ",");
            send(msg, RoomMsgType.Download);
        }
    }

    public void sendPrivateDownloadImageRequest(String toUserId, ArrayList<String> imageIds){
        if(imageIds.size() > 0){
            String msg = Strings.joinArr(imageIds, ",");
            sendPrivate(msg, toUserId, RoomMsgType.Download);
        }
    }

    public void sendStartPlaying(String id, int stageNumber, StageType stageType, BonusType bonusType, String extra){
        JsonObj jsonObj = new JsonObj();
        jsonObj.put("id", id);
        jsonObj.put("bonusType", bonusType.name());
        jsonObj.put("stageType", stageType.name());
        jsonObj.put("stageNumber", stageNumber);
        jsonObj.put("extra", extra);
        send(jsonObj.toString(), RoomMsgType.StartPlaying);
    }


    public void setRoomMsgListener(RoomMsgListener listener) {
        this.listener = listener;
    }
}
