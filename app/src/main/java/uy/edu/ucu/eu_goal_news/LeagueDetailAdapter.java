package uy.edu.ucu.eu_goal_news;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.IListViewType;
import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.TeamLeague;


public class LeagueDetailAdapter extends ArrayAdapter<TeamLeague> implements Filterable {
    /**
     * Adapter that turns forecasts into Views that will be displayed in a ListView
     */
    private static final String TAG = LeagueDetailAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<TeamLeague> mTeamLeague;
    private final List<TeamLeague> mFilteredTeamLeague;
    private ItemFilter mFilter = new ItemFilter();

    public LeagueDetailAdapter(Context context, int resource, List<TeamLeague> teamLeague) {
        super(context, resource, teamLeague);
        this.mContext = context;
        this.mTeamLeague = new ArrayList<>();
        this.mTeamLeague.addAll(teamLeague);
        this.mFilteredTeamLeague = new ArrayList<>();
        this.mFilteredTeamLeague.addAll(teamLeague);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.team_league_list_item, parent, false);

        if (mFilteredTeamLeague.size() == mTeamLeague.size()) {
            if (pos < 4) {
                rowView.setBackgroundColor(Color.parseColor("#E1F5A9"));
            } else if (pos + 3 >= this.mFilteredTeamLeague.size()) {
                rowView.setBackgroundColor(Color.parseColor("#F6CECE"));
            }
        }

        TextView teamNameView = (TextView) rowView.findViewById(R.id.team_name);
        TextView position = (TextView) rowView.findViewById(R.id.position);
        TextView playedGames = (TextView) rowView.findViewById(R.id.playedGames);
        TextView points = (TextView) rowView.findViewById(R.id.points);
        TextView goals = (TextView) rowView.findViewById(R.id.goals);
        TextView goalsAgainst = (TextView) rowView.findViewById(R.id.goalsAgainst);
        TextView goalDifference = (TextView) rowView.findViewById(R.id.goalDifference);

        teamNameView.setText(this.mFilteredTeamLeague.get(pos).getTeamName());
        position.setText(this.mFilteredTeamLeague.get(pos).getPosition().toString());
        playedGames.setText(this.mFilteredTeamLeague.get(pos).getPlayedGames().toString());
        points.setText(this.mFilteredTeamLeague.get(pos).getPoints().toString());
        goals.setText(this.mFilteredTeamLeague.get(pos).getGoals().toString());
        goalsAgainst.setText(this.mFilteredTeamLeague.get(pos).getGoalsAgainst().toString());
        goalDifference.setText(this.mFilteredTeamLeague.get(pos).getGoalDifference().toString());

        return rowView;
    }

    @Override
    public int getCount() {
        return mFilteredTeamLeague.size();
    }

    @Override
    public TeamLeague getItem(int position) {
        return mFilteredTeamLeague.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }


    private class ItemFilter extends Filter {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredTeamLeague.clear();
            mFilteredTeamLeague.addAll((List<TeamLeague>) results.values);
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            ArrayList<TeamLeague> filteredArrayNames = new ArrayList<>();

            constraint = constraint.toString().toLowerCase();
            for (TeamLeague item : mTeamLeague) {
                if (item.getTeamName().toLowerCase().contains(constraint.toString())) {
                    filteredArrayNames.add(item);
                }
            }

            results.count = filteredArrayNames.size();
            results.values = filteredArrayNames;
            return results;
        }
    }

}
