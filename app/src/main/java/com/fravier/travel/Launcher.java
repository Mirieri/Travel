package com.fravier.travel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fravier.travel.auth.SignIn;

public class  Launcher extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
//        this.getSupportActionBar().hide();


        Thread splashscreen = new Thread() {// the thread to run the halal
            // splash screen
            @Override
            public void run() {
                try {
                    sleep(2000); // the splash screen should run for 2 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intentDashboard = new Intent(Launcher.this,
                            SignIn.class);
                    startActivity(intentDashboard);
                    finish();
                }

            }
        };
        splashscreen.start();
    }
//
//    @Override
//    public void onResume() {
//
//    }
}
