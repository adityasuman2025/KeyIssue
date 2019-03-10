package com.example.keyissue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class IssuedKeysHistory extends AppCompatActivity
{
    TextView text;
    ListView listIssuedKeys;

    String type;

    String issue_ids[];
    String key_names[];
    String issued_by_names[];
    String issued_by_rolls[];
    String issued_ons[];
    String statuses[];

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issued_keys_history);

        text = findViewById(R.id.text);
        listIssuedKeys = findViewById(R.id.listIssuedKeys);

        //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            try
            {
                //to list all the not returned keys
                type= "list_issued_keys_history";
                String list_issued_keys_historyResult = (new DatabaseActions().execute(type).get());

                if(!list_issued_keys_historyResult.equals("0") && !list_issued_keys_historyResult.equals("-1") && !list_issued_keys_historyResult.equals("Something went wrong"))
                {
                    //parse JSON data
                    JSONArray ja = new JSONArray(list_issued_keys_historyResult);
                    JSONObject jo = null;

                    issue_ids = new String[ja.length()];
                    key_names = new String[ja.length()];
                    issued_by_names = new String[ja.length()];
                    issued_by_rolls = new String[ja.length()];
                    issued_ons = new String[ja.length()];
                    statuses = new String[ja.length()];

                    for (int i =0; i<ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String issue_id = jo.getString("id");
                        String key_name = jo.getString("key_name");

                        String issued_by_name = jo.getString("issued_by_name");
                        String issued_by_roll = jo.getString("issued_by_roll");
                        String issued_on = jo.getString("issued_on");
                        String status = jo.getString("status");

                        issue_ids[i] = issue_id;
                        key_names[i] = key_name;

                        issued_by_names[i] = issued_by_name;
                        issued_by_rolls[i] = issued_by_roll;
                        issued_ons[i] = issued_on;
                        statuses[i] = status;
                    }

                    //listing all the ever issued keys in listview
                    IssuedKeysHistoryAdapter issuedKeysHistoryAdapter = new IssuedKeysHistoryAdapter();
                    listIssuedKeys.setAdapter(issuedKeysHistoryAdapter);

                    //on clicking on list
                    listIssuedKeys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                        {
                            editor.putString("key_issue_history_of_id", issue_ids[position]);
                            editor.apply();

                        //redirecting the key issue history details page
                            Intent KeyIssueHistoryDetailsIntent = new Intent(IssuedKeysHistory.this, KeyIssueHistoryDetails.class);
                            startActivity(KeyIssueHistoryDetailsIntent);
                        }
                    });
                }
                else
                {
                    text.setText("Something went wrong in listing the issued keys history");
                }
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            text.setText("Internet connection is not available");
        }
    }

    //creating custom adapter to list issued keys
    class IssuedKeysHistoryAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return issue_ids.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            //rendering the layout
            view = getLayoutInflater().inflate(R.layout.issued_keys_history_adapter, null);

            //defining variables
            TextView key_name = view.findViewById(R.id.key_name);
            TextView issue_date = view.findViewById(R.id.issue_date);

            TextView issue_status = view.findViewById(R.id.issue_status);

            //setting the variables to a value
            key_name.setText(key_names[i]);
            issue_date.setText(issued_ons[i]);

            String status = "NA";
            if(statuses[i].equals("1"))//not returned
            {
                status = "N-R"; //Not-Returned
            }
            else if(statuses[i].equals("2"))//returned
            {
                status = "R"; //Returned
            }

            issue_status.setText(status);

            return view;
        }
    }
}
