package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.models.LeaderboardRecord;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 22/3/2016.
 */
public class LeaderboardHelper {

    public static void fillEmptyRecords(ArrayList<LeaderboardRecord> records){
        LeaderboardRecord leaderboardRecord = new LeaderboardRecord();
        leaderboardRecord.addUserName("", "");
        leaderboardRecord.setScore(0);
        for(int i = records.size(); i < Global.LEADERBOARD_COUNT; i ++){
            records.add(leaderboardRecord);
        }
    }

    //check my record is in leaderboard but not in those appended one
    public static boolean isMyRecordInLeaderboard(ArrayList<LeaderboardRecord> records, String myUserId){
        boolean found = false;
        for(int i = 0; i < Math.min(Global.LEADERBOARD_COUNT, records.size()); i++){
            if(records.get(i).containUser(myUserId)){
                found = true;
                break;
            }
        }
        return found;
    }


}
