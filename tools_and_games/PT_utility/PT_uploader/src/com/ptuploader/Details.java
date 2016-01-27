package com.ptuploader;

import com.sun.deploy.util.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Details {

    private String _path;
    public String name, version, min_players, max_players, description, abbr, team_min_players, team_max_players, team_count;
    public boolean mustFairTeam;

    public Details(String _path) {
        this._path = _path;
    }

    public void run() throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(new FileReader(_path));

        JSONObject jsonObject = (JSONObject) obj;

        name = (String) jsonObject.get("name");
        if(!jsonObject.containsKey("version")){
            version = "0.99";
        }
        else{
            version = (String) jsonObject.get("version");
        }

        min_players = (String) jsonObject.get("min_players");
        max_players = (String) jsonObject.get("max_players");
        description = (String) jsonObject.get("description");
        team_max_players = (String) jsonObject.get("team_max_players");
        team_min_players = (String) jsonObject.get("team_min_players");
        team_count = (String) jsonObject.get("team_count");
        abbr = (String) jsonObject.get("abbr");
        String mustFair = (String) jsonObject.get("must_fair_team");
        if(mustFair.equals("false")){
            mustFairTeam = false;
        }
        else{
            mustFairTeam = true;
        }

        if(!isNumberWith2Decimals(version) || !isInteger(min_players) || !isInteger(max_players)){
            throw new ParseException(0);
        }


    }

    public void addVersion(){
        double v = Double.valueOf(version);
        v += 0.01;
        version = String.format("%.2f", v);
    }

    public void writeBackJson() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("min_players", min_players);
        obj.put("max_players", max_players);
        obj.put("version", version);
        obj.put("description", description);
        obj.put("team_min_players", team_min_players);
        obj.put("team_max_players", team_max_players);
        obj.put("team_count", team_count);
        obj.put("abbr", abbr);
        obj.put("must_fair_team", mustFairTeam ? "true" : "false");

        File f = new File(_path);
        if(f.exists()) f.delete();

        try (FileWriter file = new FileWriter(_path)) {
            file.write(obj.toJSONString());
            file.close();
        }

    }

    public String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }

    public boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public boolean isNumberWith2Decimals(String string) {
        return string.matches("^\\d+\\.\\d{2}$");
    }

}
