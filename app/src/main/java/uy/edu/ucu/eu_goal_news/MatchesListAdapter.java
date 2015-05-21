package uy.edu.ucu.eu_goal_news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.Soccerseason;

/**
 * Created by juliorima on 03/05/2015.
 */
public class MatchesListAdapter extends ArrayAdapter<Match>  {
/**
 * Adapter that turns forecasts into Views that will be displayed in a ListView
 */
    private static final String TAG = MatchesListAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<Match> mMatches;
    private ArrayList<Integer> mLeaguesPopulated;
    private HashMap<String,Soccerseason> mLeagues;

    public MatchesListAdapter(Context context, int resource, List<Match> matches, HashMap<String,Soccerseason> leagues) {
        super(context, resource, matches);

        this.mContext = context;
        this.mMatches = matches;
        this.mLeaguesPopulated = new ArrayList<>();
        this.mLeagues = leagues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.match_list_item, parent, false);

        if (position == 0){
            mLeaguesPopulated = new ArrayList<>();
        }

        TextView dateTextView = (TextView) rowView.findViewById(R.id.day_match);
        TextView timeTextView = (TextView) rowView.findViewById(R.id.time_match);
        TextView homeTeamTextView = (TextView) rowView.findViewById(R.id.home_team);
        TextView awayTeamTextView = (TextView) rowView.findViewById(R.id.away_team);

        // Get and Format DateTime of the Match
        Date matchDate = this.mMatches.get(position).getDate();
        DateFormat formatDate = new SimpleDateFormat("dd/MM/yy");
        dateTextView.setText(formatDate.format(matchDate));
        DateFormat formatTime = new SimpleDateFormat("HH:mm");
        timeTextView.setText(formatTime.format(matchDate));

        homeTeamTextView.setText(this.mMatches.get(position).getHomeTeamName());
        awayTeamTextView.setText(this.mMatches.get(position).getAwayTeamName());

        int leagueId = this.mMatches.get(position).getMatchLeagueId();
        if (!mLeaguesPopulated.contains(leagueId)){
            TextView leagueNameTextView = (TextView) rowView.findViewById(R.id.league_name);
            leagueNameTextView.setVisibility(View.VISIBLE); // Not working

            leagueNameTextView.setText(getLeagueName(leagueId));

            View hSeparator = rowView.findViewById(R.id.horizontal_separator);
            hSeparator.setVisibility(View.VISIBLE);

            mLeaguesPopulated.add(leagueId);
        }else{
            TextView leagueNameTextView = (TextView) rowView.findViewById(R.id.league_name);
            leagueNameTextView.setVisibility(View.GONE); // Not working
        }

        return rowView;
    }

    private String getLeagueName(int matchLeagueId){
        int leagueId;
        for (Soccerseason item : mLeagues.values()){
            if (item != null) {
                leagueId = item.getLeagueId();
                if (leagueId == matchLeagueId) {
                    return item.getCaption();
                }
            }
        }

        return "";
    }

}
