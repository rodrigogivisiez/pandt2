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
    public String name, version, min_players, max_players, description, abbr;

    public Details(String _path) {
        this._path = _path;
    }

    public void run() throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(new FileReader(_path));

        JSONObject jsonObject = (JSONObject) obj;

        name = (String) jsonObject.get("name");
        version = (String) jsonObject.get("version");
        min_players = (String) jsonObject.get("min_players");
        max_players = (String) jsonObject.get("max_players");
        description = (String) jsonObject.get("description");
        abbr = (String) jsonObject.get("abbr");

        if(!isNumberWith2Decimals(version) || !isInteger(min_players) || !isInteger(max_players)){
            throw new ParseException(0);
        }


    }

    public void addVersion(){
        double v = Double.valueOf(version);
        v += 0.01;
        version = String.valueOf(v);
    }

    public void writeBackJson() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("min_players", min_players);
        obj.put("max_players", max_players);
        obj.put("version", version);
        obj.put("description", description);
        obj.put("abbr", abbr);

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
