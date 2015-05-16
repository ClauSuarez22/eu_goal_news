package uy.edu.ucu.eu_goal_news.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by juliorima on 26/04/2015.
 */
public class Soccerseason {

    private String caption;
    private String league;
    private Integer year;
    private Integer numberOfTeams;
    private Integer numberOfGames;
    private Date lastUpdated;

    private String self;
    private String teams;
    private String fixtures;
    private String leagueTable;

    public Soccerseason(){
        this.caption = null;
    }

    public Soccerseason(JSONObject object) throws JSONException {
        this.caption = object.getString("caption");
        this.league = object.getString("league");
        this.year = object.getInt("year");
        this.numberOfTeams = object.getInt("numberOfTeams");
        this.numberOfGames = object.getInt("numberOfGames");
        //this.lastUpdated = (Date) object.get("lastUpdated");

        JSONObject links = object.getJSONObject("_links");
        this.self = links.getJSONObject("self").getString("href");
        this.teams = links.getJSONObject("teams").getString("href");
        this.fixtures = links.getJSONObject("fixtures").getString("href");
        this.leagueTable = links.getJSONObject("leagueTable").getString("href");

    }

    public String getCaption() {
        return caption;
    }

    public String getLeague() {
        return league;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getNumberOfTeams() {
        return numberOfTeams;
    }

    public Integer getNumberOfGames() {
        return numberOfGames;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getSelf() {
        return self;
    }

    public String getTeams() {
        return teams;
    }

    public String getFixtures() {
        return fixtures;
    }

    public String getLeagueTable() {
        return leagueTable;
    }
}
