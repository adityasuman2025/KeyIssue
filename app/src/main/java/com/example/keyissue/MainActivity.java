package com.example.keyissue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
//defining variables
    Button issue_btn;
    Button return_btn;
    Button history_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        issue_btn = findViewById(R.id.issue_btn);
        return_btn = findViewById(R.id.return_btn);
        history_btn = findViewById(R.id.history_btn);

    //on clicking on issue btn
        issue_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            //redirecting the list course page
                Intent IssueScanKeyIntent = new Intent(MainActivity.this, IssueScanKey.class);
                startActivity(IssueScanKeyIntent);
                //finish(); //used to delete the last activity history which we want to delete
            }
        });
    }
}
