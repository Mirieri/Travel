package com.fravier.travel.modules;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.Home;
import com.fravier.travel.R;
import com.fravier.travel.adapters.RouteAutoCompleteAdapter;
import com.fravier.travel.adapters.TripRecyclerAdapter;
import com.fravier.travel.adapters.VehicleAutoCompleteAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Vehicle;
import com.fravier.travel.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllTrips extends Fragment {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private RouteAutoCompleteAdapter autoCompleteAdapter;
    private VehicleAutoCompleteAdapter autoCompleteAdapterVehicle;
    public long autoCompleteSelectedRoute;
    public long autoCompleteSelectedVehicle;
    public FragmentManager fragmentManager;
    List<Trip> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();
    //    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time1, time2;
    public static String strDateFrom, strDateTo;

    //
    Calendar myCalendar = Calendar.getInstance();
    TextView txDateFrom, txDateTo;
    Button btSearch;
    DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {
        private void updateDate() {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            txDateFrom.setText(localSimpleDateFormat.format(myCalendar.getTime()));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };
    DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
        private void updateDate() {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            txDateTo.setText(localSimpleDateFormat.format(myCalendar.getTime()));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };
//


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_trips, container, false);
        ctx = container.getContext();
        fragmentManager = getActivity().getSupportFragmentManager();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("TRIPS");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all trips");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecord();
            }
        });

        txDateFrom = (TextView) v.findViewById(R.id.txDateFrom);
        txDateTo = (TextView) v.findViewById(R.id.txDateTo);
        btSearch = (Button) v.findViewById(R.id.btSearch);

        //setting dates as default
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        txDateFrom.setText(localSimpleDateFormat.format(myCalendar.getTime()));
        txDateTo.setText(localSimpleDateFormat.format(myCalendar.getTime()));

        txDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ctx, dateFrom, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        txDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ctx, dateTo, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData();
            }
        });
        strDateFrom = txDateFrom.getText().toString().trim();
        strDateTo = txDateTo.getText().toString().trim();

        recyclerStuff(strDateFrom, strDateTo);
        return v;
    }

    public void searchData() {
        strDateFrom = txDateFrom.getText().toString().trim();
        strDateTo = txDateTo.getText().toString().trim();

        recyclerStuff(strDateFrom, strDateTo);
    }

    public void recyclerStuff(String dateFrom, String dateTo) {
        final GestureDetector mGestureDetector = new GestureDetector(
                ctx,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        lstItems = new ArrayList<Trip>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Trip.class).getCursor();
        try {
//            QueryResultIterable<Trip> itr = cupboard().withCursor(itemsCursor).iterate(Trip.class);
            QueryResultIterable<Trip> itr = cupboard()
                    .withDatabase(db)
                    .query(Trip.class)
                    .withSelection("created_at BETWEEN ? AND ? ", dateFrom, dateTo)
                    .query();
            for (Trip d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new TripRecyclerAdapter(lstItems);
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
                            GlobalVars.selectedTrip = lstItems.get(pos).getId();
                            String data = "" +
//                                    "Route: " + cupboard().withDatabase(db).get(Route.class, lstItems.get(pos).getRoute_id()).getName() + "\n" +
                                    "Vehicle:" + cupboard().withDatabase(db).get(Vehicle.class, lstItems.get(pos).getVehicle_id()).getRegistration_plate() + "\n" +

                                    "Travel Date: " + lstItems.get(pos).getDate_travel() + "\n" +
                                    "Reporting Time:" + lstItems.get(pos).getTime_reporting() + "\n" +
                                    "Departure Time:" + lstItems.get(pos).getTime_departure() + "\n" +
                                    "Passenger Fare:" + lstItems.get(pos).getFare() + "\n\n" +

                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItems.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at() + "\n" +
                                    "Updated By: " + lstItems.get(pos).getUpdated_by();
//                            String title = "Route :" + cupboard().withDatabase(db).get(Route.class, lstItems.get(pos).getRoute_id()).getName()  + "\n" +
//                                    "Vehicle:" + cupboard().withDatabase(db).get(Vehicle.class, lstItems.get(pos).getVehicle_id()).getRegistration_plate();
                            String title = "Route :" + lstItems.get(pos).getDate_travel() + "\n" +
                                    "Vehicle:" + cupboard().withDatabase(db).get(Vehicle.class, lstItems.get(pos).getVehicle_id()).getRegistration_plate();

                            new MaterialDialog.Builder(ctx)
                                    .title(title)
                                    .icon(Utilities.generateNameImage(title))
                                    .content(data)
                                    .positiveText("DISMISS")
                                    .negativeText("MODIFY")
                                    .neutralText("TICKETS")
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
                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.container, new TripTickets()).commit();
                                        }
                                    })
//                                    .neutralText("DELETE")
//                                    .neutralColor(getResources().getColor(R.color.red))
//                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
//                                        @Override
//                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                            Snackbar
//                                                    .make(coordinatorLayout, "Are you sure you would like to delete this item ?.", Snackbar.LENGTH_LONG)
//                                                    .setAction("DELETE", new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            if (cupboard().withDatabase(db).delete(lstItems.get(pos))) {
//                                                                mAdapter.notifyDataSetChanged();
//                                                                recyclerStuff();
//
//                                                                Snackbar.make(coordinatorLayout, "The item has been deleted.", Snackbar.LENGTH_LONG)
//                                                                        .show();
//                                                            } else {
//                                                                Snackbar.make(coordinatorLayout, "A problem was encountered while attempting to delete the item.", Snackbar.LENGTH_SHORT)
//                                                                        .show();
//                                                            }
//
//                                                        }
//                                                    })
//                                                    .show();
//                                        }
//                                    })
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
        builder.title("New Trip");
        builder.customView(R.layout.popup_trip_create, wrapInScrollView);
        builder.positiveText("Save");

        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View cv = dialog.getCustomView();
                AutoCompleteTextView etRoute = (AutoCompleteTextView) cv.findViewById(R.id.etRoute);
                AutoCompleteTextView etVehicle = (AutoCompleteTextView) cv.findViewById(R.id.etVehicle);

                EditText etDateTravel = (EditText) cv.findViewById(R.id.etDateTravel);
                EditText etTimeReporting = (EditText) cv.findViewById(R.id.etTimeReporting);
                EditText etDepartureTime = (EditText) cv.findViewById(R.id.etDepartureTime);
                EditText etFare = (EditText) cv.findViewById(R.id.etFare);


                String strDateTravel = etDateTravel.getText().toString().trim();
                String strTimeReporting = etTimeReporting.getText().toString().trim();
                String strDepartureTime = etDepartureTime.getText().toString().trim();
                String strFare = etFare.getText().toString().trim();

                String errors = "";
                if (strDateTravel.trim().isEmpty()) {
                    errors += "The date of travel is required\n";
                }
                if (strTimeReporting.trim().isEmpty()) {
                    errors += "The time reporting is required\n";
                }
                if (strDepartureTime.trim().isEmpty()) {
                    errors += "The departure time is required\n";
                }
                if (strFare.trim().isEmpty()) {
                    errors += "The fare is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {


                    Trip trip = new Trip();
                    trip.setDate_travel(strDateTravel);
                    trip.setTime_reporting(strTimeReporting);
                    trip.setTime_departure(strDepartureTime);
                    trip.setFare(Double.parseDouble(strFare));
                    trip.setRoute_id(autoCompleteSelectedRoute);
                    trip.setVehicle_id(autoCompleteSelectedVehicle);
                    trip.setClose_status("0");
                    trip.setCreated_by(GlobalVars.username);
                    trip.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                    trip.setUpdated_by(GlobalVars.username);
                    trip.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(trip);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuff(strDateFrom, strDateTo);
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(coordinatorLayout, "The trip record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the trips records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        final AutoCompleteTextView etRoute = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etRoute);
        final AutoCompleteTextView etVehicle = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etVehicle);
        final EditText etDateTravel = (EditText) dialog.getCustomView().findViewById(R.id.etDateTravel);
        final EditText etTimeReporting = (EditText) dialog.getCustomView().findViewById(R.id.etTimeReporting);
        final EditText etDepartureTime = (EditText) dialog.getCustomView().findViewById(R.id.etDepartureTime);


        autoCompleteAdapter = new RouteAutoCompleteAdapter((Activity) ctx, getRouteList());
        autoCompleteAdapterVehicle = new VehicleAutoCompleteAdapter((Activity) ctx, getVehicleList());
        etRoute.setAdapter(autoCompleteAdapter);
        etRoute.setThreshold(1);

        etVehicle.setAdapter(autoCompleteAdapterVehicle);
        etVehicle.setThreshold(1);

        etRoute.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etRoute.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etRoute.setText("");
                }
            }

        });
        etRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedRoute = id;
                System.out.println(id);
            }

        });


        etVehicle.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etVehicle.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapterVehicle.getCount(); i++) {
                        String temp = autoCompleteAdapterVehicle.getItem(i).getRegistration_plate();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etVehicle.setText("");
                }
            }

        });
        etVehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedVehicle = id;
                System.out.println(id);
            }

        });

        //dates

        date = new DatePickerDialog.OnDateSetListener() {
            private void updateDate() {
                SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                etDateTravel.setText(localSimpleDateFormat.format(myCalendar.getTime()));
            }

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDate();
            }
        };
        time1 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR, hour);
                myCalendar.set(Calendar.MINUTE, minute);

                updateTime();
            }

            private void updateTime() {
                SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                etTimeReporting.setText(localSimpleDateFormat.format(myCalendar.getTime()));
            }


        };
        time2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR, hour);
                myCalendar.set(Calendar.MINUTE, minute);

                updateTime();
            }

            private void updateTime() {
                SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                etDepartureTime.setText(localSimpleDateFormat.format(myCalendar.getTime()));
            }


        };
        etDateTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(ctx, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate(-2208999600000L);
                datePickerDialog.show();

            }
        });
        etTimeReporting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog datePickerDialog;
                datePickerDialog = new TimePickerDialog(ctx, time1, myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE), true);
                datePickerDialog.show();

            }
        });
        etDepartureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog datePickerDialog;
                datePickerDialog = new TimePickerDialog(ctx, time2, myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE), true);
                datePickerDialog.show();

            }
        });
        dialog.show();
    }

    public void modifyRecord(final Trip oldTrip) {
        String title = "Route:" + cupboard().withDatabase(db).get(Route.class, oldTrip.getRoute_id()).getName() + "\n" +
                "Vehicle:" + cupboard().withDatabase(db).get(Vehicle.class, oldTrip.getVehicle_id()).getRegistration_plate();

        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(title);
        builder.customView(R.layout.popup_trip_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                AutoCompleteTextView etRoute = (AutoCompleteTextView) dialog.findViewById(R.id.etRoute);
                AutoCompleteTextView etVehicle = (AutoCompleteTextView) dialog.findViewById(R.id.etVehicle);

                EditText etDateTravel = (EditText) dialog.findViewById(R.id.etDateTravel);
                EditText etTimeReporting = (EditText) dialog.findViewById(R.id.etTimeReporting);
                EditText etDepartureTime = (EditText) dialog.findViewById(R.id.etDepartureTime);
                EditText etFare = (EditText) dialog.findViewById(R.id.etFare);


                String strDateTravel = etDateTravel.getText().toString().trim();
                String strTimeReporting = etTimeReporting.getText().toString().trim();
                String strDepartureTime = etDepartureTime.getText().toString().trim();
                String strFare = etFare.getText().toString().trim();


                Trip updatedTrip = new Trip();
                updatedTrip.setId(oldTrip.getId());

                updatedTrip.setDate_travel(strDateTravel);
                updatedTrip.setTime_reporting(strTimeReporting);
                updatedTrip.setTime_departure(strDepartureTime);
                updatedTrip.setFare(Double.parseDouble(strFare));
                updatedTrip.setRoute_id(autoCompleteSelectedRoute);
                updatedTrip.setVehicle_id(autoCompleteSelectedVehicle);

                updatedTrip.setCreated_by(oldTrip.getCreated_by());
                updatedTrip.setCreated_at(oldTrip.getCreated_at());

                updatedTrip.setUpdated_by(GlobalVars.username);
                updatedTrip.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(updatedTrip);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff(strDateFrom, strDateTo);
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(coordinatorLayout, "The trip record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the trips records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        final AutoCompleteTextView etRoute = (AutoCompleteTextView) dialog.findViewById(R.id.etRoute);
        final AutoCompleteTextView etVehicle = (AutoCompleteTextView) dialog.findViewById(R.id.etVehicle);

        EditText etDateTravel = (EditText) dialog.findViewById(R.id.etDateTravel);
        EditText etTimeReporting = (EditText) dialog.findViewById(R.id.etTimeReporting);
        EditText etDepartureTime = (EditText) dialog.findViewById(R.id.etDepartureTime);
        EditText etFare = (EditText) dialog.findViewById(R.id.etFare);

        etRoute.setText(cupboard().withDatabase(db).get(Route.class, oldTrip.getRoute_id()).getName());
        etVehicle.setText(cupboard().withDatabase(db).get(Vehicle.class, oldTrip.getVehicle_id()).getRegistration_plate());
        etDateTravel.setText(oldTrip.getDate_travel());
        etTimeReporting.setText(oldTrip.getTime_reporting());
        etDepartureTime.setText(oldTrip.getTime_departure());
        etFare.setText(oldTrip.getFare() + "");


        autoCompleteAdapter = new RouteAutoCompleteAdapter((Activity) ctx, getRouteList());
        autoCompleteAdapterVehicle = new VehicleAutoCompleteAdapter((Activity) ctx, getVehicleList());
        etRoute.setAdapter(autoCompleteAdapter);
        etRoute.setThreshold(1);

        etVehicle.setAdapter(autoCompleteAdapterVehicle);
        etVehicle.setThreshold(1);

        etRoute.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etRoute.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etRoute.setText("");
                }
            }

        });
        etRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedRoute = id;
                System.out.println(id);
            }

        });


        etVehicle.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etVehicle.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapterVehicle.getCount(); i++) {
                        String temp = autoCompleteAdapterVehicle.getItem(i).getRegistration_plate();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etVehicle.setText("");
                }
            }

        });
        etVehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedVehicle = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }


    public ArrayList<Route> getRouteList() {
        ArrayList<Route> lstRoutes = new ArrayList<Route>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Route.class).getCursor();
        try {
            QueryResultIterable<Route> itr = cupboard().withCursor(itemsCursor).iterate(Route.class);
            for (Route d : itr) {
                lstRoutes.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstRoutes;
    }

    public ArrayList<Vehicle> getVehicleList() {
        ArrayList<Vehicle> lstVehicles = new ArrayList<Vehicle>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Vehicle.class).getCursor();
        try {
            QueryResultIterable<Vehicle> itr = cupboard().withCursor(itemsCursor).iterate(Vehicle.class);
            for (Vehicle d : itr) {
                lstVehicles.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstVehicles;
    }


}
