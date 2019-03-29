package com.example.keyissue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class DoneKeyIssue extends AppCompatActivity {

    TextView text;

    ImageView person_image;
    TextView person_name;
    TextView person_roll;

    Button issue_btn;
    Button cancel_btn;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_key_issue);

        text = findViewById(R.id.text);

        person_image = findViewById(R.id.person_image);
        person_name = findViewById(R.id.person_name);
        person_roll = findViewById(R.id.person_roll);

        issue_btn = findViewById(R.id.issue_btn);
        cancel_btn = findViewById(R.id.cancel_btn);

        //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //getting the key details
        final String key_name = sharedPreferences.getString("key_name", "DNE");
        final String key_secret = sharedPreferences.getString("key_secret", "DNE");

        final String issue_person_name = sharedPreferences.getString("issue_person_name", "DNE");
        final String issue_person_roll = sharedPreferences.getString("issue_person_roll", "DNE");
        final String issue_person_secret = sharedPreferences.getString("issue_person_secret", "DNE");

        if(issue_person_name != null && issue_person_roll!= null && issue_person_secret != null)
        {
            //checking if phone if connected to net or not
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                person_name.setText("Name: " + issue_person_name);
                person_roll.setText("Roll: " + issue_person_roll);

            //getting the photo of that person and setting it
                String type = "get_person_photo";
                try
                {
                    Bitmap person_imageBitmap = new ServerActions().execute(type, issue_person_roll.toLowerCase()).get();

                    if(person_imageBitmap == null)//invalid person //not registered person
                    {
                    //inserting the invalid person into the database
                        type = "insert_invalid_students_in_db";

                        String insert_invalid_students_in_dbResult = new DatabaseActions().execute(type, issue_person_name, issue_person_roll).get();

                    //redirecting to the unknown person screen
                        Intent UnknownPersonIntent = new Intent(DoneKeyIssue.this, UnknownPerson.class);
                        startActivity(UnknownPersonIntent);
                        finish();
                    }
                    else //that person is available in database
                    {
                        person_image.setImageBitmap(person_imageBitmap);

                    //handling private keys, if that key is private //(private means, that key can only be issued only to some specific persons)
                        type = "handling_private_keys";
                        String handling_private_keysResult = new DatabaseActions().execute(type, key_name, issue_person_roll).get();

                        if(handling_private_keysResult.equals("0")) //that key is not a private key //so it can be issued to any person
                        {
                            //everything is fine, key can be issued from this page by clicking on issue button
                        }
                        else if(handling_private_keysResult.equals("1"))//that key is a private key and that person is also authorized for issuing that key
                        {
                            //everything is fine, key can be issued from this page by clicking on issue button
                        }
                        else if(handling_private_keysResult.equals("2"))
                        {
                        //redirecting to the person is not authorized for that key page
                            Intent NotAuthorizedPersonIntent = new Intent(DoneKeyIssue.this, NotAuthorizedPerson.class);
                            startActivity(NotAuthorizedPersonIntent);
                            finish();
                        }
                        else
                        {
                        //redirecting to the something went wrong page
                            Toast.makeText(this, "Something went wrong in handling private keys", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //on clicking on issue button
                issue_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //inserting the new issued key in the database
                        try
                        {
                            String type = "issue_key_for_a_person";

                            String issue_key_for_a_personResult = new DatabaseActions().execute(type, key_name, key_secret, issue_person_name, issue_person_roll, issue_person_secret).get();

                            if(issue_key_for_a_personResult.equals("1")) //key is successfully issued
                            {
                                Toast.makeText(DoneKeyIssue.this, "Key Successfully Issued", Toast.LENGTH_LONG).show();

                            //redirecting to the success screen
                                Intent SuccessIntent = new Intent(DoneKeyIssue.this, SuccessScreen.class);
                                startActivity(SuccessIntent);
                                finish();
                            }
                            else
                            {
                                text.setText("Something went wrong in issuing the key to the person");
                            }

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else
            {
                text.setText("Internet Connection is not available");
            }
        }
        else
        {
            text.setText("Wrong QR Code");
        }

        //on clicking on cancel button
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
