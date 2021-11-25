package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class home extends AppCompatActivity {
    private Button button_addFood;
    private Button button_addRecord;
    private Button button_viewFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button_addFood = findViewById(R.id.button_add_food);

        button_addRecord = findViewById(R.id.button_add_record);


        button_addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(home.this, New_Record.class);
                startActivity(i);
            }
        });

        button_viewFood =findViewById(R.id.button_view_food);


    }
}