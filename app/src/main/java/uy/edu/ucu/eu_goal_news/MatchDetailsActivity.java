package uy.edu.ucu.eu_goal_news;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import uy.edu.ucu.eu_goal_news.Model.MatchDetails;
import uy.edu.ucu.eu_goal_news.Model.Team;

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private class GetMatchDetailsAsyncTask extends AsyncTask<String, Integer, MatchDetails> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetMatchDetailsAsyncTask(Context context){ this.mContext = context; }

        @Override
        public void onPreExecute(){
            mProgressDialog = ProgressDialog.show(MatchDetailsActivity.this, null, "Cargando detalles del partido ...", true, false);
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




                //this.mHomeTeamImg = (ImageView) findViewById(R.id.home_team_img);
            //mProthis.mAwayTeamImg = (ImageView) findViewById(R.id.away_team_img);
            }
            else{
                Toast.makeText(mContext, "Error downloading teams details", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

        }
    }

    private class DownloadMoviePosterAsyncTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];
            HttpURLConnection connection = null;
            Bitmap poster = null;
            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);

                InputStream is = connection.getInputStream();
                poster = BitmapFactory.decodeStream(is);
                is.close();

                return poster;

            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(connection != null) connection.disconnect();
            }

            return poster;

        }

        @Override
        public void onPostExecute(Bitmap poster){

            if(poster != null){
               /* mMovieImage.setImageBitmap(poster);
                mMovieImage.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);      */
            }

        }

    }
}
