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
import com.fravier.travel.models.Town;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class TownAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Town> towns;
    public Context ctx;
    Filter autoCompleteFilter;

    public TownAutoCompleteAdapter(Activity activity, ArrayList<Town> towns) {
        assert activity != null;
        assert towns != null;

        this.towns = towns;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == towns)
            return 0;
        else
            return towns.size();
    }

    @Override
    public Town getItem(int position) {
        if (null == towns)
            return null;
        else
            return towns.get(position);
    }

    @Override
    public long getItemId(int position) {
        Town town = towns.get(position);
        return town.getId();
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
        ArrayList<Town> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Town) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Town> results = new ArrayList<Town>();
            if (orig == null)
                orig = towns;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Town g : orig) {
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
            towns = (ArrayList<Town>) results.values;
            notifyDataSetChanged();
        }
    }

}
