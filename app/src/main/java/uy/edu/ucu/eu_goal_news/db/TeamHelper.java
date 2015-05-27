package uy.edu.ucu.eu_goal_news.db;

/**
 * Created by csuarez on 27/05/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TeamHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "team.db";

    public TeamHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TABLE_NAME = "team";
    public static final String COLUMN_FIXTURE_URL = "fixtures_url";
    public static final String COLUMN_PLAYERS_URL = "players_url";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_SHORTNAME = "shortname";
    public static final String COLUMN_SQUAD_MARKET_VALUE = "squad_market_value";
    public static final String COLUMN_CREST_URL = "crest_url";


    public static final String[] ALL_COLUMNS = {COLUMN_FIXTURE_URL, COLUMN_PLAYERS_URL,
            COLUMN_NAME, COLUMN_CODE, COLUMN_SHORTNAME, COLUMN_SQUAD_MARKET_VALUE, COLUMN_CREST_URL};

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "("
            + COLUMN_FIXTURE_URL + " text, "
            + COLUMN_PLAYERS_URL + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_CODE + " text, "
            + COLUMN_SHORTNAME + " text, "
            + COLUMN_SQUAD_MARKET_VALUE + " text, "
            + COLUMN_CREST_URL + " text, PRIMARY KEY(" + COLUMN_CODE + "))";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}


