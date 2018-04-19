package com.fravier.travel.wizards.ticketing;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.adapters.AirplaneAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Vehicle;

import org.codepond.wizardroid.WizardStep;

import java.util.ArrayList;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepSeats extends WizardStep {

    private static final int COLUMNS = 3;
    private TextView txtSeatSelected;
    public StepSeats() {
        // Required empty public constructor
    }
    public EditText etSeatNumber;
    public Context ctx;

    SQLiteDatabase db = MyApplication.getDbHelper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_seats, container, false);
        ctx = this.getContext();
//        notifyIncomplete();
        etSeatNumber = (EditText) v.findViewById(R.id.etSeatNumber);
        txtSeatSelected = (TextView) v.findViewById(R.id.txt_seat_selected);

        final Trip tripData = cupboard().withDatabase(db).get(Trip.class, GlobalVars.steppedTripId);;
        Vehicle vehicleData = cupboard().withDatabase(db).get(Vehicle.class, tripData.getVehicle_id());
        int seats_total=vehicleData.getSeats();

        final ArrayList<String> items = new ArrayList<>();
        for (int i=0; i<seats_total; i++) {

            items.add(i+ "");
        }

        GridLayoutManager manager = new GridLayoutManager(ctx, COLUMNS);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.lst_items);
        try {
            recyclerView.setLayoutManager(manager);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        AirplaneAdapter  adapter = new AirplaneAdapter(ctx, items,txtSeatSelected);

        recyclerView.setAdapter(adapter);


        txtSeatSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String strSeatNo = etSeatNumber.getText().toString().trim();
//                GlobalVars.steppedSeatNo = strSeatNo;
                //notifyCompleted();

                Fragment mFragment = new StepTicketing();
                getFragmentManager().beginTransaction().replace(R.id.generic_list, mFragment).commit();
            }
        });
        return v;

    }
}
