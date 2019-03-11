package com.example.keyissue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class DoneKeyReturn extends AppCompatActivity {

    TextView text;

    ImageView person_image;
    TextView person_name;
    TextView person_roll;

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

    //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    //getting the key details
        String return_key_id = sharedPreferences.getString("return_key_id", "DNE");

        String return_person_name = sharedPreferences.getString("return_person_name", "DNE");
        String return_person_roll = sharedPreferences.getString("return_person_roll", "DNE");
        String return_person_secret = sharedPreferences.getString("return_person_secret", "DNE");

        if(return_person_name != null && return_person_roll!= null && return_person_secret != null)
        {
            //checking if phone if connected to net or not
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                person_name.setText(return_person_name);
                person_roll.setText(return_person_roll.toLowerCase());

            //getting the photo of that person
                String type = "get_person_photo";
                try
                {
                    Bitmap person_imageBitmap = new ServerActions().execute(type, return_person_roll.toLowerCase()).get();

                    if(person_imageBitmap.equals(null))
                    {
                        text.setText("Person image not found on server");
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
    }
}
