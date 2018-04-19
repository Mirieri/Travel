package com.fravier.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.global.Fonting;
import com.fravier.travel.models.Vehicle;
import com.fravier.travel.textdrawable.TextDrawable;
import com.fravier.travel.textdrawable.util.ColorGenerator;
import com.fravier.travel.utilities.Utilities;

import java.util.List;

/**
 * Created by francis on 21/04/2016.
 */
public class VehicleRecyclerAdapter extends RecyclerView.Adapter<VehicleRecyclerAdapter.MyViewHolder> {

    private List<Vehicle> vehicles;
    public Context ctx;

    public VehicleRecyclerAdapter(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    @Override
    public long getItemId(int position) {
        long id = vehicles.get(position).getId();

        return id;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Vehicle ci = vehicles.get(i);
        viewHolder.vRegistrationPlate.setText(ci.getRegistration_plate());
        viewHolder.vDescription.setText(ci.getDescription());
//        viewHolder.vComments.setText(ci.getNational_id());
        viewHolder.vCreatedAt.setText(ci.getCreated_at());
        viewHolder.vCreatedBy.setText(ci.getCreated_by());
        viewHolder.vThumbnail.setImageDrawable(Utilities.generateNameImage(ci.getRegistration_plate()));


        viewHolder.vRegistrationPlate.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        viewHolder.vDescription.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        ctx = viewGroup.getContext();


        return new MyViewHolder(itemView);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        protected TextView vRegistrationPlate;
        protected TextView vDescription;
        protected TextView vCreatedAt;
        protected TextView vCreatedBy;
        protected ImageView vThumbnail;

        public MyViewHolder(View v) {
            super(v);
            vRegistrationPlate = (TextView) v.findViewById(R.id.txName);
            vDescription = (TextView) v.findViewById(R.id.txDescription);
            vCreatedAt = (TextView) v.findViewById(R.id.txCreatedAt);
            vCreatedBy = (TextView) v.findViewById(R.id.txCreatedBy);
            vThumbnail = (ImageView) v.findViewById(R.id.imgThumbnail);

        }
    }


}