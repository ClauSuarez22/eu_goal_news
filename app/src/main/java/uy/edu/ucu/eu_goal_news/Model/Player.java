package uy.edu.ucu.eu_goal_news.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by juliorima on 28/04/2015.
 */
public class Player {

    private Integer id;
    private String name;
    private String position;
    private Integer jerseyNumber;
    private String dateOfBirth;
    private String nationality;
    private String contractUntil;
    private String marketValue;

    public Player(JSONObject object) throws JSONException, ParseException {
        this.id = object.getInt("id");
        this.name = object.getString("name");
        this.position = object.getString("position");
        this.jerseyNumber = object.getInt("jerseyNumber");
        this.dateOfBirth = object.getString("dateOfBirth");
        this.nationality = object.getString("nationality");
        this.contractUntil = object.getString("contractUntil");
        this.marketValue = object.getString("marketValue");
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public String getContractUntil() {
        return contractUntil;
    }

    public String getMarketValue() {
        return marketValue;
    }
}
