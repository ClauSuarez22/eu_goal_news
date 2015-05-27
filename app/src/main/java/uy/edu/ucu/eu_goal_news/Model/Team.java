package uy.edu.ucu.eu_goal_news.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by juliorima on 28/04/2015.
 */
public class Team {

    private String name;
    private String code;
    private String shortName;
    private String squadMarketValue;
    private String crestUrl;
    private String fixturesUrl;
    private String playersUrl;

    public Team(){}

    public Team(JSONObject object) throws JSONException{
        this.name = object.getString("name");
        this.code = object.getString("code");
        this.shortName = object.getString("shortName");
        this.squadMarketValue = object.getString("squadMarketValue");
        this.crestUrl = object.getString("crestUrl");

        JSONObject link = object.getJSONObject("_links");
        this.fixturesUrl = link.getJSONObject("fixtures").getString("href");
        this.playersUrl = link.getJSONObject("players").getString("href");
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getSquadMarketValue() {
        return squadMarketValue;
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    public String getFixturesUrl() {
        return fixturesUrl;
    }

    public String getPlayersUrl() {
        return playersUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setSquadMarketValue(String squadMarketValue) {
        this.squadMarketValue = squadMarketValue;
    }

    public void setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
    }

    public void setFixturesUrl(String fixturesUrl) {
        this.fixturesUrl = fixturesUrl;
    }

    public void setPlayersUrl(String playersUrl) {
        this.playersUrl = playersUrl;
    }
}
