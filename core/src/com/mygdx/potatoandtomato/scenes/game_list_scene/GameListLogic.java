package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.RoomInfoListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.services.Confirm;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.enums.GestureType;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.potatoandtomato.common.enums.Status;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListLogic extends LogicAbstract implements TutorialPartListener {

    private GameListScene _scene;
    private ArrayList<Room> _rooms;
    private Room _selectedRoom;
    private String _continueRoomId;
    private int tutorialStep;
    private boolean retrievedInbox;
    private boolean ratedAppsChecked;

    public GameListLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _scene = new GameListScene(services, screen);
        _rooms = new ArrayList();
    }

    @Override
    public void onShow() {
        super.onShow();
        checkCanContinue();
        _scene.setUsername(_services.getProfile().getDisplayName(15));

        getInboxMessages();
        checkRatedApps();
    }

    @Override
    public void onShown() {
        super.onShown();

        if(!_services.getTutorials().completedTutorialBefore(Terms.PREF_BASIC_TUTORIAL)){
            _services.getAutoJoiner().stopAutoJoinRoom();
            _services.getTutorials().startTutorialIfNotCompleteBefore(Terms.PREF_BASIC_TUTORIAL, true, this);
        }
        else{
            if(_services.getAutoJoiner().isAutoJoining()){
                joinRoom(PrerequisiteLogic.JoinType.JOINING, _services.getAutoJoiner().getAutoJoinRoomId());
            }
            else{
                showRateAppsNotYetRated();
            }
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        _scene.setContinueButtonEnabled(false);
    }

    private void joinGamePreCheck(final Runnable toRun){
        if(_continueRoomId != null){
            _confirm.show(ConfirmIdentifier.ConfirmAbandon , _texts.confirmNotContinueGame(), Confirm.Type.YESNO, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if(result == Result.YES){
                        _services.getProfile().getUserPlayingState().abandonGame();
                        _services.getDatabase().updateProfile(_services.getProfile(), null);
                        toRun.run();
                    }
                }
            });
        }
        else{
            toRun.run();
        }
    }

    private void checkCanContinue(){
        _scene.setContinueButtonEnabled(false);
        _continueRoomId = null;
        if(!_services.getProfile().getUserPlayingState().getRoomId().equals("0")){
            final UserPlayingState state = _services.getProfile().getUserPlayingState();
            _services.getDatabase().getRoomById(state.getRoomId(), new DatabaseListener<Room>(Room.class) {
                @Override
                public void onCallback(final Room obj, Status st) {
                    if(st == Status.SUCCESS && obj != null){
                        if(obj.canContinue(_services.getProfile())){
                            _services.getGamingKit().addListener(getClassTag(), new RoomInfoListener(obj.getWarpRoomId(), getClassTag()) {
                                @Override
                                public void onRoomInfoRetrievedSuccess(String[] inRoomUserIds) {
                                    _services.getGamingKit().removeListenersByClassTag(getClassTag());

                                    if(inRoomUserIds.length > 0){
                                        _continueRoomId = obj.getId();
                                        _scene.setContinueButtonEnabled(true);
                                    }
                                    else{
                                        _services.getProfile().getUserPlayingState().abandonGame();
                                        _services.getDatabase().updateProfile(_services.getProfile(), null);
                                    }
                                }

                                @Override
                                public void onRoomInfoFailed() {
                                    _services.getGamingKit().removeListenersByClassTag(getClassTag());
                                }
                            });
                            _services.getGamingKit().getRoomInfo(obj.getWarpRoomId(), getClassTag());
                        } else {
                            _services.getProfile().getUserPlayingState().abandonGame();
                            _services.getDatabase().updateProfile(_services.getProfile(), null);
                        }
                    }
                }
            });


        }
    }

    public void roomDataChanged(final Room room, final boolean playSound){
        if(room.isOpen()){
            if(room.getHost().equals(_services.getProfile())){      //orphan room fix
                if(isSceneVisible()){
                    room.setOpen(false);
                    _services.getDatabase().updateRoomPlayingAndOpenState(room, false, false, null);
                }
            }
            else{
                if(!_scene.alreadyContainsRoom(room) && playSound && isSceneVisible()){
                    _services.getSoundsPlayer().playSoundEffect(Sounds.Name.GAME_CREATED);
                }
                _scene.updatedRoom(room, new RunnableArgs<Actor>() {
                     @Override
                     public void run() {
                         if(this.getFirstArg() != null){
                             final Actor clicked = this.getFirstArg();
                             clicked.clearListeners();
                             clicked.addListener(new ClickListener() {
                                 @Override
                                 public void clicked(InputEvent event, float x, float y) {
                                     super.clicked(event, x, y);
                                     _selectedRoom = room;
                                     _scene.gameRowHighlight(room.getId());
                                 }
                             });
                         }
                     }
                });
            }

        }
        else{
            if(_selectedRoom != null && _selectedRoom.getId().equals(room.getId())) {
                _selectedRoom = null;
                _scene.gameRowHighlight("-1");
            }
            _scene.removeRoom(room.getId());
        }
    }

    private void joinRoom(PrerequisiteLogic.JoinType joinType, String roomId){
        _screen.toScene(SceneEnum.PREREQUISITE, null, joinType, roomId);
    }

    private void getInboxMessages(){
        if(!retrievedInbox){
            retrievedInbox = true;
            _services.getDatabase().getAllInboxMessage(_services.getProfile().getUserId(), new DatabaseListener<ArrayList<InboxMessage>>(InboxMessage.class) {
                @Override
                public void onCallback(ArrayList<InboxMessage> result, Status st) {
                    if(result != null && st == Status.SUCCESS){
                        for(final InboxMessage inboxMessage : result){
                            _scene.addInboxMessageToList(inboxMessage, new RunnableArgs<Actor>() {
                                @Override
                                public void run() {
                                    this.getFirstArg().addListener(new ClickListener(){
                                        @Override
                                        public void clicked(InputEvent event, float x, float y) {
                                            super.clicked(event, x, y);
                                            inboxMessageOpened(inboxMessage);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void checkRatedApps(){
        if(!ratedAppsChecked){
            _services.getDatabase().checkFeedbackExist(_services.getProfile().getUserId(), new DatabaseListener<Boolean>() {
                @Override
                public void onCallback(Boolean obj, Status st) {
                    ratedAppsChecked = true;
                    if(st == Status.SUCCESS && obj != null){
                        _services.getProfile().setRatedApps(obj);
                        showRateAppsNotYetRated();
                    }
                    else{
                        _services.getProfile().setRatedApps(true);
                    }
                }
            });
        }
    }

    private void showRateAppsNotYetRated(){
        if(ratedAppsChecked){
            if(!_services.getProfile().isRatedApps()){
                String value = _services.getPreferences().get(Terms.TOTAL_GAME_COUNT);
                int total = 0;
                if(!Strings.isEmpty(value) && Strings.isNumeric(value)){
                    total = Integer.valueOf(value);
                }
                if(total >= 5){
                    //show
                    _services.getProfile().setRatedApps(true);
                }
            }
        }
    }

    private void rateApps(boolean liked, String reason){
        _services.getDatabase().sendFeedback(_services.getProfile().getUserId(), new RateAppsModel(liked, reason), null);
        //hide
    }

    private void inboxMessageOpened(InboxMessage inboxMessage){
        _scene.changeInboxMessage(inboxMessage, new RunnableArgs<Array<Label>>() {
            @Override
            public void run() {
                for(final Label label : this.getFirstArg()){
                    label.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            Gdx.net.openURI(label.getText().toString());
                        }
                    });
                }
            }
        });
        _scene.toggleInboxMessage();
        if(!inboxMessage.isRead()){
            _scene.inboxMessageIsRead(inboxMessage);
            _services.getDatabase().inboxMessageRead(_services.getProfile().getUserId(), inboxMessage.getId(), null);
            inboxMessage.setRead(true);
        }
    }

    @Override
    public void setListeners() {
        super.setListeners();

        _services.getDatabase().monitorAllRooms(_rooms, getClassTag(), new SpecialDatabaseListener<ArrayList<Room>, Room>(Room.class) {
            @Override
            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
                if (st == Status.SUCCESS) {
                    for (Room r : obj) {
                        roomDataChanged(r, false);
                    }
                }
            }

            @Override
            public void onCallbackTypeTwo(Room obj, Status st) {
                if (st == Status.SUCCESS) {
                    roomDataChanged(obj, true);
                }
            }
        });

        _scene.getNewGameButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                joinGamePreCheck(new Runnable() {
                    @Override
                    public void run() {
                        _screen.toScene(SceneEnum.CREATE_GAME);
                    }
                });
            }
        });

        _scene.getContinueGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_continueRoomId != null){
                    joinRoom(PrerequisiteLogic.JoinType.CONTINUING, _continueRoomId);
                }
            }
        });

        _scene.getJoinGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_selectedRoom != null){
                    joinGamePreCheck(new Runnable() {
                        @Override
                        public void run() {
                            joinRoom(PrerequisiteLogic.JoinType.JOINING, _selectedRoom.getId());
                        }
                    });
                }
            }
        });


        _scene.getSettingsButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.SETTINGS);
            }
        });

        _scene.getLeaderBoardsButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.MULTIPLE_GAMES_LEADER_BOARD);
            }
        });

        _scene.getShareButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                publishBroadcast(BroadcastEvent.SHARE_P_AND_T);
            }
        });

        _scene.getInboxButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _scene.toggleInboxList();
            }
        });

    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    @Override
    public void nextTutorial() {
        tutorialStep++;
        if(tutorialStep == 1){
            _services.getTutorials().showMessage(null, _texts.tutorialWelcomeMessage());
        }
        else if(tutorialStep == 2){
            _services.getTutorials().expectGestureOnActor(GestureType.PointUp,
                    _scene.getGameTitleTable(), _texts.tutorialAboutLobbies(), 0 , -100);
        }
        else if(tutorialStep == 3){
            _services.getTutorials().expectGestureOnActor(GestureType.PointUp,
                    _scene.getJoinGameButton(), _texts.tutorialAboutJoinGame(), 0 ,0);
        }
        else if(tutorialStep == 4){
            _services.getTutorials().showMessage(null, _texts.tutorialCreateGameMessage());
        }
        else if(tutorialStep == 5){
            _services.getTutorials().expectGestureOnActor(GestureType.Tap,
                    _scene.getNewGameButton(), _texts.tutorialAboutCreateGame(), 0 ,0);

        }
        else if(tutorialStep == 7){
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    _services.getTutorials().showMessage(null, _texts.tutorialConclude());
                    _services.getTutorials().expectGestureOnActor(GestureType.None,
                            _scene.getTopBar().getTopBarCoinControl(), "", 0, 0);
                    _scene.getTopBar().getTopBarCoinControl().showFreeCoinPointing();
                }
            });
        }
        else if(tutorialStep == 8){
            _scene.getTopBar().getTopBarCoinControl().hideFreeCoinPointing();
            _services.getTutorials().completeTutorial();
        }
    }
}
