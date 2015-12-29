package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Texts {

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
    public String pickAGame() { return "Pick a game!"; }

    public String createGameTitle() { return "Create Game"; }
    public String roomTitle() { return "Game Room"; }
    public String settingsTitle() { return "Settings"; }
    public String inviteTitle() { return "Invite Friends"; }

    public String details() { return "Details"; }
    public String screenShots() { return "Screen Shots"; }
    public String description() { return "Description"; }
    public String create() { return "Create"; }
    public String startGame() { return "Start Game"; }
    public String invite() { return "Invite"; }
    public String displayName() { return "Display Name"; }
    public String facebook() { return "Facebook"; }
    public String login() { return "Login"; }
    public String logout() { return "Logout"; }
    public String confirmLogoutFacebook() { return "Are you sure you want to logout from facebook? (Potato and Tomato will need to restart after this action.)"; }
    public String confirmLoginFacebook() { return "Are you sure you want to login into facebook? (Potato and Tomato will need to restart after this action.)"; }
    public String facebookLoginFailed() { return "Facebook request failed..."; }


    public String yes() { return "Yes"; }
    public String no() { return "No"; }
    public String retry() { return "Retry"; }
    public String team() { return "Team"; }
    public String open() { return "Open"; }
    public String send() { return "Send"; }

    public String loading() { return "Loading..."; }


    public String checkGameVersion() { return "Checking game version..."; }
    public String downloadingGame() { return "Downloading game content..."; }
    public String creatingRoom() { return "Hosting game now..."; }
    public String joiningRoom() { return "Joining room..."; }
    public String lookingForServer() { return "Looking for server..."; }
    public String locatingRoom() { return "Locating room server..."; }
    public String gameClientFailed() { return "Failed to retrieve game client, please try again later."; }
    public String joinRoomFailed() { return "Failed to join room, please try again later."; }
    public String hostLeft() { return "Room host has left the room, this room will now be dismissed."; }
    public String roomError() { return "Unexpected error occurred, you will be dismissed from this room now."; }

    public String notEnoughPlayers() { return "You need at least %s players each team to start the game."; }
    public String stillDownloadingClient() { return "Please wait until all players has downloaded game content."; }

    public String confirmLeaveRoom() { return "Are you sure you want to leave this room?"; }
    public String confirmHostLeaveRoom() { return "Are you sure you want to leave this room? (This room will be dismissed)"; }

    public String userHasJoinedRoom() { return "%s has joined the room."; }
    public String userHasLeftRoom() { return "%s has left the room."; }

    public String recentlyPlay() { return "Recently Played Together"; }
    public String faebookFriends() { return "Facebook Friends"; }
    public String facebookCannotGetFriends() { return "You need to login with your facebook first.";}
    public String noRecords() { return "No records found.";}
    public String requestFailed() { return "Request failed.";}

    public String xInvitedX() { return "%s has invited %s to join this room.";}
    public String xInvitedXFailed() { return "%s has invited %s to join this room, but request failed.";}

    public String gameStartingIn() { return "Game starting in %s...";}
    public String gameStartStop() { return "Game start process is stopped by %s...";}

    public String confirmQuit() { return "Are you sure you want to quit Potato and Tomato?";}

    public String loadGameFailed() { return "The following users failed to load the game: %s";}


    //push notifications part
    public String PUSHGameInvitationsTitle() { return "P&T Game Invitations"; }
    public String PUSHGameInvitationsContent() { return "You have %s new inviations from your friends.";}
    public String PUSHGameInvitationContent() { return "%s have invited you to play %s together.";}
    public String PUSHRoomUpdateTitle() { return "Room Update";}
    public String PUSHRoomUpdateContent() { return "Current players %s / %s";}
    public String PUSHRoomUpdateGameReadyTitle() { return "Game is ready to start";}
    public String PUSHRoomUpdateGameStartedTitle() { return "Game started!";}
}
