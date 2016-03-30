package com.potatoandtomato.games.absint;

import com.potatoandtomato.common.Team;
import com.potatoandtomato.common.models.ScoreDetails;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 25/3/2016.
 */
public abstract class ScoresListener{
    public abstract void onCallBack(HashMap<Team, ArrayList<ScoreDetails>> winnerResult, ArrayList<Team> losers);
}
