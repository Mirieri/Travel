package com.fravier.travel.wizards.initialconifigs;


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
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.RouteRecyclerAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Route;
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

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step_routes, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ROUTES");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all routes");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wrapInScrollView = true;
                new MaterialDialog.Builder(ctx)
                        .title("New Route")
                        .customView(R.layout.popup_route_create, wrapInScrollView)
                        .positiveText("Save")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View cv = dialog.getCustomView();
                                EditText etName = (EditText) cv.findViewById(R.id.etName);
                                EditText etDescription = (EditText) cv.findViewById(R.id.etDescription);
                                EditText etTownFrom = (EditText) cv.findViewById(R.id.etTownFrom);
                                EditText etTownTo = (EditText) cv.findViewById(R.id.etTownTo);
                                EditText etEstimatedTime = (EditText) cv.findViewById(R.id.etEstimatedTime);
                                EditText etComments = (EditText) cv.findViewById(R.id.etComments);

                                String strName = etName.getText().toString().trim();
                                String strDescription = etDescription.getText().toString().trim();
                                String strTownFrom = etTownFrom.getText().toString().trim();
                                String strTownTo = etTownTo.getText().toString().trim();
                                String strEstimatedTime = etEstimatedTime.getText().toString().trim();
                                String strComments = etComments.getText().toString().trim();

                                String errors="";
                                if (strName.trim().isEmpty()) {
                                    errors += "The name is required\n";
                                }
                                if (strDescription.trim().isEmpty()) {
                                    errors += "The description is required\n";
                                }
                                if (strTownFrom.trim().isEmpty()) {
                                    errors += "The name is required\n";
                                }
                                if (strTownTo.trim().isEmpty()) {
                                    errors += "The description is required\n";
                                }
                                if (strEstimatedTime.trim().isEmpty()) {
                                    errors += "The description is required\n";
                                }
                                if (!errors.trim().isEmpty()) {
                                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                                } else {


                                    Route route = new Route();
                                    route.setName(strName);
                                    route.setDescription(strDescription);
                                    route.setTown_from(strTownFrom);
                                    route.setTown_to(strTownTo);
                                    route.setEstimated_time(strEstimatedTime);
                                    route.setComments(strComments);
                                    route.setCreated_by(GlobalVars.username);
                                    route.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                                    route.setUpdated_by(GlobalVars.username);
                                    route.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                                    long id = cupboard().withDatabase(db).put(route);
                                    if (id > 0) {
                                        dialog.dismiss();
                                        recyclerStuff();
                                        mAdapter.notifyDataSetChanged();

                                        Snackbar.make(coordinatorLayout, "The route record was successfully saved", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(coordinatorLayout, "There were problems saving the routes records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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

    public void modifyRecord(final Route oldRoute) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldRoute.getName());
        builder.customView(R.layout.popup_route_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                EditText etName = (EditText) dialog.getCustomView().findViewById(R.id.etName);
                EditText etDescription = (EditText) dialog.getCustomView().findViewById(R.id.etDescription);
                EditText etTownFrom = (EditText) dialog.getCustomView().findViewById(R.id.etTownFrom);
                EditText etTownTo = (EditText) dialog.getCustomView().findViewById(R.id.etTownTo);
                EditText etEstimatedTime = (EditText) dialog.getCustomView().findViewById(R.id.etEstimatedTime);
                EditText etComments = (EditText) dialog.getCustomView().findViewById(R.id.etComments);

                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strTownFrom = etTownFrom.getText().toString().trim();
                String strTownTo = etTownTo.getText().toString().trim();
                String strEstimatedTime = etEstimatedTime.getText().toString().trim();
                String strComments = etComments.getText().toString().trim();


                Route updatedRoute = new Route();
                updatedRoute.setId(oldRoute.getId());
                updatedRoute.setName(strName);
                updatedRoute.setDescription(strDescription);
                updatedRoute.setTown_from(strTownFrom);
                updatedRoute.setTown_to(strTownTo);
                updatedRoute.setEstimated_time(strEstimatedTime);
                updatedRoute.setComments(strComments);
                updatedRoute.setCreated_by(oldRoute.getCreated_by());
                updatedRoute.setCreated_at(oldRoute.getCreated_at());
                updatedRoute.setUpdated_by(GlobalVars.username);
                updatedRoute.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(updatedRoute);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(coordinatorLayout, "The route record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the routes records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        EditText etDescription = (EditText) dialog.getCustomView().findViewById(R.id.etDescription);
        EditText etTownFrom = (EditText) dialog.getCustomView().findViewById(R.id.etTownFrom);
        EditText etTownTo = (EditText) dialog.getCustomView().findViewById(R.id.etTownTo);
        EditText etEstimatedTime = (EditText) dialog.getCustomView().findViewById(R.id.etEstimatedTime);
        EditText etComments = (EditText) dialog.getCustomView().findViewById(R.id.etComments);


        etName.setText(oldRoute.getName());
        etDescription.setText(oldRoute.getDescription());
        etTownFrom.setText(oldRoute.getTown_from());
        etTownTo.setText(oldRoute.getTown_to());
        etEstimatedTime.setText(oldRoute.getEstimated_time());
        etComments.setText(oldRoute.getComments());
        dialog.show();
    }

}
