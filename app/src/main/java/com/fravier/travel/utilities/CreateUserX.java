package com.fravier.travel.utilities;

/**
 * Created by francis on 20/07/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.RoleUser;
import com.fravier.travel.models.Roles;
import com.fravier.travel.models.Users;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class CreateUserX {
    public Context ctx;
    SQLiteDatabase db = MyApplication.getDbHelper();
    public String default_permissions = "{\n" +
            "\"drivers.create\":1,\"drivers.edit\":1,\"drivers.index\":1,\"drivers.show\":1,\n" +
            "\"expenses.create\":1,\"expenses.edit\":1,\"expenses.index\":1,\"expenses.show\":1,\n" +
            "\"passengers.create\":1,\"passengers.edit\":1,\"passengers.index\":1,\"passengers.show\":1,\n" +
            "\"roles.create\":1,\"roles.edit\":1,\"roles.index\":1,\"roles.show\":1,\n" +
            "\"roleusers.create\":1,\"roleusers.edit\":1,\"roleusers.index\":1,\"roleusers.show\":1,\n" +
            "\"routes.create\":1,\"routes.edit\":1,\"routes.index\":1,\"routes.show\":1,\n" +
            "\"stations.create\":1,\"stations.edit\":1,\"stations.index\":1,\"stations.show\":1,\n" +
            "\"tickets.create\":1,\"tickets.edit\":1,\"tickets.index\":1,\"tickets.show\":1,\n" +
            "\"towns.create\":1,\"towns.edit\":1,\"towns.index\":1,\"towns.show\":1,\n" +
            "\"trips.create\":1,\"trips.edit\":1,\"trips.index\":1,\"trips.show\":1,\n" +
            "\"users.create\":1,\"users.edit\":1,\"users.index\":1,\"users.show\":1,\n" +
            "\"vehicles.create\":1,\"vehicles.edit\":1,\"vehicles.index\":1,\"vehicles.show\":1,\n" +
            "}";

    public CreateUserX(Context ctx) {
        this.ctx = ctx;
    }

    public void createUser() {
        Users user = new Users();
        user.setId(1L);
        user.setFirst_name("Administrator");
        user.setLast_name("Admin");
        user.setPassword("1234");
        user.setPermissions("ADMIN");
        user.setEmail("admin@admin.com");
        user.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
        user.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
        long id_users = cupboard().withDatabase(db).put(user);
        if (id_users > 0) {
            System.out.println("User X created");
        }
        Roles roles = new Roles();
        roles.setId(1L);
        roles.setName("Administrator");
        roles.setPermissions(default_permissions);
        roles.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
        roles.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

        long id_roles = cupboard().withDatabase(db).put(user);
        if (id_roles > 0) {
            System.out.println("Role X created");
        }

        RoleUser roleUser = new RoleUser();
        roleUser.setUser_id(1L);
        roleUser.setRole_id(1L);
        roleUser.setCreated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));
        roleUser.setUpdated_at(Utilities.getDateCurrentTimeZone(System.currentTimeMillis() / 1000));

        long id_roleUsers = cupboard().withDatabase(db).put(user);
        if (id_roleUsers > 0) {
            System.out.println("Role User X created");
        }


    }


}
