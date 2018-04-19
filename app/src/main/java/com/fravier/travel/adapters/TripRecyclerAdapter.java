package com.fravier.travel.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.global.Fonting;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Vehicle;
import com.fravier.travel.textdrawable.TextDrawable;
import com.fravier.travel.textdrawable.util.ColorGenerator;

import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by francis on 21/04/2016.
 */
public class TripRecyclerAdapter extends RecyclerView.Adapter<TripRecyclerAdapter.MyViewHolder> {

    private List<Trip> trips;
    public Context ctx;
    SQLiteDatabase db = MyApplication.getDbHelper();

    public TripRecyclerAdapter(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public long getItemId(int position) {
        long id = trips.get(position).getId();

        return id;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Trip ci = trips.get(i);
        String title = null;


            title = cupboard().withDatabase(db).get(Route.class, ci.getRoute_id()).getName()  + ", " +
                    " : " + cupboard().withDatabase(db).get(Vehicle.class, ci.getVehicle_id()).getRegistration_plate();



//        String title="Trip #"+ci.getDate_travel();

        viewHolder.vName.setText(title);
        viewHolder.vDescription.setText("Reporting time: "+ci.getTime_reporting()+" Fare at: Ksh."+ci.getFare());
        viewHolder.vCreatedAt.setText(ci.getCreated_at());
        viewHolder.vCreatedBy.setText(ci.getCreated_by());
        viewHolder.vThumbnail.setImageDrawable(generateNameImage(title));


        viewHolder.vName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
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
        protected TextView vName;
        protected TextView vDescription;
        protected TextView vCreatedAt;
        protected TextView vCreatedBy;
        protected ImageView vThumbnail;

        public MyViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.txName);
            vDescription = (TextView) v.findViewById(R.id.txDescription);
            vCreatedAt = (TextView) v.findViewById(R.id.txCreatedAt);
            vCreatedBy = (TextView) v.findViewById(R.id.txCreatedBy);
            vThumbnail = (ImageView) v.findViewById(R.id.imgThumbnail);

        }
    }

    public TextDrawable generateNameImage(String fullname) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getRandomColor();

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();
        try {
            String firstname = fullname.split(" ")[0];
            String secondname = fullname.split(" ")[1];

            char letter1 = firstname.toUpperCase().charAt(0);
            char letter2 = secondname.toUpperCase().charAt(0);

            TextDrawable drawable = builder.build(letter1 + "" + letter2, color);
            return drawable;
        } catch (Exception ex) {
            char letter1 = fullname.toUpperCase().charAt(0);
            TextDrawable drawable = builder.build(letter1+"", color);
            return drawable;
        }
    }

}