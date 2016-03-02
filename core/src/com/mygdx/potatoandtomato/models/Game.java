package com.mygdx.potatoandtomato.models;

import com.badlogic.gdx.Gdx;
import com.mygdx.potatoandtomato.helpers.utils.DateTimes;
import com.mygdx.potatoandtomato.helpers.utils.Strings;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 13/12/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    String name, minPlayers, maxPlayers, teamMinPlayers, teamMaxPlayers, teamCount,
            iconUrl, gameUrl, abbr, description, version, commonVersion;
    long createTimestamp, lastUpdatedTimestamp, gameSize;
    boolean mustFairTeam;


    public Game() {
    }

    public boolean getMustFairTeam() {
        return mustFairTeam;
    }

    public void setMustFairTeam(boolean mustFairTeam) {
        this.mustFairTeam = mustFairTeam;
    }

    public String getCommonVersion() {
        return commonVersion;
    }

    public void setCommonVersion(String commonVersion) {
        this.commonVersion = commonVersion;
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

    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public String getLastUpdatedAgo(){
        return DateTimes.calculateTimeAgo(lastUpdatedTimestamp);
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public long getGameSize() {
        return gameSize;
    }

    public void setGameSize(long gameSize) {
        this.gameSize = gameSize;
    }

    public String getGameSizeInMb() {
        return Strings.byteToMb(gameSize);
    }


    @JsonIgnore
    public String getFullBasePath(){
        return Gdx.files.local(getBasePath()).file().getAbsolutePath();
    }

    @JsonIgnore
    public String getBasePath(){
        return ".pt_downloads/" + this.getAbbr();
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
