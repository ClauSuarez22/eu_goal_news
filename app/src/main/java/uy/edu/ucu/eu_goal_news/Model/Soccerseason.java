package uy.edu.ucu.eu_goal_news.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Soccerseason implements IListViewType {

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

    private Integer leagueId;

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

        int index = self.lastIndexOf("/");
        this.leagueId = Integer.parseInt(self.substring(index+1));

    }

    public int getViewType(){
        return SECTION;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public Integer getIntegerLeagueId() {
        return leagueId;
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
