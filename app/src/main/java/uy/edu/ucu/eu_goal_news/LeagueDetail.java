package uy.edu.ucu.eu_goal_news;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_detail);

        Bundle extras = getIntent().getExtras();

        if ( extras != null )
        {
            String leagueName = extras.getString( "leagueName" );
            String selectedLeagueUrl = extras.getString("selectedLeagueUrl");

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(item));
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
