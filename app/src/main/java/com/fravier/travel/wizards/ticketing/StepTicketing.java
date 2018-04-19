package com.fravier.travel.wizards.ticketing;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Driver;
import com.fravier.travel.models.Passenger;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Ticket;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Users;
import com.fravier.travel.models.Vehicle;
import com.fravier.travel.utilities.Utilities;

import org.codepond.wizardroid.WizardStep;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepTicketing extends WizardStep {

    public TextView txPayment;
    public Button btSubmit;
    SQLiteDatabase db = MyApplication.getDbHelper();
    private CoordinatorLayout coordinatorLayout;
    public Context ctx;

    public StepTicketing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_ticketing, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        txPayment = (TextView) v.findViewById(R.id.txPayment);
        btSubmit = (Button) v.findViewById(R.id.btSubmit);
        ctx = container.getContext();

        final Trip tripData = cupboard().withDatabase(db).get(Trip.class, GlobalVars.steppedTripId);
        Ticket ticketData = cupboard().withDatabase(db).get(Ticket.class, tripData.getId());
        final Passenger passengerData = cupboard().withDatabase(db).get(Passenger.class, GlobalVars.steppedCustomerId);
        Route routeData = cupboard().withDatabase(db).get(Route.class, GlobalVars.steppedRouteId);
        Vehicle vehicleData = cupboard().withDatabase(db).get(Vehicle.class, tripData.getVehicle_id());
        Driver driverData = cupboard().withDatabase(db).get(Driver.class, vehicleData.getDriver_id());

        Users usersData = cupboard().withDatabase(db).get(Users.class, GlobalVars.user_id);

        final String generatedTicketSerial = System.currentTimeMillis() + "";
        String generatedCreatedAt = Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000);

        double fare = tripData.getFare();
        String data = "Kindly confirm the following ticket details\n\n";
        data += "Travel Date : " + tripData.getDate_travel() + "\n";
        data += "Reporting Time : " + tripData.getTime_reporting() + "\n";
        data += "Departure Time : " + tripData.getTime_departure() + "\n";
        data += "Amount Kes. : " + tripData.getFare() + "\n";

        data += "Town From  : " + routeData.getTown_from() + "\n";
        data += "Town To  : " + routeData.getTown_to() + "\n";

        data += "Passenger Name  : " + passengerData.getFirst_name() + " " + passengerData.getLast_name() + "\n";
        data += "Telephone  : " + passengerData.getTelephone() + "\n";

        data += "Serial No. : " + generatedTicketSerial + "\n";
        data += "Seat No.  : " + GlobalVars.steppedSeatNo + "\n";

        data += "Created at : " + generatedCreatedAt + "\n";
        data += "Agent Name : " + usersData.getFirst_name() + " " + usersData.getLast_name() + "\n";

        txPayment.setText(data);
//        btSubmit.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            Snackbar.make(coordinatorLayout, "Kindly be patient as we confirm the details", Snackbar.LENGTH_LONG).show();
//
//                                            Ticket ticket = new Ticket();
//                                            ticket.setPassenger_id(passengerData.getId());
//                                            ticket.setTrip_id(GlobalVars.steppedTripId);
//                                            ticket.setSeat_no(generatedTicketSerial);
//                                            ticket.setSeat_no(GlobalVars.steppedSeatNo);
//
//                                            ticket.setCreated_by(GlobalVars.username);
//                                            ticket.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
//                                            ticket.setUpdated_by(GlobalVars.username);
//                                            ticket.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
//
//                                            long id = cupboard().withDatabase(db).put(ticket);
//                                            if (id > 0) {
//
//                                            }
//
//
//                                        }
//                                    }
//
//        );


        return v;
    }


}
