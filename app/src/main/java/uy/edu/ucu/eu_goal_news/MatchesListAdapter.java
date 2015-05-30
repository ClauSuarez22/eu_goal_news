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
import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.IListViewType;
import uy.edu.ucu.eu_goal_news.Model.Match;
import uy.edu.ucu.eu_goal_news.Model.Soccerseason;

public class MatchesListAdapter extends ArrayAdapter<IListViewType>  {

    private final Context mContext;

    private List<IListViewType> mItems;

    public MatchesListAdapter(Context context, int resource, List<IListViewType> items) {
        super(context, resource, items);

        this.mContext = context;
        this.mItems = new ArrayList<>();
        this.mItems.addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        int viewType = mItems.get(position).getViewType();

        if(viewType == IListViewType.SECTION){
            rowView = inflater.inflate(R.layout.match_list_section, parent, false);
        }else{
            rowView = inflater.inflate(R.layout.match_list_item, parent, false);
        }

        if (viewType == IListViewType.ITEM) {

            Match match = (Match)mItems.get(position);

            LinearLayout matchItem = (LinearLayout)rowView.findViewById(R.id.layout_match_item);

            TextView dateTextView = (TextView) matchItem.findViewById(R.id.day_match);
            TextView timeTextView = (TextView) rowView.findViewById(R.id.time_match);
            TextView homeTeamTextView = (TextView) rowView.findViewById(R.id.home_team);
            TextView awayTeamTextView = (TextView) rowView.findViewById(R.id.away_team);

            // Get and Format DateTime of the Match
            Date matchDate = match.getDate();
            DateFormat formatDate = new SimpleDateFormat("dd/MM/yy");
            dateTextView.setText(formatDate.format(matchDate));
            DateFormat formatTime = new SimpleDateFormat("HH:mm");
            timeTextView.setText(formatTime.format(matchDate));

            homeTeamTextView.setText(match.getHomeTeamName());
            awayTeamTextView.setText(match.getAwayTeamName());

        }else{

            Soccerseason season = (Soccerseason)mItems.get(position);
            TextView sectionNameTextView = (TextView)rowView.findViewById(R.id.section_name);
            sectionNameTextView.setText(season.getCaption());
        }

        return rowView;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public IListViewType getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setmItems(List<IListViewType> mItems) {
        this.mItems.clear();
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public String getMatchLeagueName(int position){
        int leagueId = mItems.get(position).getLeagueId();
        for(IListViewType item : mItems){
            if(item.getViewType() == IListViewType.SECTION
                    && item.getLeagueId() == leagueId){
                Soccerseason season = (Soccerseason)item;
                return season.getCaption();
            }
        }

        return "";
    }
}
