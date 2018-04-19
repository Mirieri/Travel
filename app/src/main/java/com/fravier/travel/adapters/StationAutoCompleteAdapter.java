package com.fravier.travel.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.global.Fonting;
import com.fravier.travel.models.Station;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class StationAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Station> stations;
    public Context ctx;
    Filter autoCompleteFilter;

    public StationAutoCompleteAdapter(Activity activity, ArrayList<Station> stations) {
        assert activity != null;
        assert stations != null;

        this.stations = stations;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == stations)
            return 0;
        else
            return stations.size();
    }

    @Override
    public Station getItem(int position) {
        if (null == stations)
            return null;
        else
            return stations.get(position);
    }

    @Override
    public long getItemId(int position) {
        Station station = stations.get(position);
        return station.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(
                    R.layout.list_item_sp, null);

        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);

        tvName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        tvName.setText(getItem(position).getName());

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return autoCompleteFilter;

    }

    public class AutoCompleteFilter extends Filter {
        ArrayList<Station> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Station) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Station> results = new ArrayList<Station>();
            if (orig == null)
                orig = stations;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Station g : orig) {
                        if (g.getName().toString().toLowerCase().contains(constraint.toString().toLowerCase()))
                            results.add(g);
                    }
                }
                oReturn.values = results;
            }
            return oReturn;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stations = (ArrayList<Station>) results.values;
            notifyDataSetChanged();
        }
    }

}
