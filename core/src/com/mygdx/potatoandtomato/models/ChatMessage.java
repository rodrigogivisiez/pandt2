package com.mygdx.potatoandtomato.models;

import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class ChatMessage {

    public enum FromType{
        USER, SYSTEM, IMPORTANT, USER_VOICE
    }

    public String _message;
    public FromType _fromType;
    public String _senderId;
    public String _extra;

    public ChatMessage() {
    }

    public ChatMessage(byte[] bytes){
        String json = new String(bytes);
        try {
            ChatMessage o = Vars.getObjectMapper().readValue(json, ChatMessage.class);

            this.setSenderId(o.getSenderId());
            this.setExtra(o.getExtra());
            this.setFromType(o.getFromType());
            this.setMessage(o.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatMessage(String _message, FromType _fromType, String _senderId, String _extra) {
        this._message = _message;
        this._fromType = _fromType;
        this._senderId = _senderId;
        this._extra = _extra;
    }

    public String getExtra() {
        return _extra;
    }

    public void setExtra(String _extra) {
        this._extra = _extra;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String _message) {
        this._message = _message;
    }

    public FromType getFromType() {
        return _fromType;
    }

    public void setFromType(FromType _fromType) {
        this._fromType = _fromType;
    }

    public String getSenderId() {
        return _senderId;
    }

    public void setSenderId(String _senderId) {
        this._senderId = _senderId;
    }

    public byte[] toBytes(){
        ObjectMapper objectMapper = Vars.getObjectMapper();
        try {
            String result = objectMapper.writeValueAsString(this);
            return result.getBytes();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }

}
