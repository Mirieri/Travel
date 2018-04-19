package com.fravier.travel.wizards.ticketing;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.PassengerRecyclerAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Passenger;
import com.fravier.travel.utilities.Utilities;

import org.codepond.wizardroid.WizardStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class StepPassengers extends WizardStep {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    List<Passenger> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    private EditText etIdNumber;
    private Button btSearch;
    public static String national_id = "";

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_step_passengers, container, false);
        ctx = container.getContext();
//        notifyIncomplete();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("PASSENGERS");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all passengers");

        etIdNumber = (EditText) v.findViewById(R.id.etIdNumber);
        btSearch = (Button) v.findViewById(R.id.btSearch);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                national_id = etIdNumber.getText().toString().trim();
                recyclerStuff(national_id);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                String errors = "";
                                if (strFirstName.trim().isEmpty()) {
                                    errors += "The first name is required\n";
                                }
                                if (strLastName.trim().isEmpty()) {
                                    errors += "The last name is required\n";
                                }
                                if (strTelephone.trim().isEmpty()) {
                                    errors += "The telephone is required\n";
                                }
                                if (strNationalID.trim().isEmpty()) {
                                    errors += "The national ID is required\n";
                                }
                                if (!errors.trim().isEmpty()) {
                                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                                } else {

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
                                        recyclerStuff("");
                                        mAdapter.notifyDataSetChanged();
                                        Snackbar.make(coordinatorLayout, "The passenger record was successfully saved", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(coordinatorLayout, "There were problems saving the passengers records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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

        recyclerStuff("");
        return v;
    }

    public void recyclerStuff(String national_id) {
        final GestureDetector mGestureDetector = new GestureDetector(
                ctx,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        lstItems = new ArrayList<Passenger>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Passenger.class).getCursor();
        try {
            QueryResultIterable<Passenger> itr;
            if (national_id.isEmpty()) {
                itr = cupboard().withCursor(itemsCursor).iterate(Passenger.class);
            } else {
                itr = cupboard()
                        .withDatabase(db)
                        .query(Passenger.class)
                        .withSelection("national_id = ? ", national_id)
                        .query();
            }
            for (Passenger d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new PassengerRecyclerAdapter(lstItems);
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
                                    "FirstName:" + lstItems.get(pos).getFirst_name() + "\n" +
                                    "LastName " + lstItems.get(pos).getLast_name() + "\n\n" +
                                    "Telephone: " + lstItems.get(pos).getTelephone() + "\n" +
                                    "Email: " + lstItems.get(pos).getEmail() + "\n" +
                                    "National ID: " + lstItems.get(pos).getNational_id() + "\n" +
                                    "DOB: " + lstItems.get(pos).getDob() + "\n\n" +
                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Created By: " + lstItems.get(pos).getCreated_by() + "\n\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at() + "\n" +
                                    "Updated By: " + lstItems.get(pos).getUpdated_by();
                            new MaterialDialog.Builder(ctx)
                                    .title(lstItems.get(pos).getFirst_name())
                                    .icon(Utilities.generateNameImage(lstItems.get(pos).getFirst_name()))
                                    .content(data)
                                    .positiveText("DISMISS")
                                    .negativeText("SELECT CUSTOMER")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            GlobalVars.steppedCustomerId = lstItems.get(pos).getId();

                                            Fragment mFragment = new StepSeats();
                                            getFragmentManager().beginTransaction().replace(R.id.generic_list, mFragment).commit();

                                            // notifyCompleted();
                                            //intent to change activity
//                                            Intent intent = new Intent(getActivity(), StepSeats.class);
//                                            startActivity(intent);

//                                            modifyRecord(lstItems.get(pos));
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

    public void modifyRecord(final Passenger oldPassenger) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldPassenger.getFirst_name());
        builder.customView(R.layout.popup_passenger_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                EditText etFirstName = (EditText) dialog.getCustomView().findViewById(R.id.etFirstName);
                EditText etLastName = (EditText) dialog.getCustomView().findViewById(R.id.etLastName);
                EditText etTelephone = (EditText) dialog.getCustomView().findViewById(R.id.etTelephone);
                EditText etEmail = (EditText) dialog.getCustomView().findViewById(R.id.etEmail);
                EditText etNationalID = (EditText) dialog.getCustomView().findViewById(R.id.etNationalID);
                EditText etDOB = (EditText) dialog.getCustomView().findViewById(R.id.etDOB);

                String strFirstName = etFirstName.getText().toString().trim();
                String strLastName = etLastName.getText().toString().trim();
                String strTelephone = etTelephone.getText().toString().trim();
                String strEmail = etEmail.getText().toString().trim();
                String strNationalID = etNationalID.getText().toString().trim();
                String strDOB = etDOB.getText().toString().trim();


                Passenger updatedPassenger = new Passenger();
                updatedPassenger.setId(oldPassenger.getId());
                updatedPassenger.setFirst_name(strFirstName);
                updatedPassenger.setLast_name(strLastName);
                updatedPassenger.setTelephone(strTelephone);
                updatedPassenger.setEmail(strEmail);
                updatedPassenger.setNational_id(strNationalID);
                updatedPassenger.setDob(strDOB);
                updatedPassenger.setCreated_by(oldPassenger.getCreated_by());
                updatedPassenger.setCreated_at(oldPassenger.getCreated_at());
                updatedPassenger.setUpdated_by(GlobalVars.username);
                updatedPassenger.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(updatedPassenger);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff("");
                    mAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(getActivity(), StepSeats.class);
                    startActivity(intent);
                   // Snackbar.make(coordinatorLayout, "The passenger record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the passengers records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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

        EditText etFirstName = (EditText) dialog.getCustomView().findViewById(R.id.etFirstName);
        EditText etLastName = (EditText) dialog.getCustomView().findViewById(R.id.etLastName);
        EditText etTelephone = (EditText) dialog.getCustomView().findViewById(R.id.etTelephone);
        EditText etEmail = (EditText) dialog.getCustomView().findViewById(R.id.etEmail);
        EditText etNationalID = (EditText) dialog.getCustomView().findViewById(R.id.etNationalID);
        EditText etDOB = (EditText) dialog.getCustomView().findViewById(R.id.etDOB);


        etFirstName.setText(oldPassenger.getFirst_name());
        etLastName.setText(oldPassenger.getLast_name());
        etTelephone.setText(oldPassenger.getTelephone());
        etEmail.setText(oldPassenger.getEmail());
        etNationalID.setText(oldPassenger.getNational_id());
        etDOB.setText(oldPassenger.getDob());
        dialog.show();
    }

}
