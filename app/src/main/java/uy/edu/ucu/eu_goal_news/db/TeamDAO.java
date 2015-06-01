package uy.edu.ucu.eu_goal_news.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.Team;
import uy.edu.ucu.eu_goal_news.Model.TeamLeague;

/**
 * Datasource class
 */
public class TeamDAO {
    private TeamHelper dbHelper;
    private SQLiteDatabase database;
    /**
     * Gets a read/write SQLiteDatabase instance
     */
    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the SQLiteHelper instance to release resouces
     */
    public void close(){
        dbHelper.close();
    }

    /**
     * Constructor
     * @param context
     */
    public TeamDAO(Context context){
        dbHelper = new TeamHelper(context);
        open();
    }

    public Team createTeam(JSONObject teamJSONObject ){
        try{
            ContentValues values = new ContentValues(1);

            JSONObject link = teamJSONObject.getJSONObject("_links");
            values.put(TeamHelper.COLUMN_NAME, teamJSONObject.getString("name"));
            values.put(TeamHelper.COLUMN_CODE, teamJSONObject.getString("code"));
            values.put(TeamHelper.COLUMN_SHORTNAME, teamJSONObject.getString("shortName"));
            values.put(TeamHelper.COLUMN_SQUAD_MARKET_VALUE, teamJSONObject.getString("squadMarketValue"));
            values.put(TeamHelper.COLUMN_CREST_URL, teamJSONObject.getString("crestUrl"));
            values.put(TeamHelper.COLUMN_FIXTURE_URL, link.getJSONObject("fixtures").getString("href"));
            values.put(TeamHelper.COLUMN_PLAYERS_URL, link.getJSONObject("players").getString("href"));
            if(findByName(teamJSONObject.getString("name")) == null){
                long newRowId = database.insert(TeamHelper.TABLE_TEAM, null, values);
                return findByName(teamJSONObject.getString("name"));
            }
            return null;

        }catch(Exception e ){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Updates a todo in the database with the values received in the todo object
     * @param team
     * @return updated rows count
     */
    public int updateTeam(Team team){

        ContentValues values = new ContentValues();
        values.put(TeamHelper.COLUMN_NAME, team.getName());
        values.put(TeamHelper.COLUMN_CODE, team.getCode());
        values.put(TeamHelper.COLUMN_SHORTNAME, team.getShortName());
        values.put(TeamHelper.COLUMN_SQUAD_MARKET_VALUE, team.getSquadMarketValue());
        values.put(TeamHelper.COLUMN_CREST_URL, team.getCrestUrl());
        values.put(TeamHelper.COLUMN_FIXTURE_URL, team.getFixturesUrl());
        values.put(TeamHelper.COLUMN_PLAYERS_URL, team.getPlayersUrl());

        String selection = TeamHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = { team.getName()};
        return database.update(TeamHelper.TABLE_TEAM, values, selection, selectionArgs);
    }

    /**
     * Deletes a given todo object by it's Id
     * @param todo
     * @return deleted rows count
     */
    public int delete(Team todo ){
        String selection = TeamHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = { todo.getName() };
        return database.delete(TeamHelper.TABLE_TEAM, selection, selectionArgs);
    }

    /**
     * Gets all todos stored on the todos table
     * Executes a Select * from todos query
     * @return
     */
    public List<Team> getAllTeams(){

        // database.rawQuery("select * from todos", null);
        // You can execute raw SQL queries and get a Cursor object, not recommended though
        // you should always try to use methods interface, query(), insert() ..

        List<Team> todos = new ArrayList<>();
        Cursor cursor = database.query(TeamHelper.TABLE_TEAM, TeamHelper.ALL_COLUMNS_TEAM_TABLE, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            todos.add(cursorToTeam(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return todos;
    }

    /**
     * Finds a Team by it's id
     * @param name
     * @return todo object
     */
    public Team findByName(String name){

        String selection = TeamHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };
        Cursor cursor = database.query(TeamHelper.TABLE_TEAM, TeamHelper.ALL_COLUMNS_TEAM_TABLE, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        Team todo = null;
        if (!cursor.isAfterLast()) {
            todo = cursorToTeam(cursor);
        }

        cursor.close();
        return todo;
    }

    /**
     * Turns a cursor into a Team model object
     * @param cursor cursor with row data
     * @return todo object
     */
    private Team cursorToTeam(Cursor cursor) {
        Team todo = new Team();
        todo.setName(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_NAME)));
        todo.setCode(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_CODE)));
        todo.setShortName(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_SHORTNAME)));
        todo.setSquadMarketValue(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_SQUAD_MARKET_VALUE)));
        todo.setCrestUrl(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_CREST_URL)));
        todo.setFixturesUrl(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_FIXTURE_URL)));
        todo.setPlayersUrl(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_PLAYERS_URL)));
        return todo;

    }

}

