package com.example.keyissue;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UnknownPerson extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unknown_person);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run(){
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
