package com.fravier.travel;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fravier.travel.global.Fonting;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Users;
import com.fravier.travel.modules.AllDrivers;
import com.fravier.travel.modules.AllPassengers;
import com.fravier.travel.modules.AllRoutes;
import com.fravier.travel.modules.AllStations;
import com.fravier.travel.modules.AllTowns;
import com.fravier.travel.modules.AllTrips;
import com.fravier.travel.modules.AllUsers;
import com.fravier.travel.modules.AllVehicles;
import com.fravier.travel.utilities.Utilities;
import com.fravier.travel.wizards.initialconifigs.HostInitConf;
import com.fravier.travel.wizards.ticketing.HostTicketing;

import java.lang.reflect.Field;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean isHome = false;

    public TextView customerName, customerEmail;
    public ImageView customerImage;

    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    FragmentManager fragmentManager = getSupportFragmentManager();
    SQLiteDatabase db = MyApplication.getDbHelper();

    public Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ctx = this;


        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        customerName = (TextView) headerView.findViewById(R.id.customerName);
        customerEmail = (TextView) headerView.findViewById(R.id.customerEmail);
        customerImage = (ImageView) headerView.findViewById(R.id.customerPic);


        String name = GlobalVars.username;
        String email = GlobalVars.telephone;

        customerName.setText(name.toUpperCase());
        customerEmail.setText(email);
        customerImage.setImageDrawable(Utilities.generateNameImage(name));

        try {

            Field mDragger = mDrawerLayout.getClass().getDeclaredField("mLeftDragger");//mRightDragger for right obviously
            mDragger.setAccessible(true);
            ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(mDrawerLayout);

            Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            int edge = mEdgeSize.getInt(draggerObj);
            mEdgeSize.setInt(draggerObj, edge * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Fonting.setTypeFaceForViewGroup((ViewGroup) customerName.getRootView(), ctx,
                Fonting.KEY_LIGHT);

        if (savedInstanceState == null) {
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AllTrips()).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String role = cupboard().withDatabase(db).get(Users.class, GlobalVars.user_id).getPermissions();

        mDrawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_home:
                isHome = true;
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AllTrips()).commit();
                break;
            case R.id.nav_trips:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AllTrips()).commit();
                isHome = false;
                break;
            case R.id.nav_expenses:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, new VoucherRequest()).commit();
                isHome = false;
                break;
//            case R.id.nav_tickets:
////                fragmentManager.beginTransaction()
////                        .replace(R.id.container, new VoucherPending()).commit();
//                isHome = false;
//                break;
            case R.id.nav_drivers:
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllDrivers()).commit();
                    isHome = false;
                }
                break;
            case R.id.nav_vehicles:
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllVehicles()).commit();
                    isHome = false;
                }
                break;
            case R.id.nav_routes:
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllRoutes()).commit();
                    isHome = false;
                }
                break;


            case R.id.nav_passengers:
//                if (!role.equals("ADMIN")) {
//                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
//                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllPassengers()).commit();
                    isHome = false;
//                }
                break;
            case R.id.nav_towns:
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllTowns()).commit();
                    isHome = false;
                }
                break;

            case R.id.nav_stations:
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllStations()).commit();
                    isHome = false;
                }
                break;
            case R.id.nav_users:
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new AllUsers()).commit();
                    isHome = false;
                }
                break;
            case R.id.nav_roles:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new Home()).commit();
                isHome = false;
                break;
            case R.id.nav_signout:
                finish();
                break;
            case R.id.nav_wizard_config:
                isHome = false;
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                   startActivity(new Intent(ctx, HostInitConf.class));
                }
                break;
            case R.id.nav_wizard_ticketing:
                isHome = false;
                if (!role.equals("ADMIN")) {
                    Toast.makeText(ctx, "You are not authorized to view this page", Toast.LENGTH_LONG).show();
                }else {
                    startActivity(new Intent(ctx, HostTicketing.class));
                }
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AllTrips()).commit();
                isHome = false;
                break;
        }
        return false;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.dashboard, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    long lastPress;

    @Override
    public void onBackPressed() {
        if (isHome) {
            exitApplication();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new AllTrips()).commit();
            isHome = true;
        }

    }

    public void exitApplication() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 5000) {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        } else {
            finish();
        }
    }


}
