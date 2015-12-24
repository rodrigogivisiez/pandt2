package com.mygdx.potatoandtomato.scenes.invite_scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

import java.util.ArrayList;

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
        _invitedUsers = new ArrayList<>();

        _room = (Room) objs[0];
    }

    public ArrayList<Profile> getInvitedUsers() {
        return _invitedUsers;
    }

    @Override
    public void onInit() {
        super.onInit();

        _scene.putMessageToTable(_services.getTexts().loading(), _scene.getRecentPlayedTable());

        _scene.putMessageToTable(_services.getSocials().isFacebookLogon() ? _texts.loading() : _texts.facebookCannotGetFriends(),
                _scene.getFacebookFriendsTable());

        if(_services.getSocials().isFacebookLogon()){
            Broadcaster.getInstance().subscribe(BroadcastEvent.FACEBOOK_GET_FRIENDS_RESPONSE, new BroadcastListener() {
                @Override
                public void onCallback(Object obj, Status st) {
                    if(st == Status.SUCCESS){

                    }
                    else{
                        _scene.putMessageToTable(_texts.requestFailed(), _scene.getFacebookFriendsTable());
                    }
                }
            });
            Broadcaster.getInstance().broadcast(BroadcastEvent.FACEBOOK_GET_FRIENDS_REQUEST);
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
                            _scene.putUserToTable(gameHistory.getPlayedWith(), _scene.getRecentPlayedTable()).addListener(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    toggleUserSelection(gameHistory.getPlayedWith());
                                }
                            });
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

    public void toggleUserSelection(Profile profile){
        if(_scene.toggleUserSelection(profile)){
            _invitedUsers.add(profile);
        }
        else{
            _invitedUsers.remove(profile);
        }
    }

    public void sendInvitation(){
        if(_invitedUsers.size() > 0){
            boolean added = false;
            for(Profile user : _invitedUsers){
                boolean result = _room.addInvitedUser(user);
                if(!added){
                    added = result;
                }
            }
            if(added){
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
                                        push.setTitle(_texts.PUSHGameInvitationsTitle());
                                        if(obj == 1){
                                            push.setMessage(String.format(_texts.PUSHGameInvitationContent(),
                                                    _services.getProfile().getDisplayName(), _room.getGame().getName()));
                                        }
                                        else if(obj > 1){
                                            push.setMessage(String.format(_texts.PUSHGameInvitationsContent(),
                                                    obj));
                                        }
                                        _services.getGcmSender().send(user, push);
                                        _services.getChat().newChatMessage(new ChatMessage(String.format(_texts.xInvitedX(),
                                                        _services.getProfile().getDisplayName(), user.getDisplayName()), ChatMessage.FromType.SYSTEM, null));
                                    }
                                    else{
                                        _services.getChat().newChatMessage(new ChatMessage(String.format(_texts.xInvitedXFailed(),
                                                _services.getProfile().getDisplayName(), user.getDisplayName()), ChatMessage.FromType.IMPORTANT, null));
                                    }
                                    done[0]++;
                                }
                            });
                        }

                        while (done[0] < _invitedUsers.size()){
                            Threadings.sleep(1000);
                        }

                        _services.getDatabase().saveRoom(_room, new DatabaseListener<String>() {
                            @Override
                            public void onCallback(String obj, Status st) {
                                killKeepAlive();
                            }
                        });
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
