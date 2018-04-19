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
import com.fravier.travel.models.Vehicle;

import java.util.ArrayList;

/**
 * Created by francis on 23/02/2016.
 */
public class VehicleAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private final Activity activity;
    ArrayList<Vehicle> vehicles;
    public Context ctx;
    Filter autoCompleteFilter;

    public VehicleAutoCompleteAdapter(Activity activity, ArrayList<Vehicle> vehicles) {
        assert activity != null;
        assert vehicles != null;

        this.vehicles = vehicles;
        this.activity = activity;
        autoCompleteFilter = new AutoCompleteFilter();
        ctx = activity;
    }

    @Override
    public int getCount() {
        if (null == vehicles)
            return 0;
        else
            return vehicles.size();
    }

    @Override
    public Vehicle getItem(int position) {
        if (null == vehicles)
            return null;
        else
            return vehicles.get(position);
    }

    @Override
    public long getItemId(int position) {
        Vehicle vehicle = vehicles.get(position);
        return vehicle.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(
                    R.layout.list_item_sp, null);

        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);

        tvName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        tvName.setText(getItem(position).getRegistration_plate());

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return autoCompleteFilter;

    }

    public class AutoCompleteFilter extends Filter {
        ArrayList<Vehicle> orig;



        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Vehicle) (resultValue)).getRegistration_plate();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults oReturn = new FilterResults();
            ArrayList<Vehicle> results = new ArrayList<Vehicle>();
            if (orig == null)
                orig = vehicles;

            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (Vehicle g : orig) {
                        if (g.getRegistration_plate().toString().toLowerCase().contains(constraint.toString().toLowerCase()))
                            results.add(g);
                    }
                }
                oReturn.values = results;
            }
            return oReturn;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            vehicles = (ArrayList<Vehicle>) results.values;
            notifyDataSetChanged();
        }
    }

}
