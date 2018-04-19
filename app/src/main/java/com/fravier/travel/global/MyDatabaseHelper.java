package com.fravier.travel.global;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.fravier.travel.models.Driver;
import com.fravier.travel.models.Expense;
import com.fravier.travel.models.Passenger;
import com.fravier.travel.models.RoleUser;
import com.fravier.travel.models.Roles;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Station;
import com.fravier.travel.models.Ticket;
import com.fravier.travel.models.Town;
import com.fravier.travel.models.Trip;
import com.fravier.travel.models.Users;
import com.fravier.travel.models.Vehicle;

import java.io.File;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by francis on 06/07/2016.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_PATH="";
    private static final String DATABASE_NAME = "travel.db";
    private static final String FILE_DIR="travel";
    private static final int DATABASE_VERSION = 1;


    public MyDatabaseHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR
                + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    static {
        // register our models
        cupboard().register(Driver.class);
        cupboard().register(Expense.class);
        cupboard().register(Passenger.class);
        cupboard().register(Roles.class);
        cupboard().register(RoleUser.class);
        cupboard().register(Route.class);
        cupboard().register(Station.class);
        cupboard().register(Ticket.class);
        cupboard().register(Town.class);
        cupboard().register(Trip.class);
        cupboard().register(Users.class);
        cupboard().register(Vehicle.class);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
        // add indexes and other database tweaks in this method if you want
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted
        cupboard().withDatabase(db).upgradeTables();
        // do migration work if you have an alteration to make to your schema here

    }
    public String getTotalFare() {
        Cursor cursor;
        //calculate the total amount of fare
        SQLiteDatabase db = MyApplication.getDbHelper();

        cursor = db.rawQuery("SELECT SUM(fare)\n" +
                "FROM Trip\n" +
                "where Trip.vehicle_id = vehicle_id", null);

        while (cursor.moveToFirst()) {
            cursor.getInt(0);
        }

        return String.valueOf(cursor);
    }
    public String getTotalExpense() {
        Cursor cursor1;
        SQLiteDatabase db = MyApplication.getDbHelper();
        //calculate the total amount of expenses for a vehicle
        cursor1 = db.rawQuery("SELECT SUM(total_expenses)\n" +
                "FROM Trip\n" +
                "where Trip.vehicle_id = vehicle_id", null);

        while (cursor1.moveToFirst()) {
            cursor1.getInt(0);
        }
        return String.valueOf(cursor1);
    }

}

