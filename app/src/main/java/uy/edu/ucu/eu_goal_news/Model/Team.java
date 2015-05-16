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

    public Team(JSONObject object) throws JSONException{
        this.name = object.getString("name");
        this.code = object.getString("code");
        this.shortName = object.getString("shortName");
        this.squadMarketValue = object.getString("squadMarketValue");
        this.crestUrl = object.getString("crestUrl");
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
}
