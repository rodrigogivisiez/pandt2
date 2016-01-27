package com.mygdx.potatoandtomato.scenes.invite_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.utils.Strings;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

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
    }

    public ArrayList<Profile> getInvitedUsers() {
        return _invitedUsers;
    }

    @Override
    public void onInit() {
        super.onInit();

        _services.getChat().hide();

        _scene.putMessageToTable(_services.getTexts().loading(), _scene.getRecentPlayedTable());

        _scene.putMessageToTable(_services.getSocials().isFacebookLogon() ? _texts.loading() : _texts.facebookCannotGetFriends(),
                _scene.getFacebookFriendsTable());

        if(_services.getSocials().isFacebookLogon()){
            subscribeBroadcastOnceWithTimeout(BroadcastEvent.FACEBOOK_GET_FRIENDS_RESPONSE, 10000, new BroadcastListener<ArrayList<FacebookProfile>>() {
                @Override
                public void onCallback(ArrayList<FacebookProfile> obj, Status st) {
                    if (st == Status.SUCCESS) {
                        if (obj.size() == 0) {
                            _scene.putMessageToTable(_texts.noRecords(), _scene.getFacebookFriendsTable());
                        } else {
                            for (final FacebookProfile facebookProfile : obj) {
                                _services.getDatabase().getProfileByFacebookUserId(facebookProfile.getUserId(), new DatabaseListener<Profile>(Profile.class) {
                                    @Override
                                    public void onCallback(Profile profile, Status st) {
                                        if (st == Status.SUCCESS) {
                                            if (!facebookProfile.getName().equals(profile.getDisplayName(0))) {
                                                profile.setGameName(facebookProfile.getName() + " / " + profile.getDisplayName(0));
                                            }
                                            putProfileToTable(profile, _scene.getFacebookFriendsTable());
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        _scene.putMessageToTable(_texts.requestFailed(), _scene.getFacebookFriendsTable());
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
                        _scene.putMessageToTable(_texts.noRecords(), _scene.getRecentPlayedTable());
                    }
                    else{
                        for(final GameHistory gameHistory : obj){
                            putProfileToTable(gameHistory.getPlayedWith(), _scene.getRecentPlayedTable());
                        }
                    }
                }
                else{
                    _scene.putMessageToTable(_texts.requestFailed(), _scene.getRecentPlayedTable());
                }
            }
        });

        _scene.getInviteButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sendInvitation();
            }
        });


    }

    private void putProfileToTable(final Profile profile, Table table){
        _scene.putUserToTable(profile, table).addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                toggleUserSelection(profile);
            }
        });
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
                if(_room.getInvitedUserByUserId(user.getUserId()) == null){
                    invitedUserIds.add(user.getUserId());
                }
            }
            if(invitedUserIds.size() > 0){
                keepAlive();
                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        final int[] done = {0};
                        for(final Profile user : _invitedUsers){
                            _services.getDatabase().getPendingInvitationsCount(user, new DatabaseListener<Integer>() {
                                @Override
                                public void onCallback(Integer obj, Status st) {
                                    if(st == Status.SUCCESS){
                                        obj++;  //add the current invitation
                                        PushNotification push = new PushNotification();
                                        push.setId(PushCode.SEND_INVITATION);
                                        push.setSilentNotification(false);
                                        push.setSticky(false);
                                        push.setSilentIfInGame(false);
                                        push.setTitle(_texts.PUSHGameInvitationsTitle());
                                        if(obj == 1){
                                            push.setMessage(String.format(_texts.PUSHGameInvitationContent(),
                                                    _services.getProfile().getDisplayName(15), _room.getGame().getName()));
                                        }
                                        else if(obj > 1){
                                            push.setMessage(String.format(_texts.PUSHGameInvitationsContent(),
                                                    obj));
                                        }
                                        _services.getGcmSender().send(user, push);

                                        _services.getGamingKit().sendRoomMessage(new ChatMessage(String.format(_texts.xInvitedX(),
                                                _services.getProfile().getDisplayName(0), user.getDisplayName(0)), ChatMessage.FromType.SYSTEM, null));
                                    }
                                    else{
                                        _services.getGamingKit().sendRoomMessage(new ChatMessage(String.format(_texts.xInvitedXFailed(),
                                                _services.getProfile().getDisplayName(0), user.getDisplayName(0)), ChatMessage.FromType.IMPORTANT, null));
                                    }
                                    done[0]++;
                                }
                            });
                        }

                        while (done[0] < _invitedUsers.size()){
                            Threadings.sleep(1000);
                        }

                        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.INVTE_USERS, Strings.joinArr(invitedUserIds, ","));
                        killKeepAlive();
                    }
                });
            }
        }
        _screen.back();
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

}
