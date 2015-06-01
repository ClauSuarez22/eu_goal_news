package uy.edu.ucu.eu_goal_news;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.MatchDetails;
import uy.edu.ucu.eu_goal_news.Model.Team;
import uy.edu.ucu.eu_goal_news.Model.TeamLeague;
import uy.edu.ucu.eu_goal_news.db.TeamDAO;

public class MatchDetailsActivity extends Activity {
    private static final String URL_TO_SHARE = "http://www.eu_goal_news.apk";
    private ShareActionProvider mShareActionProvider;
    private TextView mMatchday;
    private TextView mMatchStatus;
    private TextView mMatchDate;
    private TextView mMatchTime;
    private TextView mHomeTeamName;
    private TextView mAwayTeamName;
    private TextView mHomeTeamGoals;
    private TextView mAwayTeamGoals;
    private TextView mHomeTeamWins;
    private TextView mAwayTeamWins;
    private TextView mH2HDraws;
    private TextView mH2HHomeTeamName;
    private TextView mH2HAwayTeamName;
    private ImageView mAwayTeamImg;
    private ImageView mHomeTeamImg;

    private ListView mPreviousMatchesView;
    private List<Match> previousMatches;
    private MatchDetails matchData;
    private Team homeTeam;
    private Team awayTeam;
    private HashMap<String, TeamLeague> mLeagueTableHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);
        Bundle extras = getIntent().getExtras();
        if ( extras != null )
        {
            String leagueName = extras.getString( "leagueName" );
            String selectedMatchDetailsUrl = extras.getString("selectedMatchUrl");
            getActionBar().setTitle( leagueName );
            mMatchday = (TextView) findViewById(R.id.matchday);
            mMatchStatus = (TextView) findViewById(R.id.match_status);
            mMatchDate = (TextView) findViewById(R.id.match_date);
            mMatchTime = (TextView) findViewById(R.id.match_time);
            mHomeTeamName = (TextView) findViewById(R.id.home_team_name);
            mAwayTeamName = (TextView) findViewById(R.id.away_team_name);
            mHomeTeamGoals = (TextView) findViewById(R.id.home_team_goals);
            mAwayTeamGoals = (TextView) findViewById(R.id.away_team_goals);
            mHomeTeamWins = (TextView) findViewById(R.id.h2h_home_team_wins);
            mAwayTeamWins = (TextView) findViewById(R.id.h2h_away_team_wins);
            mH2HHomeTeamName = (TextView) findViewById(R.id.h2h_home_team_name);
            mH2HAwayTeamName = (TextView) findViewById(R.id.h2h_away_team_name);
            mH2HDraws = (TextView) findViewById(R.id.h2h_draws);
            mPreviousMatchesView = (ListView) findViewById(R.id.h2h_previous_matches);
            mHomeTeamImg = (ImageView) findViewById(R.id.home_team_img);
            mAwayTeamImg = (ImageView) findViewById(R.id.away_team_img);

            new GetMatchDetailsAsyncTask( MatchDetailsActivity.this )
                    .execute(selectedMatchDetailsUrl);
        }else{}

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                //Intent homeIntent = new Intent(this, MainActivity.class);
                //startActivity(homeIntent);
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    private class GetMatchDetailsAsyncTask extends AsyncTask<String, Integer, MatchDetails> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetMatchDetailsAsyncTask(Context context){ this.mContext = context; }

        @Override
        public void onPreExecute(){
            mProgressDialog = ProgressDialog.show(MatchDetailsActivity.this, null, "Loading...", true, false);
        }

        @Override
        protected MatchDetails doInBackground(String... params) {

            HttpURLConnection connection = null;
            MatchDetails matchDetails = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Auth-Token","7641996b673943d0a712f2f4493c7bbd");
                connection.setConnectTimeout(3000);
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }

                String response = sb.toString();
                Log.d(MatchDetailsActivity.class.getSimpleName(), response);
                JSONObject jsonResponse = new JSONObject(response);
                if(jsonResponse != null){
                    matchDetails = new MatchDetails(jsonResponse);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(connection != null) connection.disconnect();
            }

            return matchDetails;
        }

        @Override
        public void onPostExecute(final MatchDetails matchDetails){
            if(matchDetails != null) {
                matchData = matchDetails;
                //allow sharing via social network
                String message =  matchData.getStartDate() + "\n"
                        + matchData.getStartTime() + "\n"
                        +  matchData.getHomeTeamName() + "\tvs\t" + matchData.getAwayTeamName() + "\n"
                        +  matchData.getHomeTeamGoals() + "\t-\t" + matchData.getAwayTeamGoals() + "\n"
                        +  matchData.getStatus() + "\n"
                        +  URL_TO_SHARE;
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                mShareActionProvider.setShareIntent(shareIntent);

                previousMatches = matchData.getPreviousMatches();
                mPreviousMatchesView.setAdapter(new MatchListAdapter());

                mMatchday.setText("" + matchData.getMatchday());
                mMatchStatus.setText("" + matchData.getStatus());
                mMatchDate.setText(matchData.getStartDate());
                mMatchTime.setText(matchData.getStartTime());
                mHomeTeamName.setText(matchData.getHomeTeamName());
                mAwayTeamName.setText(matchData.getAwayTeamName());
                mH2HHomeTeamName.setText(matchData.getHomeTeamName());
                mH2HAwayTeamName.setText(matchData.getAwayTeamName());
                mHomeTeamGoals.setText("" + matchData.getHomeTeamGoals());
                mAwayTeamGoals.setText("" + matchData.getAwayTeamGoals());
                mHomeTeamWins.setText("" + matchData.getHomeTeamWins());
                mAwayTeamWins.setText("" + matchData.getAwayTeamWins());
                mH2HDraws.setText("" + matchData.getDraws());

                //bring data of each team stored in local DB
                TeamDAO mTeamDAO = new TeamDAO(MatchDetailsActivity.this);
                mTeamDAO.open();
                homeTeam = mTeamDAO.findByName( matchData.getHomeTeamName() );
                awayTeam = mTeamDAO.findByName( matchData.getAwayTeamName() );
                mTeamDAO.close();
                /*
                   Only in case that is the first time the app is executed
                   and it was not enough time to load all the teams
                */
                if(homeTeam != null){
                    new GetSVGAsyncTask( MatchDetailsActivity.this, mHomeTeamImg )
                            .execute(homeTeam.getCrestUrl());
                    mHomeTeamName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamName", homeTeam.getName());
                        intent.putExtra("squadMarketValue", homeTeam.getSquadMarketValue());
                        intent.putExtra("playersUrl", homeTeam.getPlayersUrl());
                        intent.putExtra("crestUrl", homeTeam.getCrestUrl());
                        intent.putExtra("teamLeagueUrl", matchData.getSoccerseasonUrl() + "/leagueTable");
                        startActivity(intent);
                        }
                    });
                }else {
                    new LoadTeamToDBAsyncTask( MatchDetailsActivity.this, "home" )
                            .execute(matchData.getHomeTeamUrl());
                }
                if(awayTeam != null){
                    new GetSVGAsyncTask( MatchDetailsActivity.this, mAwayTeamImg )
                            .execute(awayTeam.getCrestUrl());
                    mAwayTeamName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamName",awayTeam.getName());
                        intent.putExtra("squadMarketValue", awayTeam.getSquadMarketValue());
                        intent.putExtra("playersUrl", awayTeam.getPlayersUrl());
                        intent.putExtra("crestUrl", awayTeam.getCrestUrl());
                        intent.putExtra("teamLeagueUrl", matchData.getSoccerseasonUrl() + "/leagueTable");
                        startActivity(intent);
                        }
                    });
                }else {
                    new LoadTeamToDBAsyncTask( MatchDetailsActivity.this, "away" )
                            .execute(matchDetails.getAwayTeamUrl());
                }
            }


            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

        }
    }

    private class LoadTeamToDBAsyncTask extends AsyncTask<String, Integer, Team> {
        private Context mContext;
        private String typeTeam;
        public LoadTeamToDBAsyncTask(Context context, String team){
            mContext = context;
            typeTeam = team;
        }

        @Override
        protected Team doInBackground(String... params) {
            HttpURLConnection connection = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Auth-Token","7641996b673943d0a712f2f4493c7bbd");
                connection.setConnectTimeout(3000);
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }
                Team team = null;

                String response = sb.toString();
                JSONObject item = new JSONObject(response);
                if(item != null){
                    TeamDAO mTeamDAO = new TeamDAO(MatchDetailsActivity.this);
                    mTeamDAO.open();
                    team = mTeamDAO.createTeam(item);
                    mTeamDAO.close();
                }
                return team;

            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        public void onPostExecute(Team team){
            if(team != null){
                if(typeTeam.equals("home")){
                    homeTeam = team;
                    new GetSVGAsyncTask( MatchDetailsActivity.this, mHomeTeamImg )
                            .execute(homeTeam.getCrestUrl());
                    mHomeTeamName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamName", homeTeam.getName());
                        intent.putExtra("squadMarketValue", homeTeam.getSquadMarketValue());
                        intent.putExtra("playersUrl", homeTeam.getPlayersUrl());
                        intent.putExtra("crestUrl", homeTeam.getCrestUrl());
                        intent.putExtra("teamLeagueUrl", matchData.getSoccerseasonUrl() + "/leagueTable");
                        startActivity(intent);
                        }
                    });
                }else{
                    awayTeam = team;
                    new GetSVGAsyncTask( MatchDetailsActivity.this, mAwayTeamImg )
                            .execute(awayTeam.getCrestUrl());
                    mAwayTeamName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                            intent.putExtra("teamName", awayTeam.getName());
                            intent.putExtra("squadMarketValue", awayTeam.getSquadMarketValue());
                            intent.putExtra("playersUrl", awayTeam.getPlayersUrl());
                            intent.putExtra("crestUrl", awayTeam.getCrestUrl());
                            intent.putExtra("teamLeagueUrl", matchData.getSoccerseasonUrl() + "/leagueTable");
                            startActivity(intent);
                        }
                    });

                }
                Log.d(MainActivity.class.getSimpleName(), "team correctly loaded into database.");
            }else{
                Log.d(MainActivity.class.getSimpleName(), "error loading team");
            }
        }

    }

    private class MatchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return previousMatches.size();
        }

        @Override
        public Object getItem(int position) { return previousMatches.get(position); }

        @Override
        public long getItemId(int position) {
            return previousMatches.get(position).getDate().getTime();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View matchView = convertView;
            if (matchView == null) {
                matchView = inflater.inflate(R.layout.match_details_h2h_list_item, null);
            }

            TextView matchDate = (TextView) matchView.findViewById(R.id.h2h_match_date);
            TextView matchHomeTeamName = (TextView) matchView.findViewById(R.id.h2h_home_team_name);
            TextView matchAwayTeamName = (TextView) matchView.findViewById(R.id.h2h_away_team_name);
            TextView matchHomeTeamGoals = (TextView) matchView.findViewById(R.id.h2h_home_team_goals);
            TextView matchAwayTeamGoals = (TextView) matchView.findViewById(R.id.h2h_away_team_goals);

            Match currentMatch = previousMatches.get(position);
            matchDate.setText(currentMatch.getDateStringFormat());
            matchHomeTeamName.setText(currentMatch.getHomeTeamName());
            matchAwayTeamName.setText(currentMatch.getAwayTeamName());
            matchHomeTeamGoals.setText("" + currentMatch.getGoalsHomeTeam());
            matchAwayTeamGoals.setText("" + currentMatch.getGoalsAwayTeam());

            return matchView;
        }
    }
}
