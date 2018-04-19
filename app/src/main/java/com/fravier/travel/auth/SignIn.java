package com.fravier.travel.auth;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fravier.travel.Dashboard;
import com.fravier.travel.R;
import com.fravier.travel.global.GlobalVars;
import com.fravier.travel.global.MyApplication;
import com.fravier.travel.models.Route;
import com.fravier.travel.models.Station;
import com.fravier.travel.models.Users;
import com.fravier.travel.utilities.CreateUserX;

import java.util.HashMap;
import java.util.Map;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class SignIn extends AppCompatActivity {
    public EditText etUsername, etPassword;
    public Button btLogin;
    public Context ctx;
    SQLiteDatabase db = MyApplication.getDbHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ctx = this;
        Users users = cupboard().withDatabase(db).query(Users.class).get();
        if (users == null) {
            System.out.println("users is null");
            new CreateUserX(ctx).createUser();
        } else {
            System.out.println("user is not null");
        }

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preValidate();
            }
        });
    }

    public void preValidate() {
        String strUsername = etUsername.getText().toString().trim();
        String strPassword = etPassword.getText().toString().trim();

        if (strUsername.isEmpty()) {
            Toast.makeText(ctx, "The username is required", Toast.LENGTH_LONG).show();
        } else if (strPassword.isEmpty()) {
            Toast.makeText(ctx, "The password is required", Toast.LENGTH_LONG).show();
        } else {
            GlobalVars.username = strUsername;
            Users users = cupboard().withDatabase(db).query(Users.class)
                    .withSelection("email = ? and password= ?", strUsername, strPassword)
                    .get();

            if (users == null) {
                Toast.makeText(ctx, "Wrong authentication provided", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(ctx, Dashboard.class));
                GlobalVars.user_id = users.getId();
            }


        }
    }
}
