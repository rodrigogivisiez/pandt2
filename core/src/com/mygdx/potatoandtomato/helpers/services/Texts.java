package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Texts {

    public Label.LabelStyle getH3Style(Fonts fonts){
        return  new Label.LabelStyle(fonts.getArialBold(18, Color.WHITE, 0, Color.BLACK, 1, Color.GRAY), Color.WHITE);
    }
    public Label.LabelStyle getH4Style(Fonts fonts){
        return  new Label.LabelStyle(fonts.getArialBold(15, Color.WHITE, 0, Color.BLACK, 1, Color.GRAY), Color.WHITE);
    }


    public String mascotQuestion() { return "Are you a Potato or \nTomato?";};
    public String mascotPotato() { return "Potatoes welcome \nYOU!";};
    public String mascotTomato() { return "Tomatoes welcome \nYOU!";};
    public String socialLogin() { return "Login with facebook?\nTomato will help you find more friends to play with you. (Also Potato promises not to post anything on your behalf.)";}
    public String socialLoginProcessing() { return "Logging in...";}
    public String socialLoginFailed() { return "Login with Facebook failed... \nTry again?";}
    public String creatingUser() { return "Creating new user profile..."; }
    public String failedRetrieveProfile() { return "Failed to retrieve user profile, try again?"; }
    public String loginProcessing() { return "Logging in..."; }

    public String gamesList() { return "Games List"; }
    public String game() { return "GAME"; }
    public String players() { return "PLAYERS"; }
    public String host() { return "HOST"; }
    public String newGame() { return "Create"; }
    public String joinGame() { return "Join"; }

    public String createGameTitle() { return "Create Game"; }

    public String details() { return "Details"; }
    public String screenShots() { return "Screen Shots"; }
    public String description() { return "Description"; }
    public String create() { return "Create"; }

    public String yes() { return "Yes"; }
    public String no() { return "No"; }
    public String retry() { return "Retry"; }

    public String loading() { return "Loading..."; }


    public String checkGameVersion() { return "Checking game version..."; }
    public String downloadingGame() { return "Downloading game content..."; }
    public String creatingRoom() { return "Hosting game now..."; }
    public String joiningRoom() { return "Joining room..."; }
    public String gameClientFailed() { return "Failed to retrieve game client, please try again later."; }
    public String joinRoomFailed() { return "Failed to join room, please try again later."; }
}
