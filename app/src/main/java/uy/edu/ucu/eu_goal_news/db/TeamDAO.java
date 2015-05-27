package uy.edu.ucu.eu_goal_news.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.Team;

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

    /**
     * Inserts a new todo in the todos database table
     * @param name
     * @param code
     * @param shortName
     * @param squadMarketValue
     * @param crestUrl
     * @param fixturesUrl
     * @param playersUrl
     * @return created todo object
     */
    public Team create(String name, String code, String shortName, String squadMarketValue,
                       String crestUrl, String fixturesUrl, String playersUrl){

        ContentValues values = new ContentValues(1);
        values.put(TeamHelper.COLUMN_NAME, name);
        values.put(TeamHelper.COLUMN_CODE, code);
        values.put(TeamHelper.COLUMN_SHORTNAME, shortName);
        values.put(TeamHelper.COLUMN_SQUAD_MARKET_VALUE, squadMarketValue);
        values.put(TeamHelper.COLUMN_CREST_URL, crestUrl);
        values.put(TeamHelper.COLUMN_FIXTURE_URL, fixturesUrl);
        values.put(TeamHelper.COLUMN_PLAYERS_URL, playersUrl);

        long newRowId = database.insert(TeamHelper.TABLE_NAME, null, values);
        return findByCode( code );
    }

    /**
     * Updates a todo in the database with the values received in the todo object
     * @param team
     * @return updated rows count
     */
    public int update(Team team){

        ContentValues values = new ContentValues();
        values.put(TeamHelper.COLUMN_NAME, team.getName());
        values.put(TeamHelper.COLUMN_CODE, team.getCode());
        values.put(TeamHelper.COLUMN_SHORTNAME, team.getShortName());
        values.put(TeamHelper.COLUMN_SQUAD_MARKET_VALUE, team.getSquadMarketValue());
        values.put(TeamHelper.COLUMN_CREST_URL, team.getCrestUrl());
        values.put(TeamHelper.COLUMN_FIXTURE_URL, team.getFixturesUrl());
        values.put(TeamHelper.COLUMN_PLAYERS_URL, team.getPlayersUrl());

        String selection = TeamHelper.COLUMN_CODE + " = ?";
        String[] selectionArgs = {String.valueOf(team.getCode())};
        return database.update(TeamHelper.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Deletes a given todo object by it's Id
     * @param todo
     * @return deleted rows count
     */
    public int delete(Team todo){
        String selection = TeamHelper.COLUMN_CODE + " = ?";
        String[] selectionArgs = {String.valueOf(todo.getCode())};
        return database.delete(TeamHelper.TABLE_NAME, selection, selectionArgs);
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
        Cursor cursor = database.query(TeamHelper.TABLE_NAME, TeamHelper.ALL_COLUMNS, null, null, null, null, null);

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
        String[] selectionArgs = {name};
        Cursor cursor = database.query(TeamHelper.TABLE_NAME, TeamHelper.ALL_COLUMNS, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        Team todo = null;
        if (!cursor.isAfterLast()) {
            todo = cursorToTeam(cursor);
        }

        cursor.close();
        return todo;
    }

    /**
     * Finds a Team by it's id
     * @param shortName
     * @return todo object
     */
    public Team findByShortName(String shortName){

        String selection = TeamHelper.COLUMN_SHORTNAME + " = ?";
        String[] selectionArgs = {shortName};
        Cursor cursor = database.query(TeamHelper.TABLE_NAME, TeamHelper.ALL_COLUMNS, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        Team todo = null;
        if (!cursor.isAfterLast()) {
            todo = cursorToTeam(cursor);
        }

        cursor.close();
        return todo;
    }

    /**
     * Finds a Team by it's id
     * @param code
     * @return todo object
     */
    public Team findByCode(String code){

        String selection = TeamHelper.COLUMN_CODE + " = ?";
        String[] selectionArgs = {code};
        Cursor cursor = database.query(TeamHelper.TABLE_NAME, TeamHelper.ALL_COLUMNS, selection, selectionArgs, null, null, null);

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
        todo.setCrestUrl(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_CREST_URL)));
        todo.setShortName(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_SHORTNAME)));
        todo.setSquadMarketValue(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_SQUAD_MARKET_VALUE)));
        todo.setFixturesUrl(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_FIXTURE_URL)));
        todo.setPlayersUrl(cursor.getString(cursor.getColumnIndex(TeamHelper.COLUMN_PLAYERS_URL)));
        return todo;

    }

}

