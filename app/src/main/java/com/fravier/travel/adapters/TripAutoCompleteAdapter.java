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
import com.fravier.travel.models.Trip;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class TripAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Trip> trips;
    public Context ctx;
    Filter autoCompleteFilter;

    public TripAutoCompleteAdapter(Activity activity, ArrayList<Trip> trips) {
        assert activity != null;
        assert trips != null;

        this.trips = trips;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == trips)
            return 0;
        else
            return trips.size();
    }

    @Override
    public Trip getItem(int position) {
        if (null == trips)
            return null;
        else
            return trips.get(position);
    }

    @Override
    public long getItemId(int position) {
        Trip trip = trips.get(position);
        return trip.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(
                    R.layout.list_item_sp, null);

        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);

        tvName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        tvName.setText(getItem(position).getDate_travel());

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return autoCompleteFilter;

    }

    public class AutoCompleteFilter extends Filter {
        ArrayList<Trip> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Trip) (resultValue)).getDate_travel();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Trip> results = new ArrayList<Trip>();
            if (orig == null)
                orig = trips;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Trip g : orig) {
                        if (g.getDate_travel().toString().toLowerCase().contains(constraint.toString().toLowerCase()))
                            results.add(g);
                    }
                }
                oReturn.values = results;
            }
            return oReturn;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            trips = (ArrayList<Trip>) results.values;
            notifyDataSetChanged();
        }
    }

}
