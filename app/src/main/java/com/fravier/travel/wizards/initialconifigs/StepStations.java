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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.StationRecyclerAdapter;
import com.fravier.travel.adapters.TownAutoCompleteAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Station;
import com.fravier.travel.models.Town;
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
public class StepStations extends WizardStep {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private TownAutoCompleteAdapter autoCompleteAdapter;
    public long autoCompleteSelectedTown;
    List<Station> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step_stations, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("STATIONS");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all stations");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Town town = cupboard().withDatabase(db).query(Town.class).get();
                if (town == null) {
                    Toast.makeText(ctx, "You need to configure at least one town before you configure a station.\nThank you", Toast.LENGTH_LONG).show();
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

        lstItems = new ArrayList<Station>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Station.class).getCursor();
        try {
            QueryResultIterable<Station> itr = cupboard().withCursor(itemsCursor).iterate(Station.class);
            for (Station d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new StationRecyclerAdapter(lstItems);
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
                                    "Station Name:" + lstItems.get(pos).getName() + "\n" +
                                    "Description: " + lstItems.get(pos).getDescription() + "\n" +
                                    "Town:" + cupboard().withDatabase(db).get(Town.class, lstItems.get(pos).getTown_id()).getName() + "\n\n" +

                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItems.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at() + "\n" +
                                    "Updated By: " + lstItems.get(pos).getUpdated_by();
                            new MaterialDialog.Builder(ctx)
                                    .title(lstItems.get(pos).getName())
                                    .icon(Utilities.generateNameImage(lstItems.get(pos).getName()))
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

    public void createRecord() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        boolean wrapInScrollView = true;
        builder.title("New Station");
        builder.customView(R.layout.popup_station_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View cv = dialog.getCustomView();
                EditText etName = (EditText) cv.findViewById(R.id.etName);
                EditText etDescription = (EditText) cv.findViewById(R.id.etDescription);
                AutoCompleteTextView etTown = (AutoCompleteTextView) cv.findViewById(R.id.etTown);

                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strTown = etTown.getText().toString().trim();


                String errors = "";
                if (strName.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if ((autoCompleteSelectedTown + "").trim().isEmpty()) {
                    errors += "The town is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Station station = new Station();
                    station.setName(strName);
                    station.setDescription(strDescription);
                    station.setTown_id(autoCompleteSelectedTown);
                    station.setCreated_by(GlobalVars.username);
                    station.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                    station.setUpdated_by(GlobalVars.username);
                    station.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(station);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuff();
                        mAdapter.notifyDataSetChanged();

                        Snackbar.make(coordinatorLayout, "The station record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the stations records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        final AutoCompleteTextView etTown = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etTown);

        autoCompleteAdapter = new TownAutoCompleteAdapter((Activity) ctx, getTownList());
        etTown.setAdapter(autoCompleteAdapter);
        etTown.setThreshold(1);
        etTown.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etTown.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etTown.setText("");
                }
            }

        });
        etTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedTown = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }

    public void modifyRecord(final Station oldStation) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldStation.getName());
        builder.customView(R.layout.popup_station_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText etName = (EditText) dialog.findViewById(R.id.etName);
                EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);
                AutoCompleteTextView etTown = (AutoCompleteTextView) dialog.findViewById(R.id.etTown);


                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strTown = etTown.getText().toString().trim();

                String errors = "";
                if (strName.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if ((autoCompleteSelectedTown + "").trim().isEmpty()) {
                    errors += "The town is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Station updatedStation = new Station();
                    updatedStation.setId(oldStation.getId());
                    updatedStation.setName(strName);
                    updatedStation.setDescription(strDescription);
                    updatedStation.setTown_id(autoCompleteSelectedTown);
                    updatedStation.setCreated_by(oldStation.getCreated_by());
                    updatedStation.setCreated_at(oldStation.getCreated_at());

                    updatedStation.setUpdated_by(GlobalVars.username);
                    updatedStation.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(updatedStation);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuff();
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(coordinatorLayout, "The station record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the stations records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        final AutoCompleteTextView etTown = (AutoCompleteTextView) dialog.findViewById(R.id.etTown);

        etName.setText(oldStation.getName());
        etDescription.setText(oldStation.getDescription());
        etTown.setText(cupboard().withDatabase(db).get(Town.class, oldStation.getTown_id()).getName());
        etDescription.setText(oldStation.getDescription());


        autoCompleteAdapter = new TownAutoCompleteAdapter((Activity) ctx, getTownList());
        etTown.setAdapter(autoCompleteAdapter);
        etTown.setThreshold(1);
        etTown.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etTown.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getName();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etTown.setText("");
                }
            }

        });
        etTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedTown = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }


    public ArrayList<Town> getTownList() {
        ArrayList<Town> lstTowns = new ArrayList<Town>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Town.class).getCursor();
        try {
            QueryResultIterable<Town> itr = cupboard().withCursor(itemsCursor).iterate(Town.class);
            for (Town d : itr) {
                lstTowns.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstTowns;
    }
}
