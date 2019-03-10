package com.example.keyissue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DoneKeyIssue extends AppCompatActivity {

    Button ok_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_key_issue);

        ok_btn = findViewById(R.id.ok_btn);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //used to delete the last activity history which we want to delete
            }
        });
    }
}
