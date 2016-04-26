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
    private ArrayList<Runnable> runOnSetListener;

    public RoomMsgHandler(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.runOnSetListener = new ArrayList();

        gameCoordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                received(s, s1);
            }
        });
    }

    private void received(final String json, final String userId){
        if(listener == null){
            runOnSetListener.add(new Runnable() {
                @Override
                public void run() {
                    received(json, userId);
                }
            });
        }
        else{
            try {
                JSONObject jsonObject = new JSONObject(json);
                String msg = jsonObject.getString("msg");
                RoomMsgType type = RoomMsgType.valueOf(jsonObject.getString("type"));

                if(type == RoomMsgType.Touched){
                    ObjectMapper objectMapper = new ObjectMapper();
                    listener.onTouched(objectMapper.readValue(msg, TouchedPoint.class), userId);
                }
                else if(type == RoomMsgType.Lose){
                    listener.onLose();
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
                    ObjectMapper objectMapper = new ObjectMapper();
                    GameModel gameModel = null;
                    if(!jsonObj.getString("gameModel").equals("")){
                        gameModel =  objectMapper.readValue(jsonObj.getString("gameModel"), GameModel.class);
                    }

                    listener.onGoToNextStage(jsonObj.getString("id"), StageType.valueOf(jsonObj.getString("stageType")),
                            BonusType.valueOf(jsonObj.getString("bonusType")),
                            jsonObj.getString("extra"), gameModel);
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

    public void sendLose(){
        send("", RoomMsgType.Lose);
    }

    public void sendGotoNextStage(String id, StageType stageType, BonusType bonusType, String extra, GameModel gameModel){
        try {
            JsonObj jsonObj = new JsonObj();
            jsonObj.put("id", id);
            jsonObj.put("bonusType", bonusType.name());
            jsonObj.put("stageType", stageType.name());
            ObjectMapper objectMapper = new ObjectMapper();
            jsonObj.put("gameModel", gameModel == null ? "" : objectMapper.writeValueAsString(gameModel));
            jsonObj.put("extra", extra);
            send(jsonObj.toString(), RoomMsgType.NextStage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendDownloadImageRequest(ArrayList<String> imageIds){
        if(imageIds.size() > 0){
            String msg = Strings.joinArr(imageIds, ",");
            send(msg, RoomMsgType.Download);
        }
    }

    public void setRoomMsgListener(RoomMsgListener listener) {
        this.listener = listener;
        for(Runnable runnable : runOnSetListener){
            runnable.run();
        }
        runOnSetListener.clear();
    }
}
