package com.fravier.travel.wizards.ticketing;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.RouteAutoCompleteAdapter;
import com.fravier.travel.adapters.RouteRecyclerAdapter;
import com.fravier.travel.adapters.VehicleAutoCompleteAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Vehicle;
import com.fravier.travel.utilities.Utilities;

import org.codepond.wizardroid.WizardStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepRoutes extends WizardStep {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    List<Route> lstItems;
    public long autoCompleteSelectedRoute;
    public long autoCompleteSelectedVehicle;

    private RouteAutoCompleteAdapter autoCompleteAdapter;
    private VehicleAutoCompleteAdapter autoCompleteAdapterVehicle;
    MaterialDialog.Builder builder;
    MaterialDialog dialog;
//    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step_routes3, container, false);
        ctx = container.getContext();
//        notifyIncomplete();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
//        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ROUTES");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all routes");
//
//        fabAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean wrapInScrollView = true;
//                new MaterialDialog.Builder(ctx)
//                        .title("New Route")
//                        .customView(R.layout.popup_route_create, wrapInScrollView)
//                        .positiveText("Save")
//                        .negativeText("Cancel")
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                View cv = dialog.getCustomView();
//                                EditText etName = (EditText) cv.findViewById(R.id.etName);
//                                EditText etDescription = (EditText) cv.findViewById(R.id.etDescription);
//                                EditText etTownFrom = (EditText) cv.findViewById(R.id.etTownFrom);
//                                EditText etTownTo = (EditText) cv.findViewById(R.id.etTownTo);
//                                EditText etEstimatedTime = (EditText) cv.findViewById(R.id.etEstimatedTime);
//                                EditText etComments = (EditText) cv.findViewById(R.id.etComments);
//
//                                String strName = etName.getText().toString().trim();
//                                String strDescription = etDescription.getText().toString().trim();
//                                String strTownFrom = etTownFrom.getText().toString().trim();
//                                String strTownTo = etTownTo.getText().toString().trim();
//                                String strEstimatedTime = etEstimatedTime.getText().toString().trim();
//                                String strComments = etComments.getText().toString().trim();
//
//                                String errors="";
//                                if (strName.trim().isEmpty()) {
//                                    errors += "The name is required\n";
//                                }
//                                if (strDescription.trim().isEmpty()) {
//                                    errors += "The description is required\n";
//                                }
//                                if (strTownFrom.trim().isEmpty()) {
//                                    errors += "The name is required\n";
//                                }
//                                if (strTownTo.trim().isEmpty()) {
//                                    errors += "The description is required\n";
//                                }
//                                if (strEstimatedTime.trim().isEmpty()) {
//                                    errors += "The description is required\n";
//                                }
//                                if (!errors.trim().isEmpty()) {
//                                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
//                                } else {
//
//
//                                    Route route = new Route();
//                                    route.setName(strName);
//                                    route.setDescription(strDescription);
//                                    route.setTown_from(strTownFrom);
//                                    route.setTown_to(strTownTo);
//                                    route.setEstimated_time(strEstimatedTime);
//                                    route.setComments(strComments);
//                                    route.setCreated_by(GlobalVars.username);
//                                    route.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
//                                    route.setUpdated_by(GlobalVars.username);
//                                    route.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
//
//                                    long id = cupboard().withDatabase(db).put(route);
//                                    if (id > 0) {
//                                        dialog.dismiss();
//                                        recyclerStuff();
//                                        mAdapter.notifyDataSetChanged();
//                                        Snackbar.make(coordinatorLayout, "The route record was successfully saved", Snackbar.LENGTH_LONG).show();
//                                    } else {
//                                        Snackbar.make(coordinatorLayout, "There were problems saving the routes records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
//                                    }
//
//                                }
//                            }
//                        })
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
//
//            }
//        });

        recyclerStuff();
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

        lstItems = new ArrayList<Route>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Route.class).getCursor();
        try {
            QueryResultIterable<Route> itr = cupboard().withCursor(itemsCursor).iterate(Route.class);
            for (Route d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new RouteRecyclerAdapter(lstItems);
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
                            String data = "" +
                                    "Name:" + lstItems.get(pos).getName() + "\n" +
                                    "Description " + lstItems.get(pos).getDescription() + "\n\n" +
                                    "Town From: " + lstItems.get(pos).getTown_from() + "\n" +
                                    "Town To: " + lstItems.get(pos).getTown_to() + "\n" +
                                    "Estimated time: " + lstItems.get(pos).getEstimated_time() + " hours\n" +
                                    "Comments: " + lstItems.get(pos).getComments() + "\n\n" +
                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItems.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at() + "\n" +
                                    "Updated By: " + lstItems.get(pos).getUpdated_by();
                            new MaterialDialog.Builder(ctx)
                                    .title(lstItems.get(pos).getName())
                                    .icon(Utilities.generateNameImage(lstItems.get(pos).getName()))
                                    .content(data)
                                    .positiveText("DISMISS")
                                    .negativeText("SELECT ROUTE")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            //search for trips with this ID and that they are not closed
                                            Long route_id = lstItems.get(pos).getId();
                                            Trip trip = cupboard().withDatabase(db).query(Trip.class).withSelection("route_id = ? and close_status= ?", "" + route_id, "0").get();
                                            if (trip == null) {
                                                createRecord();
                                                Snackbar.make(coordinatorLayout, "No trips were found with this route. Create a new trip.", Snackbar.LENGTH_LONG).show();
//
                                            } else {
                                                GlobalVars.steppedTripId = trip.getId();
                                                GlobalVars.steppedRouteId = lstItems.get(pos).getId();
                                                Snackbar.make(coordinatorLayout, "The route record was successfully selected", Snackbar.LENGTH_LONG).show();
//                                                Intent intent = new Intent(getActivity(), StepPassengers.class);
//                                                startActivity(intent);
                                                Fragment mFragment = new StepPassengers();
                                                getFragmentManager().beginTransaction().replace(R.id.generic_list, mFragment).commit();


                                                //notifyCompleted();
                                            }
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
                    errors += "The description is required\n";
                }
                if (strDepartureTime.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strFare.trim().isEmpty()) {
                    errors += "The description is required\n";
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
                        recyclerStuff();
                        mAdapter.notifyDataSetChanged();
                        Intent intent = new Intent(getActivity(), StepPassengers.class);
                        startActivity(intent);
                        //Snackbar.make(coordinatorLayout, "The trip record was successfully saved", Snackbar.LENGTH_LONG).show();
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
