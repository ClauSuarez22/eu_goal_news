package uy.edu.ucu.eu_goal_news.Model;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by albarenguenatalia on 22/05/2015.
 */
public class MatchDetails {

    private int matchday;
    private String status;
    private Date startDate;
    private String homeTeamName;
    private String awayTeamName;
    private int homeTeamGoals;
    private int awayTeamGoals;
    private String homeTeamUrl;
    private String awayTeamUrl;
    private String selfUrl;

    public String getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(String selfUrl) {
        this.selfUrl = selfUrl;
    }

    public String getSoccerseasonUrl() {
        return soccerseasonUrl;
    }

    public void setSoccerseasonUrl(String soccerseasonUrl) {
        this.soccerseasonUrl = soccerseasonUrl;
    }

    private String soccerseasonUrl;
    private Team homeTeam;
    private Team awayTeam;
    private int homeTeamWins;
    private int awayTeamWins;
    private int draws;
    private List<Match> previousMatches;

    public MatchDetails(){}

    public MatchDetails(JSONObject jsonResponse) throws JSONException {

        JSONObject fixture = jsonResponse.getJSONObject("fixture");
        JSONObject head2head = jsonResponse.getJSONObject("head2head");
        JSONObject result = fixture.getJSONObject("result");
        JSONObject links = fixture.getJSONObject("_links");

        this.matchday = fixture.getInt("matchday");
        this.status = fixture.getString("status");
        this.homeTeamName = fixture.getString("homeTeamName");
        this.awayTeamName = fixture.getString("awayTeamName");
        try{
            String strDate = fixture.getString("date");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            this.startDate = dateFormat.parse(strDate);
        }catch (ParseException e){
            e.printStackTrace();
        }

        this.homeTeamGoals = result.getInt("goalsHomeTeam");
        this.awayTeamGoals = result.getInt("goalsAwayTeam");
        this.homeTeamUrl = links.getJSONObject("homeTeam").getString("href");
        this.awayTeamUrl = links.getJSONObject("awayTeam").getString("href");
        this.selfUrl = links.getJSONObject("self").getString("href");
        this.soccerseasonUrl = links.getJSONObject("soccerseason").getString("href");

        this.homeTeamWins = head2head.getInt("homeTeamWins");
        this.awayTeamWins = head2head.getInt("awayTeamWins");
        this.draws = head2head.getInt("draws");
        this.parsePreviousMatches(head2head.getJSONArray("fixtures"));

    }

    private void parsePreviousMatches(JSONArray arrayPreviousMatches){
        this.previousMatches = new ArrayList<Match>();
        for (int i = 0; i < arrayPreviousMatches.length(); i++) {
            JSONObject item = null;
            try {
                item = arrayPreviousMatches.getJSONObject(i);
                this.previousMatches.add(new Match(item));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStartDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(this.startDate);
    };

    public String getStartTime(){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(this.startDate);
    };

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getMatchday() {
        return matchday;
    }

    public void setMatchday(int matchday) {
        this.matchday = matchday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public void setHomeTeamGoals(int homeTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
    }

    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public void setAwayTeamGoals(int awayTeamGoals) {
        this.awayTeamGoals = awayTeamGoals;
    }

    public String getHomeTeamUrl() {
        return homeTeamUrl;
    }

    public void setHomeTeamUrl(String homeTeamUrl) {
        this.homeTeamUrl = homeTeamUrl;
    }

    public String getAwayTeamUrl() {
        return awayTeamUrl;
    }

    public void setAwayTeamUrl(String awayTeamUrl) {
        this.awayTeamUrl = awayTeamUrl;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getHomeTeamWins() {
        return homeTeamWins;
    }

    public void setHomeTeamWins(int homeTeamWins) {
        this.homeTeamWins = homeTeamWins;
    }

    public int getAwayTeamWins() {
        return awayTeamWins;
    }

    public void setAwayTeamWins(int awayTeamWins) {
        this.awayTeamWins = awayTeamWins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public List<Match> getPreviousMatches() {
        return previousMatches;
    }

    public void setPreviousMatches(List<Match> previousMatches) {
        this.previousMatches = previousMatches;
    }


}
