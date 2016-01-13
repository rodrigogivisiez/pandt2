package com.mygdx.potatoandtomato.models;

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

    public ChatMessage() {
    }

    public ChatMessage(String _message, FromType _fromType, String _senderId) {
        this._message = _message;
        this._fromType = _fromType;
        this._senderId = _senderId;
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
}
