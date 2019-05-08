package com.example.keyissue;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ListBlock extends AppCompatActivity {

    TextView text;
    ListView listBlock;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String blocks[] = {"Block 3", "Block 6", "Block 9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_block);

        text = findViewById(R.id.text);
        listBlock = findViewById(R.id.listBlock);

    //checking cookies
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ArrayAdapter<String> listBlockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, blocks);
        listBlock.setAdapter(listBlockAdapter);

        //on clicking on any item of the list
        listBlock.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                String blockStr = blocks[position];
                String[] parts = blockStr.split(" ");

                String blockID = (parts[parts.length - 1]).trim();

            //making cookie of the selected block
                editor.putString("blockID", blockID);
                editor.apply();

                //Toast.makeText(ListBlock.this, blockID, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
