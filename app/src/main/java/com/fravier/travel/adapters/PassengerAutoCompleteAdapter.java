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
import com.fravier.travel.models.Passenger;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class PassengerAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Passenger> passengers;
    public Context ctx;
    Filter autoCompleteFilter;

    public PassengerAutoCompleteAdapter(Activity activity, ArrayList<Passenger> passengers) {
        assert activity != null;
        assert passengers != null;

        this.passengers = passengers;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == passengers)
            return 0;
        else
            return passengers.size();
    }

    @Override
    public Passenger getItem(int position) {
        if (null == passengers)
            return null;
        else
            return passengers.get(position);
    }

    @Override
    public long getItemId(int position) {
        Passenger passenger = passengers.get(position);
        return passenger.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(
                    R.layout.list_item_sp, null);

        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);

        tvName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        tvName.setText(getItem(position).getFirst_name());

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return autoCompleteFilter;

    }

    public class AutoCompleteFilter extends Filter {
        ArrayList<Passenger> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Passenger) (resultValue)).getFirst_name();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Passenger> results = new ArrayList<Passenger>();
            if (orig == null)
                orig = passengers;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Passenger g : orig) {
                        if (g.getFirst_name().toString().toLowerCase().contains(constraint.toString().toLowerCase()))
                            results.add(g);
                    }
                }
                oReturn.values = results;
            }
            return oReturn;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            passengers = (ArrayList<Passenger>) results.values;
            notifyDataSetChanged();
        }
    }

}
