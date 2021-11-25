package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class New_Record extends AppCompatActivity {
    private TextView tv_back;
    private Button button_saveDataRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        tv_back = findViewById(R.id.back_home);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button_saveDataRecord = findViewById(R.id.button_saveDataRecord);

        button_saveDataRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(New_Record.this, home.class);
                startActivity(i);
                finish();
            }
        });
    }
}