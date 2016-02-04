package abstracts;

import com.potatoandtomato.common.Team;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class MockTeam {

    public static ArrayList<Team> getTeams(int expectedTeamCount, int eachTeamPlayerCount){
        ArrayList<Team> teams = new ArrayList<Team>();
        int i = 0;
        for(int q = 0; q< expectedTeamCount; q++){
            Team team = new Team();
            for(int b=0; b< eachTeamPlayerCount; b++){
                team.addPlayer(MockPlayer.mockPlayer(String.valueOf(i), i == 0));
                i++;
            }
            teams.add(team);
        }
        return teams;
    }


}
