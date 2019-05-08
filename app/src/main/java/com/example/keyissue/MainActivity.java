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
    Button not_return_keys_btn;
    Button history_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        issue_btn = findViewById(R.id.issue_btn);
        return_btn = findViewById(R.id.return_btn);
        not_return_keys_btn = findViewById(R.id.not_return_keys_btn);
        history_btn = findViewById(R.id.history_btn);

    //on clicking on issue btn
        issue_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            //redirecting to issue key page
                Intent IssueScanKeyIntent = new Intent(MainActivity.this, IssueScanKey.class);
                startActivity(IssueScanKeyIntent);
            }
        });

    //on clicking on return btn
        return_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            //redirecting to list issued keys page
                Intent ListIssuedKeysIntent = new Intent(MainActivity.this, ReturnScanKey.class);
                startActivity(ListIssuedKeysIntent);
            }
        });

    //on clicking on not return keys btn
        not_return_keys_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //redirecting to list issued keys page
                Intent ListBlockIntent = new Intent(MainActivity.this, ListBlock.class);
                startActivity(ListBlockIntent);
            }
        });

    //on clicking on history btn
        history_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            //redirecting to list issued keys history
                Intent IssuedKeysHistoryIntent = new Intent(MainActivity.this, IssuedKeysHistory.class);
                startActivity(IssuedKeysHistoryIntent);
            }
        });
    }
}
