package com.ptuploader.process;

import com.ptuploader.process.Paths;
import com.ptuploader.utils.FileIO;
import com.ptuploader.utils.Logs;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Details {

    private Paths _paths;
    private Logs _logs;
    private HashMap<String, Object> _detailsMap;
    private final String MAX_PLAYERS = "maxPlayers";
    private final String MIN_PLAYERS = "minPlayers";
    public final String NAME = "name";
    private final String TEAM_MIN_PLAYERS = "teamMinPlayers";
    private final String TEAM_MAX_PLAYERS = "teamMaxPlayers";
    public final String ABBR = "abbr";
    private final String TEAM_COUNT = "teamCount";
    private final String VERSION = "version";
    private final String COMMON_VERSION = "commonVersion";
    private final String MUST_FAIR_TEAM = "mustFairTeam";
    private final String ICON_URL = "iconUrl";
    private final String GAME_URL = "gameUrl";
    private final String GAME_SIZE = "gameSize";
    private final String UPDATE_TIMESTAMP = "lastUpdatedTimestamp";
    private final String CREATE_TIMESTAMP = "createTimestamp";

    public Details(Paths paths, Logs logs) {
        this._paths = paths;
        this._logs = logs;
    }

    public boolean extract(boolean modified, boolean isTesting){
        _logs.write("Extract Json Details Start");
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(_paths.getDetailsFile()));
            _detailsMap = new HashMap<>();
            JSONObject jsonObject = (JSONObject) obj;
            for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                _detailsMap.put(key, jsonObject.get(key));
            }

            setVersionIfMissing(_detailsMap);

            setTimestamps(_detailsMap, isTesting);

            if(!hasAllEssentialKey(_detailsMap)) return false;

            if(!checkAllPropertyValid(_detailsMap)) return false;

            if(modified || _detailsMap.get(VERSION).equals("0.99")) addVersion(_detailsMap);

            writeBackJson();

            _logs.write("Json extraction completed successfully.");
            return true;
        } catch (IOException | ParseException e) {
            _logs.write("Details file cannot be found.");
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasAllEssentialKey(HashMap<String, Object> map){
        ArrayList<String> keysNeeded = new ArrayList<>();
        keysNeeded.add(MAX_PLAYERS);
        keysNeeded.add(MIN_PLAYERS);
        keysNeeded.add(NAME);
        keysNeeded.add(TEAM_MIN_PLAYERS);
        keysNeeded.add(TEAM_MAX_PLAYERS);
        keysNeeded.add(ABBR);
        keysNeeded.add(TEAM_COUNT);
        keysNeeded.add(VERSION);

        for(String key : keysNeeded){
            if(!map.containsKey(key)){
                _logs.write("Property " + key + " not found in details json.");
                return false;
            }
        }
        return true;
    }

    private void setTimestamps(HashMap<String, Object> map, boolean isTesting){

        if(isTesting){
            if(!map.containsKey("T" + CREATE_TIMESTAMP)){
                map.put("T" + CREATE_TIMESTAMP, getUnixTimestamp());
            }
            map.put(CREATE_TIMESTAMP, map.get("T" + CREATE_TIMESTAMP));
        }
        else{
            if(!map.containsKey("P" + CREATE_TIMESTAMP)){
                map.put("P" + CREATE_TIMESTAMP, getUnixTimestamp());
            }
            map.put(CREATE_TIMESTAMP, map.get("P" + CREATE_TIMESTAMP));
        }



        map.put(UPDATE_TIMESTAMP, getUnixTimestamp());
    }

    private void setVersionIfMissing(HashMap<String, Object> map){
        if(!map.containsKey(VERSION)){
            map.put(VERSION, "0.99");
        }

        String commonVersion = FileIO.read(_paths.getCommonVersionFile());
        if(commonVersion.equals("")) commonVersion = "1";
        map.put(COMMON_VERSION, commonVersion);
    }


    private boolean checkAllPropertyValid(HashMap<String, Object> map){
        ArrayList<String> mustBeIntKeys = new ArrayList<>();
        mustBeIntKeys.add(MAX_PLAYERS);
        mustBeIntKeys.add(MIN_PLAYERS);
        mustBeIntKeys.add(TEAM_MIN_PLAYERS);
        mustBeIntKeys.add(TEAM_MAX_PLAYERS);
        mustBeIntKeys.add(TEAM_COUNT);
        for(String key : mustBeIntKeys){
            if(!isInteger((String) map.get(key))){
                _logs.write("Property " + key + " must be integer.");
                return false;
            }
        }

        if(!isNumberWith2Decimals((String) map.get(VERSION))){
            _logs.write("Invalid version value.");
            return false;
        }

        return true;
    }

    public HashMap<String, Object> getDetailsMap() {
        return _detailsMap;
    }

    public void writeBackJson() throws IOException {
        JSONObject obj = new JSONObject();
        for(String key : _detailsMap.keySet()){
            if(!key.equals(COMMON_VERSION)){
                obj.put(key, _detailsMap.get(key));
            }
        }

        FileIO.write(_paths.getDetailsFile(), obj.toJSONString());
    }
//
//    public String strJoin(String[] aArr, String sSep) {
//        StringBuilder sbStr = new StringBuilder();
//        for (int i = 0, il = aArr.length; i < il; i++) {
//            if (i > 0)
//                sbStr.append(sSep);
//            sbStr.append(aArr[i]);
//        }
//        return sbStr.toString();
//    }
//

    public void addVersion(HashMap<String, Object> map){
        String version = (String) map.get(VERSION);
        double v = Double.valueOf(version);
        v += 0.01;
        version = String.format("%.2f", v);
        map.put(VERSION, version);
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
//
    public boolean isNumberWith2Decimals(String string) {
        return string.matches("^\\d+\\.\\d{2}$");
    }

    public String getAbbr(){
        return (String) _detailsMap.get(ABBR);
    }

    public void setIconUrl(String iconUrl){
        _detailsMap.put(ICON_URL, iconUrl);
    }

    public String getIconUrl(){
        return (String) _detailsMap.get(ICON_URL);
    }

    public void setGameUrl(String gameUrl){
        _detailsMap.put(GAME_URL, gameUrl);
    }

    public String getGameUrl(){
        return (String) _detailsMap.get(GAME_URL);
    }

    public void setGameSize(long size){
        _detailsMap.put(GAME_SIZE, size);
    }

    public long getGameSize(){
        return (long) _detailsMap.get(GAME_SIZE);
    }

    public void print(){
        for(String key : _detailsMap.keySet()){
            _logs.write(key + " = " + _detailsMap.get(key));
        }
    }

    private long getUnixTimestamp(){
        return System.currentTimeMillis() / 1000L;
    }

}
