package uy.edu.ucu.eu_goal_news;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.Soccerseason;


public class MainActivity extends ListActivity{

    //public ArrayList<String> leagues;
    private Spinner mSpinner;
    private String[] mLeagues;
    private HashMap<String,Soccerseason> mLeaguesHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("EU Goal News");

        // Populate leagues spinner
        mSpinner = (Spinner)findViewById(R.id.spinner_leagues);

        new GetSeasonsAsyncTask(MainActivity.this).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_league_detail:
                // Esperar que Julio termine el combobox para obtener la liga seleccionada
                // En caso que la liga seleccionada sea todas deshabilitar el boton
                // Claudia usa mLeaguesHash para obtener la url de la liga, este dic esta
                // cargado con todas las ligas del Combo.
                //  mLeaguesHash.get(captionDeLaLiga);
                String leagueName = "Liga BBVA";
                Integer leagueId = 368;
                Intent intent = new Intent(this, uy.edu.ucu.eu_goal_news.LeagueDetail.class);
                intent.putExtra("leagueName", leagueName);
                intent.putExtra("leagueId", leagueId);
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
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDay = dateFormat.format(calendar.getTime());

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
                return matchList;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if(connection != null)  connection.disconnect();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<Match> matches){

            // initialize adapter with matches
            if(matches != null) {
                setListAdapter(new MatchesListAdapter(mContext,R.layout.match_list_item, matches));
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
                        new GetMatchesAsyncTask(MainActivity.this)
                                .execute(mLeagues[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        //
                    }
                });
            }else{
                Toast.makeText(mContext, "Error loading leagues", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
        }

    }


}
