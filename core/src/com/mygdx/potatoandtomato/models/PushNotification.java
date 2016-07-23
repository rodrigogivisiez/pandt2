package com.mygdx.potatoandtomato.models;

import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by SiongLeng on 23/12/2015.
 */
public class PushNotification {

    private int id;
    private boolean sticky;
    private String message;
    private String extras;
    private String title;
    private boolean silentNotification;
    private boolean silentIfInGame;

    public PushNotification() {
    }

    public PushNotification(String jsonInString) {
        ObjectMapper mapper = Vars.getObjectMapper();
        try {
            PushNotification p = mapper.readValue(jsonInString, PushNotification.class);
            this.setExtras(p.getExtras());
            this.setId(p.getId());
            this.setSticky(p.isSticky());
            this.setMessage(p.getMessage());
            this.setTitle(p.getTitle());
            this.setSilentNotification(p.isSilentNotification());
            this.setSilentIfInGame(p.isSilentIfInGame());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSilentNotification() {
        return silentNotification;
    }

    public void setSilentNotification(boolean silentNotification) {
        this.silentNotification = silentNotification;
    }

    public boolean isSilentIfInGame() {
        return silentIfInGame;
    }

    public void setSilentIfInGame(boolean silentIfInGame) {
        this.silentIfInGame = silentIfInGame;
    }

    @JsonIgnore
    @Override
    public String toString() {
        ObjectMapper mapper = Vars.getObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
