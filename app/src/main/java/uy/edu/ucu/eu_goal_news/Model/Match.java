package uy.edu.ucu.eu_goal_news.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * Created by juliorima on 28/04/2015.
 */
public class Match {

    private Date date;
    private String status;
    private Integer matchday;
    private String homeTeamName;
    private String awayTeamName;

    private Integer goalsHomeTeam;
    private Integer goalsAwayTeam;

    private String selfUrl;
    private String soccerseasonUrl;
    private String homeTeamUrl;
    private String awayTeamUrl;

    private Integer matchLeagueId;
    private Boolean showLeagueName;

    public Match(JSONObject object) throws JSONException {
        this.status = object.getString("status");
        this.matchday = object.getInt("matchday");
        this.homeTeamName = object.getString("homeTeamName");
        this.awayTeamName = object.getString("awayTeamName");

        JSONObject result = object.getJSONObject("result");
        this.goalsHomeTeam = result.getInt("goalsHomeTeam");
        this.goalsAwayTeam = result.getInt("goalsAwayTeam");

        JSONObject links = object.getJSONObject("_links");
        this.selfUrl = links.getJSONObject("self").getString("href");
        this.soccerseasonUrl = links.getJSONObject("soccerseason").getString("href");
        this.homeTeamUrl = links.getJSONObject("homeTeam").getString("href");
        this.awayTeamUrl = links.getJSONObject("awayTeam").getString("href");
        try{
            String strDate = object.getString("date");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            this.date = dateFormat.parse(strDate);
        }catch (ParseException e){
            e.printStackTrace();
        }

        int index = soccerseasonUrl.lastIndexOf("/");
        this.matchLeagueId = Integer.parseInt(soccerseasonUrl.substring(index+1));
        this.showLeagueName = false;
    }

    public Boolean getShowLeagueName() {
        return showLeagueName;
    }

    public void setShowLeagueName(Boolean showLeagueName) {
        this.showLeagueName = showLeagueName;
    }

    public Integer getMatchLeagueId() {
        return matchLeagueId;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Integer getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public Integer getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public String getSoccerseasonUrl() {
        return soccerseasonUrl;
    }

    public String getHomeTeamUrl() {
        return homeTeamUrl;
    }

    public String getAwayTeamUrl() {
        return awayTeamUrl;
    }
}

