package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.SpeechActionType;
import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.utils.Pair;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 6/12/2015.
 */
public class Texts {

    ///////////////////////////////////////////////////////////
    //boot scene
    ///////////////////////////////////////////////////////////
    public String build() {return "Build %s";}
    public String debug() {return "DEBUG";}

    public String socialLoginTitle() { return "Login with Facebook";};
    public String socialLoginTomato() { return "Tomato will help you find more friends to play with you.";}
    public String socialLoginPotato() { return "Potato promises not to post anything on your behalf.";}
    public String socialLoginProcessing() { return "Facebook login processing...";}
    public String socialLoginFailed() { return "Login with Facebook failed... \nTry again?";}

    public String creatingUser() { return "Creating new user profile..."; }
    public String failedRetrieveProfile() { return "Failed to retrieve data from server, please check your connection and try again."; }
    public String ptIsDown() { return "P&T server is down for maintenance at the moment, please come back later."; }
    public String loginProcessing() { return "Logging in..."; }

    public String confirmAppsCrashed() {return "Opps, looks like P&T has crashed on the last time you played, a error log has been received by Tomato and will be fixed asap.";}
    public String confirmQuit() { return "Are you sure you want to quit Potato and Tomato?";}


    //////////////////////////////////////////////////////////////////////////
    //Input name scene
    //////////////////////////////////////////////////////////////////////////
    public String askForNameTitle() { return "Please input your in-game display name"; }

    public String confirmDuplicateNameError() { return "This name has already been taken, please use another name.";}
    public String confirmEmptyNameError() { return "Game name cannot be empty.";}
    public String confirmNameLengthError() { return "Game name cannot be more than %s characters.";}

    ///////////////////////////////////////////////////////////////////////////
    //Settings scene
    //////////////////////////////////////////////////////////////////////////
    public String settingsSceneTitle() { return "Settings"; }

    public String displayNameTitle() { return "Display Name"; }
    public String soundsTitle() { return "Sounds"; }
    public String facebookTitle() { return "Facebook"; }

    public String confirmLogoutFacebook() { return "Are you sure you want to logout from facebook? (Potato and Tomato will need to restart after this action.)"; }
    public String confirmLoginFacebook() { return "Are you sure you want to login into facebook? (Potato and Tomato will need to restart after this action.)"; }
    public String confirmFacebookRequestFailed() { return "Facebook request failed..."; }

    ///////////////////////////////////////////////////////////////////////
    //Create game scene
    ///////////////////////////////////////////////////////////////////////
    public String createGameSceneTitle() { return "Create Game"; }

    public String pickAGame() { return "Pick A Game"; }
    public String btnTextCreate() { return "Create"; }

    ///////////////////////////////////////////////////////////////////////
    //Game List scene
    //////////////////////////////////////////////////////////////////////
    public String gamesListSceneTitle() { return "Games List"; }

    public String gameHeader() { return "GAME"; }
    public String playersHeader() { return "PLAYERS"; }
    public String hostHeader() { return "HOST"; }

    public String btnTextNewGame() { return "Create Game"; }
    public String btnTextJoinGame() { return "Join\nGame"; }
    public String btnTextContinueLastGame() { return "Continue"; }

    public String confirmNotContinueGame() { return "You still have an unfinished game! Are you sure you want to abandon it?" ;}

    /////////////////////////////////////////////////////////////////////////
    //Prerequisite scene
    ////////////////////////////////////////////////////////////////////////
    public String joiningRoom() { return "Joining room..."; }
    public String lookingForServer() { return "Looking for server..."; }
    public String locatingRoom() { return "Locating room server..."; }

    public String joinRoomFailed() { return "Failed to join room, please try again later."; }
    public String cannotContinue() { return "Failed to continue game, the game might be already finished.";}
    public String roomIsFull() { return "Room is full, please try again later.";}
    public String roomNotAvailable() { return "Game is not available to join.";}

    /////////////////////////////////////////////////////////////////////////////////
    //Room scene
    //////////////////////////////////////////////////////////////////////////////////
    public String roomSceneTitle() { return "Game Room"; }

    public String btnTextWaitingHost() { return "Waiting For Host";}
    public String btnTextStartGame() { return "Start\nGame"; }

    public String teamTitle() { return "Team %s"; }
    public String slotOpen() { return "Open"; }

    public String confirmLeaveRoom() { return "Are you sure you want to leave this room?"; }
    public String confirmHostLeaveRoom() { return "Are you sure you want to leave this room? (This room will be dismissed)"; }
    public String confirmKick() { return "Confirm kick %s from this room?";}
    public String confirmYouAreKicked() { return "You are kicked from the room.";}
    public String confirmGameClientOutdated() { return "This game required latest game client to run, please update your P&T game client.";}
    public String confirmGameVersionOutdated() { return "This game has newer version, host need to recreate the room.";}
    public String confirmFailedRetriveGameData() { return "Failed to retrive game data, please check your connection and try again.";}
    public String confirmHostLeft() { return "Room host has left the room, this room will now be dismissed."; }
    public String confirmRoomError() { return "Unexpected error occurred, you will be dismissed from this room now."; }

    public String confirmNotEnoughPlayers() { return "You need at least %s players each team to start the game."; }
    public String confirmStillDownloadingClient() { return "Please wait until all players has downloaded game content."; }
    public String confirmWaitAllUsersReady() { return "Please wait for all users ready before start.";}
    public String confirmWaitTemporaryDisconnectedUsers() { return "Please wait for all users connection stabilize before start.";}
    public String confirmFairTeamNeeded() { return "All teams must have equal amount of players to start.";}

    public String chatMsgUserHasJoinedRoom() { return "%s has joined the room."; }
    public String chatMsgUserHasLeftRoom() { return "%s has left the room."; }
    public String chatMsgUserKicked() { return "%s has been kicked from the room.";}
    public String chatMsgGameStarting() { return "Game will be starting soon after %s coin(s) inserted.";}
    public String chatMsgGameStartStop() { return "Game start process is stopped by %s.";}
    public String chatMsgGameStarted() { return "Game started.";}
    public String chatMsgInvitationAccepted() { return "%s has accepted the game invitation and is joining soon.";}
    public String chatMsgInvitationRejected() { return "%s is too busy for a game at this moment.";}

    public String PUSHRoomUpdateTitle() { return "Room Update";}
    public String PUSHRoomUpdateGameReadyTitle() { return "Game is ready to start";}
    public String PUSHRoomUpdateGameStartingTitle() { return "Game starting!";}
    public String PUSHRoomUpdateContent() { return "Current players %s / %s";}

    ////////////////////////////////////////////////////////////////////////////
    //Invite scene
    /////////////////////////////////////////////////////////////////////////////
    public String inviteSceneTitle() { return "Invite Friends"; }

    public String recentlyPlayTabTitle() { return "Recently Played Together"; }
    public String faebookFriendsTabTitle() { return "Facebook Friends"; }
    public String leaderBoardsTabTitle() { return "Leaderboards"; }

    public String playedXAgo() { return "Played %s together %s";}
    public String gameNameIs() { return "Game name: %s";}
    public String xPoints() {return "%s points";}

    public String facebookCannotGetFriends() { return "You need to login with your facebook first.";}
    public String noLeaderBoard() { return "This game doesn't have leaderboard function enabled.";}

    public String chatMsgxInvitedX() { return "%s has invited %s to join this room.";}
    public String chatMsgxInvitedXAlias() { return "%s has invited %s (previously known as %s) to join this room.";}

    public String PUSHGameInvitationsTitle() { return "P&T Game Invitations"; }
    public String PUSHGameInvitationsContent() { return "You have %s new inviations from your friends.";}
    public String PUSHGameInvitationContent() { return "%s have invited you to play %s together.";}

    /////////////////////////////////////////////////////////////////////
    //Game Sandbox Scene
    ////////////////////////////////////////////////////////////////////////
    public String remainingTime() { return "Remaining Time: ";}
    public String ready() { return "Ready";}
    public String failed() { return "Failed";}

    //show on chat messaging when any player failed to load game
    public String chatMsgLoadGameFailed() { return "Some players have failed to load the game.";}
    public String chatMsgGameEnded() { return "Game ended.";}

    public String notificationAbandon() { return "%s has abandoned the game!";}
    public String notificationAbandonDueToTimeout() { return "%s has abandoned the game because of failed to reconnect in " + Global.ABANDON_TOLERANCE_SECS + " seconds.";}
    public String notificationConnected() { return "%s has connected to the game.";}
    public String notificationDisconnected() { return "%s has disconnected from the game, he/she has " + Global.ABANDON_TOLERANCE_SECS + " seconds to reconnect, please wait.";}

    public String notificationYouAbandon() { return "You have abandoned the game!";}
    public String notificationYouAbandonDueToTimeout() { return "You have abandoned the game because of failed to reconnect in " + Global.ABANDON_TOLERANCE_SECS + " seconds.";}

    public String notificationYouConnected() { return "You have connected to the game.";}
    public String notificationYouDisconnected() { return "You have disconnected from the game, you have " + Global.ABANDON_TOLERANCE_SECS + " seconds to reconnect.";}

    public String notificationYouKicked() { return "You have been kicked from the room, game will now be abandoned.";}
    public String notificationKicked() { return "%s has been kicked from the room.";}

    public String confirmAbandonNoCons(){ return "Are you sure you want to abandon this game? \n\n(No consequence)";}
    public String confirmAbandonLoseStreak(){ return "Are you sure you want to abandon this game? \n\n(You will be penalized and lose your winning streaks by doing so)";}
    public String confirmCannotAbandon(){ return "You are not allowed to abandon at this point.";}

    ///////////////////////////////////////////////////////////////
    //Leaderboard scene
    ////////////////////////////////////////////////////////////////
    public String leaderBoardSceneTitle() { return "Leaderboards"; }

    public String updatingScores() { return "Updating Scores..."; }

    ////////////////////////////////////////////////////////////////////////
    //Shop scene and coins
    /////////////////////////////////////////////////////////////////////////
    public String shopSceneTitle() { return "Shop"; }

    public String purseTitle() { return "Mum's purse";}
    public String growthRateForShop() { return "Grow Coin In";}
    public String maxPurseTextForShop() { return "Full";}
    public String watchAdsDescription() { return "30secs video ads"; }

    public String btnTextRetrieveCoins() { return "Retrieve Now"; }
    public String btnTextWatchAds() { return "Watch Now"; }
    public String btnTextBuyNow() { return "Buy Now"; }

    public String xCoin() {return "Coin x %s";}

    public String confirmPurchaseFailed() { return "Failed to purchase coins..";}

    //coin machines tab title
    public String freeCoinsTabTitle() { return "Mum's purse";}
    public String coinsInsertedTabTitle() { return "Coins Inserted";}
    public String buyCoinsTabTitle() { return "Buy Coins"; }

    ///////////////////////////////////////////////////////////////////////
    //Connection related
    /////////////////////////////////////////////////////////////////////////
    public String confirmLostConnection() {return "Lost connection with server, trying to reconnect, \n%s secs remaining";}
    public String confirmConnectionRecovered() {return "Connection established, trying to resume game session, \n%s secs remaining";}
    public String confirmNoConnection() { return "You have been disconnected from the server, please check your connection."; }

    public String btnTextClickToDisconnect(){ return "Click to Disconnect";}

    ///////////////////////////////////////////////////////////
    //Chats
    /////////////////////////////////////////////////////////////
    //audio recorder
    public String slideUpCancel() { return "Slide up to cancel";}

    //chat templates
    public String settingsTitle() { return "Settings"; }
    public String favouritesTitle() { return "Favourites"; }

    /////////////////////////////////////////////////////////////
    //Game info
    //////////////////////////////////////////////////////////
    public String details() { return "Details"; }
    public String version() { return "Version %s";}
    public String xMb() { return "%sMb";}
    public String xPlayers() { return "From %s to %s players";}
    public String description() { return "Description"; }

    ///////////////////////////////////////////////////////////
    //Shared
    //////////////////////////////////////////////////////////
    public String loading() { return "Loading..."; }
    public String confirm() { return "Confirm"; }
    public String invite() { return "Invite"; }
    public String quit(){ return "Quit";}
    public String retry() { return "Retry"; }
    public String cancel() { return "Cancel";}
    public String login() { return "Login"; }
    public String logout() { return "Logout"; }
    public String insert() { return "Insert";}
    public String coin() { return "Coin";}
    public String save() { return "Save"; }

    public String noRecords() { return "No records found.";}
    public String requestFailed() { return "Request failed.";}
    public String generalError() { return "Unexpected error occurred, please try again.";}
    public String workingDoNotClose() { return "Potato and Tomato are working now, please do not close apps now...";}

    ///////////////////////////////////////////////////////////
    //Mascots speeches in coin machine
    //////////////////////////////////////////////////////////
    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutStartGame(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("We need %expectingCoin% coin(s) to start", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Insert coin to the slot below", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }


    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutPurse(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("free coin free coin free coin", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("i want more!!", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }


    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutPurchaseCoins(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("hmmmmmmmmm", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("hahahahahhaha", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }

    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutEnoughCoin(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("Lets rollll", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Gogogogo", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }

    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutNoMoreCoins(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            //potatoSpeechActions.add(new SpeechAction("Lets rollll", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("No coin no coin no coin!!", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }


    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutLostStreaks(int streakCount){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 0);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("Revive your " + streakCount + " streaks?", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("More streak more scores", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }



}
