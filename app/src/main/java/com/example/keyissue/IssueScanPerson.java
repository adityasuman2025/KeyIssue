package com.example.keyissue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutionException;

public class IssueScanPerson extends AppCompatActivity
{
    Button scan_person_qr_btn;
    TextView scan_person_qr_feed;

    String key_name;
    String key_secret;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_scan_person);

        scan_person_qr_btn = findViewById(R.id.scan_person_qr_btn);
        scan_person_qr_feed = findViewById(R.id.scan_person_qr_feed);

    //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    //getting the key details
        key_name = sharedPreferences.getString("key_name", "DNE");;
        key_secret = sharedPreferences.getString("key_secret", "DNE");;

    //on clicking on scan person qr btn
        scan_person_qr_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator intentIntegrator = new IntentIntegrator(IssueScanPerson.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("scanning");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });
    }

    //for getting results after scanning the qr code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null && result.getContents() != null)
        {
            String scannedResult = result.getContents();
            String person[] = scannedResult.split("\n");

            String person_name = scannedResult.split("\n")[0];
            String person_roll = scannedResult.split("\n")[1];
            String person_secret = scannedResult.split("\n")[2];

            //checking if phone if connected to net or not
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                //checking if key is already issued
                String type = "issue_key_for_a_person";

                scan_person_qr_feed.setText("Key: " + key_name + "\n is issued to: \n Name: " + person_name);

                try
                {
                    String issue_key_for_a_personResult = new DatabaseActions().execute(type, key_name, key_secret, person_name, person_roll, person_secret).get();

                    if(issue_key_for_a_personResult.equals("-1"))
                    {
                        scan_person_qr_feed.setText("Database issue found");
                    }
                    else if (issue_key_for_a_personResult.equals("Something went wrong"))
                    {
                        scan_person_qr_feed.setText(issue_key_for_a_personResult);
                    }
                    else if(issue_key_for_a_personResult.equals("1")) //key is successfully issued
                    {
                    //redirecting the done issuing page
                        Intent IssueScanPersonIntent = new Intent(IssueScanPerson.this, DoneKeyIssue.class);
                        startActivity(IssueScanPersonIntent);
                        finish(); //used to delete the last activity history which we want to delete
                    }
                    else
                    {
                        scan_person_qr_feed.setText("unKnown Error");
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                scan_person_qr_feed.setText("Internet Connection is not available");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
