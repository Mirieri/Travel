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
import com.fravier.travel.models.Route;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class RouteAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Route> routes;
    public Context ctx;
    Filter autoCompleteFilter;

    public RouteAutoCompleteAdapter(Activity activity, ArrayList<Route> routes) {
        assert activity != null;
        assert routes != null;

        this.routes = routes;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == routes)
            return 0;
        else
            return routes.size();
    }

    @Override
    public Route getItem(int position) {
        if (null == routes)
            return null;
        else
            return routes.get(position);
    }

    @Override
    public long getItemId(int position) {
        Route route = routes.get(position);
        return route.getId();
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
        ArrayList<Route> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Route) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Route> results = new ArrayList<Route>();
            if (orig == null)
                orig = routes;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Route g : orig) {
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
            routes = (ArrayList<Route>) results.values;
            notifyDataSetChanged();
        }
    }

}
