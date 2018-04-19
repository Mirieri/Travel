package com.fravier.travel.modules;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.RecyclerAdapter;
import com.fravier.travel.adapters.TownRecyclerAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Station;
import com.fravier.travel.models.Town;
import com.fravier.travel.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllTowns extends Fragment {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    List<Town> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_towns, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("TOWNS");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all towns");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(ctx)
                        .title("New Town")
                        .customView(R.layout.popup_town_create, wrapInScrollView)
                        .positiveText("Save")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
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
                                    Town town = new Town();
                                    town.setName(strName);
                                    town.setDescription(strDescription);
                                    town.setCreated_by(GlobalVars.username);
                                    town.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                                    town.setUpdated_by(GlobalVars.username);
                                    town.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                                    long id = cupboard().withDatabase(db).put(town);
                                    if (id > 0) {
                                        dialog.dismiss();
                                        recyclerStuff();
                                        mAdapter.notifyDataSetChanged();
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "The town record was successfully saved", Snackbar.LENGTH_LONG);
                                        View view = snackbar.getView();
                                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(Color.WHITE);
                                        snackbar.show();
                                    } else {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "There were problems saving the towns records\n" +
                                                "Kindly try again later", Snackbar.LENGTH_LONG);
                                        View view = snackbar.getView();
                                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(Color.WHITE);
                                        snackbar.show();
                                    }
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

        lstItems = new ArrayList<Town>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Town.class).getCursor();
        try {
            QueryResultIterable<Town> itr = cupboard().withCursor(itemsCursor).iterate(Town.class);
            for (Town d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new TownRecyclerAdapter(lstItems);
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
                                                            Station station = cupboard().withDatabase(db).query(Station.class).withSelection("town_id = ?", "" + lstItems.get(pos).getId()).get();
                                                            if (station == null) {
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
                                                                Toast.makeText(ctx, "This item has station as dependencies. Delete the dependencies before deleting this item.", Toast.LENGTH_LONG).show();
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

    public void modifyRecord(final Town oldTown) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldTown.getName());
        builder.customView(R.layout.popup_town_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText etName = (EditText) dialog.getCustomView().findViewById(R.id.etName);
                EditText etDescription = (EditText) dialog.getCustomView().findViewById(R.id.etIdNumber);

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
                    Town updatedTown = new Town();
                    updatedTown.setId(oldTown.getId());
                    updatedTown.setName(strName);
                    updatedTown.setDescription(strDescription);
                    updatedTown.setCreated_by(oldTown.getCreated_by());
                    updatedTown.setCreated_at(oldTown.getCreated_at());

                    updatedTown.setUpdated_by(GlobalVars.username);
                    updatedTown.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(updatedTown);
                    if (id > 0) {
                        dialog.dismiss();
                        recyclerStuff();
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(coordinatorLayout, "The town record was successfully saved", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "There were problems saving the towns records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        EditText etName = (EditText) dialog.getCustomView().findViewById(R.id.etName);
        EditText etDescription = (EditText) dialog.getCustomView().findViewById(R.id.etIdNumber);
        etName.setText(oldTown.getName());
        etDescription.setText(oldTown.getDescription());
        dialog.show();
    }

}
