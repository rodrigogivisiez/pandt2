package com.potatoandtomato.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class GameLibCoordinator {

    public String jarPath;
    public String assetsPath;
    public String basePath;
    public GameEntranceAbstract gameEntrance;
    public ArrayList<Team> teams;

    public GameLibCoordinator(String jarPath, String assetsPath,
                                    String basePath, ArrayList<Team> teams) {
        this.jarPath = jarPath;
        this.assetsPath = assetsPath;
        this.basePath = basePath;
        this.teams = teams;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    public GameEntranceAbstract getGameEntrance() {
        return gameEntrance;
    }

    public void setGameEntrance(GameEntranceAbstract gameEntrance) {
        this.gameEntrance = gameEntrance;
    }

    public FileHandle getFileH(String path){
        if(Gdx.files.internal(path).exists()){
            return Gdx.files.internal(path);
        }
        else{
            return Gdx.files.local(basePath + "/" + path);
        }
    }

}
