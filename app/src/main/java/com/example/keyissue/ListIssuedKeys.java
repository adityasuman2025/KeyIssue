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

public class ListIssuedKeys extends AppCompatActivity
{
    TextView text;
    ListView listIssuedKeys;

    String type;

    String issue_ids[];
    String key_names[];
    String issued_by_names[];
    String issued_by_rolls[];
    String issued_ons[];

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_issued_keys);

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
                type= "list_not_returned_keys";
                String list_not_returned_keysResult = (new DatabaseActions().execute(type).get());

                if(!list_not_returned_keysResult.equals("0") && !list_not_returned_keysResult.equals("-1") && !list_not_returned_keysResult.equals("Something went wrong"))
                {
                    //parse JSON data
                    JSONArray ja = new JSONArray(list_not_returned_keysResult);
                    JSONObject jo = null;

                    issue_ids = new String[ja.length()];
                    key_names = new String[ja.length()];
                    issued_by_names = new String[ja.length()];
                    issued_by_rolls = new String[ja.length()];
                    issued_ons = new String[ja.length()];

                    for (int i =0; i<ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String issue_id = jo.getString("id");
                        String key_name = jo.getString("key_name");

                        String issued_by_name = jo.getString("issued_by_name");
                        String issued_by_roll = jo.getString("issued_by_roll");
                        String issued_on = jo.getString("issued_on");

                        issue_ids[i] = issue_id;
                        key_names[i] = key_name;

                        issued_by_names[i] = issued_by_name;
                        issued_by_rolls[i] = issued_by_roll;
                        issued_ons[i] = issued_on;
                    }

                    //listing not returned keys
                    IssuedKeyDetailsAdapter issuedKeyDetailsAdapter = new IssuedKeyDetailsAdapter();
                    listIssuedKeys.setAdapter(issuedKeyDetailsAdapter);

                    //on clicking on list
                    listIssuedKeys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                        {
                            editor.putString("return_key_id", issue_ids[position]);
                            editor.apply();

                        //redirecting the key qr scan page for returning the key
                            Intent ReturnScanKeyIntent = new Intent(ListIssuedKeys.this, ReturnScanKey.class);
                            startActivity(ReturnScanKeyIntent);
                            finish();
                        }
                    });
                }
                else
                {
                    text.setText("Something went wrong in listing not-returned keys");
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
    class IssuedKeyDetailsAdapter extends BaseAdapter
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
            view = getLayoutInflater().inflate(R.layout.issued_key_details_adapter, null);

            //defining variables
            TextView key_name = view.findViewById(R.id.key_name);
            TextView issue_date = view.findViewById(R.id.issue_date);

            TextView issued_by_name = view.findViewById(R.id.issued_by_name);
            TextView issued_by_roll = view.findViewById(R.id.issued_by_roll);

            //setting the variables to a value
            key_name.setText(key_names[i]);
            issue_date.setText(issued_ons[i]);

            issued_by_name.setText(issued_by_names[i]);
            issued_by_roll.setText(issued_by_rolls[i]);

            return view;
        }
    }
}
