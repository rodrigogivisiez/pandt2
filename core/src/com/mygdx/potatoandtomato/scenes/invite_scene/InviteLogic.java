package com.mygdx.potatoandtomato.scenes.invite_scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.utils.*;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by SiongLeng on 23/12/2015.
 */
public class InviteLogic extends LogicAbstract {

    InviteScene _scene;
    Room _room;
    ArrayList<Profile> _invitedUsers;

    public InviteLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new InviteScene(_services, _screen);
        _invitedUsers = new ArrayList();

        _room = (Room) objs[0];

        _scene.addTitleToContainer(_texts.recentlyPlayTabTitle(), InviteScene.InviteType.Recent);
        _scene.addTitleToContainer(_texts.faebookFriendsTabTitle(), InviteScene.InviteType.Facebook);
        _scene.addTitleToContainer(_room.getGame().getName() + " " + _texts.leaderBoardsTabTitle(), InviteScene.InviteType.Leaderboard);
        _scene.changeTab(InviteScene.InviteType.Recent);
    }

    public ArrayList<Profile> getInvitedUsers() {
        return _invitedUsers;
    }

    @Override
    public void onInit() {
        super.onInit();

        _scene.putMessageToTable(_services.getTexts().loading(), InviteScene.InviteType.Recent);

        _scene.putMessageToTable(_services.getSocials().isFacebookLogon() ? _texts.loading() : _texts.facebookCannotGetFriends(),
                InviteScene.InviteType.Facebook);

        _scene.putMessageToTable(_room.getGame().hasLeaderboard() ? _texts.loading() : _texts.noLeaderBoard(),
                InviteScene.InviteType.Leaderboard);


        if(_services.getSocials().isFacebookLogon()){
            subscribeBroadcastOnceWithTimeout(BroadcastEvent.FACEBOOK_GET_FRIENDS_RESPONSE, 10000, new BroadcastListener<ArrayList<FacebookProfile>>() {
                @Override
                public void onCallback(final ArrayList<FacebookProfile> fbProfiles, Status st) {
                    _scene.clearTableContent(InviteScene.InviteType.Facebook);
                    if (st == Status.SUCCESS) {
                        if (fbProfiles.size() == 0) {
                            _scene.putMessageToTable(_texts.noRecords(), InviteScene.InviteType.Facebook);
                        } else {

                            Threadings.runInBackground(new Runnable() {
                                @Override
                                public void run() {
                                    ThreadsPool threadsPool = new ThreadsPool();
                                    final int[] successCount = {0};

                                    for (final FacebookProfile facebookProfile : fbProfiles) {
                                        final Threadings.ThreadFragment fragment = new Threadings.ThreadFragment();
                                        _services.getDatabase().getProfileByFacebookUserId(facebookProfile.getUserId(), new DatabaseListener<Profile>(Profile.class) {
                                            @Override
                                            public void onCallback(Profile profile, Status st) {
                                                if (st == Status.SUCCESS) {
                                                    putProfileToTable(profile, InviteScene.InviteType.Facebook, facebookProfile);
                                                    successCount[0]++;
                                                }
                                                fragment.setFinished(true);
                                            }
                                        });
                                        threadsPool.addFragment(fragment);
                                    }

                                    while (!threadsPool.allFinished()){
                                        Threadings.sleep(300);
                                        if(isDisposing()) return;
                                    }

                                    if(successCount[0] == 0){
                                        _scene.putMessageToTable(_texts.noRecords(), InviteScene.InviteType.Facebook);
                                    }
                                }
                            });
                        }
                    } else {
                        _scene.putMessageToTable(_texts.requestFailed(), InviteScene.InviteType.Facebook);
                    }
                }
            });
            publishBroadcast(BroadcastEvent.FACEBOOK_GET_FRIENDS_REQUEST);
        }

        _services.getDatabase().getPlayedHistories(_services.getProfile(), new DatabaseListener<ArrayList<GameHistory>>(GameHistory.class) {
            @Override
            public void onCallback(ArrayList<GameHistory> obj, Status st) {
                if(st == Status.SUCCESS){
                    if(obj.size() == 0){
                        _scene.putMessageToTable(_texts.noRecords(), InviteScene.InviteType.Recent);
                    }
                    else{
                        _scene.clearTableContent(InviteScene.InviteType.Recent);
                        for(final GameHistory gameHistory : obj){
                            putProfileToTable(gameHistory.getPlayedWith(), InviteScene.InviteType.Recent, gameHistory);
                        }
                    }
                }
                else{
                    _scene.putMessageToTable(_texts.requestFailed(), InviteScene.InviteType.Recent);
                }
            }
        });

        if(_room.getGame().hasLeaderboard()){
            _services.getDatabase().getLeaderBoardAndStreak(_room.getGame(), Global.LEADERBOARD_COUNT, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
                @Override
                public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                    if (st == Status.SUCCESS) {
                        if (records.size() == 0) {
                            _scene.putMessageToTable(_texts.noRecords(), InviteScene.InviteType.Leaderboard);
                        } else {
                            _scene.clearTableContent(InviteScene.InviteType.Leaderboard);
                            int i = 1;
                            ArrayList<String> processedUserIds = new ArrayList<String>();

                            for (final LeaderboardRecord record : records) {
                                for (Map.Entry<String, String> entry : record.getUserIdToNameMap().entrySet()) {
                                    String userId = entry.getKey();
                                    if (!processedUserIds.contains(userId)) {
                                        processedUserIds.add(userId);
                                        String userName = entry.getValue();
                                        Profile p = new Profile();
                                        p.setUserId(userId);
                                        p.setGameName(userName);
                                        putProfileToTable(p, InviteScene.InviteType.Leaderboard, i, record.getScore(), record.getStreak());
                                    }
                                }
                                i++;
                            }
                        }
                    } else {
                        _scene.putMessageToTable(_texts.requestFailed(), InviteScene.InviteType.Leaderboard);
                    }
                }
            });
        }
    }

    private void putProfileToTable(final Profile profile, final InviteScene.InviteType inviteType, final Object... objs){
        if(!profile.getUserId().equals(_services.getProfile().getUserId())){
            _scene.putUserToTable(profile, inviteType, _room.getUserIsInvited(profile.getUserId()), new RunnableArgs<Actor>() {
                @Override
                public void run() {
                    if(inviteType == InviteScene.InviteType.Facebook){
                        final FacebookProfile facebookProfile = (FacebookProfile) objs[0];
                        subscribeBroadcast(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
                            @Override
                            public void onCallback(Pair<String, Texture> pair, Status st) {
                                if (st == Status.SUCCESS && pair.getFirst().equals(facebookProfile.getProfilePicUrl())) {
                                    _scene.putFacebookProfilePicture(facebookProfile.getUserId(), pair.getSecond());
                                }
                            }
                        });
                        publishBroadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, facebookProfile.getProfilePicUrl());
                    }

                    if(!_room.getUserIsInvited(profile.getUserId())){
                        this.getFirstArg().addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                toggleUserSelection(profile);
                            }
                        });
                    }
                }
            }, objs);
        }
    }

    public void toggleUserSelection(Profile profile){
        if(_scene.toggleUserSelection(profile)){
            _invitedUsers.add(profile);
        }
        else{
            _invitedUsers.remove(profile);
        }
    }

    public void sendInvitation(){
        final ArrayList<String> invitedUserIds = new ArrayList<String>();
        if(_invitedUsers.size() > 0){
            for(Profile user : _invitedUsers){
                if(!_room.getUserIsInvited(user.getUserId())){
                    invitedUserIds.add(user.getUserId());
                }
            }
            if(invitedUserIds.size() > 0){
                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        for(final Profile user : _invitedUsers){
                            ThreadsPool threadsPool = new ThreadsPool();

                            final Pair<Profile, ArrayList<String>> latestProfileToInvitationRoomIdsPair = new Pair();

                            final Threadings.ThreadFragment threadFragment1 = new Threadings.ThreadFragment();
                            _services.getDatabase().getProfileByUserId(user.getUserId(), new DatabaseListener<Profile>(Profile.class) {
                                @Override
                                public void onCallback(Profile obj, Status st) {
                                    if (st == Status.SUCCESS) {
                                        latestProfileToInvitationRoomIdsPair.setFirst(obj);
                                    }
                                    threadFragment1.setFinished(true);
                                }
                            });
                            threadsPool.addFragment(threadFragment1);


                            final Threadings.ThreadFragment threadFragment2 = new Threadings.ThreadFragment();
                            _services.getDatabase().getPendingInvitationRoomIds(user, new DatabaseListener<ArrayList<String>>() {
                                @Override
                                public void onCallback(ArrayList<String> roomIds, Status st) {
                                    if (st == Status.SUCCESS) {
                                        latestProfileToInvitationRoomIdsPair.setSecond(roomIds);
                                    }
                                    threadFragment2.setFinished(true);
                                }
                            });
                            threadsPool.addFragment(threadFragment2);

                            while (!threadsPool.allFinished()){
                                Threadings.sleep(500);
                            }

                            if (latestProfileToInvitationRoomIdsPair.getFirst() != null &&
                                    latestProfileToInvitationRoomIdsPair.getSecond() != null) {
                                ArrayList<String> roomIds = latestProfileToInvitationRoomIdsPair.getSecond();
                                roomIds.add(_room.getId());

                                Profile latestProfile = latestProfileToInvitationRoomIdsPair.getFirst();

                                InvitationModel invitationModel = new InvitationModel();
                                invitationModel.setInvitedUserId(latestProfile.getUserId());
                                invitationModel.setPendingInvitationRoomIds(roomIds);

                                PushNotification push = new PushNotification();
                                push.setId(PushCode.SEND_INVITATION);
                                push.setSilentNotification(false);
                                push.setSticky(false);
                                push.setSilentIfInGame(false);
                                push.setTitle(_texts.PUSHGameInvitationsTitle());
                                push.setExtras(invitationModel.toJson());
                                if (roomIds.size() == 1) {
                                    push.setMessage(String.format(_texts.PUSHGameInvitationContent(),
                                            _services.getProfile().getDisplayName(15), _room.getGame().getName()));
                                } else if (roomIds.size() > 1) {
                                    push.setMessage(String.format(_texts.PUSHGameInvitationsContent(),
                                            roomIds.size()));
                                }
                                _services.getGcmSender().send(user, push);

                                boolean nameChanged = !(latestProfile.getDisplayName(0).equals(user.getDisplayName(0)));

                                String msg = String.format(_texts.chatMsgxInvitedX(),
                                        _services.getProfile().getDisplayName(0), user.getDisplayName(0));
                                if(nameChanged){
                                    msg = String.format(_texts.chatMsgxInvitedXAlias(),
                                            _services.getProfile().getDisplayName(0), latestProfile.getDisplayName(0),
                                            user.getDisplayName(0));
                                }

                                ChatMessage chatMessage = new ChatMessage(msg,
                                                        ChatMessage.FromType.SYSTEM, null, "");
                                _services.getChat().newMessage(chatMessage);
                                _services.getGamingKit().sendRoomMessage(chatMessage);
                            }
                        }

                        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.INVTE_USERS, Strings.joinArr(invitedUserIds, ","));
                    }
                });
            }
        }
        _screen.back();
    }

    @Override
    public void setListeners() {
        super.setListeners();

        _scene.getInviteButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sendInvitation();
            }
        });

        _scene.getRecentTabTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _scene.changeTab(InviteScene.InviteType.Recent);
            }
        });

        _scene.getLeaderboardTabTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _scene.changeTab(InviteScene.InviteType.Leaderboard);
            }
        });

        _scene.getFacebookTabTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _scene.changeTab(InviteScene.InviteType.Facebook);
            }
        });
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

}
