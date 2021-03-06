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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    String issued_by_phones[];

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

        final String blockID = sharedPreferences.getString("blockID", "DNE");

        //checking if phone if connected to net or not
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            try
            {
            //to list all the not returned keys
                type= "list_not_returned_keys";
                String list_not_returned_keysResult = (new DatabaseActions().execute(type, blockID).get());

                if(!list_not_returned_keysResult.equals("0") && !list_not_returned_keysResult.equals("-1") && !list_not_returned_keysResult.equals("Something went wrong"))
                {
                //parse JSON data
                    JSONArray ja = new JSONArray(list_not_returned_keysResult);
                    JSONObject jo = null;

                    if(ja.length() < 1 )
                    {
                        text.setText("No keys are currently issued");
                    }

                    issue_ids = new String[ja.length()];
                    key_names = new String[ja.length()];

                    issued_by_names = new String[ja.length()];
                    issued_by_rolls = new String[ja.length()];
                    issued_by_phones = new String[ja.length()];

                    issued_ons = new String[ja.length()];

                    for (int i =0; i<ja.length(); i++)
                    {
                        jo = ja.getJSONObject(i);

                        String issue_id = jo.getString("id");
                        String key_name = jo.getString("key_name");

                        String issued_by_name = jo.getString("issued_by_name");
                        String issued_by_roll = jo.getString("issued_by_roll");
                        String issued_by_phone = jo.getString("issued_by_phone");

                        //String issued_by_phone = "NA";

                        //getting and formatting date
                        String issued_on = jo.getString("issued_on");

                        Date issued_onDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(issued_on);

                        DateFormat df = new SimpleDateFormat("dd MMM yyyy, h:mm a");

                        String issued_onDate_str = df.format(issued_onDate);

                        issue_ids[i] = issue_id;
                        key_names[i] = key_name;

                        issued_by_names[i] = issued_by_name;
                        issued_by_rolls[i] = issued_by_roll;
                        issued_by_phones[i] = issued_by_phone;

                        issued_ons[i] = issued_onDate_str;
                    }

                    //listing not returned keys
                    IssuedKeyDetailsAdapter issuedKeyDetailsAdapter = new IssuedKeyDetailsAdapter();
                    listIssuedKeys.setAdapter(issuedKeyDetailsAdapter);

                    //on clicking on any item of the list
                    listIssuedKeys.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                        {
                           Toast.makeText(ListIssuedKeys.this, key_names[position], Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    //text.setText(list_not_returned_keysResult);
                    text.setText("Something went wrong in listing not-returned keys");
                }
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
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
            TextView issued_by_phone = view.findViewById(R.id.issued_by_phone);

            //setting the variables to a value
            key_name.setText(key_names[i]);
            issue_date.setText(issued_ons[i]);

            issued_by_name.setText("Name: " + issued_by_names[i]);
            issued_by_roll.setText("Roll No: " + issued_by_rolls[i]);
            issued_by_phone.setText("Contact No: " + issued_by_phones[i]);

            return view;
        }
    }
}
