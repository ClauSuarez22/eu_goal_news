package uy.edu.ucu.eu_goal_news;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.TeamLeague;


public class LeagueDetail extends ListActivity {

    String leagueName;
    int leagueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_detail);

        Bundle extras = getIntent().getExtras();

        if ( extras != null )
        {
            String leagueName = extras.getString( "leagueName" );
            this.leagueName = leagueName;
            String selectedLeagueUrl = extras.getString("selectedLeagueUrl");
            String[] selectedLeagueUrlList = selectedLeagueUrl.split("/");
            int selectedLeagueUrlListLength = selectedLeagueUrlList.length;
            this.leagueId = Integer.parseInt( selectedLeagueUrlList[ selectedLeagueUrlListLength - 2 ] );

            getActionBar().setTitle( leagueName );

            new GetLeagueAsyncTask( LeagueDetail.this )
                    .execute( selectedLeagueUrl );
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league_detail, menu);

        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        int favouriteLeague = sharedPreferences.getInt( "favourite_league", -1);
        MenuItem item = menu.findItem( R.id.action_add_favourite );

        if ( favouriteLeague == this.leagueId )
        {
            item.getIcon().setAlpha(255);
        }
        else
        {
            item.getIcon().setAlpha(30);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Intent homeIntent = new Intent(this, MainActivity.class);
                //startActivity(homeIntent);
                finish();
                break;
            case R.id.action_add_favourite:
                SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
                int favouriteLeague = sharedPreferences.getInt( "favourite_league", -1);
                SharedPreferences.Editor editor  = sharedPreferences.edit();

                if ( favouriteLeague == -1 || favouriteLeague != this.leagueId )
                {
                    item.getIcon().setAlpha(255);
                    editor.putInt("favourite_league", this.leagueId);
                    editor.commit();
                    Toast.makeText(LeagueDetail.this, this.leagueName + " is your favourite league!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    item.getIcon().setAlpha(30);
                    editor.remove( "favourite_league" );
                    editor.commit();
                    Toast.makeText(LeagueDetail.this, this.leagueName + " is not your favourite league anymore!", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }

        return true;
    }

    // Async Task for Team Leagues
    private class GetLeagueAsyncTask extends AsyncTask<String, Integer, ArrayList<TeamLeague>> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetLeagueAsyncTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute(){

            mProgressDialog = ProgressDialog.show(mContext, null,mContext.getString(R.string.loading), true, false);
        }

        @Override
        protected ArrayList<TeamLeague> doInBackground(String... params) {

            HttpURLConnection connection = null;


            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Auth-Token","7641996b673943d0a712f2f4493c7bbd");
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();

                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }

                JSONArray jsonArray = new JSONObject(sb.toString()).getJSONArray("standing");

                ArrayList<TeamLeague> teamLeaguesList = new ArrayList<TeamLeague>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    teamLeaguesList.add(new TeamLeague(item));
                }
                return teamLeaguesList;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if(connection != null)  connection.disconnect();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<TeamLeague> leagues){

            // initialize adapter with teams leagues
            if(leagues != null) {
                setListAdapter(new LeagueDetailAdapter(mContext, R.layout.activity_league_detail, leagues));
            }else{
                Toast.makeText(mContext, "Error downloading teams details", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();

        }

    }
}
