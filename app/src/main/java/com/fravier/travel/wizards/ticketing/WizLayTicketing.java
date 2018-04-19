package com.fravier.travel.wizards.ticketing;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.Dashboard;
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
import com.fravier.travel.wizards.initialconifigs.*;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class WizLayTicketing extends BasicWizardLayout {
    SQLiteDatabase db = MyApplication.getDbHelper();
    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    Context ctx;

    public WizLayTicketing() {
        super();
    }

    //You must override this method and create a wizard flow by
    //using WizardFlow.Builder as shown in this example
    @Override
    public WizardFlow onSetup() {
//        setNextButtonLabel("Advance");
//        setBackButtonLabel("Return");
//        setFinishButtonLabel("Finalize");

        return new WizardFlow.Builder()
                .addStep(com.fravier.travel.wizards.ticketing.StepRoutes.class, true)
                .addStep(StepPassengers.class, true)
                .addStep(StepSeats.class, true)
                .addStep(StepPayments.class, true)
                .addStep(StepTicketing.class)
                .create();
    }

    @Override
    public void onWizardComplete() {
        ticketData();
    }

    public void ticketData() {
        ctx = getActivity().getApplicationContext();

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


        Ticket ticket = new Ticket();
        ticket.setPassenger_id(passengerData.getId());
        ticket.setTrip_id(GlobalVars.steppedTripId);
        ticket.setSeat_no(generatedTicketSerial);
        ticket.setSeat_no(GlobalVars.steppedSeatNo);

        ticket.setCreated_by(GlobalVars.username);
        ticket.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
        ticket.setUpdated_by(GlobalVars.username);
        ticket.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));




        long id = cupboard().withDatabase(db).put(ticket);
        if (id > 0) {
            getActivity().finish();
            startActivity(new Intent(getActivity().getApplicationContext(), Dashboard.class));
        }

    }
}