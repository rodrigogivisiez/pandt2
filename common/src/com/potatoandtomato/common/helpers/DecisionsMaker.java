package com.potatoandtomato.common.helpers;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.absints.DecisionMakerChangedListener;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.enums.RoomUpdateType;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.ArrayUtils;
import com.potatoandtomato.common.utils.Strings;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class DecisionsMaker implements Disposable {

    private ArrayList<String> _userIDs;
    private String myUserId;
    private IGameSandBox gameSandBox;
    private ArrayList<DecisionMakerChangedListener> decisionMakerChangedListeners;

    public DecisionsMaker(ArrayList<Team> teams, String myUserId, IGameSandBox gameSandBox) {
        _userIDs = new ArrayList<String>();
        this.myUserId = myUserId;
        this.gameSandBox = gameSandBox;
        this.decisionMakerChangedListeners = new ArrayList();
        teamsInit(teams);
    }

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    //only called at initialization
    public void teamsInit(ArrayList<Team> teams){
        _userIDs.clear();

        for(Team team : teams){
            for(Player player : team.getPlayers()){
                _userIDs.add(player.getUserId());
            }
        }

        Collections.sort(_userIDs);
    }

    public void userConnectionChanged(String userId, boolean isConnected){
        boolean decisionMakerChanged = false;

        if(!isConnected){
            decisionMakerChanged = getDecisionMakerUserId().equals(userId);
            _userIDs.remove(userId);
        }
        else{
            String currentDesionMaker = null;
            if(_userIDs.size() > 0){
                currentDesionMaker = _userIDs.get(0);
            }

            if(!_userIDs.contains(userId)){
                _userIDs.add(userId);
            }
            Collections.sort(_userIDs);

            if(!Strings.isEmpty(currentDesionMaker)){
                _userIDs.remove(currentDesionMaker);
                _userIDs.add(0, currentDesionMaker);
            }
            else{
                decisionMakerChanged = true;
            }

            if(!userId.equals(myUserId)){
                gameSandBox.sendPrivateUpdate(RoomUpdateType.DecisionMakerUpdate, userId, Strings.joinArr(getDecisionMakersSequence(), ","));
            }
        }

        if(decisionMakerChanged){
            onDecisionMakerChanged();
        }
    }

    public void receivedDecisionMakerUpdate(String decisionMakersSequence){
        String originalUserId = getDecisionMakerUserId();
        ArrayList<String> receivedSequences = Strings.split(decisionMakersSequence, ",");
        _userIDs = receivedSequences;

        if(receivedSequences.size() > 0 && !receivedSequences.get(0).equals(originalUserId)){
            onDecisionMakerChanged();
        }
    }

    public boolean checkIsDecisionMaker(String userId){
        if(_userIDs.size() > 0){
            return _userIDs.get(0).equals(userId);
        }
        return true;
    }

    public String getDecisionMakerUserId(){
        if(_userIDs.size() > 0){
            return _userIDs.get(0);
        }
        return "";
    }

    public ArrayList<String> getDecisionMakersSequence(){
        return _userIDs;
    }

    public boolean meIsDecisionMaker(){
        return checkIsDecisionMaker(myUserId);
    }

    public void addDecisionMakerChangedListener(DecisionMakerChangedListener listener){
        decisionMakerChangedListeners.add(listener);
    }

    public void removeDecisionMakerChangedListener(DecisionMakerChangedListener listener){
        decisionMakerChangedListeners.remove(listener);
    }

    public void onDecisionMakerChanged(){
        for(DecisionMakerChangedListener listener : decisionMakerChangedListeners){
            listener.onChanged(getDecisionMakerUserId());
        }
    }

    @Override
    public void dispose() {
        decisionMakerChangedListeners.clear();
    }
}
