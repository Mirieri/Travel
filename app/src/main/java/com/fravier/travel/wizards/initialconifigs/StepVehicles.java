package com.fravier.travel.wizards.initialconifigs;


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
import com.fravier.travel.adapters.DriverAutoCompleteAdapter;
import com.fravier.travel.adapters.VehicleRecyclerAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Driver;
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
public class StepVehicles extends WizardStep {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private DriverAutoCompleteAdapter autoCompleteAdapter;
    public long autoCompleteSelectedDriver;
    List<Vehicle> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step_vehicles, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("VEHICLES");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all vehicles");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Driver driver = cupboard().withDatabase(db).query(Driver.class).get();
                if (driver == null) {
                    Toast.makeText(ctx, "You need to configure at least one driver before you configure a vehicle.\nThank you", Toast.LENGTH_LONG).show();
                } else {
                    createRecord();
                }
            }
        });

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

        lstItems = new ArrayList<Vehicle>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Vehicle.class).getCursor();
        try {
            QueryResultIterable<Vehicle> itr = cupboard().withCursor(itemsCursor).iterate(Vehicle.class);
            for (Vehicle d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new VehicleRecyclerAdapter(lstItems);
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
                                    "Registration Plate:" + lstItems.get(pos).getRegistration_plate() + "\n" +
                                    "Description: " + lstItems.get(pos).getDescription() + "\n" +
                                    "Licence:" + lstItems.get(pos).getLicence() + "\n" +
                                    "Condition:" + lstItems.get(pos).getCondition() + "\n" +
                                    "Driver:" + cupboard().withDatabase(db).get(Driver.class, lstItems.get(pos).getDriver_id()).getName() + "\n" +
                                    "No. of Seats:" + lstItems.get(pos).getSeats() + "\n" +
                                    "Comments:" + lstItems.get(pos).getComments() + "\n\n" +
                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItems.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at() + "\n" +
                                    "Updated By: " + lstItems.get(pos).getUpdated_by();
                            new MaterialDialog.Builder(ctx)
                                    .title(lstItems.get(pos).getRegistration_plate())
                                    .icon(Utilities.generateNameImage(lstItems.get(pos).getRegistration_plate()))
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
                                                            Vehicle vehicle = cupboard().withDatabase(db).query(Vehicle.class).withSelection("driver_id = ?", "" + lstItems.get(pos).getId()).get();
                                                            if (vehicle == null) {
                                                                if (cupboard().withDatabase(db).delete(lstItems.get(pos))) {
                                                                    mAdapter.notifyDataSetChanged();
                                                                    recyclerStuff();

                                                                    Snackbar.make(coordinatorLayout, "The item has been deleted.", Snackbar.LENGTH_LONG)
                                                                            .show();
                                                                } else {
                                                                    Snackbar.make(coordinatorLayout, "A problem was encountered while attempting to delete the item.", Snackbar.LENGTH_SHORT)
                                                                            .show();
                                                                }
                                                            } else {
                                                                Toast.makeText(ctx, "This item has vehicle as dependencies. Delete the dependencies before deleting this item.", Toast.LENGTH_LONG).show();
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
        builder.title("New Vehicle");
        builder.customView(R.layout.popup_vehicle_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View cv = dialog.getCustomView();
                EditText etRegistrationPlate = (EditText) cv.findViewById(R.id.etRegistrationPlate);
                EditText etDescription = (EditText) cv.findViewById(R.id.etDescription);
                EditText etLicence = (EditText) cv.findViewById(R.id.etLicence);
                EditText etCondition = (EditText) cv.findViewById(R.id.etCondition);
                AutoCompleteTextView etDriver = (AutoCompleteTextView) cv.findViewById(R.id.etDriver);
                EditText etSeats = (EditText) cv.findViewById(R.id.etSeats);
                EditText etComments = (EditText) cv.findViewById(R.id.etComments);


                String strRegsitrationPlate = etRegistrationPlate.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strLicence = etLicence.getText().toString().trim();
                String strCondition = etCondition.getText().toString().trim();
                String strDriver = etDriver.getText().toString().trim();
                String strSeats = etSeats.getText().toString().trim();
                String strComments = etComments.getText().toString().trim();

                String errors = "";
                if (strRegsitrationPlate.trim().isEmpty()) {
                    errors += "The registration is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if (strSeats.trim().isEmpty()) {
                    errors += "The seat capacity is required\n";
                }
                if ((autoCompleteSelectedDriver + "").trim().isEmpty()) {
                    errors += "The driver is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Vehicle vehicle = new Vehicle();
                    vehicle.setRegistration_plate(strRegsitrationPlate);
                    vehicle.setDescription(strDescription);
                    vehicle.setLicence(strLicence);
                    vehicle.setCondition(strCondition);
                    vehicle.setDriver_id(autoCompleteSelectedDriver);
                    vehicle.setSeats(Integer.parseInt(strSeats));
                    vehicle.setComments(strComments);
                    vehicle.setCreated_by(GlobalVars.username);
                    vehicle.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                    vehicle.setUpdated_by(GlobalVars.username);
                    vehicle.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(vehicle);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuff();
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(coordinatorLayout, "The vehicle record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the vehicles records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        final AutoCompleteTextView etDriver = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etDriver);

        autoCompleteAdapter = new DriverAutoCompleteAdapter((Activity) ctx, getDriverList());
        etDriver.setAdapter(autoCompleteAdapter);
        etDriver.setThreshold(1);
        etDriver.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etDriver.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etDriver.setText("");
                }
            }

        });
        etDriver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedDriver = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }

    public void modifyRecord(final Vehicle oldVehicle) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldVehicle.getRegistration_plate());
        builder.customView(R.layout.popup_vehicle_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText etRegistrationPlate = (EditText) dialog.findViewById(R.id.etRegistrationPlate);
                EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);
                EditText etLicence = (EditText) dialog.findViewById(R.id.etLicence);
                EditText etCondition = (EditText) dialog.findViewById(R.id.etCondition);
                AutoCompleteTextView etDriver = (AutoCompleteTextView) dialog.findViewById(R.id.etDriver);
                EditText etSeats = (EditText) dialog.findViewById(R.id.etSeats);
                EditText etComments = (EditText) dialog.findViewById(R.id.etComments);


                String strRegsitrationPlate = etRegistrationPlate.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strLicence = etLicence.getText().toString().trim();
                String strCondition = etCondition.getText().toString().trim();
                String strDriver = etDriver.getText().toString().trim();
                String strSeats = etSeats.getText().toString().trim();
                String strComments = etComments.getText().toString().trim();

                Vehicle updatedVehicle = new Vehicle();
                updatedVehicle.setId(oldVehicle.getId());
                updatedVehicle.setRegistration_plate(strRegsitrationPlate);
                updatedVehicle.setDescription(strDescription);
                updatedVehicle.setLicence(strLicence);
                updatedVehicle.setCondition(strCondition);
                updatedVehicle.setDriver_id(autoCompleteSelectedDriver);
                updatedVehicle.setSeats(Integer.parseInt(strSeats));
                updatedVehicle.setComments(strComments);
                updatedVehicle.setCreated_by(oldVehicle.getCreated_by());
                updatedVehicle.setCreated_at(oldVehicle.getCreated_at());

                updatedVehicle.setUpdated_by(GlobalVars.username);
                updatedVehicle.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(updatedVehicle);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(coordinatorLayout, "The vehicle record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the vehicles records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        EditText etRegistrationPlate = (EditText) dialog.findViewById(R.id.etRegistrationPlate);
        EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);
        EditText etLicence = (EditText) dialog.findViewById(R.id.etLicence);
        EditText etCondition = (EditText) dialog.findViewById(R.id.etCondition);
        final AutoCompleteTextView etDriver = (AutoCompleteTextView) dialog.findViewById(R.id.etDriver);
        EditText etSeats = (EditText) dialog.findViewById(R.id.etSeats);
        EditText etComments = (EditText) dialog.findViewById(R.id.etComments);

        etRegistrationPlate.setText(oldVehicle.getRegistration_plate());
        etDescription.setText(oldVehicle.getDescription());
        etLicence.setText(oldVehicle.getLicence());
        etCondition.setText(oldVehicle.getCondition());
        etDriver.setText(cupboard().withDatabase(db).get(Driver.class, oldVehicle.getDriver_id()).getName());
        etSeats.setText(oldVehicle.getSeats() + "");
        etComments.setText(oldVehicle.getComments());
        etDescription.setText(oldVehicle.getDescription());


        autoCompleteAdapter = new DriverAutoCompleteAdapter((Activity) ctx, getDriverList());
        etDriver.setAdapter(autoCompleteAdapter);
        etDriver.setThreshold(1);
        etDriver.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etDriver.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etDriver.setText("");
                }
            }

        });
        etDriver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedDriver = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }


    public ArrayList<Driver> getDriverList() {
        ArrayList<Driver> lstDrivers = new ArrayList<Driver>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Driver.class).getCursor();
        try {
            QueryResultIterable<Driver> itr = cupboard().withCursor(itemsCursor).iterate(Driver.class);
            for (Driver d : itr) {
                lstDrivers.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstDrivers;
    }


}
