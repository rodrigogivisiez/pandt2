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
    ////////////////////////////////////////////////////////////////
    //splash
    //////////////////////////////////////////////////////////////
    public String splashPhrase1() { return "Please wait..."; }
    public String splashPhrase2() { return "The vegetables "; }
    public String splashPhrase3() { return "wo"; }
    public String splashPhrase4() { return "rk"; }
    public String splashPhrase5() { return "to connect you and your friends...."; }

    ///////////////////////////////////////////////////////////
    //boot scene
    ///////////////////////////////////////////////////////////
    public String build() {return "Build %s";}
    public String debug() {return "DEBUG";}

    public String socialLoginTitle() { return "Login with Facebook";};
    public String socialLoginTomato() { return "And nothing will be posted on your wall!";}
    public String socialLoginPotato() { return "We can find more friends to play with you...";}
    public String socialLoginProcessing() { return "Facebook login processing...";}
    public String socialLoginFailed() { return "Login with Facebook has failed... \nTry again?";}

    public String creatingUser() { return "Creating new user profile..."; }
    public String failedRetrieveProfile() { return "Fail to connect. Please check your connection and try again."; }
    public String ptIsDown() { return "P&T server is being maintained at the moment, please come back later."; }
    public String loginProcessing() { return "Logging in..."; }

    public String confirmAppsCrashed() {return "Opps, looks like P&T has crashed on the last time you played, an error log has been received and we will fix the issue asap.";}
    public String confirmQuit() { return "Are you sure you are going to leave us?";}


    //////////////////////////////////////////////////////////////////////////
    //Input name scene
    //////////////////////////////////////////////////////////////////////////
    public String askForNameTitle() { return "Please input your in-game name"; }

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

    public String confirmLogoutFacebook() { return "Are you sure you want to logout from Facebook? (Potato and Tomato will need to restart after this.)"; }
    public String confirmLoginFacebook() { return "Are you sure you want to login into Facebook? (Potato and Tomato will need to restart after this.)"; }
    public String confirmFacebookRequestFailed() { return "Facebook request fails..."; }

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

    public String confirmNotContinueGame() { return "You still have an unfinished game! Do you want to abandon it?" ;}

    /////////////////////////////////////////////////////////////////////////
    //Prerequisite scene
    ////////////////////////////////////////////////////////////////////////
    public String joiningRoom() { return "Joining room..."; }
    public String lookingForServer() { return "Looking for potato..."; }
    public String locatingRoom() { return "Locating potato..."; }

    public String joinRoomFailed() { return "Fail to join room, please try again later."; }
    public String cannotContinue() { return "Fail to continue game, the game jas finished.";}
    public String roomIsFull() { return "Room is too crowded, please try again later.";}
    public String roomNotAvailable() { return "Game is not available.";}

    /////////////////////////////////////////////////////////////////////////////////
    //Room scene
    //////////////////////////////////////////////////////////////////////////////////
    public String roomSceneTitle() { return "Game Room"; }

    public String btnTextWaitingHost() { return "Waiting For Host";}
    public String btnTextStartGame() { return "Start\nGame"; }

    public String teamTitle() { return "Team %s"; }
    public String slotOpen() { return "Open"; }

    public String confirmLeaveRoom() { return "Are you sure you want to leave this room?"; }
    public String confirmHostLeaveRoom() { return "Are you sure you want to leave this room? (This room will be gone)"; }
    public String confirmKick() { return "Confirm kicking %s from this room?";}
    public String confirmYouAreKicked() { return "You are kicked from this room.";}
    public String confirmGameClientOutdated() { return "This game requires latest App version to run, please update your P&T in App Store.";}
    public String confirmGameVersionOutdated() { return "This game has a newer version, a new room needs to be created.";}
    public String confirmFailedRetriveGameData() { return "Fail to connect, please check your connection and try again.";}
    public String confirmHostLeft() { return "Room host has left the room, all participants to exit room now."; }
    public String confirmRoomError() { return "Unexpected error has occurred, you will leave the room now."; }

    public String confirmNotEnoughPlayers() { return "You need at least %s players in each team to start the game."; }
    public String confirmStillDownloadingClient() { return "Please wait until all players have finished downloads."; }
    public String confirmWaitAllUsersReady() { return "Please wait for all users to be ready.";}
    public String confirmWaitTemporaryDisconnectedUsers() { return "Please wait for all player connection to stabilize.";}
    public String confirmFairTeamNeeded() { return "All teams must have same number of players to start.";}

    public String chatMsgUserHasJoinedRoom() { return "%s has joined the room."; }
    public String chatMsgUserHasLeftRoom() { return "%s has left the room."; }
    public String chatMsgUserKicked() { return "%s has been kicked from the room.";}
    public String chatMsgGameStarting() { return "Game will be starting soon after %s coin(s) are inserted.";}
    public String chatMsgGameStartStop() { return "Game start is stopped by %s.";}
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

    public String facebookCannotGetFriends() { return "You need to login with your Facebook account first.";}
    public String noLeaderBoard() { return "This game doesn't have the Leaderboard function enabled.";}

    public String chatMsgxInvitedX() { return "%s has invited %s to join this room.";}
    public String chatMsgxInvitedXAlias() { return "%s has invited %s (previously known as %s) to join this room.";}

    public String PUSHGameInvitationsTitle() { return "P&T Game Invitations"; }
    public String PUSHGameInvitationsContent() { return "You have %s new invitations from your friends.";}
    public String PUSHGameInvitationContent() { return "%s has invited you to play %s together.";}

    /////////////////////////////////////////////////////////////////////
    //Game Sandbox Scene
    ////////////////////////////////////////////////////////////////////////
    public String remainingTime() { return "Remaining Time: ";}
    public String ready() { return "Ready";}
    public String failed() { return "Fail";}

    //show on chat messaging when any player failed to load game
    public String chatMsgLoadGameFailed() { return "Some players have failed to load the game.";}
    public String chatMsgGameEnded() { return "Game ended.";}

    public String notificationAbandon() { return "%s has abandoned everyone!";}
    public String notificationAbandonDueToTimeout() { return "%s has abandoned the game because of failure to reconnect in " + Global.ABANDON_TOLERANCE_SECS + " seconds.";}
    public String notificationConnected() { return "%s has connected to the game.";}
    public String notificationDisconnected() { return "%s has disconnected from the game, he/she has " + Global.ABANDON_TOLERANCE_SECS + " seconds to reconnect, please wait.";}

    public String notificationYouAbandon() { return "You have abandoned the game!";}
    public String notificationYouAbandonDueToTimeout() { return "You have abandoned the game because of failure to reconnect in " + Global.ABANDON_TOLERANCE_SECS + " seconds.";}

    public String notificationYouConnected() { return "You have connected to the game.";}
    public String notificationYouDisconnected() { return "You have disconnected from the game, you have " + Global.ABANDON_TOLERANCE_SECS + " seconds to reconnect.";}

    public String notificationYouKicked() { return "You have been kicked from the room, the game will now be abandoned.";}
    public String notificationKicked() { return "%s has been kicked from the room.";}

    public String confirmAbandonNoCons(){ return "Are you sure you want to abandon this game? \n(You will not be penalized)";}
    public String confirmAbandonLoseStreak(){ return "Are you sure you want to abandon this game? \n(You will be penalized by losing your winning streak)";}
    public String confirmCannotAbandon(){ return "Sorry, the game cannot be abandoned now this way.";}

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
    public String growthRateForShop() { return "Grow A Coin In";}
    public String maxPurseTextForShop() { return "Full";}
    public String watchAdsDescription() { return "Words from our sponsors"; }

    public String btnTextRetrieveCoins() { return "Retrieve Now"; }
    public String btnTextWatchAds() { return "Watch Now"; }
    public String btnTextBuyNow() { return "Buy Now"; }

    public String xCoin() {return "Coin x %s";}
    public String freexCoin() {return "Free Coin x %s";}

    public String confirmPurchaseFailed() { return "Fail to purchase coins..";}

    //coin machines tab title
    public String freeCoinsTabTitle() { return "Mum's purse";}
    public String coinsInsertedTabTitle() { return "Coins Inserted";}
    public String buyCoinsTabTitle() { return "Buy Coins"; }

    ///////////////////////////////////////////////////////////////////////////////
    //Tutorials
    //////////////////////////////////////////////////////////////////////////////////
    public String btnTextSkipTutorial() { return "Skip Tutorial"; }
    public String btnTextStartTutorial() { return "Start Tutorial"; }

    public String tutorialWelcomeMessage() { return "Welcome to P&T, let us guide you how to create your first game.";}
    public String tutorialAboutCreateGame() { return "Tap Create Game button";}

    public String tutorialAboutGameList() { return "Here you can see all P&T games, lets try create a game.";}
    public String tutorialTapChooseGame() { return "Tap this game";}
    public String tutorialTapCreateGame() { return "Tap to create game";}

    public String tutorialAboutRoom() { return "This is the game room, you can invite your friends, chat with room users here, we leave you to explore more yourself, now let's try to start the game!";}
    public String tutorialAboutStartGame() { return "Lets try start game!";}


    public String tutorialAboutCoinMachine() { return "P&T use coins to start a game.";}
    public String tutorialAboutCoinCount() { return "This shows you coins number needed";}
    public String tutorialAboutInsertCoin() { return "Now try to insert coin here";}
    public String tutorialAboutNoCoin() { return "Looks like you don't have any coin, let's see if there is any free coins in mum's purse....";}
    public String tutorialAboutHasCoin() { return "Looks like you already have some coins, but anyway let's continue our tutorial....";}


    public String tutorialAboutTapMumPurse() { return "Tap mum purse";}
    public String tutorialAboutTapGetFreeCoins() { return "Tap to free coins from mum purse!";}

    public String tutorialAboutGetCoinsSuccess() { return "Now you should have enough coins to start game, mum's purse grow coin over time, remember to come back and retrieve it frequently, or you can visit the coin shop to purchase coins if you run out of it.";}

    public String tutorialConclude() { return "Do remember that you can always access coin shop by tapping the coin icon for various free coin method other than mum's purse, that conclude our tutorial, enjoy the games!";}


    ///////////////////////////////////////////////////////////////////////
    //Connection related
    /////////////////////////////////////////////////////////////////////////
    public String confirmLostConnection() { return "Lost connection and trying to reconnect, \n%s secs remaining";}
    public String confirmConnectionRecovered() {return "Connection established, trying to resume game session, \n%s secs remaining";}
    public String confirmNoConnection() { return "You have been disconnected, please check your connection."; }

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
    public String requestFailed() { return "Request fails.";}
    public String generalError() { return "Unexpected error occurs, please try again.";}
    public String workingDoNotClose() { return "Potato and Tomato are working now, please do not close apps now...";}

    ///////////////////////////////////////////////////////////
    //Mascots speeches in coin machine
    //////////////////////////////////////////////////////////
    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutStartGame(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 3);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("We need %expectingCoin% coin(s) to start this game..", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Let's do it", SpeechActionType.Add));
        }
        else if(style == 1){
            potatoSpeechActions.add(new SpeechAction("From seeds to plants...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Tomato will never stop at %expectingCoin% coin(s)!", SpeechActionType.Add));
        }
        else if(style == 2){
            potatoSpeechActions.add(new SpeechAction("%expectingCoin% coin(s) beyond that slot below..", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Is where the fun begins!", SpeechActionType.Add));
        }
        else if(style == 3){
            potatoSpeechActions.add(new SpeechAction("Blink, blink, blink, %expectingCoin% more coin(s)..", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Time to roll!", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }


    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutPurse(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 3);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("Shhh...........", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Shhh...........", SpeechActionType.Add));
        }
        if(style == 1){
            potatoSpeechActions.add(new SpeechAction("Sometimes I open this purse...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("And coins jump out like frogs!", SpeechActionType.Add));
        }
        if(style == 2){
            potatoSpeechActions.add(new SpeechAction("I am a potato...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 600));
            tomatoSpeechActions.add(new SpeechAction("Do we really have the same mum?", SpeechActionType.Add));
        }
        if(style == 3){
            potatoSpeechActions.add(new SpeechAction("Should we buy her a new purse?", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Don't fix what is not broken!", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }


    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutPurchaseCoins(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 3);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("Every step is not easy...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("But your support brings us a long way!", SpeechActionType.Add));
        }
        else if(style == 1){
            potatoSpeechActions.add(new SpeechAction("Every little bit...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Helps this community to grow into a beautiful farm!", SpeechActionType.Add));
        }
        else if(style == 2){
            potatoSpeechActions.add(new SpeechAction("Thank you, thank you, thank you...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Even if you are just window shopping!", SpeechActionType.Add));
        }
        else if(style == 3){
            potatoSpeechActions.add(new SpeechAction("Thank you for your support...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("Ka-ching, Ka-ching, Ka-ching!", SpeechActionType.Add));
        }


        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }

    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutEnoughCoin(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 3);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("Ready....?", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Let's roll!", SpeechActionType.Add));
        }
        if(style == 1){
            potatoSpeechActions.add(new SpeechAction("Potate, potate, potate...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Tomate, Tomate, Tomate!", SpeechActionType.Add));
        }
        if(style == 2){
            potatoSpeechActions.add(new SpeechAction("It is all about the preparation...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Go, go, go!", SpeechActionType.Add));
        }
        if(style == 3){
            potatoSpeechActions.add(new SpeechAction("Defend....", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction("Attack!!!!", SpeechActionType.Add));
        }

        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }

    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutNoMoreCoins(){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 1);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("No more coin! Let's get something from Mum's purse..", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 4300));
            tomatoSpeechActions.add(new SpeechAction("Or give us your support by buying some coins!", SpeechActionType.Add));
        }
        else if(style == 1){
            potatoSpeechActions.add(new SpeechAction("We have no more coin...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 1000));
            tomatoSpeechActions.add(new SpeechAction("Hold on! Why don't you go and check what is in the coin shop?", SpeechActionType.Add));
        }
        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }


    public Pair<ArrayList<SpeechAction>, ArrayList<SpeechAction>> getRandomMascotsSpeechAboutLostStreaks(int coinCount){
        ArrayList<SpeechAction> potatoSpeechActions = new ArrayList();
        ArrayList<SpeechAction> tomatoSpeechActions = new ArrayList();

        int style = MathUtils.random(0, 1);
        if(style == 0){
            potatoSpeechActions.add(new SpeechAction("No, no, no...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 800));
            tomatoSpeechActions.add(new SpeechAction("Don't cry, protect your streak with " + coinCount + " coin(s)!", SpeechActionType.Add));
        }
        else if(style == 1){
            potatoSpeechActions.add(new SpeechAction("Put " + coinCount + " coin(s) down that wishing slot...", SpeechActionType.Add));
            tomatoSpeechActions.add(new SpeechAction(SpeechActionType.Delay, 2000));
            tomatoSpeechActions.add(new SpeechAction("For one more streak of luck!", SpeechActionType.Add));
        }


        return new Pair<>(potatoSpeechActions, tomatoSpeechActions);
    }



}
