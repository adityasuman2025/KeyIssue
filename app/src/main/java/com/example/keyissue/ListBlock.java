package com.example.keyissue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ListBlock extends AppCompatActivity {

    TextView text;
    ListView listBlock;

    String blocks[] = {"Block 3", "Block 6", "Block 9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_block);

        text = findViewById(R.id.text);
        listBlock = findViewById(R.id.listBlock);

        text.setText(Arrays.toString(blocks));
    }
}
