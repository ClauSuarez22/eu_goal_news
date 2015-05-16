package uy.edu.ucu.eu_goal_news;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setTitle("EU Goal News");

        final EditText searchBox = (EditText) findViewById(R.id.search_box);
        Button mSearchButton = (Button) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchBox.getText().toString();
                if(query.equals("")){
                    Toast.makeText(MainActivity.this, "Please enter a league in the search box", Toast.LENGTH_SHORT).show();
                }else{
                    new GetMatchesAsyncTask(MainActivity.this)
                            .execute(query);
                }
            }
        });

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

    /*
    public void viewSeason(View view){
        startActivity(new Intent(this, SoccerSeasonsActivity.class));
    }
    */

    // Async Task for Matches
    private class GetMatchesAsyncTask extends AsyncTask<String, Integer, ArrayList<Match>> {

        private final String mGetMatchesTodayApiUrl = "http://api.football-data.org/alpha/fixtures/?timeFrameStart=%s&timeFrameEnd=%s";
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
                setListAdapter(new MatchesListAdapter(mContext, R.layout.match_list_item, matches));
            }else{
                Toast.makeText(mContext, "Error downloading matches", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();

        }

    }

}
