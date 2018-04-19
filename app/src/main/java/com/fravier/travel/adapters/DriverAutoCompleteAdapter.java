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
import com.fravier.travel.models.Driver;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class DriverAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Driver> drivers;
    public Context ctx;
    Filter autoCompleteFilter;

    public DriverAutoCompleteAdapter(Activity activity, ArrayList<Driver> drivers) {
        assert activity != null;
        assert drivers != null;

        this.drivers = drivers;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == drivers)
            return 0;
        else
            return drivers.size();
    }

    @Override
    public Driver getItem(int position) {
        if (null == drivers)
            return null;
        else
            return drivers.get(position);
    }

    @Override
    public long getItemId(int position) {
        Driver driver = drivers.get(position);
        return driver.getId();
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
        ArrayList<Driver> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Driver) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Driver> results = new ArrayList<Driver>();
            if (orig == null)
                orig = drivers;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Driver g : orig) {
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
            drivers = (ArrayList<Driver>) results.values;
            notifyDataSetChanged();
        }
    }

}
