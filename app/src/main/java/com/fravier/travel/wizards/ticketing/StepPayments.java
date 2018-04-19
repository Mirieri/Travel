package com.fravier.travel.wizards.ticketing;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Trip;

import org.codepond.wizardroid.WizardStep;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepPayments extends WizardStep {

    public TextView txPayment;
    public Button btSubmit;
    SQLiteDatabase db = MyApplication.getDbHelper();
    private CoordinatorLayout coordinatorLayout;
    public Context ctx;

    public StepPayments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_payments, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        txPayment = (TextView) v.findViewById(R.id.txPayment);
        btSubmit = (Button) v.findViewById(R.id.btSubmit);
        ctx = container.getContext();
//        notifyIncomplete();

        final Trip tripData = cupboard().withDatabase(db).get(Trip.class, GlobalVars.steppedTripId);
//        Route routeData = cupboard().withDatabase(db).get(Route.class, tripData.getRoute_id());
//        Vehicle vehicleData = cupboard().withDatabase(db).get(Vehicle.class, tripData.getVehicle_id());
//        Driver driverData = cupboard().withDatabase(db).get(Driver.class, vehicleData.getDriver_id());

        double fare = tripData.getFare();
        String data = "Kindly confirm that you have received the fare of amount KSh. " + fare + " from the customer.";
        txPayment.setText(data);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(coordinatorLayout, "There payment has been confirmed.", Snackbar.LENGTH_LONG).show();
//                Intent intent = new Intent(getActivity(), StepTicketing.class);
//                startActivity(intent);
//                notifyCompleted();
                Fragment mFragment = new StepSeats();
                getFragmentManager().beginTransaction().replace(R.id.generic_list, mFragment).commit();


            }
        });


        return v;
    }

}
