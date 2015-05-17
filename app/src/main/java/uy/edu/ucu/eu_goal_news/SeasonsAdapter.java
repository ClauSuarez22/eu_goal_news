package uy.edu.ucu.eu_goal_news;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import uy.edu.ucu.eu_goal_news.Model.Soccerseason;

/**
 * Created by juliorima on 16/05/2015.
 */
public class SeasonsAdapter extends ArrayAdapter<Soccerseason> {

    private Context mContext;
    private List<Soccerseason> mSoccerseasonList;

    public SeasonsAdapter(Context context, int resource, List<Soccerseason> soccerseasons) {
        super(context, resource, soccerseasons);

        this.mContext = context;
        this.mSoccerseasonList = soccerseasons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(mContext);
        view.setTextColor(Color.BLACK);
        view.setGravity(Gravity.CENTER);
        view.setText(mSoccerseasonList.get(position).getCaption());

        return view;
    }

    public int getCount(){
        return mSoccerseasonList.size();
    }

    public Soccerseason getItem(int position){
        return mSoccerseasonList.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    //View of Spinner on dropdown Popping
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        TextView view = new TextView(mContext);
        view.setTextColor(Color.BLACK);
        view.setText(mSoccerseasonList.get(position).getCaption());
        view.setHeight(60);

        return view;
    }


}
