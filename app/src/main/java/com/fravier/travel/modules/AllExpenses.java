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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.ExpenseRecyclerAdapter;
import com.fravier.travel.adapters.TripAutoCompleteAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Expense;
import com.fravier.travel.models.Trip;
import com.fravier.travel.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllExpenses extends Fragment {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private TripAutoCompleteAdapter autoCompleteAdapter;
    public long autoCompleteSelectedTrip;
    List<Expense> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_expenses, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("EXPENSES");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all expenses");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trip trip = cupboard().withDatabase(db).query(Trip.class).get();
                if (trip == null) {
                    Toast.makeText(ctx, "You need to configure at least one trip before you configure a expense.\nThank you", Toast.LENGTH_LONG).show();
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

        lstItems = new ArrayList<Expense>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Expense.class).getCursor();
        try {
            QueryResultIterable<Expense> itr = cupboard().withCursor(itemsCursor).iterate(Expense.class);
            for (Expense d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new ExpenseRecyclerAdapter(lstItems);
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
                                    "Expense Name:" + lstItems.get(pos).getName() + "\n" +
                                    "Description: " + lstItems.get(pos).getDescription() + "\n" +
                                    "Trip:" + cupboard().withDatabase(db).get(Trip.class, lstItems.get(pos).getTrip_id()).getDate_travel() + "\n\n" +

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
        builder.title("New Expense");
        builder.customView(R.layout.popup_expense_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View cv = dialog.getCustomView();
                EditText etName = (EditText) cv.findViewById(R.id.etName);
                EditText etDescription = (EditText) cv.findViewById(R.id.etDescription);
                AutoCompleteTextView etTrip = (AutoCompleteTextView) cv.findViewById(R.id.etTrip);

                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strTrip = etTrip.getText().toString().trim();


                String errors = "";
                if (strName.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if ((autoCompleteSelectedTrip + "").trim().isEmpty()) {
                    errors += "The trip is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Expense expense = new Expense();
                    expense.setName(strName);
                    expense.setDescription(strDescription);
                    expense.setTrip_id(autoCompleteSelectedTrip);
                    expense.setCreated_by(GlobalVars.username);
                    expense.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                    expense.setUpdated_by(GlobalVars.username);
                    expense.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                    long id = cupboard().withDatabase(db).put(expense);
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
        final AutoCompleteTextView etTrip = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.etTrip);

        autoCompleteAdapter = new TripAutoCompleteAdapter((Activity) ctx, getTripList());
        etTrip.setAdapter(autoCompleteAdapter);
        etTrip.setThreshold(1);
        etTrip.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etTrip.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getDate_travel();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etTrip.setText("");
                }
            }

        });
        etTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedTrip = id;
                System.out.println(id);
            }

        });


        dialog.show();
    }

    public void modifyRecord(final Expense oldExpense) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldExpense.getName());
        builder.customView(R.layout.popup_expense_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                EditText etName = (EditText) dialog.findViewById(R.id.etName);
                EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);
                AutoCompleteTextView etTrip = (AutoCompleteTextView) dialog.findViewById(R.id.etTrip);


                String strName = etName.getText().toString().trim();
                String strDescription = etDescription.getText().toString().trim();
                String strTrip = etTrip.getText().toString().trim();

                String errors = "";
                if (strName.trim().isEmpty()) {
                    errors += "The name is required\n";
                }
                if (strDescription.trim().isEmpty()) {
                    errors += "The description is required\n";
                }
                if ((autoCompleteSelectedTrip + "").trim().isEmpty()) {
                    errors += "The trip is required\n";
                }
                if (!errors.trim().isEmpty()) {
                    Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                } else {

                    Expense updatedExpense = new Expense();
                    updatedExpense.setId(oldExpense.getId());
                    updatedExpense.setName(strName);
                    updatedExpense.setDescription(strDescription);
                    updatedExpense.setTrip_id(autoCompleteSelectedTrip);
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
        final AutoCompleteTextView etTrip = (AutoCompleteTextView) dialog.findViewById(R.id.etTrip);

        etName.setText(oldExpense.getName());
        etDescription.setText(oldExpense.getDescription());
        etTrip.setText(cupboard().withDatabase(db).get(Trip.class, oldExpense.getTrip_id()).getDate_travel());
        etDescription.setText(oldExpense.getDescription());


        autoCompleteAdapter = new TripAutoCompleteAdapter((Activity) ctx, getTripList());
        etTrip.setAdapter(autoCompleteAdapter);
        etTrip.setThreshold(1);
        etTrip.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = etTrip.getText().toString().trim();

                    for (int i = 0; i < autoCompleteAdapter.getCount(); i++) {
                        String temp = autoCompleteAdapter.getItem(i).getDate_travel();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    etTrip.setText("");
                }
            }

        });
        etTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteSelectedTrip = id;
                System.out.println(id);
            }

        });
        dialog.show();
    }


    public ArrayList<Trip> getTripList() {
        ArrayList<Trip> lstTrips = new ArrayList<Trip>();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Trip.class).getCursor();
        try {
            QueryResultIterable<Trip> itr = cupboard().withCursor(itemsCursor).iterate(Trip.class);
            for (Trip d : itr) {
                lstTrips.add(d);
            }
        } finally {
            itemsCursor.close();
        }
        return lstTrips;
    }
}
