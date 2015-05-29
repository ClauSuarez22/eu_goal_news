package uy.edu.ucu.eu_goal_news;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.Soccerseason;
import uy.edu.ucu.eu_goal_news.Model.Team;
import uy.edu.ucu.eu_goal_news.db.TeamDAO;


public class MainActivity extends ListActivity{

    //public ArrayList<String> leagues;
    private Spinner mSpinner;
    private ListView mMatchList;
    private String[] mLeagues;
    private HashMap<String,Soccerseason> mLeaguesHash;
    private String selectedLeague;
    private String selectedLeagueTableUrl;
    private Menu mMenu;
    private TeamDAO mTeamDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("EU Goal News");

        // Populate leagues spinner
        mSpinner = (Spinner)findViewById(R.id.spinner_leagues);

        /*mTeamDAO = new TeamDAO(this);
        mTeamDAO.open();

        String name = "prueba nombre 1";
        String code = "prueba code 2";
        String shortName = "prueba shortName 3";
        String squadMarketValue = "prueba squadMarketValue 4";
        String crestUrl = "prueba crestUrl 5";
        String fixturesUrl = "prueba fixturesUrl 6";
        String playersUrl = "prueba playersUrl 7";

        Team team = mTeamDAO.create(name, code, shortName, squadMarketValue, crestUrl, fixturesUrl, playersUrl);

        Team teamFoundByName = mTeamDAO.findByName( name );
        Team teamFoundByShortName = mTeamDAO.findByShortName( shortName );
        Team teamFoundByCode = mTeamDAO.findByCode( code );*/

        new GetSeasonsAsyncTask(MainActivity.this).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_league_detail:
                Intent intent = new Intent(this, uy.edu.ucu.eu_goal_news.LeagueDetail.class);
                intent.putExtra("selectedLeagueUrl", selectedLeagueTableUrl);
                intent.putExtra("leagueName", selectedLeague);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    // Async Task for Matches
    private class GetMatchesAsyncTask extends AsyncTask<String, Integer, ArrayList<Match>> {

        private String mGetMatchesTodayApiUrl = "http://api.football-data.org/alpha/fixtures/?timeFrameStart=%s&timeFrameEnd=%s";
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetMatchesAsyncTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute(){

            mProgressDialog = ProgressDialog.show(mContext, null,mContext.getString(R.string.loading), true, false);
        }

        @Override
        protected ArrayList<Match> doInBackground(String... params) {

            // Parametro para filtrar por liga
            String league = params[0];

            Soccerseason leagueFilter = mLeaguesHash.get(params[0]);
            if (leagueFilter != null){
                mGetMatchesTodayApiUrl = leagueFilter.getFixtures();
                mGetMatchesTodayApiUrl += "/?timeFrameStart=%s&timeFrameEnd=%s";
            }else{
                mGetMatchesTodayApiUrl = "http://api.football-data.org/alpha/fixtures/?timeFrameStart=%s&timeFrameEnd=%s";
            }

            HttpURLConnection connection = null;

            // Get current Day
            //Calendar calendar = Calendar.getInstance();
            //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //String currentDay = dateFormat.format(calendar.getTime());
            String currentDay = "2015-04-18";

            try {
                URL url = new URL(String.format(mGetMatchesTodayApiUrl, currentDay, currentDay));

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Auth-Token","7641996b673943d0a712f2f4493c7bbd");
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();

                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }

                JSONArray jsonArray = new JSONObject(sb.toString()).getJSONArray("fixtures");

                ArrayList<Match> matchList = new ArrayList<Match>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    matchList.add(new Match(item));

                }

                // Sort by MatchLeagueId
                Collections.sort(matchList, new Comparator<Match>() {
                    public int compare(Match m1, Match m2) {
                        return m1.getMatchLeagueId().compareTo(m2.getMatchLeagueId());
                    }
                });

                if ( league.compareTo( "All leagues" ) == 0 )
                {
                    return orderMatchesForFavouriteLeague(matchList);
                }

                return matchList;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if(connection != null)  connection.disconnect();
            }
            return null;
        }

        public ArrayList orderMatchesForFavouriteLeague( ArrayList matchList )
        {
            SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
            int favouriteLeague = sharedPreferences.getInt( "favourite_league", -1);

            ArrayList matchesFavouriteLeague = new ArrayList<>();

            if ( favouriteLeague != -1 )
            {
                for( int i = 0; i < matchList.size(); i++ )
                {
                    Match match = (Match) matchList.get( i );
                    int matchLeague = match.getMatchLeagueId();

                    if ( matchLeague == favouriteLeague )
                    {
                        matchesFavouriteLeague.add( match );
                        matchList.remove( i );
                    }
                }

                ArrayList finalMatchList = matchesFavouriteLeague;
                finalMatchList.addAll( matchList );

                return finalMatchList;

            }

            return matchList;

        }

        @Override
        public void onPostExecute(ArrayList<Match> matches){

            // initialize adapter with matches
            if(matches != null) {
                ListView matchAdapterView =(ListView) findViewById(android.R.id.list);
                final MatchesListAdapter matchesAdapter = new MatchesListAdapter(mContext, R.layout.match_list_item, matches, mLeaguesHash);
                matchAdapterView.setAdapter(matchesAdapter);
                matchAdapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, MatchDetailsActivity.class);
                        intent.putExtra("selectedMatchUrl", matchesAdapter.getItem(position).getSelfUrl());
                        intent.putExtra("leagueName", matchesAdapter.getMatchLeagueName(position));
                        startActivity(intent);
                    }
                });


            }else{
                Toast.makeText(mContext, "Error downloading matches", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();

        }

    }

    // Async Task for Leagues
    private class GetSeasonsAsyncTask extends AsyncTask<String, Integer, ArrayList<Soccerseason>> {

        private final String mGetSeasonApiUrl = "http://api.football-data.org/alpha/soccerseasons";
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetSeasonsAsyncTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute(){

            mProgressDialog = ProgressDialog.show(mContext, null,mContext.getString(R.string.loading), true, false);
        }

        @Override
        protected ArrayList<Soccerseason> doInBackground(String... params) {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(mGetSeasonApiUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Auth-Token","7641996b673943d0a712f2f4493c7bbd");
                connection.connect();

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();

                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }

                JSONArray jsonArray = new JSONArray(sb.toString());

                ArrayList<Soccerseason> seasonList = new ArrayList<Soccerseason>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    seasonList.add(new Soccerseason(item));
                }

                // Sort by LeagueId
                Collections.sort(seasonList, new Comparator<Soccerseason>() {
                    public int compare(Soccerseason s1, Soccerseason s2) {
                        return s1.getLeagueId().compareTo(s1.getLeagueId());
                    }
                });

                return seasonList;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if(connection != null)  connection.disconnect();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<Soccerseason> soccerseasons){

            // initialize adapter with matches
            if(soccerseasons != null) {
                mLeaguesHash = new HashMap<>();
                mLeagues = new String[soccerseasons.size() + 1];
                mLeagues[0] = "All leagues";
                mLeaguesHash.put("All leagues",null);
                int i = 1;
                for (Soccerseason item : soccerseasons) {
                    mLeagues[i] = item.getCaption();
                    mLeaguesHash.put(item.getCaption(),item);
                    i++;
                }

                ArrayAdapter<String> spAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, mLeagues);
                spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(spAdapter);

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        MenuItem item = mMenu.findItem(R.id.action_league_detail);

                        if ( mLeagues[position].compareTo( "All leagues" ) != 0 )
                        {
                            selectedLeagueTableUrl = mLeaguesHash.get(mLeagues[position]).getLeagueTable();
                            item.setEnabled(true);
                            item.getIcon().setAlpha(255);
                        }
                        else
                        {
                            item.setEnabled(false);
                            item.getIcon().setAlpha(30);
                        }

                        selectedLeague = mLeagues[position];

                        new GetMatchesAsyncTask(MainActivity.this)
                                .execute(mLeagues[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Nop
                    }
                });
            }else{
                Toast.makeText(mContext, "Error loading leagues", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
        }

    }
}
