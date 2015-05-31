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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.MatchDetails;
import uy.edu.ucu.eu_goal_news.Model.Team;
import uy.edu.ucu.eu_goal_news.Model.TeamLeague;

public class MatchDetailsActivity extends Activity {
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
    private Team homeTeam;
    private Team awayTeam;
    private TeamLeague homeTeamLeague;
    private TeamLeague awayTeamLeague;

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
        public void onPostExecute(MatchDetails matchDetails){

            if(matchDetails != null) {
                previousMatches = matchDetails.getPreviousMatches();
                mPreviousMatchesView.setAdapter(new MatchListAdapter());

                mMatchday.setText("" + matchDetails.getMatchday());
                mMatchStatus.setText("" + matchDetails.getStatus());
                mMatchDate.setText(matchDetails.getStartDate());
                mMatchTime.setText(matchDetails.getStartTime());
                mHomeTeamName.setText(matchDetails.getHomeTeamName());
                mAwayTeamName.setText(matchDetails.getAwayTeamName());
                mH2HHomeTeamName.setText(matchDetails.getHomeTeamName());
                mH2HAwayTeamName.setText(matchDetails.getAwayTeamName());
                mHomeTeamGoals.setText("" + matchDetails.getHomeTeamGoals());
                mAwayTeamGoals.setText("" + matchDetails.getAwayTeamGoals());
                mHomeTeamWins.setText("" + matchDetails.getHomeTeamWins());
                mAwayTeamWins.setText("" + matchDetails.getAwayTeamWins());
                mH2HDraws.setText("" + matchDetails.getDraws());

                //bring data of each team stored in local DB
                /*  TeamDAO mTeamDAO = new TeamDAO(this);
                *   mTeamDAO.open();
                *   Team homeTeam = mTeamDAO.findByName( matchDetails.getHomeTeamName() );
                *   Team awayTeam = mTeamDao.findByName( matchDetails.getAwayTeamName() );
                *   mTeamDao.close();
                * */

                awayTeam = new Team();
                homeTeam = new Team();
                homeTeamLeague = new TeamLeague();

                homeTeam.setName("SpVgg Greuther Fürth");
                homeTeam.setShortName("F�rth");
                homeTeam.setCode("GRE");
                homeTeam.setSquadMarketValue("17,975,000 �");
                homeTeam.setCrestUrl("http://upload.wikimedia.org/wikipedia/de/9/96/Logo_Olympiakos_Piräus.svg");
                homeTeam.setPlayersUrl("http://api.football-data.org/alpha/teams/21/players");

                homeTeamLeague.setTeamName("SpVgg Greuther Fürth");
                homeTeamLeague.setPosition(1);
                homeTeamLeague.setPlayedGames(31);
                homeTeamLeague.setGoals(40);
                homeTeamLeague.setGoalsAgainst(40);
                homeTeamLeague.setGoalDifference(40);
                homeTeamLeague.setPoints(73);

                awayTeamLeague = new TeamLeague();

                awayTeam.setName("1. FSV Mainz 05");
                awayTeam.setShortName("Mainz");
                awayTeam.setCode("M05");
                awayTeam.setSquadMarketValue("75,200,000 �");
                awayTeam.setCrestUrl("http://upload.wikimedia.org/wikipedia/de/e/e7/Logo_TSG_Hoffenheim.svg");
                awayTeam.setPlayersUrl("http://api.football-data.org/alpha/teams/15/players");

                homeTeamLeague.setTeamName("SpVgg Greuther Fürth");
                homeTeamLeague.setPosition(1);
                homeTeamLeague.setPlayedGames(31);
                homeTeamLeague.setGoals(40);
                homeTeamLeague.setGoalsAgainst(40);
                homeTeamLeague.setGoalDifference(40);
                homeTeamLeague.setPoints(73);

                mHomeTeamName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamName", homeTeam.getName());
                        intent.putExtra("squadMarketValue", homeTeam.getSquadMarketValue());
                        intent.putExtra("playersUrl", homeTeam.getPlayersUrl());
                        intent.putExtra("crestUrl", homeTeam.getCrestUrl());
                        intent.putExtra("position", homeTeamLeague.getPosition());
                        intent.putExtra("playedGames", homeTeamLeague.getPlayedGames());
                        intent.putExtra("points", homeTeamLeague.getPoints());
                        intent.putExtra("goals", homeTeamLeague.getGoals());
                        intent.putExtra("goalsAgainst", homeTeamLeague.getGoalsAgainst());
                        intent.putExtra("goalsDifference", homeTeamLeague.getGoalDifference());
                        startActivity(intent);

                    }
                });

                mAwayTeamName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamName",awayTeam.getName());
                        intent.putExtra("squadMarketValue", awayTeam.getSquadMarketValue());
                        intent.putExtra("playersUrl", awayTeam.getPlayersUrl());
                        intent.putExtra("crestUrl", awayTeam.getCrestUrl());
                        intent.putExtra("position", awayTeamLeague.getPosition());
                        intent.putExtra("playedGames", awayTeamLeague.getPlayedGames());
                        intent.putExtra("points", awayTeamLeague.getPoints());
                        intent.putExtra("goals", awayTeamLeague.getGoals());
                        intent.putExtra("goalsAgainst", awayTeamLeague.getGoalsAgainst());
                        intent.putExtra("goalsDifference", awayTeamLeague.getGoalDifference() );
                        startActivity(intent);

                    }
                });
                new GetSVGAsyncTask( MatchDetailsActivity.this, mHomeTeamImg )
                        .execute(homeTeam.getCrestUrl());
                new GetSVGAsyncTask( MatchDetailsActivity.this, mAwayTeamImg )
                        .execute(awayTeam.getCrestUrl());
            }
            else{
                Toast.makeText(mContext, "Error loading teams details", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
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
