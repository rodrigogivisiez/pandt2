package com.ptuploader.process;

import com.ptuploader.models.FileData;
import com.ptuploader.process.Paths;
import com.ptuploader.utils.FileIO;
import com.ptuploader.utils.Logs;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Details {

    private Paths _paths;
    private Logs _logs;
    private FireDB _fireDB;
    private AssetsHelper assetsHelper;
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
    public final String ICON_MODIFIED = "iconModified";
    private final String GAME_SIZE = "gameSize";
    private final String UPDATE_TIMESTAMP = "lastUpdatedTimestamp";
    private final String CREATE_TIMESTAMP = "createTimestamp";
    public final String GAME_FILES = "gameFiles";
    private boolean checkingCloudComplete = false;
    private boolean checkingIconComplete = false;
    private boolean iconModified = false;
    private HashMap<String, FileData> cloudDatas, toUploadFiles, toDeleteFromCloudFiles;


    public Details(Paths paths, Logs logs, FireDB fireDB, AssetsHelper assetsHelper) {
        this._paths = paths;
        this._logs = logs;
        this._fireDB = fireDB;
        this.assetsHelper = assetsHelper;
    }

    public boolean extract(boolean isTesting, HashMap<String, FileData> currentFileDatas, String size){
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


            checkIconLastModified();
            while (!checkingIconComplete){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            checkCloudGameFiles();
            while (!checkingCloudComplete){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            processedGameDatas(currentFileDatas);

            if(toUploadFiles.size() > 0 || _detailsMap.get(VERSION).equals("0.99")) addVersion(_detailsMap);

            writeBackJson();

            _detailsMap.put(GAME_SIZE, size);

            _logs.write("Json extraction completed successfully.");
            return true;
        } catch (IOException | ParseException e) {
            _logs.write("Details file cannot be found.");
            e.printStackTrace();
        }
        return false;
    }

    public void setGameDataFilesJson(HashMap<String, FileData> currentFileDatas){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            _detailsMap.put(GAME_FILES, objectMapper.writeValueAsString(currentFileDatas));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        _detailsMap.put(ICON_MODIFIED, String.valueOf(_paths.getIconFile().lastModified()));
        try {
            writeBackJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIconUrl(){
        return (String) _detailsMap.get(ICON_URL);
    }

    public void print(){
        for(String key : _detailsMap.keySet()){
            _logs.write(key + " = " + _detailsMap.get(key));
        }
    }

    private long getUnixTimestamp(){
        return System.currentTimeMillis() / 1000L;
    }

    public void checkIconLastModified(){
        _logs.write("Checking icon last modified from cloud.");
        _fireDB.getIconLastModified(this);
    }

    public void iconLastModifiedReceived(String lastModified){
        String currentLastModified = String.valueOf(_paths.getIconFile().lastModified());
        if(currentLastModified.equals(lastModified)){
            iconModified = false;
        }
        else{
            iconModified = true;
        }

        checkingIconComplete = true;
    }

    public void checkCloudGameFiles(){
        _logs.write("Getting cloud game file datas.");
        _fireDB.getFilesData(this);
    }

    public void cloudGameFilesRetrieved(String data){
        cloudDatas = new HashMap<String, FileData>();
        HashMap<String, LinkedHashMap<String, Object>> dbData = new HashMap();
        ObjectMapper objectMapper = new ObjectMapper();
        if(data != null && !data.equals("")){
            try {
                dbData = objectMapper.readValue(data, HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(Map.Entry<String, LinkedHashMap<String, Object>> entry : dbData.entrySet()){
            String fileName = entry.getKey();
            LinkedHashMap<String, Object> fileDataMap = entry.getValue();

            String size = fileDataMap.get("size").toString();

            cloudDatas.put(fileName, new FileData(fileDataMap.get("modifiedAt").toString(),
                    fileDataMap.get("url").toString(), "", Long.valueOf(size)));
        }

        checkingCloudComplete = true;
    }

    public void processedGameDatas(HashMap<String, FileData> currentFileDatas){
        toUploadFiles = new HashMap();
        toDeleteFromCloudFiles = new HashMap();

        File jarFile = _paths.getJarFile();
        File afterDxFile = _paths.getAfterDxGameJar();

        currentFileDatas.put(afterDxFile.getName(), new FileData(String.valueOf(jarFile.lastModified()), "",
                afterDxFile.getAbsolutePath(), afterDxFile.length()));

        for (Map.Entry<String, FileData> entry : currentFileDatas.entrySet()) {
            String fileName = entry.getKey();
            FileData fileData = entry.getValue();

            if(cloudDatas.containsKey(fileName)){
                FileData cloudFileData = cloudDatas.get(fileName);
                if(cloudFileData.getModifiedAt().equals(fileData.getModifiedAt())){
                    fileData.setUrl(cloudFileData.getUrl());
                    continue;
                }
            }

            toUploadFiles.put(fileName, fileData);
            _logs.write("Pending upload: " + fileName);
        }

        for (Map.Entry<String, FileData> entry : cloudDatas.entrySet()) {
            String cloudFileName = entry.getKey();
            FileData cloudFileData = entry.getValue();

            if(!currentFileDatas.containsKey(cloudFileName)){
                toDeleteFromCloudFiles.put(cloudFileName, cloudFileData);
                _logs.write("Pending delete: " + cloudFileName);
            }
        }


        _logs.write("Total files to upload: " + toUploadFiles.size());
        _logs.write("Total files to delete: " + toDeleteFromCloudFiles.size());
    }


    public boolean isCheckingCloudComplete() {
        return checkingCloudComplete;
    }

    public HashMap<String, FileData> getToUploadFiles() {
        return toUploadFiles;
    }

    public HashMap<String, FileData> getToDeleteFromCloudFiles() {
        return toDeleteFromCloudFiles;
    }

    public boolean isIconModified() {
        return iconModified;
    }

    public void removeNonNecessaryFieldsForGameSimple(){
        _detailsMap.remove(GAME_FILES);
    }

}
