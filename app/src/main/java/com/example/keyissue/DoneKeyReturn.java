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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DoneKeyReturn extends AppCompatActivity {

    TextView text;

    ImageView person_image;
    TextView person_name;
    TextView person_roll;

    Button return_btn;
    Button cancel_btn;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_key_return);

        text = findViewById(R.id.text);

        person_image = findViewById(R.id.person_image);
        person_name = findViewById(R.id.person_name);
        person_roll = findViewById(R.id.person_roll);

        return_btn = findViewById(R.id.return_btn);
        cancel_btn = findViewById(R.id.cancel_btn);

    //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    //getting the key details
        final String return_key_id = sharedPreferences.getString("return_key_id", "DNE");

        final String return_person_name = sharedPreferences.getString("return_person_name", "DNE");
        final String return_person_roll = sharedPreferences.getString("return_person_roll", "DNE");
        final String return_person_secret = sharedPreferences.getString("return_person_secret", "DNE");

        if(return_person_name != null && return_person_roll!= null && return_person_secret != null)
        {
            //checking if phone if connected to net or not
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                person_name.setText("Name: " + return_person_name);
                person_roll.setText("Roll: " + return_person_roll);

            //getting the photo of that person and setting it
                String type = "get_person_photo";
                try
                {
                    Bitmap person_imageBitmap = new ServerActions().execute(type, return_person_roll.toLowerCase()).get();

                    if(person_imageBitmap == null) //invalid person //not registered person
                    {
                        //redirecting to the unknown person screen
                        Intent UnknownPersonIntent = new Intent(DoneKeyReturn.this, UnknownPerson.class);
                        startActivity(UnknownPersonIntent);
                        finish();

                        //text.setText("Person image not found on server");
                    }
                    else
                    {
                        person_image.setImageBitmap(person_imageBitmap);
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            //on clicking on return button
                return_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                    //updating the return person details in the database
                        try
                        {
                            String type = "return_issued_key";
                            String return_issued_keyResult = (new DatabaseActions().execute(type, return_key_id, return_person_name, return_person_roll, return_person_secret).get());

                            if(return_issued_keyResult.equals("1"))
                            {
                                Toast.makeText(DoneKeyReturn.this, "Key Successfully Returned", Toast.LENGTH_LONG).show();

                            //redirecting to the success screen
                                Intent SuccessIntent = new Intent(DoneKeyReturn.this, SuccessScreen.class);
                                startActivity(SuccessIntent);
                                finish();
                            }
                            else
                            {
                                text.setText("Something went wrong in returning the issued key");
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
