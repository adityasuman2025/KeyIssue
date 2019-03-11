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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ReturnScanKey extends AppCompatActivity {

    TextView key_name;
    Button scan_key_qr_btn;
    TextView scan_key_qr_feed;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String type;
    String return_key_id;

    String key_name_str;
    String key_secret_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_scan_key);

        key_name = findViewById(R.id.key_name);
        scan_key_qr_btn = findViewById(R.id.scan_key_qr_btn);
        scan_key_qr_feed = findViewById(R.id.scan_key_qr_feed);

        //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //getting the key_id from cookie
        return_key_id = sharedPreferences.getString("return_key_id", "DNE");

    //getting the key issue details of that issue_id from database
        //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            try {
                //to list all the not returned keys
                type = "get_key_issue_history_details";
                String list_issued_keys_historyResult = (new DatabaseActions().execute(type, return_key_id).get());

                if (!list_issued_keys_historyResult.equals("0") && !list_issued_keys_historyResult.equals("-1") && !list_issued_keys_historyResult.equals("Something went wrong")) {
                    //parse JSON data
                    JSONArray ja = new JSONArray(list_issued_keys_historyResult);
                    JSONObject jo = null;

                    for (int i = 0; i < ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        key_name_str = jo.getString("key_name");
                        key_secret_str = jo.getString("key_secret");

                        String issued_by_name_str = jo.getString("issued_by_name");
                        String issued_by_roll_str = jo.getString("issued_by_roll");

                        key_name.setText(key_name_str);
                    }
                } else {
                    scan_key_qr_feed.setText("Something went wrong in getting the issued keys details");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            scan_key_qr_feed.setText("Internet Connection is not available");
        }

        //on clicking on scan key qr btn
        scan_key_qr_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator intentIntegrator = new IntentIntegrator(ReturnScanKey.this);
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

            //checking QR Code
            String test[] = scannedResult.split("\n");
            if(test.length == 3)
            {
                String key_name = scannedResult.split("\n")[0];
                String key_secret = scannedResult.split("\n")[2];

                //checking if phone if connected to net or not
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                {
                //checking if the scanned key is right one
                    if(key_name.equals(key_name_str) && key_secret.equals(key_secret_str))
                    {
                    //if the scanned key details is same as the details of key we want to return
                    //redirecting the person qr scan page for returning the key
                        Intent ReturnScanPersonIntent = new Intent(ReturnScanKey.this, ReturnScanPerson.class);
                        startActivity(ReturnScanPersonIntent);
                        finish();
                    }
                    else
                    {
                        scan_key_qr_feed.setText("This is wrong key!!");
                    }
                }
                else
                {
                    scan_key_qr_feed.setText("Internet Connection is not available");
                }
            }
            else
            {
                scan_key_qr_feed.setText("Wrong QR Code");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
