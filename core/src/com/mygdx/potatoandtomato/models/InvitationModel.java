package com.mygdx.potatoandtomato.models;

import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 4/7/2016.
 */
public class InvitationModel {

    String invitedUserId;
    ArrayList<String> pendingInvitationRoomIds;


    public InvitationModel() {
        pendingInvitationRoomIds = new ArrayList();
    }

    public InvitationModel(String json){
        pendingInvitationRoomIds = new ArrayList();
        ObjectMapper objectMapper = Vars.getObjectMapper();
        try {
            InvitationModel newInvitationModel = objectMapper.readValue(json, InvitationModel.class);
            this.setInvitedUserId(newInvitationModel.getInvitedUserId());
            this.setPendingInvitationRoomIds(newInvitationModel.getPendingInvitationRoomIds());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(String invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public ArrayList<String> getPendingInvitationRoomIds() {
        return pendingInvitationRoomIds;
    }

    public void setPendingInvitationRoomIds(ArrayList<String> pendingInvitationRoomIds) {
        this.pendingInvitationRoomIds = pendingInvitationRoomIds;
    }

    public String toJson(){
        ObjectMapper objectMapper =Vars.getObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
