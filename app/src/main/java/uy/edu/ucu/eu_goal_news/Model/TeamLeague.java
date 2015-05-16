package uy.edu.ucu.eu_goal_news.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by csuarez on 16/05/15.
 */
public class TeamLeague {

    private String teamName;
    private Integer position;
    private Integer playedGames;
    private Integer points;
    private Integer goals;
    private Integer goalsAgainst;
    private Integer goalDifference;

    public TeamLeague(JSONObject object) throws JSONException {
        this.teamName = object.getString("teamName");
        this.position = object.getInt("position");
        this.playedGames = object.getInt("playedGames");
        this.points = object.getInt("points");
        this.goals = object.getInt("goals");
        this.goalsAgainst = object.getInt("goalsAgainst");
        this.goalDifference = object.getInt("goalDifference");
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(Integer playedGames) {
        this.playedGames = playedGames;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer poIntegers) {
        this.points = poIntegers;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public Integer getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(Integer goalDifference) {
        this.goalDifference = goalDifference;
    }
}
