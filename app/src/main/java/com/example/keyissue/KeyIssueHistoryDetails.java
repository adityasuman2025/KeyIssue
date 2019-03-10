package com.example.keyissue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class KeyIssueHistoryDetails extends AppCompatActivity {

    TextView key_name;
    TextView issue_date;

    TextView issued_by_name;
    TextView issued_by_roll;

    TextView returned_by_name;
    TextView returned_by_roll;

    TextView status;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_issue_history_details);

        key_name = findViewById(R.id.key_name);
        issue_date = findViewById(R.id.issue_date);

        issued_by_name = findViewById(R.id.issued_by_name);
        issued_by_roll = findViewById(R.id.issued_by_roll);

        returned_by_name = findViewById(R.id.returned_by_name);
        returned_by_roll = findViewById(R.id.returned_by_roll);

        status = findViewById(R.id.status);

    //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    //getting the key_issue_history_of_id
        String key_issue_history_of_id = sharedPreferences.getString("key_issue_history_of_id", "DNE");

        //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            try {
                //to list all the not returned keys
                type = "get_key_issue_history_details";
                String list_issued_keys_historyResult = (new DatabaseActions().execute(type, key_issue_history_of_id).get());

                if (!list_issued_keys_historyResult.equals("0") && !list_issued_keys_historyResult.equals("-1") && !list_issued_keys_historyResult.equals("Something went wrong")) {
                    //parse JSON data
                    JSONArray ja = new JSONArray(list_issued_keys_historyResult);
                    JSONObject jo = null;

                    for (int i = 0; i < ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String key_name_str = jo.getString("key_name");
                        String issued_on_str = jo.getString("issued_on");

                        String issued_by_name_str = jo.getString("issued_by_name");
                        String issued_by_roll_str = jo.getString("issued_by_roll");

                        String returned_by_name_str = jo.getString("returned_by_name");
                        String returned_by_roll_str = jo.getString("returned_by_roll");

                        String status_str = jo.getString("status");
                        if(status_str.equals("1"))
                            status.setText("Not Returned");
                        else if(status_str.equals("2"))
                            status.setText("Returned");
                        else
                            status.setText("NA");

                        key_name.setText(key_name_str);
                        issue_date.setText(issued_on_str);

                        issued_by_name.setText("Issued By: " + issued_by_name_str);
                        issued_by_roll.setText("Roll No: " + issued_by_roll_str);

                        returned_by_name.setText("Returned By: " + returned_by_name_str);
                        returned_by_roll.setText("Roll No: " + returned_by_roll_str);
                    }
                } else {
                    key_name.setText("Something went wrong in listing the issued keys history");
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
            key_name.setText("Internet Connection is not available");
        }
    }
}
