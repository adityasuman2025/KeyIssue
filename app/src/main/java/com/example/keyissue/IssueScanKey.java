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

public class IssueScanKey extends AppCompatActivity
{
    Button scan_key_qr_btn;
    TextView scan_key_qr_feed;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_scan_key);

        scan_key_qr_btn = findViewById(R.id.scan_key_qr_btn);
        scan_key_qr_feed = findViewById(R.id.scan_key_qr_feed);

    //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    //on clicking on scan key qr btn
        scan_key_qr_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator intentIntegrator = new IntentIntegrator(IssueScanKey.this);
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
            String key_name = scannedResult.split("\n")[0];
            String key_secret = scannedResult.split("\n")[2];

            //checking if phone if connected to net or not
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
            //checking if key is already issued
                String type = "check_key_issued";

                try
                {
                    String check_key_issuedResult = new DatabaseActions().execute(type, key_name, key_secret).get();

                    if(check_key_issuedResult.equals("-1"))
                    {
                        scan_key_qr_feed.setText("Database issue found");
                    }
                    else if (check_key_issuedResult.equals("Something went wrong"))
                    {
                        scan_key_qr_feed.setText(check_key_issuedResult);
                    }
                    else if(check_key_issuedResult.equals("1")) //key is already issued
                    {
                        scan_key_qr_feed.setText("This key has already been issued");

                    }
                    else if(check_key_issuedResult.equals("0")) //everything is fine // it can be issued
                    {
                        editor.putString("key_name", key_name);
                        editor.putString("key_secret", key_secret);
                        editor.apply();

                        //redirecting the scan person qr page
                        Intent IssueScanPersonIntent = new Intent(IssueScanKey.this, IssueScanPerson.class);
                        startActivity(IssueScanPersonIntent);
                        finish(); //used to delete the last activity history which we want to delete
                    }
                    else
                    {
                        //scan_key_qr_feed.setText(check_key_issuedResult);
                        scan_key_qr_feed.setText("unKnown Error");
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                scan_key_qr_feed.setText("Internet Connection is not available");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
