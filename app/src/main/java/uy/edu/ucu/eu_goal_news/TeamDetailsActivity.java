package uy.edu.ucu.eu_goal_news;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.Player;


public class TeamDetailsActivity extends Activity {
    private ImageView mLogo;
    private TextView mMarketValue;
    private TextView mTeamName;
    private TextView mPosition;
    private TextView mPlayedGames;
    private TextView mGoals;
    private TextView mGoalsAgainst;
    private TextView mGoalsDifference;
    private TextView mPoints;
    private ListView mPlayersView;
    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);
        Bundle extras = getIntent().getExtras();
        if ( extras != null )
        {
            String teamName = extras.getString( "teamName" );
            String squadMarketValue = extras.getString("squadMarketValue");
            String playersUrl = extras.getString("playersUrl");
            String crestUrl = extras.getString("crestUrl");
            String position = "" + extras.getInt("position");
            String playedGames = "" + extras.getInt("playedGames");
            String points = "" + extras.getInt("points");
            String goals = "" + extras.getInt("goals");
            String goalsAgainst = "" + extras.getInt("goalsAgainst");
            String goalsDifference = "" + extras.getInt("goalsDifference");

            mTeamName = (TextView) findViewById(R.id.team_name);
            mMarketValue = (TextView) findViewById(R.id.market_value);
            mPosition = (TextView) findViewById(R.id.position);
            mPlayedGames = (TextView) findViewById(R.id.playedGames);
            mPoints = (TextView) findViewById(R.id.points);
            mGoals= (TextView) findViewById(R.id.goals);
            mGoalsAgainst = (TextView) findViewById(R.id.goalsAgainst);
            mGoalsDifference = (TextView) findViewById(R.id.goalsDifference);
            mLogo = (ImageView) findViewById(R.id.team_logo);
            mPlayersView = (ListView) findViewById(R.id.players);

            mTeamName.setText(teamName);
            mMarketValue.setText(squadMarketValue);
            mPosition.setText(position);
            mPlayedGames.setText(playedGames);
            mPoints.setText(points);
            mGoals.setText(goals);
            mGoalsAgainst.setText(goalsAgainst);
            mGoalsDifference.setText(goalsDifference);

            getActionBar().setTitle(teamName);
            new GetSVGAsyncTask( TeamDetailsActivity.this, mLogo)
                    .execute(crestUrl);
            new GetPlayersAsyncTask( TeamDetailsActivity.this )
                    .execute(playersUrl);
        }else{}

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_details, menu);
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

    private class GetPlayersAsyncTask extends AsyncTask<String, Integer, List<Player>> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetPlayersAsyncTask(Context context){ this.mContext = context; }

        @Override
        public void onPreExecute(){
            mProgressDialog = ProgressDialog.show(TeamDetailsActivity.this, null, "Loading...", true, false);
        }

        @Override
        protected List<Player> doInBackground(String... params) {
            HttpURLConnection connection = null;
            players= new ArrayList<Player>();

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
                Log.d(TeamDetailsActivity.class.getSimpleName(), response);
                JSONArray jsonArray = new JSONObject(response).getJSONArray("players");
                if(jsonArray != null){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        players.add(new Player(item));

                    }
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(connection != null) connection.disconnect();
            }

            return players;
        }

        @Override
        public void onPostExecute(List<Player> playerList){
            if(players != null) {
                mPlayersView.setAdapter(new PlayersListAdapter());
            }
            else{
                Toast.makeText(mContext, "Error loading players", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

        }
    }

    private class PlayersListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) { return players.get(position); }

        @Override
        public long getItemId(int position) {
            return players.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View playerView = convertView;
            if (playerView == null) {
                playerView = inflater.inflate(R.layout.team_details_player_list_item, null);
            }

            TextView playerNumber = (TextView) playerView.findViewById(R.id.player_number);
            TextView playerName = (TextView) playerView.findViewById(R.id.player_name);
            TextView playerPosition = (TextView) playerView.findViewById(R.id.player_position);
            TextView playerNationality = (TextView) playerView.findViewById(R.id.player_nationality);

            Player currentPlayer = players.get(position);
            playerNumber.setText(currentPlayer.getJerseyNumber().toString());
            playerName.setText(currentPlayer.getName());
            playerPosition.setText(currentPlayer.getPosition());
            playerNationality.setText(currentPlayer.getNationality());


            return playerView;
        }
    }
}
