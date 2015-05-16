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

import uy.edu.ucu.eu_goal_news.Model.TeamLeague;


public class LeagueDetailAdapter extends ArrayAdapter<TeamLeague> {
    /**
     * Adapter that turns forecasts into Views that will be displayed in a ListView
     */
    private static final String TAG = LeagueDetailAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<TeamLeague> mTeamLeague;

    public LeagueDetailAdapter(Context context, int resource, List<TeamLeague> teamLeague) {
        super(context, resource, teamLeague);

        this.mContext = context;
        this.mTeamLeague = teamLeague;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.team_league_list_item, parent, false);

        TextView teamNameView = (TextView) rowView.findViewById(R.id.team_name);
        TextView position = (TextView) rowView.findViewById(R.id.position);
        TextView playedGames = (TextView) rowView.findViewById(R.id.playedGames);
        TextView points = (TextView) rowView.findViewById(R.id.points);
        TextView goals = (TextView) rowView.findViewById(R.id.goals);
        TextView goalsAgainst = (TextView) rowView.findViewById(R.id.goalsAgainst);
        TextView goalDifference = (TextView) rowView.findViewById(R.id.goalDifference);

        teamNameView.setText(this.mTeamLeague.get(pos).getTeamName());
        position.setText(this.mTeamLeague.get(pos).getPosition().toString());
        playedGames.setText(this.mTeamLeague.get(pos).getPlayedGames().toString());
        points.setText(this.mTeamLeague.get(pos).getPoints().toString());
        goals.setText(this.mTeamLeague.get(pos).getGoals().toString());
        goalsAgainst.setText(this.mTeamLeague.get(pos).getGoalsAgainst().toString());
        goalDifference.setText(this.mTeamLeague.get(pos).getGoalDifference().toString());

        return rowView;
    }

}
