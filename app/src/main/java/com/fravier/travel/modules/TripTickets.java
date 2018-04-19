package com.fravier.travel.modules;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.ExpenseRecyclerAdapter;
import com.fravier.travel.adapters.PassengerAutoCompleteAdapter;
import com.fravier.travel.adapters.StationAutoCompleteAdapter;
import com.fravier.travel.adapters.TicketRecyclerAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.global.MyDatabaseHelper;
import com.fravier.travel.models.Driver;
import com.fravier.travel.models.Expense;
import com.fravier.travel.models.Passenger;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Station;
import com.fravier.travel.models.Ticket;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Vehicle;
import com.fravier.travel.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripTickets extends Fragment {

    public Context ctx;
    private RecyclerView mRecyclerView, mRecyclerViewExpenses;
    private RecyclerView.Adapter mAdapter,mAdapterExpenses;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutManagerExpenses;
    private CoordinatorLayout coordinatorLayout;
    private StationAutoCompleteAdapter autoCompleteAdapter;
    private PassengerAutoCompleteAdapter autoCompleteAdapterPassenger;
    public long autoCompleteSelectedStation;
    public long autoCompleteSelectedPassenger;
    public TextView txRoute, txDriver, txVehicle, txDateTravel, txTimeReporting, txTimeDeparture, txFare, txTotalFare, txTotalExpenses, txTotalNet, txTripCapacity;
    List<Ticket> lstItems;
    List<Expense> lstItemsExpenses;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();
    public static int TripCapacity = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trip_tickets, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerViewExpenses = (RecyclerView) v.findViewById(R.id.recyclerViewExpenses);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerViewExpenses.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mLayoutManagerExpenses = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewExpenses.setLayoutManager(mLayoutManagerExpenses);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("TRIP DETAILS");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all tickets");

        txRoute = (TextView) v.findViewById(R.id.txRoute);
        txDriver = (TextView) v.findViewById(R.id.txDriver);
        txVehicle = (TextView) v.findViewById(R.id.txVehicle);
        txDateTravel = (TextView) v.findViewById(R.id.txDateTravel);
        txTimeReporting = (TextView) v.findViewById(R.id.txTimeReporting);
        txTimeDeparture = (TextView) v.findViewById(R.id.txTimeDeparture);
        txFare = (TextView) v.findViewById(R.id.txFare);
        txTotalFare = (TextView) v.findViewById(R.id.txTotalFare);
        txTotalExpenses = (TextView) v.findViewById(R.id.txTotalExpenses);
        txTotalNet = (TextView) v.findViewById(R.id.txTotalNet);
        txTripCapacity = (TextView) v.findViewById(R.id.txTripCapacity);


        final Trip tripData = cupboard().withDatabase(db).get(Trip.class, GlobalVars.selectedTrip);
        Route routeData = cupboard().withDatabase(db).get(Route.class, tripData.getRoute_id());
        Vehicle vehicleData = cupboard().withDatabase(db).get(Vehicle.class, tripData.getVehicle_id());
        Driver driverData = cupboard().withDatabase(db).get(Driver.class, vehicleData.getDriver_id());

        TripCapacity = vehicleData.getSeats();



        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ctx);
        txRoute.setText("Route : " + routeData.getTown_from() + " to " + routeData.getTown_to());
        txDriver.setText("Driver : " + driverData.getName());
        txVehicle.setText("Vehicle : " + vehicleData.getRegistration_plate() + " Licence No. : " + vehicleData.getLicence());
        txDateTravel.setText("Travel Date : " + tripData.getDate_travel());
        txTimeReporting.setText("Reporting Time : " + tripData.getTime_reporting());
        txTimeDeparture.setText("Departure Time  : " + tripData.getTime_departure());

        txFare.setText("Fare per Passenger : Kes. " + tripData.getFare());
        txFare.setText("Total Fare : Kes. " + dbHelper.getTotalFare());
        txFare.setText("Total Expenses : Kes. " + dbHelper.getTotalExpense());
        txFare.setText("Total Net : Kes. " + tripData.getTotal_net());

        txTripCapacity.setText("Trip Capacity " + TripCapacity);


        fabAdd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
                boolean wrapInScrollView = true;
                builder.title("Select Action");
                builder.positiveText("Create Ticket");
                builder.negativeText("Create Expense");
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (lstItems.size() > TripCapacity) {
                            Snackbar
                                    .make(coordinatorLayout, "The trip is in full capacity.", Snackbar.LENGTH_LONG)
                                    .setAction("AUTHORISE TRIP", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            tripData.setClose_status("1");
                                            long id_trip = cupboard().withDatabase(db).put(tripData);
                                            if (id_trip > 0) {
                                                Snackbar.make(coordinatorLayout, "The trip has been authorized.", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }else{
                                                Snackbar.make(coordinatorLayout, "A problem was encountered while saving the trip.", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }

                                        }
                                    })
                                    .show();

                        } else {
                            createRecord();
                        }
                    }
                });
                builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        createExpense();
                    }
                });

                MaterialDialog dialog = builder.build();
                dialog.show();


            }
        });

        recyclerStuff();
        recyclerStuffExpenses();
        return v;
    }

    public void recyclerStuff() {
        final GestureDetector mGestureDetector = new GestureDetector(
                ctx,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        lstItems = new ArrayList<Ticket>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Ticket.class).withSelection("trip_id = ?", GlobalVars.selectedTrip + "").getCursor();
        try {
            QueryResultIterable<Ticket> itr = cupboard().withCursor(itemsCursor).iterate(Ticket.class);
            for (Ticket d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new TicketRecyclerAdapter(lstItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView
                .addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(
                            RecyclerView recyclerView, MotionEvent motionEvent) {
                        View child = recyclerView.findChildViewUnder(
                                motionEvent.getX(), motionEvent.getY());

                        if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                            final int pos = mRecyclerView.getChildAdapterPosition(child);
                            long id = mAdapter.getItemId(pos);
                            String passangerName = "" +
                                    "Passenger Name:" + cupboard().withDatabase(db).get(Passenger.class, lstItems.get(pos).getPassenger_id()).getFirst_name() + " " +
                                    cupboard().withDatabase(db).get(Passenger.class, lstItems.get(pos).getPassenger_id()).getLast_name();

                            String data = "" +
                                    "Passenger Name:" + passangerName + "\n" +
                                    "Ticket Serial: " + lstItems.get(pos).getTicket_serial() + "\n" +
                                    "Pickup Station:" + lstItems.get(pos).getStation_id() + "\n" +
                                    "Seat Number: " + lstItems.get(pos).getSeat_no() + "\n\n" +

                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItems.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at() + "\n" +

                                    "Updated By: " + lstItems.get(pos).getUpdated_by();
                            new MaterialDialog.Builder(ctx)
                                    .title(passangerName)
                                    .icon(Utilities.generateNameImage(passangerName))
                                    .content(data)
                                    .positiveText("DISMISS")
                                    .negativeText("MODIFY")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            modifyRecord(lstItems.get(pos));
                                        }
                                    })
                                    .neutralText("DELETE")
                                    .neutralColor(getResources().getColor(R.color.red))
                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Snackbar
                                                    .make(coordinatorLayout, "Are you sure you would like to delete this item ?.", Snackbar.LENGTH_LONG)
                                                    .setAction("DELETE", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (cupboard().withDatabase(db).delete(lstItems.get(pos))) {
                                                                mAdapter.notifyDataSetChanged();
                                                                recyclerStuff();

                                                                Snackbar.make(coordinatorLayout, "The item has been deleted.", Snackbar.LENGTH_LONG)
                                                                        .show();
                                                            } else {
                                                                Snackbar.make(coordinatorLayout, "A problem was encountered while attempting to delete the item.", Snackbar.LENGTH_SHORT)
                                                                        .show();
                                                            }

                                                        }
                                                    })
                                                    .show();
                                        }
                                    })
                                    .show();


                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView recyclerView,
                                             MotionEvent motionEvent) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });
    }

    public void recyclerStuffExpenses() {
        final GestureDetector mGestureDetector = new GestureDetector(
                ctx,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        lstItemsExpenses = new ArrayList<Expense>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Expense.class).getCursor();
        try {
            QueryResultIterable<Expense> itr = cupboard().withCursor(itemsCursor).iterate(Expense.class);
            for (Expense d : itr) {
                lstItemsExpenses.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItemsExpenses);
        mAdapterExpenses = new ExpenseRecyclerAdapter(lstItemsExpenses);
        mRecyclerViewExpenses.setAdapter(mAdapterExpenses);
        mRecyclerViewExpenses
                .addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(
                            RecyclerView recyclerView, MotionEvent motionEvent) {
                        View child = recyclerView.findChildViewUnder(
                                motionEvent.getX(), motionEvent.getY());

                        if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                            final int pos = mRecyclerViewExpenses.getChildAdapterPosition(child);
                            long id = mAdapterExpenses.getItemId(pos);
                            String data = "" +
                                    "Expense Name:" + lstItemsExpenses.get(pos).getName() + "\n" +
                                    "Description: " + lstItemsExpenses.get(pos).getDescription() + "\n" +
                                    "Trip:" + cupboard().withDatabase(db).get(Trip.class, lstItemsExpenses.get(pos).getTrip_id()).getDate_travel() + "\n\n" +

                                    "Created On: " + lstItemsExpenses.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItemsExpenses.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItemsExpenses.get(pos).getUpdated_at() + "\n" +
                                    "Updated By: " + lstItemsExpenses.get(pos).getUpdated_by();
                            new MaterialDialog.Builder(ctx)
                                    .title(lstItemsExpenses.get(pos).getName())
                                    .icon(Utilities.generateNameImage(lstItemsExpenses.get(pos).getName()))
                                    .content(data)
                                    .positiveText("DISMISS")
                                    .negativeText("MODIFY")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            modifyRecordExpenses(lstItemsExpenses.get(pos));
                                        }
                                    })
                                    .neutralText("DELETE")
                                    .neutralColor(getResources().getColor(R.color.red))
                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Snackbar
                                                    .make(coordinatorLayout, "Are you sure you would like to delete this item ?.", Snackbar.LENGTH_LONG)
                                                    .setAction("DELETE", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (cupboard().withDatabase(db).delete(lstItemsExpenses.get(pos))) {
                                                                mAdapterExpenses.notifyDataSetChanged();
                                                                recyclerStuffExpenses();

                                                                Snackbar.make(coordinatorLayout, "The item has been deleted.", Snackbar.LENGTH_LONG)
                                                                        .show();
                                                            } else {
                                                                Snackbar.make(coordinatorLayout, "A problem was encountered while attempting to delete the item.", Snackbar.LENGTH_SHORT)
                                                                        .show();
                                                            }

                                                        }
                                                    })
                                                    .show();
                                        }
                                    })
                                    .show();


                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView recyclerView,
                                             MotionEvent motionEvent) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });
    }


    public void createRecord() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        boolean wrapInScrollView = true;
        builder.title("New Ticket");
        builder.customView(R.layout.popup_ticket_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.neutralText("Create New Passenger");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View cv = dialog.getCustomView();
                EditText etSeatNumber = (EditText) cv.findViewById(R.id.etSeatNumber);
                AutoCompleteTextView etStation = (AutoCompleteTextView) cv.findViewById(R.id.etStation);
                AutoCompleteTextView etPassenger = (AutoCompleteTextView) cv.findViewById(R.id.etPassenger);

                String strSeatNumber = etSeatNumber.getText().toString().trim();

                Ticket ticket = new Ticket();
                ticket.setTrip_id(GlobalVars.selectedTrip);
                ticket.setTicket_serial(System.currentTimeMillis() + "");
                ticket.setPassenger_id(autoCompleteSelectedPassenger);
                ticket.setStation_id(autoCompleteSelectedStation);
                ticket.setSeat_no(strSeatNumber);

                ticket.setCreated_by(GlobalVars.username);
                ticket.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                ticket.setUpdated_by(GlobalVars.username);
                ticket.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(ticket);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(coordinatorLayout, "The ticket record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the tickets records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
                }


            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        builder.onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(ctx)
                        .title("New Passenger")
                        .customView(R.layout.popup_passenger_create, wrapInScrollView)
                        .positiveText("Save")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View cv = dialog.getCustomView();
                                EditText etFirstName = (EditText) cv.findViewById(R.id.etFirstName);
                                EditText etLastName = (EditText) cv.findViewById(R.id.etLastName);
                                EditText etTelephone = (EditText) cv.findViewById(R.id.etTelephone);
                                EditText etEmail = (EditText) cv.findViewById(R.id.etEmail);
                                EditText etNationalID = (EditText) cv.findViewById(R.id.etNationalID);
                                EditText etDOB = (EditText) cv.findViewById(R.id.etDOB);

                                String strFirstName = etFirstName.getText().toString().trim();
                                String strLastName = etLastName.getText().toString().trim();
                                String strTelephone = etTelephone.getText().toString().trim();
                                String strEmail = etEmail.getText().toString().trim();
                                String strNationalID = etNationalID.getText().toString().trim();
                                String strDOB = etDOB.getText().toString().trim();

                                Passenger passenger = new Passenger();
                                passenger.setFirst_name(strFirstName);
                                passenger.setLast_name(strLastName);
                                passenger.setTelephone(strTelephone);
                                passenger.setEmail(strEmail);
                                passenger.setNational_id(strNationalID);
                                passenger.setDob(strDOB);
                                passenger.setCreated_by(GlobalVars.username);
                                passenger.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                                passenger.setUpdated_by(GlobalVars.username);
                                passenger.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                                long id = cupboard().withDatabase(db).put(passenger);
                                if (id > 0) {
                                    dialog.dismiss();
                                    recyclerStuff();
                                    mAdapter.notifyDataSetChanged();
                                    Snackbar.make(coordinatorLayout, "The passenger record was successfully saved", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(coordinatorLayout, "There were problems saving the passengers records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
                                }


                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }

        });
        MaterialDialog dialog = builder.build();
        final AutoCompleteTextView etStation = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etStation);
        final AutoCompleteTextView etPassenger = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etPassenger);

        autoCompleteAdapter = new StationAutoCompleteAdapter((Activity) ctx, getStationList());
        etStation.setAdapter(autoCompleteAdapter);
        etStation.setThreshold(1);

        autoCompleteAdapterPassenger = new PassengerAutoCompleteAdapter((Activity) ctx, getPassengerList());
        etPassenger.setAdapter(autoCompleteAdapterPassenger);
        etPassenger.setThreshold(1);

        etStation.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etStation.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etStation.setText("");
                }
            }

        });
        etStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedStation = id;
                System.out.println(id);
            }

        });

        etPassenger.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etPassenger.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapterPassenger.getCount(); i++) {
                        String temp = autoCompleteAdapterPassenger.getItem(i).getFirst_name();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etPassenger.setText("");
                }
            }

        });
        etPassenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedPassenger = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }

    public void createExpense() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        boolean wrapInScrollView = true;
        builder.title("New Expense");
        builder.customView(R.layout.popup_expense_create_slim, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive( new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View cv = dialog.getCustomView();
                EditText etName = (EditText) cv.findViewById(R.id.etName);
                EditText etDescription = (EditText) cv.findViewById(R.id.etDescription);

                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();


                String errors = "";
                if (strName.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Expense expense = new Expense();
                    expense.setName(strName);
                    expense.setDescription(strDescription);
                    expense.setTrip_id(GlobalVars.selectedTrip);
                    expense.setCreated_by(GlobalVars.username);
                    expense.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                    expense.setUpdated_by(GlobalVars.username);
                    expense.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(expense);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuffExpenses();
                        mAdapterExpenses.notifyDataSetChanged();
                        Snackbar.make(coordinatorLayout, "The expense record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the expenses records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        MaterialDialog dialog = builder.build();
        dialog.show();
    }


    public void modifyRecord(final Ticket oldTicket) {
        String passangerName = "" +
                "Passenger Name:" + cupboard().withDatabase(db).get(Passenger.class, oldTicket.getPassenger_id()).getFirst_name() + " " +
                cupboard().withDatabase(db).get(Passenger.class, oldTicket.getPassenger_id()).getLast_name();

        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(passangerName);
        builder.customView(R.layout.popup_ticket_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText etName = (EditText) dialog.findViewById(R.id.etName);
                EditText etSeatNumber = (EditText) dialog.findViewById(R.id.etSeatNumber);
                AutoCompleteTextView etStation = (AutoCompleteTextView) dialog.findViewById(R.id.etStation);


                String strName = etName.getText().toString().trim();
                String strSeatNumber = etSeatNumber.getText().toString().trim();
                String strStation = etStation.getText().toString().trim();

                Ticket updatedTicket = new Ticket();
                updatedTicket.setId(oldTicket.getId());
                updatedTicket.setTrip_id(GlobalVars.selectedTrip);
                updatedTicket.setTicket_serial(oldTicket.getTicket_serial());
                updatedTicket.setSeat_no(strSeatNumber);
                updatedTicket.setPassenger_id(autoCompleteSelectedPassenger);
                updatedTicket.setStation_id(autoCompleteSelectedStation);
                updatedTicket.setCreated_by(oldTicket.getCreated_by());
                updatedTicket.setCreated_at(oldTicket.getCreated_at());

                updatedTicket.setUpdated_by(GlobalVars.username);
                updatedTicket.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(updatedTicket);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(coordinatorLayout, "The ticket record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the tickets records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
                }


            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        MaterialDialog dialog = builder.build();
        EditText etSeatNumber = (EditText) dialog.findViewById(R.id.etSeatNumber);
        final AutoCompleteTextView etStation = (AutoCompleteTextView) dialog.findViewById(R.id.etStation);
        final AutoCompleteTextView etPassenger = (AutoCompleteTextView) dialog.findViewById(R.id.etPassenger);

        etStation.setText(cupboard().withDatabase(db).get(Station.class, oldTicket.getStation_id()).getName());
        etPassenger.setText(cupboard().withDatabase(db).get(Passenger.class, oldTicket.getStation_id()).getFirst_name());
        etSeatNumber.setText(oldTicket.getSeat_no());


        autoCompleteAdapter = new StationAutoCompleteAdapter((Activity) ctx, getStationList());
        etStation.setAdapter(autoCompleteAdapter);
        etStation.setThreshold(1);

        autoCompleteAdapterPassenger = new PassengerAutoCompleteAdapter((Activity) ctx, getPassengerList());
        etPassenger.setAdapter(autoCompleteAdapterPassenger);
        etPassenger.setThreshold(1);

        etStation.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etStation.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etStation.setText("");
                }
            }

        });
        etStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedStation = id;
                System.out.println(id);
            }

        });

        etPassenger.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etPassenger.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etPassenger.setText("");
                }
            }

        });
        etPassenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedPassenger = id;
                System.out.println(id);
            }

        });

        dialog.show();
    }

    public void modifyRecordExpenses(final Expense oldExpense) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldExpense.getName());
        builder.customView(R.layout.popup_expense_create_slim, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText etName = (EditText) dialog.findViewById(R.id.etName);
                EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);


                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();

                String errors = "";
                if (strName.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Expense updatedExpense = new Expense();
                    updatedExpense.setId(oldExpense.getId());
                    updatedExpense.setName(strName);
                    updatedExpense.setDescription(strDescription);
                    updatedExpense.setTrip_id(GlobalVars.selectedTrip);
                    updatedExpense.setCreated_by(oldExpense.getCreated_by());
                    updatedExpense.setCreated_at(oldExpense.getCreated_at());

                    updatedExpense.setUpdated_by(GlobalVars.username);
                    updatedExpense.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(updatedExpense);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuff();
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(coordinatorLayout, "The expense record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the expenses records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
                    }

                }
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        MaterialDialog dialog = builder.build();
        EditText etName = (EditText) dialog.findViewById(R.id.etName);
        EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);

        etName.setText(oldExpense.getName());
        etDescription.setText(oldExpense.getDescription());

        dialog.show();
    }


    public ArrayList<Station> getStationList() {
        ArrayList<Station> lstStations = new ArrayList<Station>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Station.class).getCursor();
        try {
            QueryResultIterable<Station> itr = cupboard().withCursor(itemsCursor).iterate(Station.class);
            for (Station d : itr) {
                lstStations.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstStations;
    }
    public String getTotalFare() {
        Cursor cursor;
        //calculate the total amount of fare
        SQLiteDatabase db = MyApplication.getDbHelper();

        cursor = db.rawQuery("SELECT SUM(fare)\n" +
                "FROM Trip\n" +
                "where Trip.vehicle_id = vehicle_id", null);

        while (cursor.moveToFirst()) {
            cursor.getInt(0);
        }

        return String.valueOf(cursor);
    }
    public String totalExpense() {
        Cursor cursor1;
        SQLiteDatabase db = MyApplication.getDbHelper();
        //calculate the total amount of expenses for a vehicle
        cursor1 = db.rawQuery("SELECT SUM(total_expenses)\n" +
                "FROM Trip\n" +
                "where Trip.vehicle_id = vehicle_id", null);

        while (cursor1.moveToFirst()) {
            cursor1.getInt(0);
        }
        return String.valueOf(cursor1);
    }


    public ArrayList<Passenger> getPassengerList() {
        ArrayList<Passenger> lstPassengers = new ArrayList<Passenger>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Passenger.class).getCursor();
        try {
            QueryResultIterable<Passenger> itr = cupboard().withCursor(itemsCursor).iterate(Passenger.class);
            for (Passenger d : itr) {
                lstPassengers.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstPassengers;
    }
}
