package com.mygdx.potatoandtomato.models;

import com.badlogic.gdx.Gdx;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class Game {

    String name, minPlayers, maxPlayers, teamMinPlayers, teamMaxPlayers, teamCount,
            iconUrl, gameUrl, assetUrl, abbr, description, version, clientVersion;
    ArrayList<String> screenShots;


    public Game() {
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getTeamMinPlayers() {
        return teamMinPlayers;
    }

    public void setTeamMinPlayers(String teamMinPlayers) {
        this.teamMinPlayers = teamMinPlayers;
    }

    public String getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(String teamCount) {
        this.teamCount = teamCount;
    }

    public String getTeamMaxPlayers() {
        return teamMaxPlayers;
    }

    public void setTeamMaxPlayers(String teamMaxPlayers) {
        this.teamMaxPlayers = teamMaxPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(String minPlayers) {
        this.minPlayers = minPlayers;
    }

    public String getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(String maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public String getAssetUrl() {
        return assetUrl;
    }

    public void setAssetUrl(String assetUrl) {
        this.assetUrl = assetUrl;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getScreenShots() {
        return screenShots;
    }

    public void setScreenShots(ArrayList<String> screenShots) {
        this.screenShots = screenShots;
    }

    @JsonIgnore
    public String getFullBasePath(){
        return Gdx.files.local(getBasePath()).file().getAbsolutePath();
    }

    @JsonIgnore
    public String getBasePath(){
        return "pt_downloads/" + this.getAbbr();
    }

    @JsonIgnore
    public String getLocalJarPath() {
        return getBasePath() + "/game.jar";
    }

    @JsonIgnore
    public String getFullLocalJarPath() {
        return getFullBasePath() + "/game.jar";
    }

    @JsonIgnore
    public String getLocalAssetsPath() {
        return getBasePath() + "/assets.zip";
    }


}
