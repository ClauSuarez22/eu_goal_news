package uy.edu.ucu.eu_goal_news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.Match;

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

    public MatchesListAdapter(Context context, int resource, List<Match> matches) {
        super(context, resource, matches);

        this.mContext = context;
        this.mMatches = matches;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.match_list_item, parent, false);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.day_match);
        TextView timeTextView = (TextView) rowView.findViewById(R.id.time_match);
        TextView homeTeamTextView = (TextView) rowView.findViewById(R.id.home_team);
        TextView awayTeamTextView = (TextView) rowView.findViewById(R.id.away_team);

        // Get and Format DateTime of the Match
        Date matchDate = this.mMatches.get(position).getDate();
        DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        dateTextView.setText(formatDate.format(matchDate));
        DateFormat formatTime = new SimpleDateFormat("HH:mm");
        timeTextView.setText(formatTime.format(matchDate));

        homeTeamTextView.setText(this.mMatches.get(position).getHomeTeamName());
        awayTeamTextView.setText(this.mMatches.get(position).getAwayTeamName());

        return rowView;
    }

}
