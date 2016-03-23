package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.potatoandtomato.common.models.LeaderboardRecord;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 22/3/2016.
 */
public class LeaderboardFiller {

    public static void fillEmptyRecords(ArrayList<LeaderboardRecord> records){
        LeaderboardRecord leaderboardRecord = new LeaderboardRecord();
        leaderboardRecord.addUserName("", "");
        leaderboardRecord.setScore(0);
        for(int i = records.size(); i < 200; i ++){
            records.add(leaderboardRecord);
        }
    }


}
