package com.fravier.travel.modules;


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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fravier.travel.R;
import com.fravier.travel.adapters.UserRecyclerAdapter;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Roles;
import com.fravier.travel.models.Users;
import com.fravier.travel.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


/*
1. Replaced email with username



*/

public class AllUsers extends Fragment {

    public Context ctx;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    List<Users> lstItems;

    MaterialDialog.Builder builder;
    MaterialDialog dialog;
    FloatingActionButton fabAdd;

    SQLiteDatabase db = MyApplication.getDbHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_users, container, false);
        ctx = container.getContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.generic_list);
        fabAdd = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("USERS");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("all users");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wrapInScrollView = true;
                MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
                builder.title("New Users");
                builder.customView(R.layout.popup_users_create, wrapInScrollView);
                builder.positiveText("Save");
                builder.negativeText("Cancel");
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View cv = dialog.getCustomView();
                        EditText etFirstName = (EditText) cv.findViewById(R.id.etFirstName);
                        EditText etLastName = (EditText) cv.findViewById(R.id.etLastName);
                        EditText etPassword = (EditText) cv.findViewById(R.id.etPassword);
                        EditText etEmail = (EditText) cv.findViewById(R.id.etEmail);
                        Spinner spRoles = (Spinner) cv.findViewById(R.id.spRole);


                        String strFirstName = etFirstName.getText().toString().trim();
                        String strLastName = etLastName.getText().toString().trim();
                        String strPassword = etPassword.getText().toString().trim();
                        String strEmail = etEmail.getText().toString().trim();
                        String strRole = spRoles.getSelectedItem().toString();

                        String errors = "";
                        if (strFirstName.trim().isEmpty()) {
                            errors += "The first name is required\n";
                        }
                        if (strLastName.trim().isEmpty()) {
                            errors += "The last name is required\n";
                        }
                        if (strPassword.trim().isEmpty()) {
                            errors += "The password is required\n";
                        }
                        if (strEmail.trim().isEmpty()) {
                            errors += "The username is required\n";
                        }

                        if (!errors.trim().isEmpty()) {
                            Toast.makeText(ctx, errors, Toast.LENGTH_LONG).show();
                        } else {

                            Users user = new Users();
                            user.setFirst_name(strFirstName);
                            user.setLast_name(strLastName);
                            user.setPassword(strPassword);
                            user.setPermissions(strRole);
                            user.setEmail(strEmail);
                            user.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
                            long id = cupboard().withDatabase(db).put(user);
                            if (id > 0) {
                                dialog.dismiss();
                                recyclerStuff();
                                mAdapter.notifyDataSetChanged();
                                Snackbar.make(coordinatorLayout, "The user record was successfully saved", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(coordinatorLayout, "There were problems saving the users records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
                Spinner spRoles = (Spinner) dialog.getCustomView().findViewById(R.id.spRole);

                ArrayAdapter<String> adapterRoles = new ArrayAdapter<String>(ctx, R.layout.list_item_sp_top, getDemoRoles());
                adapterRoles.setDropDownViewResource(R.layout.list_item_sp);
                spRoles.setAdapter(adapterRoles);
                dialog.show();


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

        lstItems = new ArrayList<Users>();
        final SQLiteDatabase db = MyApplication.getDbHelper();
        Cursor itemsCursor = cupboard().withDatabase(db).query(Users.class).getCursor();
        try {
            QueryResultIterable<Users> itr = cupboard().withCursor(itemsCursor).iterate(Users.class);
            for (Users d : itr) {
                lstItems.add(d);
            }
        } finally {
            itemsCursor.close();
        }

        Collections.reverse(lstItems);
        mAdapter = new UserRecyclerAdapter(lstItems);
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
                                    "Password: " + lstItems.get(pos).getPassword() + "\n" +
                                    "Username: " + lstItems.get(pos).getEmail() + "\n" +
                                    "Created On: " + lstItems.get(pos).getCreated_at() + "\n" +
                                    "Updated On: " + lstItems.get(pos).getUpdated_at();
                            new MaterialDialog.Builder(ctx)
                                    .title(lstItems.get(pos).getFirst_name())
                                    .icon(Utilities.generateNameImage(lstItems.get(pos).getFirst_name()))
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
                                                            if (lstItems.get(pos).getId() == GlobalVars.user_id) {
                                                                Toast.makeText(ctx, "Action not permitted.\nA logged in user cannot delete themselves.", Toast.LENGTH_LONG).show();
                                                            } else {
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

    public void modifyRecord(final Users oldUsers) {
        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx);
        builder.title(oldUsers.getFirst_name());
        builder.customView(R.layout.popup_users_create, wrapInScrollView);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                EditText etFirstName = (EditText) dialog.getCustomView().findViewById(R.id.etFirstName);
                EditText etLastName = (EditText) dialog.getCustomView().findViewById(R.id.etLastName);
                EditText etPassword = (EditText) dialog.getCustomView().findViewById(R.id.etPassword);
                EditText etEmail = (EditText) dialog.getCustomView().findViewById(R.id.etEmail);
                Spinner spRoles = (Spinner) dialog.getCustomView().findViewById(R.id.spRole);


                String strFirstName = etFirstName.getText().toString().trim();
                String strLastName = etLastName.getText().toString().trim();
                String strPassword = etPassword.getText().toString().trim();
                String strEmail = etEmail.getText().toString().trim();
                String strRoles = spRoles.getSelectedItem().toString();


                Users updatedUsers = new Users();
                updatedUsers.setId(oldUsers.getId());
                updatedUsers.setFirst_name(strFirstName);
                updatedUsers.setLast_name(strLastName);
                updatedUsers.setPassword(strPassword);
                updatedUsers.setPermissions(strRoles);
                updatedUsers.setEmail(strEmail);
                updatedUsers.setCreated_at(oldUsers.getCreated_at());
                updatedUsers.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

                long id = cupboard().withDatabase(db).put(updatedUsers);
                if (id > 0) {
                    dialog.dismiss();
                    recyclerStuff();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(coordinatorLayout, "The user record was successfully saved", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(coordinatorLayout, "There were problems saving the users records\nKindly try again later.", Snackbar.LENGTH_LONG).show();
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
        EditText etPassword = (EditText) dialog.getCustomView().findViewById(R.id.etPassword);
        EditText etEmail = (EditText) dialog.getCustomView().findViewById(R.id.etEmail);
        EditText etNationalID = (EditText) dialog.getCustomView().findViewById(R.id.etNationalID);
        EditText etDOB = (EditText) dialog.getCustomView().findViewById(R.id.etDOB);

        Spinner spRoles = (Spinner) dialog.getCustomView().findViewById(R.id.spRole);
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<String>(ctx, R.layout.list_item_sp_top, getDemoRoles());
        adapterRoles.setDropDownViewResource(R.layout.list_item_sp);
        spRoles.setAdapter(adapterRoles);

        etFirstName.setText(oldUsers.getFirst_name());
        etLastName.setText(oldUsers.getLast_name());
        etPassword.setText(oldUsers.getPassword());
        etEmail.setText(oldUsers.getEmail());
        dialog.show();
    }

    public static String[] getDemoRoles() {
        String[] stringArray = new String[3];
        stringArray[0] = "ADMIN";
        stringArray[1] = "CLERK";
        stringArray[2] = "SUPERUSER";


        return stringArray;
    }
}
